package com.gec.realtime.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value="course_clickcount")
public class CourseClickCount {
//    create table course_clickcount(
//            day_course varchar(50),
//    click_count Long
//);
    private String dayCourse;
    private Long clickCount;
}