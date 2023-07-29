package com.gec.realtime.dao

import java.sql.{Connection, PreparedStatement, ResultSet}

import com.gec.realtime.db.DbUtils
import com.gec.realtime.domain.CourseClickCount

import scala.collection.mutable.ListBuffer

object CourseClickCountDAO {

	// 插入数据
	def save(ele:CourseClickCount): Unit = {

		val conn: Connection = DbUtils.connectMysql()
		// 关闭自动提交 sql命令的提交由应用程序负责
		conn.setAutoCommit( false )
		val statement = conn.createStatement()
		statement.executeUpdate(
			"insert into course_clickcount(day_course,click_count) values('"
				+ele.day_course+"',"+ele.click_count+")")
		conn.commit()
		conn.close()
	}

	// 根据day_course字段查找数据
	def findByDayAndCourseId(ele:CourseClickCount): CourseClickCount ={
		val conn: Connection = DbUtils.connectMysql()

		val  pStatement= conn.prepareStatement("select * from course_clickcount where day_course=?")
		pStatement.setString(1,ele.day_course)
		val rs: ResultSet = pStatement.executeQuery()

		var courseClickCount=new CourseClickCount(null,0L)

		if(rs.next()){
			CourseClickCount(rs.getString(1),rs.getLong(2))
		}else
			null
	}


	def updateCourseId(ele:CourseClickCount): Unit ={
		val conn: Connection = DbUtils.connectMysql()
		conn.setAutoCommit( false )
		val sql="update course_clickcount set click_count=?  where day_course=?";

		val pstatement: PreparedStatement = conn.prepareStatement(sql)
		pstatement.setLong(1,ele.click_count)
		pstatement.setString(2,ele.day_course);

		pstatement.executeUpdate();

		conn.commit()
		conn.close()
	}
}
