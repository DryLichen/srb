package com.dry.srb.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication()
@ComponentScan({"com.dry.srb", "com.dry.common"})
public class ServiceCoreApplication {
    public static void main(String[] args) {
        try{
            SpringApplication.run(ServiceCoreApplication.class, args);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
