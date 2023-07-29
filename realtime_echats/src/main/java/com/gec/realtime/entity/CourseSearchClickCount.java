package com.gec.realtime.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value="course_search_clickcount")
public class CourseSearchClickCount {

//create table course_search_clickcount(
//                                         day_search_course varchar(50),
//                                         click_count Long
//);

    private String daySearchCourse;  // 对应 字段day_search_course
    private Long clickCount;    // 对应 click_count
}

