package com.iot.xust.redistest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.*;

/**
 * @Author: HuangXin
 * @Date: Created in 17:51 2019/10/1  2019
 * @Description:
 */
public class hash {

    @Autowired
    private RedisTemplate<Object, String> redisTemplate;

    /**
     * 将哈希表 key中域 field 的值设置为 value 。
     * 如果给定的哈希表并不存在， 那么一个新的哈希表将被创建并执行 HSET 操作。
     *
     * @param key
     * @param filed
     * @param value
     * @return
     */
    public boolean hSet(String key, String filed, String value) {
        if (Objects.nonNull(key) && Objects.nonNull(filed) && Objects.nonNull(value)) {
            redisTemplate.opsForHash().put(key, filed, value);
            return true;
        }
        return false;
    }

    public boolean hMSet(String key, Map<String, Object> map) {
        if (Objects.nonNull(key) && Objects.nonNull(map)) {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        }
        return false;
    }

    /***
     * 当且仅当域field 尚未存在于哈希表key中的情况下， 将它的值设置为 value
     * @param key
     * @param filed
     * @param value
     * @return
     */
    public boolean hSetNX(String key, String filed, String value) {
        if (Objects.nonNull(key) && Objects.nonNull(filed) && Objects.nonNull(value)) {
            Boolean re = redisTemplate.opsForHash().putIfAbsent(key, filed, value);
            return Objects.nonNull(re);
        }
        return false;
    }

    /**
     * 获得指定key中的一个指定filed的value
     *
     * @param key
     * @param filed
     * @return
     */
    public String hGet(String key, String filed) {
        if (Objects.nonNull(key) && Objects.nonNull(filed)) {
            return (String) redisTemplate.opsForHash().get(key, filed);
        }
        return null;
    }

    /**
     * 获得指定key中的所有filed和value
     *
     * @param key
     * @return
     */
    public Map<Object, Object> hGetAll(String key) {
        if (Objects.nonNull(key)) {
            return redisTemplate.opsForHash().entries(key);
        }
        return null;
    }

    /**
     * 获得指定key中的多个指定filed和value
     *
     * @param key
     * @param filed
     * @return
     */
    public List<Object> hMGet(String key, Object... filed) {
        if (Objects.nonNull(key) && Objects.nonNull(filed) && filed.length > 0) {
            List<Object> list = new ArrayList<>(Arrays.asList(filed));
            return redisTemplate.opsForHash().multiGet(key, list);
        }
        return null;
    }


    /**
     * 哈希表key中是否存在filed
     *
     * @param key
     * @param filed
     * @return
     */
    public boolean hExists(String key, String filed) {
        if (Objects.nonNull(key) && Objects.nonNull(filed)) {
            Boolean hasKey = redisTemplate.opsForHash().hasKey(key, filed);
            return Objects.nonNull(hasKey);
        }
        return false;
    }

    /**
     * 哈希表key的大小(filed-value对的数量)
     *
     * @param key
     * @return
     */
    public long hLen(String key) {
        if (Objects.nonNull(key)) {
            Long size = redisTemplate.opsForHash().size(key);
            return Objects.nonNull(size) ? size : -1L;
        }
        return -1L;
    }

    /**
     * 哈希表key中filed字段的value的长度
     *
     * @param key
     * @param filed
     * @return
     */
    public long hStrLen(String key, String filed) {
        if (Objects.nonNull(key) && Objects.nonNull(filed)) {
            Long lengthOfValue = redisTemplate.opsForHash().lengthOfValue(key, filed);
            return Objects.nonNull(lengthOfValue) ? lengthOfValue : -1L;
        }
        return -1L;
    }

    /**
     * 为哈希表key中的域field的值加上增量increment
     *
     * @param key
     * @param filed
     * @param increment
     * @return
     */
    public long hIncrBy(String key, String filed, long increment) {
        if (Objects.nonNull(key)) {
            Long value = redisTemplate.opsForHash().increment(key, filed, increment);
            return Objects.nonNull(value) ? value : -1L;
        }
        return -1L;
    }

    /**
     * 为哈希表key中的域field的值加上增量increment,只不过这个增量可以是浮点数
     *
     * @param key
     * @param filed
     * @param increment
     * @return
     */
    public double hIncrByFloat(String key, String filed, double increment) {
        if (Objects.nonNull(key) && Objects.nonNull(filed)) {
            Double value = redisTemplate.opsForHash().increment(key, filed, increment);
            return Objects.nonNull(value) ? value : -1L;
        }
        return -1;
    }

    /**
     * 返回hash表中的所有域
     *
     * @param key
     * @return
     */
    public Set<Object> hKeys(String key) {
        if (Objects.nonNull(key)) {
            return redisTemplate.opsForHash().keys(key);
        }
        return null;
    }

    /**
     * 返回hash表中所有域的值
     *
     * @param key
     * @return
     */
    public List<Object> hValues(String key) {
        if (Objects.nonNull(key)) {
            return redisTemplate.opsForHash().values(key);
        }
        return null;
    }



}
