package cn.dubby.redis4j.template;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.util.RedisMessageUtil;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * @author dubby
 * @date 2019/5/27 21:11
 */
public class AbstractRedisTemplate {

    protected RedisClient redisClient;

    protected AbstractRedisTemplate(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void close() throws InterruptedException {
        redisClient.close();
    }

    private ExecutorService callbackPool = Executors.newFixedThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("Redis4JCallbackThread");
            thread.setDaemon(true);
            return thread;
        }
    });

    protected Future<String> getString(Future<RedisMessage> redisMessageFuture) {
        CompletableFuture<String> future = new CompletableFuture<>();
        callbackPool.submit(() -> {
            try {
                RedisMessage redisMessage = redisMessageFuture.get();
                String value = RedisMessageUtil.parseString(redisMessage);
                future.complete(value);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    protected Future<Long> getLong(Future<RedisMessage> redisMessageFuture) {
        CompletableFuture<Long> future = new CompletableFuture<>();
        callbackPool.submit(() -> {
            try {
                RedisMessage redisMessage = redisMessageFuture.get();
                if (redisMessage instanceof IntegerRedisMessage) {
                    future.complete(((IntegerRedisMessage) redisMessage).value());
                } else {
                    future.complete(-1L);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return future;
    }

    protected Future<Boolean> getBoolean(Future<RedisMessage> redisMessageFuture) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        callbackPool.submit(() -> {
            try {
                RedisMessage redisMessage = redisMessageFuture.get();
                if (redisMessage instanceof SimpleStringRedisMessage) {
                    future.complete(Boolean.TRUE);
                } else {
                    future.complete(Boolean.FALSE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return future;
    }

}
