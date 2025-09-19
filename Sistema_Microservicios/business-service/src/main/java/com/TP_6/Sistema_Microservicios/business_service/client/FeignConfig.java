package com.TP_6.Sistema_Microservicios.business_service.client;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;

public class FeignConfig {
    @Bean
    public Logger.Level feignLoggerLevel() { return Logger.Level.BASIC; }

    @Bean
    public ErrorDecoder errorDecoder() { return new SimpleErrorDecoder(); }
}
