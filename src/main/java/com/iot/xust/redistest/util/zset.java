package com.iot.xust.redistest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import javax.validation.constraints.NotBlank;
import java.util.*;

/**
 * @Author: HuangXin
 * @Date: Created in 17:53 2019/10/1  2019
 * @Description:
 */
public class zset {

    @Autowired
    private RedisTemplate<Object, String> redisTemplate;

    /**
     * 把srcSet集合中的member元素移动到destinationSet集合中
     *
     * @param member
     * @param srcSet
     * @param destinationSet
     * @return
     */
    public boolean sMove(String srcSet, String destinationSet, String member) {
        if (Objects.nonNull(srcSet) && Objects.nonNull(destinationSet)) {
            Boolean move = redisTemplate.opsForSet().move(srcSet, destinationSet, member);
            return Objects.nonNull(move);
        }
        return false;
    }

    public Set<String> sDiff(String k1, String k2) {
        if (Objects.nonNull(k1) && Objects.nonNull(k2)) {
            return redisTemplate.opsForSet().difference(k1, k2);
        }
        return null;
    }

    public Set<String> sDiff(String... keys) {
        if (Objects.nonNull(keys) && keys.length > 0) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(keys));
            list.remove(0);
            return redisTemplate.opsForSet().difference(keys[0], list);
        }
        return null;
    }

    public Set<String> sInter(String k1, String k2) {
        if (Objects.nonNull(k1) && Objects.nonNull(k2)) {
            return redisTemplate.opsForSet().intersect(k1, k2);
        }
        return null;
    }

    public Set<String> sInter(String... keys) {
        if (Objects.nonNull(keys) && keys.length > 0) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(keys));
            list.remove(0);
            redisTemplate.opsForSet().intersect(keys[0], list);
        }
        return null;
    }

    public Set<String> sUnion(String k1, String k2) {
        if (Objects.nonNull(k1) && Objects.nonNull(k2)) {
            return redisTemplate.opsForSet().union(k1, k2);
        }
        return null;
    }

    public Set<String> sUnion(String... keys) {
        if (Objects.nonNull(keys) && keys.length > 0) {
            ArrayList<String> list = new ArrayList<>(Arrays.asList(keys));
            list.remove(0);
            redisTemplate.opsForSet().union(keys[0], list);
        }
        return null;
    }

    /**
     * 将一个或多个 member 元素及其 score值加入到有序集key 当中。
     * 如果某个 member 已经是有序集的成员，那么更新这个member的 score值，
     * 并通过重新插入这个 member元素，来保证该member在正确的位置上。
     *
     * @param key
     * @param map
     * @return
     */
    public long zAdd(String key, Map<String, Double> map) {
        if (Objects.nonNull(key) && Objects.nonNull(map) && map.size() > 0) {
            Set<ZSetOperations.TypedTuple<String>> set = new HashSet<>();
            map.forEach((k, v) -> {
                //TypeTuple的默认实现类：就是用来捆绑score和member的
                DefaultTypedTuple<String> defaultTypedTuple = new DefaultTypedTuple<>(k, v);
                set.add(defaultTypedTuple);
            });
            Long add = redisTemplate.opsForZSet().add(key, set);
            return Objects.nonNull(add) ? add : 0L;
        }
        return 0L;
    }


    public Set<?> zRange(String key, long start, long end, boolean withscore) {
        if (Objects.nonNull(key) && !"".equals(key)) {
            if (withscore) {
                return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
            }
            return redisTemplate.opsForZSet().range(key, start, end);
        }
        return null;
    }

    public Set<?> zRevRange(String key, long start, long end, boolean withscore) {
        if (Objects.nonNull(key) && !"".equals(key)) {
            if (withscore) {
                return redisTemplate.opsForZSet().reverseRangeByScore(key, start, end);
            }
            return redisTemplate.opsForZSet().reverseRange(key, start, end);
        }
        return null;
    }

    public Set<?> zRangeByScore(String key, double min, double max, boolean withscore) {
        if (Objects.nonNull(key) && !"".equals(key)) {
            if (withscore) {
                return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max);
            }
            return redisTemplate.opsForZSet().rangeByScore(key, min, max);
        }
        return null;
    }

    public Set<?> zRangeByScore(@NotBlank String key, double min, double max, boolean withsocre, boolean withLimit, long offset, long count) {
        if (Objects.nonNull(key) && !"".equals(key)) {
            if (!withLimit) {
                return zRangeByScore(key, min, max, withsocre);
            }
            return redisTemplate.opsForZSet().rangeByScoreWithScores(key, min, max, offset, count);
        }
        return null;
    }


    public void zRem(@NotBlank String key, String... members) {
        if (Objects.nonNull(key) && members.length > 0) {
            redisTemplate.opsForZSet().remove(key, members);
        }
    }

    public long zCard(@NotBlank String key) {
        if (Objects.nonNull(key)) {
            Long size = redisTemplate.opsForZSet().size(key);
            return Objects.nonNull(size) ? size : 0L;
        }
        return 0L;
    }

    public long zCount(@NotBlank String key, long start, long end) {
        if (Objects.nonNull(key)) {
            Long count = redisTemplate.opsForZSet().count(key, start, end);
            return Objects.nonNull(count) ? count : 0L;
        }
        return 0L;
    }

    public long zRank(@NotBlank String key, String member) {
        if (Objects.nonNull(key)) {
            Long rank = redisTemplate.opsForZSet().rank(key, member);
            return Objects.nonNull(rank) ? rank : 0L;
        }
        return 0L;
    }

    public long zRevRank(@NotBlank String key, String member) {
        if (Objects.nonNull(key)) {
            Long reverseRank = redisTemplate.opsForZSet().reverseRank(key, member);
            return Objects.nonNull(reverseRank) ? reverseRank : 0L;
        }
        return 0L;
    }

}
