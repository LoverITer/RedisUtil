package com.iot.xust.redistest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import javax.validation.constraints.NotBlank;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Author: HuangXin
 * @Date: Created in 17:34 2019/10/1  2019
 * @Description:
 */
public class string {

    @Autowired
    private RedisTemplate<Object, String> redisTemplate;

    /**
     * 设置字符串
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(String key, String value) {
        if (Objects.nonNull(key) && Objects.nonNull(value)) {
            redisTemplate.opsForValue().set(key, value);
            return true;
        }
        return false;
    }

    public boolean set(String key, String value, String command, long timout) {
        if (Objects.nonNull(key) && Objects.nonNull(value) && Objects.nonNull(command) && timout > 0) {
            if ("ex".equals(command.toLowerCase())) {
                redisTemplate.opsForValue().set(key, value, timout, TimeUnit.SECONDS);
            } else if ("px".equals(command.toLowerCase())) {
                redisTemplate.opsForValue().set(key, value, timout, TimeUnit.MILLISECONDS);
            }
            return true;
        }
        return false;
    }

    /**
     *
     * @param key
     * @param value
     * @param isKeyAbsent   true表示nx   false表示xx
     * @return
     */
    public boolean set(@NotBlank String key, String value, boolean isKeyAbsent) {
        Boolean res = null;
        if (Objects.nonNull(key) && Objects.nonNull(value)) {
            if (isKeyAbsent) {
                res = redisTemplate.opsForValue().setIfAbsent(key, value);
            } else  {
                res = redisTemplate.opsForValue().setIfPresent(key, value);
            }
        }
        return Objects.nonNull(res);
    }


    public boolean set(String key, String value, String command, long timout, boolean isKeyAbsent) {
        if (Objects.nonNull(command)) {
            boolean res = this.set(key, value, isKeyAbsent);
            if (res) {
                return this.set(key, key, command, timout);
            }
        } else if (Objects.nonNull(isKeyAbsent)) {
            return this.set(key, value, isKeyAbsent);
        }
        return false;
    }

    /**
     * 为多个键分别设置它们的值
     *
     * @param map
     */
    public void mset(Map<String, String> map) {
        if (Objects.nonNull(map) && map.size() > 0) {
            redisTemplate.opsForValue().multiSet(map);
        }
    }


    /**
     * 为多个键分别设置它们的值，仅当键不存在时
     *
     * @param map
     */
    public void msetnx(Map<String, String> map) {
        if (Objects.nonNull(map)) {
            redisTemplate.opsForValue().multiSetIfAbsent(map);
        }
    }


    /**
     * 获得指定key的value
     *
     * @param key
     * @return
     */
    public String get(String key) {
        if (Objects.nonNull(key)) {
            return redisTemplate.opsForValue().get(key);
        }
        return null;
    }


    /**
     * 获取所有给定键的值
     *
     * @param keys
     * @return
     */
    public List<String> mget(Collection<String> keys) {
        if (Objects.nonNull(keys) && keys.size() > 0) {
            return redisTemplate.opsForValue().multiGet(Collections.singleton(keys));
        }
        return null;
    }


    /**
     * 截取指定key的value的部分
     *
     * @param key
     * @param start 开始的下标
     * @param stop  结束的下标
     * @return
     */
    public String getRange(String key, long start, long stop) {
        if (Objects.nonNull(key)) {
            return redisTemplate.opsForValue().get(key, start, stop);
        }
        return null;
    }

    /**
     * key-->value对value的长度
     *
     * @param key
     * @return
     */
    public long strlen(String key) {
        if (Objects.nonNull(key)) {
            Long size = redisTemplate.opsForValue().size(key);
            return Objects.isNull(size) ? 0L : size;
        }
        return 0L;
    }

    /**
     * 字符串追加
     *
     * @param key
     * @param s
     * @return 执行key追加后的
     */
    public long append(String key, String s) {
        if (Objects.nonNull(key)) {
            Integer length = redisTemplate.opsForValue().append(key, s);
            return Objects.isNull(length) ? strlen(key) : length;
        }
        return 0L;
    }

    public long incr(String key) {
        return incrBy(key, 1);
    }


    public long incrBy(String key, long increment) {
        if (Objects.nonNull(key)) {
            try {
                Long value = redisTemplate.opsForValue().increment(key, increment);
                return Objects.isNull(value) ? 0L : value;
            } catch (NumberFormatException ex) {
                throw new NumberFormatException("非数字不可加");
            }
        }
        return 0L;
    }


    public long decr(String key) {
        return this.decrBy(key, 1);
    }

    public long decrBy(String key, long decrement) {
        if (Objects.nonNull(key)) {
            try {
                Long value = redisTemplate.opsForValue().decrement(key, decrement);
                return Objects.isNull(value) ? 0L : value;
            } catch (NumberFormatException ex) {
                throw new NumberFormatException("非数字不可减");
            }
        }
        return 0L;
    }

    /**
     * 在指定偏移处开始的键处覆盖字符串的一部分
     *
     * @param key
     * @param offset
     * @param value
     */
    public void setRange(String key, long offset, String value) {
        if (Objects.nonNull(key)) {
            redisTemplate.opsForValue().set(key, value, offset);
        }
    }

}
