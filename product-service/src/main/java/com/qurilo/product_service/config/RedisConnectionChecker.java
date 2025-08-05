package com.qurilo.product_service.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RedisConnectionChecker {

    public static final Logger logger = LoggerFactory.getLogger(RedisConnectionChecker.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private boolean isConnected = false;

    public RedisConnectionChecker(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    	@Scheduled(fixedRate = 5000)
    public void checkRedisConnection() {
        try {
            String pong = redisTemplate.getConnectionFactory().getConnection().ping();

            if ("PONG".equals(pong) && !isConnected) {
                logger.info(" Redis connected!");
                isConnected = true;
            } else if (!"PONG".equals(pong) && isConnected) {
                isConnected = false;
                logger.error(" Redis failer");
            } else if (!isConnected) {
                logger.error(" Redis failed!");
            }

        } catch (Exception e) {
            if (isConnected) {
                isConnected = false;
            }
            logger.error("Redis failed! - Error: {}", e.getMessage());
        }
    }

}
