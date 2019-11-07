package cn.dubby.redis4j.template;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.op.CommonOperation;
import cn.dubby.redis4j.op.HyperLogLogOperation;
import cn.dubby.redis4j.op.ServerOperation;
import cn.dubby.redis4j.op.StringOperation;
import io.netty.handler.codec.redis.RedisMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/30 11:32
 */
public class RedisTemplate extends AbstractRedisTemplate implements CommonOperation, StringOperation, ServerOperation, HyperLogLogOperation {

    public RedisTemplate(RedisClient redisClient) {
        super(redisClient);
    }

    @Override
    public Future<String> get(String key) {
        CompletableFuture<RedisMessage> redisMessageFuture = redisClient.execute("GET " + key);
        return getString(redisMessageFuture);
    }

    @Override
    public Future<Boolean> set(String key, String value) {
        CompletableFuture<RedisMessage> redisMessageFuture = redisClient.execute("SET " + key + " " + value);
        return getBoolean(redisMessageFuture);
    }

    @Override
    public Future<Long> incr(String key) {
        CompletableFuture<RedisMessage> redisMessageFuture = redisClient.execute("INCR " + key);
        return getLong(redisMessageFuture);
    }

    @Override
    public Future<Long> del(String key) {
        CompletableFuture<RedisMessage> redisMessageFuture = redisClient.execute("DEL " + key);
        return getLong(redisMessageFuture);
    }

    @Override
    public Future<String> info() {
        CompletableFuture<RedisMessage> redisMessageFuture = redisClient.execute("INFO");
        return getString(redisMessageFuture);
    }


    @Override
    public Future<Long> pfAdd(String key, String... elements) {
        StringBuilder cmd = new StringBuilder("PFADD ");
        cmd.append(key);
        for (String s : elements) {
            cmd.append(" ");
            cmd.append(s);
        }
        CompletableFuture<RedisMessage> redisMessageFuture = redisClient.execute(cmd.toString());
        return getLong(redisMessageFuture);
    }

    @Override
    public Future<Long> pfCount(String... key) {
        StringBuilder cmd = new StringBuilder("PFCOUNT");
        for (String s : key) {
            cmd.append(" ");
            cmd.append(s);
        }
        CompletableFuture<RedisMessage> redisMessageFuture = redisClient.execute(cmd.toString());
        return getLong(redisMessageFuture);
    }

    @Override
    public Future<String> pfMerge(String destKey, String... sourceKey) {
        StringBuilder cmd = new StringBuilder("PFMERGE ");
        cmd.append(destKey);
        cmd.append(" ");
        for (String s : sourceKey) {
            cmd.append(" ");
            cmd.append(s);
        }
        CompletableFuture<RedisMessage> redisMessageFuture = redisClient.execute(cmd.toString());
        return getString(redisMessageFuture);
    }
}
