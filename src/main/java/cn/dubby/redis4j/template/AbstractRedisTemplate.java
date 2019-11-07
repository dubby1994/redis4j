package cn.dubby.redis4j.template;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.exception.RedisException;
import cn.dubby.redis4j.util.RedisMessageUtil;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/5/27 21:11
 */
public class AbstractRedisTemplate {

    protected RedisClient redisClient;

    protected AbstractRedisTemplate(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void close() {
        redisClient.close();
    }

    protected Future<String> getString(CompletableFuture<RedisMessage> redisMessageFuture) {
        CompletableFuture<String> future = new CompletableFuture<>();
        redisMessageFuture.thenAccept(redisMessage -> {
            try {
                future.complete(RedisMessageUtil.parseString(redisMessage));
            } catch (RedisException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    protected Future<Long> getLong(CompletableFuture<RedisMessage> redisMessageFuture) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        redisMessageFuture.thenAccept(redisMessage -> {
            if (redisMessage instanceof IntegerRedisMessage) {
                future.complete(((IntegerRedisMessage) redisMessage).value());
            } else {
                future.complete(-1L);
            }
        });
        return future;
    }

    protected Future<Boolean> getBoolean(CompletableFuture<RedisMessage> redisMessageFuture) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        redisMessageFuture.thenAccept(redisMessage -> {
            if (redisMessage instanceof SimpleStringRedisMessage) {
                future.complete(Boolean.TRUE);
            } else {
                future.complete(Boolean.FALSE);
            }
        });
        return future;
    }

}
