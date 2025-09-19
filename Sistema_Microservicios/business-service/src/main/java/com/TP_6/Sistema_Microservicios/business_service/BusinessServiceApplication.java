package com.TP_6.Sistema_Microservicios.business_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Primary;


@SpringBootApplication
@EnableFeignClients(basePackages = "com.TP_6.Sistema_Microservicios.business_service.client")
public class BusinessServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BusinessServiceApplication.class, args);
    }
}
