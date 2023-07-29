package com.gec.realtime

import com.gec.realtime.dao.CourseClickCountDAO
import com.gec.realtime.domain.{ClickLog, CourseClickCount, CourseSearchClickCount}
import com.gec.realtime.service.{CourseClickCountService, CourseSearchClickCountService}
import com.gec.realtime.utils.DateUtils
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

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

		// step1 拆分数据 格式化为ClickLog数据结构并过滤id为0的数据
		val logs: DStream[String] = kafkaDStream.map(record => record.value())
		val cleanData = logs.map(line => {
			val infos = line.split("\t")
//			println("\n\n\n\n11111111111111111", infos(4))

			// infos(2) = "GET /class/130.html HTTP/1.1"
			// url = /class/130.html
			var courseId = 0
			if(infos!=null && infos.length>4){
				val url = infos(2).split(" ")(1)

				// 把实战课程的课程编号拿到了
				if (url.startsWith("/class")) {
					val courseIdHTML = url.split("/")(2)
					courseId = courseIdHTML.substring(0, courseIdHTML.lastIndexOf(".")).toInt
				}

				ClickLog(infos(0), DateUtils.parseToMinute(infos(1)), courseId, infos(3).toInt, infos(4))

			}else{
				//ip:String, time:String, courseId:Int, statusCode:Int, referer:String
				ClickLog(null, null, courseId, 0, null)
			}

		}).filter(clicklog => clicklog.courseId != 0)
		println("\n\n\n\n\n\n")
		cleanData.print()


		cleanData.map(x => {

			// (20230614_145,1)
			(x.time.substring(0, 8) + "_" + x.courseId, 1)
		})
			.reduceByKey((sum1,x) => sum1 + x)	// 按key 传值累加
			.foreachRDD(rdd => {
				rdd.foreachPartition(partitionRecords => {
					val list = new ListBuffer[CourseClickCount]
					partitionRecords.foreach(pair => {
						list.append(CourseClickCount(pair._1, pair._2))
				})
				CourseClickCountService.save(list)// 保存到数据库
				//CourseClickCountDAO.save(list)
			})
		})


		/**
		 * https://www.sogou.com/web?query=Spark SQL实战
		 *
		 * ==>
		 *
		 * https:/www.sogou.com/web?query=Spark SQL实战
		 */

		cleanData.map(x => {

			val referer = x.referer.replaceAll("//", "/")
			val splits = referer.split("/")
			var host = ""
			if(splits.length > 2) {
				host = splits(1)
			}

			(host, x.courseId, x.time)	 // (www.sogou.com, 145, 20230614)
		})


			.filter(_._1 != "")
			.map(x => {
				println("host="+x._3.substring(0,8) + "_" + x._1 + "_" + x._2)
				(x._3.substring(0,8) + "_" + x._1 + "_" + x._2 , 1)	 // (20230614_search.yahoo.com_145, 1)
			})
			.reduceByKey(_ + _)		// 分组累加
			.foreachRDD(rdd => {	// 累加之后的rdd保存到数据库
				rdd.foreachPartition(partitionRecords => {
					val list = new ListBuffer[CourseSearchClickCount]

					partitionRecords.foreach(pair => {
						list.append(CourseSearchClickCount(pair._1, pair._2))
						println()
					})
					CourseSearchClickCountService.save(list)
				})
			})

//		println("\n\n\n\n\n","!111111111111111111111111111111")
		cleanData.print()


		//7.开启任务
		ssc.start()
		ssc.awaitTermination()
	}

}
