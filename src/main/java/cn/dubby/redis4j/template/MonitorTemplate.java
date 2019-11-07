package cn.dubby.redis4j.template;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.exception.RedisException;
import cn.dubby.redis4j.op.MonitorOperation;
import cn.dubby.redis4j.stream.RedisStream;
import cn.dubby.redis4j.util.RedisMessageUtil;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedTransferQueue;

/**
 * @author dubby
 * @date 2019/5/27 21:34
 */
public class MonitorTemplate extends AbstractRedisTemplate implements MonitorOperation {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MonitorTemplate.class);

    private LinkedTransferQueue<String> redisMessageQueue = new LinkedTransferQueue<String>();

    private RedisStream<String> redisStream = new RedisStream<>(redisMessageQueue);

    private LinkedTransferQueue<CompletableFuture<RedisMessage>> linkedTransferQueue;

    public MonitorTemplate(RedisClient redisClient) {
        super(redisClient);
    }

    @Override
    public RedisStream<String> monitor() throws ExecutionException, InterruptedException, RedisException {
        linkedTransferQueue = redisClient.getRedisClientHandler().futureQueue;

        CompletableFuture<RedisMessage> future = redisClient.execute("MONITOR");
        RedisMessage redisMessage = future.get();
        redisMessageQueue.add(RedisMessageUtil.parseString(redisMessage));

        consumeNext();
        return redisStream;
    }

    private void consumeNext() {
        CompletableFuture<RedisMessage> completableFuture = new CompletableFuture<>();
        linkedTransferQueue.add(completableFuture);
        completableFuture.thenAccept(redisMessage -> {
            consumeNext();
            try {
                redisMessageQueue.add(RedisMessageUtil.parseString(redisMessage));
            } catch (RedisException e) {
                logger.error("MonitorTemplate", e);
                redisMessageQueue.add(e.getMessage());
            }
        });
    }

}
