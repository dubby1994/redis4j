package cn.dubby.redis4j;

import cn.dubby.redis4j.op.ServerOperation;
import cn.dubby.redis4j.op.StringOperation;
import cn.dubby.redis4j.util.RedisMessageUtil;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/30 11:32
 */
public class RedisTemplate implements StringOperation, ServerOperation {

    private RedisClient redisClient;

    private ExecutorService callbackPool = Executors.newFixedThreadPool(1);

    public RedisTemplate(RedisClient redisClient) {
        this.redisClient = redisClient;
    }

    public void close() throws InterruptedException {
        redisClient.close();
        callbackPool.shutdown();
    }

    @Override
    public Future<String> get(String key) {
        Future<RedisMessage> redisMessageFuture = redisClient.execute("GET " + key);
        return getString(redisMessageFuture);
    }

    @Override
    public Future<Boolean> set(String key, String value) {
        Future<RedisMessage> redisMessageFuture = redisClient.execute("SET " + key + " " + value);
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

    @Override
    public Future<String> info() {
        Future<RedisMessage> redisMessageFuture = redisClient.execute("INFO");
        return getString(redisMessageFuture);
    }

    private Future<String> getString(Future<RedisMessage> redisMessageFuture) {
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


}
