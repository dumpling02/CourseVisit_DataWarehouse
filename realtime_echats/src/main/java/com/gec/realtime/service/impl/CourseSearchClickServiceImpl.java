package com.gec.realtime.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gec.realtime.dao.CourseSearchClickMapper;
import com.gec.realtime.entity.CourseSearchClickCount;
import com.gec.realtime.service.CourseSearchClickService;
import org.springframework.stereotype.Service;

@Service("CourseSearchClickServiceImpl")
public class CourseSearchClickServiceImpl
        extends ServiceImpl<CourseSearchClickMapper, CourseSearchClickCount> implements CourseSearchClickService
{


}