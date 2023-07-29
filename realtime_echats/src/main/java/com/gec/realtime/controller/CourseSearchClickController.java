package com.gec.realtime.controller;

import com.alibaba.fastjson.JSON;
import com.gec.realtime.entity.CourseClickCount;
import com.gec.realtime.entity.CourseSearchClickCount;
import com.gec.realtime.service.CourseSearchClickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CourseSearchClickController {

    @Autowired
    @Qualifier("CourseSearchClickServiceImpl")
    private CourseSearchClickService courseSearchClickService;

    @GetMapping("/findCourseSearchClickCountList")
    public String clickCountList(ModelMap modelMap){

        List<CourseSearchClickCount> list = courseSearchClickService.list();

        //定义x軕坐标数据
        List<String> data1=new ArrayList<>();
        //定义y軕坐标数据
        List<Long> data2=new ArrayList<>();

        for (CourseSearchClickCount courseSearchClickCount : list) {

            data1.add(courseSearchClickCount.getDaySearchCourse());     // 从数据库中取数据getDaySearchCourse 对应 day_search_course字段
            data2.add(courseSearchClickCount.getClickCount());
        }


        //将数据转换成json数据格式

        String data01Json= JSON.toJSONString(data1);
        String data02Json=JSON.toJSONString(data2);

        System.out.println(data01Json);
        System.out.println(data02Json);

        // 参数1为html文件下的目录名, 参数2为java下的变量data01Json
        //将数据data01Json转发成data01Json到视图组件显示
        modelMap.put("data01Json",data01Json);
        modelMap.put("data02Json",data02Json);

        // 返回modelMap 到 src/main/resources/templates/coursesearchclickcount.html
        return "coursesearchclickcount";
    }

}
