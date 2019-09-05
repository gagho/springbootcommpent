package com.example.springbootcommpent.resubmit.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * @author zhouliangze
 * @date 2019/9/4 15:44
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
public class RedisLockHelper {

    private static final String DELIMITER = "|";

    private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors.newScheduledThreadPool(10);

    private final StringRedisTemplate stringRedisTemplate;


    public RedisLockHelper(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 获取锁（存在死锁风险）
     * @param lockKey
     * @param value
     * @param time
     * @param timeUnit
     * @return
     */
    public boolean tryLock(final String lockKey, final String value, final long time, final TimeUnit timeUnit){
        return stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(lockKey.getBytes(), value.getBytes(), Expiration.from(time, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT));
    }

    /**
     * 获取锁
     * @param lockKey
     * @param uuid
     * @param time
     * @param unit
     * @return
     */
    public boolean lock(String lockKey, final String uuid, long time, final TimeUnit unit){
        final long milliseconds = Expiration.from(time, unit).getExpirationTimeInMilliseconds();
        System.out.println(lockKey);
        boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
        if(flag){
            stringRedisTemplate.expire(lockKey, time, TimeUnit.SECONDS);
        }else {
            String oldVal = stringRedisTemplate.opsForValue().getAndSet(lockKey, (System.currentTimeMillis() + milliseconds) + DELIMITER + uuid);
            final String [] oldValue = oldVal.split(Pattern.quote(DELIMITER));
            if(Long.parseLong(oldValue[0]) + 1 <= System.currentTimeMillis()){
                return true;
            }
        }
        System.out.println(stringRedisTemplate.opsForValue().get(lockKey));
        return flag;
    }

    public void unlock(String lockKey, String value){
        unlock(lockKey, value, 0, TimeUnit.MILLISECONDS);
    }

    /**
     * 延迟unlock
     * @param lockKey
     * @param uuid
     * @param delayTime
     * @param unit
     */
    public void unlock(final String lockKey,final String uuid, long delayTime, TimeUnit unit){
        if(StringUtils.isEmpty(lockKey)){
            return;
        }
        if(delayTime <= 0){
            doUnlock(lockKey, uuid);
        }else {
            EXECUTOR_SERVICE.schedule(() -> doUnlock(lockKey, uuid), delayTime, unit);
        }
    }

    /**
     *
     * @param lockKey
     * @param uuid
     */
    public void doUnlock(final String lockKey, final String uuid){
        String val = stringRedisTemplate.opsForValue().get(lockKey);
        final String [] values = val.split(Pattern.quote(DELIMITER));
        if(values.length <= 0){
            return;
        }

        if(uuid.equals(values[1])){
            stringRedisTemplate.delete(lockKey);
        }
    }


}
