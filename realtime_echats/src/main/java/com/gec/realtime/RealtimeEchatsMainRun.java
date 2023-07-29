package com.gec.realtime;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.gec.realtime.dao")
public class RealtimeEchatsMainRun {

    public static void main(String[] args) {

        SpringApplication.run(RealtimeEchatsMainRun.class,args);

    }
}
