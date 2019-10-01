package com.iot.xust.redistest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @Author: HuangXin
 * @Date: Created in 17:49 2019/10/1  2019
 * @Description:
 */
public class list {


    @Autowired
    private RedisTemplate<Object, String> redisTemplate;

    public long lPush(String key, String value) {
        if (Objects.nonNull(key)) {
            Long leftPush = redisTemplate.opsForList().leftPush(key, value);
            return Objects.isNull(leftPush) ? -1L : leftPush;
        }
        return -1L;
    }

    public long lPush(String key, String... value) {
        if (Objects.nonNull(key) && Objects.nonNull(value)) {
            for (int i = 0; i < value.length; i++) {
                long l = lPush(key, value[i]);
                if (-1L == l) {
                    return -1L;
                }
            }
            return value.length;
        }
        return -1L;
    }

    public long lPush(String key, Collection<String> value) {
        if (Objects.nonNull(key)) {
            Long pushAll = redisTemplate.opsForList().leftPushAll(key, value);
            return Objects.isNull(pushAll) ? 0L : pushAll;
        }
        return -1L;
    }

    /***
     * 当key存在时才对key入栈
     * @param key
     * @param value
     * @return
     */
    public long lPushX(String key, String value) {
        if (Objects.nonNull(key)) {
            Long leftPush = redisTemplate.opsForList().leftPushIfPresent(key, value);
            return Objects.isNull(leftPush) ? -1L : leftPush;
        }
        return -1L;
    }

    /**
     * 从右边出list
     *
     * @param key
     * @param value
     * @return
     */
    public long rPush(String key, String value) {
        if (Objects.nonNull(key)) {
            Long rightPush = redisTemplate.opsForList().rightPush(key, value);
            return Objects.isNull(rightPush) ? -1L : rightPush;
        }
        return -1L;
    }


    public long rPush(String key, String... value) {
        if (Objects.nonNull(key) && Objects.nonNull(value)) {
            for (int i = 0; i < value.length; i++) {
                long l = rPush(key, value[i]);
                if (-1L == l) {
                    return -1L;
                }
            }
            return value.length;
        }
        return -1L;
    }

    /**
     * 从左边出list
     *
     * @param key
     * @return
     */
    public String lPop(String key) {
        if (Objects.nonNull(key)) {
            return redisTemplate.opsForList().leftPop(key);
        }
        return null;
    }

    public String rPop(String key) {
        if (Objects.nonNull(key)) {
            return redisTemplate.opsForList().rightPop(key);
        }
        return null;
    }

    /**
     * 范围检索,根据下标，返回[start,stop]的List
     *
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public List<String> lRange(String key, long start, long stop) {

        if (Objects.nonNull(key)) {
            return redisTemplate.opsForList().range(key, start, stop);
        }
        return null;
    }

    /**
     * @param srckey
     * @param destKey
     * @return
     */
    public String rPopLPush(String srckey, String destKey) {
        if (Objects.isNull(srckey) || Objects.isNull(destKey)) {
            return null;
        }
        return redisTemplate.opsForList().rightPopAndLeftPush(srckey, destKey);
    }

    /**
     * list的长度
     *
     * @param key
     * @return
     */
    public long llen(String key) {
        if (Objects.nonNull(key)) {
            Long size = redisTemplate.opsForList().size(key);
            return Objects.isNull(size) ? 0L : size;
        }
        return 0L;
    }

    /**
     * 删除list中的count个value
     *
     * @param key
     * @param count
     * @param value
     * @return
     */
    public long lRem(String key, long count, long value) {
        Long remove = null;
        if (Objects.nonNull(key)) {
            remove = redisTemplate.opsForList().remove(key, count, value);
        }
        return Objects.isNull(remove) ? -1L : remove;
    }

    /**
     * 从list中截取start到stop,其余的元素全部删除
     *
     * @param key
     * @param start
     * @param stop
     */
    public void lTrim(String key, long start, long stop) {
        if (Objects.nonNull(key)) {
            redisTemplate.opsForList().trim(key, start, stop);
        }
    }

    /**
     * 获取list下标为index位置的元素
     *
     * @param key
     * @param index
     * @return
     */
    public String lIndex(String key, long index) {
        if (Objects.isNull(key) || Math.abs(index) > llen(key)) {
            return null;
        }
        return redisTemplate.opsForList().index(key, index);
    }

    /**
     * 在某个元素的前后插入新的value
     *
     * @param key
     * @param command
     * @param piovt
     * @param value
     * @return
     */
    public long lInsert(String key, String command, String piovt, String value) {
        long res = -1L;
        if (Objects.nonNull(key)) {
            if ("before".equals(command.toLowerCase())) {
                Long leftPush = redisTemplate.opsForList().leftPush(key, piovt, value);
                res = Objects.isNull(leftPush) ? -1L : leftPush;
            } else if ("after".equals(command.toLowerCase())) {
                Long rightPush = redisTemplate.opsForList().rightPush(key, piovt, value);
                res = Objects.isNull(rightPush) ? -1L : rightPush;
            }
        }
        return res;
    }

    /**
     * 将列表 key 下标为 index 的元素的值设值为 value 。
     *
     * @param key
     * @param index
     * @param value
     */
    public void lSet(String key, long index, String value) {
        if (Objects.isNull(key) || (index > 0 && index >= llen(key)) || (index < 0 && Math.abs(index) >= llen(key) + 1)) {
            return;
        }
        redisTemplate.opsForList().set(key, index, value);
    }
}
