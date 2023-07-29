package com.gec.realtime.db

import java.sql.{Connection, DriverManager}

object DbUtils {

	//定义一个方法，用于连接数据库(mysql8.0)
	def connectMysql(): Connection = {
		val driver = "com.mysql.jdbc.Driver"
		val url = "jdbc:mysql://localhost:3306/spark_log_db?&serverTimezone=GMT&useSSL=false"
		val username = "root"
		val pass = "111111"
		// 创建连接对象
		Class.forName( driver )
		val conn = DriverManager.getConnection( url, username, pass )
		//将连接对象返回
		conn
	}
}