package cn.dubby.redis4j;

import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class RedisClientHandler extends ChannelDuplexHandler {

    private List<CompletableFuture<RedisMessage>> futureList = new ArrayList<>(1024);

    public Future<RedisMessage> getFuture() {
        CompletableFuture<RedisMessage> future = new CompletableFuture<>();
        futureList.add(future);
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
        RedisMessage redisMessage = (RedisMessage) msg;
        CompletableFuture<RedisMessage> future = null;
        try {
            future = futureList.remove(0);
        } catch (Exception e) {
            System.out.println("futureList.remove error\t\t" + e.getMessage() + "\t\t" + redisMessage.toString());
            System.exit(1);
        }
        future.complete(redisMessage);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.print("exceptionCaught: ");
        cause.printStackTrace(System.err);
        ctx.close();
    }
}
