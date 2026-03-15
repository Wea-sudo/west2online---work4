package com.weasudo.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate redisTemplate;

    // ==================== KV 操作 ====================
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public boolean set(String key, String value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            return true;
        } catch (Exception e) {
            log.error("Redis set 操作失败, key={}", key, e);
            return false;
        }
    }

    public boolean getAndSet(String key, String value) {
        try {
            redisTemplate.opsForValue().getAndSet(key, value);
            return true;
        } catch (Exception e) {
            log.error("Redis getAndSet 操作失败, key={}", key, e);
            return false;
        }
    }

    public boolean delete(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.delete(key));
        } catch (Exception e) {
            log.error("Redis delete 操作失败, key={}", key, e);
            return false;
        }
    }

    // ==================== 计数操作 ====================
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    public Long getCount(String key) {
        String val = redisTemplate.opsForValue().get(key);
        return val == null ? 0L : Long.parseLong(val);
    }

    // ==================== Set 操作 ====================
    public boolean addToSet(String key, String member, long timeout, TimeUnit unit) {
        try {
            Long added = redisTemplate.opsForSet().add(key, member);
            if (timeout > 0) {
                redisTemplate.expire(key, timeout, unit);
            }
            return added != null && added > 0;
        } catch (Exception e) {
            log.error("Redis addToSet 操作失败, key={}, member={}", key, member, e);
            return false;
        }
    }

    public boolean removeFromSet(String key, String member) {
        try {
            Long removed = redisTemplate.opsForSet().remove(key, member);
            return removed != null && removed > 0;
        } catch (Exception e) {
            log.error("Redis removeFromSet 操作失败, key={}, member={}", key, member, e);
            return false;
        }
    }

    public boolean isMemberOfSet(String key, String member) {
        try {
            Boolean exists = redisTemplate.opsForSet().isMember(key, member);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Redis isMemberOfSet 操作失败, key={}, member={}", key, member, e);
            return false;
        }
    }

    public Set<String> getSetMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis getSetMembers 操作失败, key={}", key, e);
            return null;
        }
    }

    // ==================== ZSet 操作 ====================
    public Double zScore(String key, String member) {
        try {
            return redisTemplate.opsForZSet().score(key, member);
        } catch (Exception e) {
            log.error("Redis zScore error, key={}, member={}", key, member, e);
            return null;
        }
    }

    public boolean zAdd(String key, String member, double score) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, member, score));
        } catch (Exception e) {
            log.error("Redis zAdd error, key={}, member={}", key, member, e);
            return false;
        }
    }

    public boolean zRemove(String key, String member) {
        try {
            Long removed = redisTemplate.opsForZSet().remove(key, member);
            return removed != null && removed > 0;
        } catch (Exception e) {
            log.error("Redis zRemove error, key={}, member={}", key, member, e);
            return false;
        }
    }

    public Set<ZSetOperations.TypedTuple<String>> zRangeWithScore(String key) {
        try {
            return redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
        } catch (Exception e) {
            log.error("Redis zRangeWithScore error, key={}", key, e);
            return null;
        }
    }

    // ==================== Hash 操作 ====================
    public boolean hSet(String key, String field, String value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            log.error("Redis hSet error, key={}, field={}", key, field, e);
            return false;
        }
    }

    public Map<String, String> hGetAll(String key) {
        try {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            return entries.entrySet().stream()
                    .collect(Collectors.toMap(
                            e -> e.getKey().toString(),
                            e -> e.getValue().toString()
                    ));
        } catch (Exception e) {
            log.error("Redis hGetAll error, key={}", key, e);
            return null;
        }
    }

    public Long hIncr(String key, String field, long delta) {
        try {
            return redisTemplate.opsForHash().increment(key, field, delta);
        } catch (Exception e) {
            log.error("Redis hIncr error, key={}, field={}", key, field, e);
            return null;
        }
    }

    public String hGet(String key, String field) {
        try {
            Object val = redisTemplate.opsForHash().get(key, field);
            return val == null ? null : val.toString();
        } catch (Exception e) {
            log.error("Redis hGet error, key={}, field={}", key, field, e);
            return null;
        }
    }

    // ==================== 通用操作 ====================
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            log.error("Redis expire 操作失败, key={}", key, e);
            return false;
        }
    }

    public Set<String> members(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("Redis members error, key={}", key, e);
            return null;
        }
    }
}
