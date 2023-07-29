package com.gec.realtime.dao

import com.gec.realtime.db.DbUtils
import com.gec.realtime.domain.CourseSearchClickCount

import java.sql.{Connection, PreparedStatement, ResultSet}
import scala.collection.mutable.ListBuffer

object CourseSearchClickCountDAO {

	/**
	 * 保存数据到mysql
	 *
	 * @param list CourseSearchClickCount集合
	 */
	def save(ele: CourseSearchClickCount): Unit = {

		val conn: Connection = DbUtils.connectMysql()
		// 关闭自动提交 sql命令的提交由应用程序负责
		conn.setAutoCommit(false)
		val statement = conn.createStatement()
		statement.executeUpdate("insert into course_search_clickcount(day_search_course,click_count) " +
			"values('" + ele.day_search_course + "'," + ele.click_count + ")")
		conn.commit()
		conn.close()
	}


	def findByDayAndCourseId(ele: CourseSearchClickCount): CourseSearchClickCount = {
		val conn: Connection = DbUtils.connectMysql()

		val pStatement = conn.prepareStatement("select * from course_search_clickcount where day_search_course=?")

		pStatement.setString(1, ele.day_search_course)
		val rs: ResultSet = pStatement.executeQuery()

		var courseSearchClickCount = new CourseSearchClickCount(null, 0L)

		if (rs.next()) {
			CourseSearchClickCount(rs.getString(1), rs.getLong(2))
		} else
			null
	}

	def updateCourseId(ele: CourseSearchClickCount): Unit = {
		val conn: Connection = DbUtils.connectMysql()
		conn.setAutoCommit(false)
		val sql = "update course_search_clickcount set click_count=?  where day_search_course=?";
		val pstatement: PreparedStatement = conn.prepareStatement(sql)
		pstatement.setLong(1, ele.click_count)
		pstatement.setString(2, ele.day_search_course);

		pstatement.executeUpdate();

		conn.commit()
		conn.close()
	}

}
