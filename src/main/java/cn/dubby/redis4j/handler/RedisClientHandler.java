package cn.dubby.redis4j.handler;

import cn.dubby.redis4j.wrapper.SendCommand;
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
import java.util.concurrent.Future;
import java.util.concurrent.LinkedTransferQueue;

public class RedisClientHandler extends ChannelDuplexHandler {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(RedisClientHandler.class);

    private LinkedTransferQueue<CompletableFuture<RedisMessage>> futureQueue = new LinkedTransferQueue<>();

    public Future<RedisMessage> getFuture() {
        CompletableFuture<RedisMessage> future = new CompletableFuture<>();
        futureQueue.add(future);
        return future;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        SendCommand sendCommand = (SendCommand) msg;
        futureQueue.add(((SendCommand) msg).getFuture());

        String[] commands = sendCommand.getCommand().split("\\s+");
        List<RedisMessage> children = new ArrayList<RedisMessage>(commands.length);
        for (String cmdString : commands) {
            children.add(new FullBulkStringRedisMessage(ByteBufUtil.writeUtf8(ctx.alloc(), cmdString)));
        }
        RedisMessage request = new ArrayRedisMessage(children);
        ctx.write(request, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws InterruptedException {
        CompletableFuture<RedisMessage> future = futureQueue.take();
        future.complete((RedisMessage) msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("exceptionCaught", cause);
        ctx.close();
    }
}
