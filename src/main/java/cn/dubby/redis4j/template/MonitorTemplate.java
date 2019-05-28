package cn.dubby.redis4j.template;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.handler.RedisClientHandler;
import cn.dubby.redis4j.op.MonitorOperation;
import cn.dubby.redis4j.stream.RedisStream;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadFactory;

/**
 * @author dubby
 * @date 2019/5/27 21:34
 */
public class MonitorTemplate extends AbstractRedisTemplate implements MonitorOperation {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(MonitorTemplate.class);

    private LinkedTransferQueue<String> redisMessageQueue = new LinkedTransferQueue<String>();

    private RedisStream<String> redisStream = new RedisStream<>(redisMessageQueue);

    private ExecutorService monitorThread = Executors.newFixedThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("Redis4J-MonitorThread");
            thread.setDaemon(true);
            return thread;
        }
    });

    public MonitorTemplate(RedisClient redisClient) {
        super(redisClient);
        monitorThread.submit(() -> {
            Future<RedisMessage> redisMessageFuture = redisClient.execute("MONITOR");
            while (true) {
                try {
                    Future<String> stringFuture = getString(redisMessageFuture);
                    redisMessageQueue.add(stringFuture.get());
                    redisMessageFuture = redisClient.getRedisClientHandler().getFuture();
                } catch (Exception e) {
                    logger.error("MonitorTemplate", e);
                }
            }
        });
    }

    @Override
    public RedisStream<String> monitor() throws ExecutionException, InterruptedException {
        return redisStream;
    }
}
