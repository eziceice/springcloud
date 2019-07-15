package com.springboot.mall.config;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class FeignConfig {

    /**
     * 重写FeignClientsConfiguration类中的Bean就会覆盖掉默认的Bean，从而达到自定义配置的目的
     * 这里重写了feignRetryer的Bean，Feign默认不重试，这里改为重试5次，重试间隔100ms，最大重试时间1s。
     *
     * @return
     */
    @Bean
    public Retryer feignRetryer() {
        return new Retryer.Default(100, SECONDS.toMillis(1), 5);
    }
}
