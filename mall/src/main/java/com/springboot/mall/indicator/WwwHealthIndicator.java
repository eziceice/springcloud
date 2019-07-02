package com.springboot.mall.indicator;

import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class WwwHealthIndicator extends AbstractHealthIndicator {

    private final static String GOOGLE_HOST = "www.google.com.au";

    private final static int TIME_OUT = 3000;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        boolean status = ping();
        if (status) {
            builder.withDetail("message", "could access Internet").up();
        } else {
            builder.withDetail("message", "can't access Internet").outOfService();
        }
    }

    private boolean ping() throws Exception {
        try {
            return InetAddress.getByName(GOOGLE_HOST).isReachable(TIME_OUT);
        } catch (Exception e) {
            return false;
        }
    }
}
