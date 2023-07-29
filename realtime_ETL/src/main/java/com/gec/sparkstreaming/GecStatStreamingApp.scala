package com.gec.sparkstreaming

import com.gec.realtime.domain.CourseClickCount
import com.gec.realtime.service.CourseClickCountService
import com.gec.sparkstreaming.domain.ClickLog
import com.gec.sparkstreaming.utils.DateUtils
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}

import scala.collection.mutable.ListBuffer

object GecStatStreamingApp {

	def main(args: Array[String]): Unit = {


		//1.创建SparkConf
		val sparkConf: SparkConf = new SparkConf().setAppName("sparkstreaming").setMaster("local[1]")
		//2.创建StreamingContext
		val ssc = new StreamingContext(sparkConf, Seconds(60))

		//3.定义Kafka参数：kafka集群地址、消费者组名称、key序列化、value序列化
		val kafkaPara: Map[String, Object] = Map[String, Object](
			ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG -> "hadoop-001:9092,hadoop-002:9092,hadoop-003:9092",
			ConsumerConfig.GROUP_ID_CONFIG -> "gecGroup",
			ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG -> "org.apache.kafka.common.serialization.StringDeserializer",
			ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG -> classOf[StringDeserializer]
		)

		//4.读取Kafka数据创建DStream
		val kafkaDStream: InputDStream[ConsumerRecord[String, String]] =
			KafkaUtils.createDirectStream[String, String](
				ssc,
				LocationStrategies.PreferConsistent, //优先位置
				ConsumerStrategies.Subscribe[String, String](Set("logtopic"), kafkaPara)// 消费策略：（订阅多个主题，配置参数）
			)

		//将kafka的数据读取到DStream对象处理
		val logs: DStream[String] = kafkaDStream.map(record => record.value())

		val cleanData = logs.map(line => {
			println(line)
			val infos = line.split("\t")

			// infos(2) = "GET /class/130.html HTTP/1.1"
			// url = /class/130.html
			val url = infos(2).split(" ")(1)
			var courseId = 0

			// 把实战课程的课程编号拿到了
			if (url.startsWith("/class")) {
				val courseIdHTML = url.split("/")(2)
				courseId = courseIdHTML.substring(0, courseIdHTML.lastIndexOf(".")).toInt
			}

			ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), courseId, infos(3).toInt, infos(4))
		}).filter(clicklog => clicklog.courseId != 0)


		cleanData.print()
		//	host=20230614_search.yahoo.com_130
		//	host=20230614_search.yahoo.com_146
		//	host=20230614_search.yahoo.com_131
		//	host=20230614_search.yahoo.com_145


		cleanData.map(x => {
			// (20230614_130, 1)
			// HBase rowkey设计： 20171111_88

			(x.time.substring(0, 8) + "_" + x.courseId, 1)
		})
			.reduceByKey(_ + _)		// 按key分组聚合
			.foreachRDD(rdd => {
				rdd.foreachPartition(partitionRecords => {
					val list = new ListBuffer[CourseClickCount]
					partitionRecords.foreach(pair => {
						list.append(CourseClickCount(pair._1, pair._2))
				})
					CourseClickCountService.save(list)
				//CourseClickCountDAO.save(list)
			})
		})

		//7.开启任务
		ssc.start()
		ssc.awaitTermination()

	}

}
