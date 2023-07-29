package com.gec.realtime.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ChartController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ChartController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/chart")
    @ResponseBody
    public Object getChartData() {
        List<String> xAxisData = new ArrayList<>();
        List<Long> yAxisData = new ArrayList<>();

        // 从数据库中查询数据
        String sql = "SELECT day_course, click_count FROM course_clickcount";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : rows) {
            String dayCourse = (String) row.get("day_course");
            Long clickCount = (Long) row.get("click_count");
            xAxisData.add(dayCourse);
            yAxisData.add(clickCount);
        }

        // 返回一个包含x轴和y轴数据的对象
        return new Object[] { xAxisData, yAxisData };
    }
}
