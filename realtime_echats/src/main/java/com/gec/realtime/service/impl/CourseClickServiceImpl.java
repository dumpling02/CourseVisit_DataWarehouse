package com.gec.realtime.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.realtime.dao.CourseClickMapper;
import com.gec.realtime.entity.CourseClickCount;
import com.gec.realtime.service.CourseClickService;
import org.springframework.stereotype.Service;

@Service("CourseClickServiceImpl")
public class CourseClickServiceImpl
        extends ServiceImpl<CourseClickMapper, CourseClickCount> implements CourseClickService
{


}