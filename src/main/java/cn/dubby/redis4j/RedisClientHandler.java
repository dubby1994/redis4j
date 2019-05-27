package cn.dubby.redis4j;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadFactory;

public class RedisClientHandler extends ChannelDuplexHandler {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(RedisClientHandler.class);

    private LinkedTransferQueue<CompletableFuture<RedisMessage>> futureQueue = new LinkedTransferQueue<CompletableFuture<RedisMessage>>();

    private ExecutorService futureFillThread = Executors.newFixedThreadPool(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("FillThread");
            return thread;
        }
    });

    private LinkedTransferQueue<Object> redisMessageBlockingQueue = new LinkedTransferQueue<Object>();

    public RedisClientHandler() {
        futureFillThread.execute(() -> {
            while (true) {
                RedisMessage redisMessage = null;
                try {
                    redisMessage = (RedisMessage) redisMessageBlockingQueue.take();
                } catch (InterruptedException e) {
                    logger.error("take interrupted", e);
                }
                try {
                    CompletableFuture<RedisMessage> future = futureQueue.take();
                    future.complete(redisMessage);
                } catch (Exception e) {
                    logger.error("futureList remove", e);
                }
            }
        });
    }

    public Future<RedisMessage> getFuture() {
        CompletableFuture<RedisMessage> future = new CompletableFuture<>();
        futureQueue.add(future);
        return future;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        String[] commands = ((String) msg).split("\\s+");
        List<RedisMessage> children = new ArrayList<RedisMessage>(commands.length);
        for (String cmdString : commands) {
            children.add(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), cmdString)));
        }
        RedisMessage request = new ArrayRedisMessage(children);
        ctx.write(request, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        redisMessageBlockingQueue.add(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exceptionCaught", cause);
        ctx.close();
    }
}
