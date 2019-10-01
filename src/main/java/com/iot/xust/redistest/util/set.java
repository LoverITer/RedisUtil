package com.iot.xust.redistest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @Author: HuangXin
 * @Date: Created in 17:52 2019/10/1  2019
 * @Description:
 */
public class set {

    @Autowired
    private RedisTemplate<Object, String> redisTemplate;


    /**
     * 添加一个Set集合
     *
     * @param key
     * @param members
     * @return
     */
    public long sAdd(String key, String... members) {
        if (Objects.nonNull(key) && Objects.nonNull(members) && members.length > 0) {
            Long add = redisTemplate.opsForSet().add(key, members);
            return Objects.nonNull(add) ? add : -1L;
        }
        return -1L;
    }


    /**
     * 返回Set集合中的所有元素
     *
     * @param key
     * @return
     */
    public Set<String> sMembers(String key) {
        if (Objects.nonNull(key)) {
            return redisTemplate.opsForSet().members(key);
        }
        return null;
    }


    /**
     * 判断member是否是集合key中的成员
     *
     * @param key
     * @param member
     * @return
     */
    public boolean SIsMember(String key, String member) {
        if (Objects.nonNull(key) && Objects.nonNull(member)) {
            Boolean isMember = redisTemplate.opsForSet().isMember(key, member);
            return Objects.nonNull(isMember);
        }
        return false;
    }

    /**
     * 获得集合Set的大小
     *
     * @param key
     * @return
     */
    public long sCard(String key) {
        if (Objects.nonNull(key)) {
            Long size = redisTemplate.opsForSet().size(key);
            return Objects.nonNull(size) ? size : -1L;
        }
        return -1L;
    }

    /**
     * 删除集合Set中指定的元素members
     *
     * @param key
     * @param members
     */
    public void sRem(String key, Object... members) {
        if (Objects.nonNull(key) && Objects.nonNull(members) && members.length > 0) {
            redisTemplate.opsForSet().remove(key, members);
        }
    }

    /**
     * 随机返回一个Set中的元素
     *
     * @param key
     * @return
     */
    public String sRandMember(String key) {
        if (Objects.nonNull(key)) {
            return redisTemplate.opsForSet().randomMember(key);
        }
        return null;
    }

    /***
     * 随机返回count个Set中的元素
     * @param key
     * @param count
     * @return
     */
    public List<String> sRandMember(String key, long count) {
        if (Objects.nonNull(key) && count > 0) {
            return redisTemplate.opsForSet().randomMembers(key, count);
        }
        return null;
    }

    /**
     * 随机删除Set集合中的count个元素
     *
     * @param key
     * @param count
     */
    public void sPop(String key, long count) {
        if (Objects.nonNull(key) && count > 0) {
            redisTemplate.opsForSet().pop(key, count);
        }
    }
}
