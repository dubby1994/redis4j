package cn.dubby.redis4j.template;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.op.AllOperation;
import cn.dubby.redis4j.op.ServerOperation;
import cn.dubby.redis4j.op.StringOperation;
import io.netty.handler.codec.redis.RedisMessage;

import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/30 11:32
 */
public class RedisTemplate extends AbstractRedisTemplate implements AllOperation, StringOperation, ServerOperation {

    public RedisTemplate(RedisClient redisClient) {
        super(redisClient);
    }

    @Override
    public Future<String> get(String key) {
        Future<RedisMessage> redisMessageFuture = redisClient.execute("GET " + key);
        return getString(redisMessageFuture);
    }

    @Override
    public Future<Boolean> set(String key, String value) {
        Future<RedisMessage> redisMessageFuture = redisClient.execute("SET " + key + " " + value);
        return getBoolean(redisMessageFuture);
    }

    @Override
    public Future<Long> incr(String key) {
        Future<RedisMessage> redisMessageFuture = redisClient.execute("INCR " + key);
        return getLong(redisMessageFuture);
    }

    @Override
    public Future<Long> del(String key) {
        Future<RedisMessage> redisMessageFuture = redisClient.execute("DEL " + key);
        return getLong(redisMessageFuture);
    }

    @Override
    public Future<String> info() {
        Future<RedisMessage> redisMessageFuture = redisClient.execute("INFO");
        return getString(redisMessageFuture);
    }


}
