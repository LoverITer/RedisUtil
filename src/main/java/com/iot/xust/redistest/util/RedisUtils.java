package com.iot.xust.redistest.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;
import java.util.*;
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

    /**
     * 操作string
     */
    public class string {


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
         * @param key
         * @param value
         * @param isKeyAbsent true表示nx   false表示xx
         * @return
         */
        public boolean set(@NotBlank String key, String value, boolean isKeyAbsent) {
            Boolean res = null;
            if (Objects.nonNull(key) && Objects.nonNull(value)) {
                if (isKeyAbsent) {
                    res = redisTemplate.opsForValue().setIfAbsent(key, value);
                } else {
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

    /**
     * 操作list
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

    /**
     * 操作hash
     */
    public class hash {

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

    /**
     * 操作set
     */
    public class set {

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

    /**
     * 操作zset
     */
    public class zset {

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


}
