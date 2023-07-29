package com.gec.realtime.domain

/**
 * 实战课程点击数实体类
 * @param day_course  对应的就是HBase中的rowkey 20230614_128
 * @param click_count 对应的访问总数 42
 */

case class CourseClickCount(day_course:String, click_count:Long)