package com.gec.realtime.controller;

import com.alibaba.fastjson.JSON;
import com.gec.realtime.entity.CourseClickCount;
import com.gec.realtime.service.CourseClickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CourseClickController {

    // 将名为 "CourseClickServiceImpl" 的
    // CourseClickService Bean 注入到 courseClickService 字段中，以便在其他地方使用该服务。
    @Autowired
    @Qualifier("CourseClickServiceImpl")
    private CourseClickService courseClickService;

    @GetMapping("/findCourseClickCountList")
    public String clickCountList(ModelMap modelMap){

        List<CourseClickCount> list = courseClickService.list();

        //定义x軕坐标数据
        List<String> data1=new ArrayList<>();
        //定义y軕坐标数据
        List<Long> data2=new ArrayList<>();

        for (CourseClickCount courseClickCount : list) {

            data1.add(courseClickCount.getDayCourse());
            data2.add(courseClickCount.getClickCount());
        }


        //将数据转换成json数据格式

        String data1Json= JSON.toJSONString(data1);
        String data2Json=JSON.toJSONString(data2);

        System.out.println(data1Json);
        System.out.println(data2Json);

        //将数据转发成视图组件显示
        modelMap.put("data1Json",data1Json);
        modelMap.put("data2Json",data2Json);

        return "courseclickcount";
    }

}
