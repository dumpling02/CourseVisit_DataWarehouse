package com.gec.realtime.service

import com.gec.realtime.dao.CourseSearchClickCountDAO
import com.gec.realtime.domain.CourseSearchClickCount

import scala.collection.mutable.ListBuffer

object CourseSearchClickCountService {

	def save(list: ListBuffer[CourseSearchClickCount]): Unit = {

		for(ele <- list) {
			//判断此实时课程当前日期是否已经存在，如果存在此数据，则进行数据合并，否则数据添加
			val old_CourseSearchClickCount: CourseSearchClickCount = CourseSearchClickCountDAO.findByDayAndCourseId(ele);
			if(old_CourseSearchClickCount==null){
				CourseSearchClickCountDAO.save(ele)
			}else {
				var updateCourseClick=CourseSearchClickCount(
					old_CourseSearchClickCount.day_search_course,
					old_CourseSearchClickCount.click_count+ele.click_count)
				CourseSearchClickCountDAO.updateCourseId(updateCourseClick);
			}

		}
	}

}
