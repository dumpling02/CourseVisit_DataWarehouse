package com.gec.realtime.service

import com.gec.realtime.dao.CourseClickCountDAO
import com.gec.realtime.domain.CourseClickCount

import scala.collection.mutable.ListBuffer
// 通过调用 CourseClickCountDAO 中的方法来实现数据库操作

object CourseClickCountService {

	def save(list: ListBuffer[CourseClickCount]): Unit = {

		for(ele <- list) {
			//判断此实时课程当前日期是否已经存在，如果存在此数据，则进行数据合并，否则数据添加
			val old_CourseClickCount: CourseClickCount = CourseClickCountDAO.findByDayAndCourseId(ele);
			if(old_CourseClickCount==null){
				CourseClickCountDAO.save(ele)
			}else {
				var updateCourseClick=CourseClickCount(
					old_CourseClickCount.day_course,
					old_CourseClickCount.click_count+ele.click_count)	// 累加
				CourseClickCountDAO.updateCourseId(updateCourseClick);
			}

		}
	}

}