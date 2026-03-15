package com.weasudo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class RedisTest {
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void testRedis() {

        redisTemplate.opsForValue().set("name","ChatHP");

        String value = redisTemplate.opsForValue().get("name");

        System.out.println(value);
    }
}
