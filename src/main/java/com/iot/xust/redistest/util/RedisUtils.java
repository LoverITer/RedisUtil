package com.iot.xust.redistest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: HuangXin
 * @Date: Created in 11:06 2019/9/30  2019
 * @Description: 操作Redis的工具类
 */
@Component
public class RedisUtils {

    @Autowired
    private RedisTemplate<Object, String> redisTemplate;

    public final string string = new string();

    public final list list = new list();

    public final hash hash = new hash();

    public final set set = new set();

    public final zset zset = new zset();


    /**
     * 设置某个key的过期时间
     *
     * @param key
     * @param timeout
     * @return
     */
    public boolean expire(String key, long timeout) {
        if (timeout > 0) {
            try {
                redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }


    /***
     * 查看某个key的过期时间
     * @param key
     * @return 过期时间 （秒/s）
     */
    public long ttl(String key) {
        if (null != key) {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            if (Objects.nonNull(expire)) {
                return expire;
            }
            return -2L;
        }
        return -2L;   //key不存在，返回-2
    }

    /**
     * 判断是否存在某个key
     *
     * @param key
     * @return
     */
    public boolean exists(String key) {
        if (null != key) {
            Boolean exists = redisTemplate.hasKey(key);
            return Objects.nonNull(exists);
        }
        return false;
    }

    /**
     * 获得某个key的类型
     *
     * @param key
     * @return
     */
    public DataType type(String key) {
        if (null != key) {
            return redisTemplate.type(key);
        }
        return null;
    }

    /**
     * 获得当前数据库中的所有key
     *
     * @param patten
     * @return
     */
    public Set<Object> allKeys(Object patten) {
        return redisTemplate.keys(patten);
    }

    /**
     * 把当前库中的一个key移动到dbID这个数据库中
     *
     * @param key
     * @param dbID
     * @return
     */
    public boolean move(String key, int dbID) {
        if (null == key || dbID < 0) {
            return false;
        }
        Boolean move = redisTemplate.move(key, dbID);
        return Objects.nonNull(move);
    }


    public void del(String key) {
        if (Objects.isNull(key) || "".equals(key)) {
            return;
        }
        redisTemplate.delete(key);
    }

    /**
     * 删除一个或多个key
     *
     * @param keys
     */
    public void del(String... keys) {
        if (Objects.nonNull(keys) && keys.length > 0) {
            for (int i = 0; i < keys.length; i++) {
                del(keys[i]);
            }
        }
    }


}
