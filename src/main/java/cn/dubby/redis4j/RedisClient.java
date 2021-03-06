package cn.dubby.redis4j;

import cn.dubby.redis4j.handler.RedisClientHandler;
import cn.dubby.redis4j.wrapper.SendCommand;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.RedisArrayAggregator;
import io.netty.handler.codec.redis.RedisBulkStringAggregator;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/29 17:46
 */
public class RedisClient {

    private static final InternalLogger logger = InternalLoggerFactory.getInstance(RedisClient.class);

    private String host;

    private int port;

    private String password;

    private Channel channel;

    private EventLoopGroup group;

    private RedisClientHandler redisClientHandler = new RedisClientHandler();

    public RedisClientHandler getRedisClientHandler() {
        return redisClientHandler;
    }

    public RedisClient(String host, int port, String password) throws InterruptedException, ExecutionException {
        this.host = host;
        this.port = port;
        this.password = password;
        init();
    }

    public void close() {
        channel.close();
        group.shutdownGracefully();
    }

    private void init() throws InterruptedException, ExecutionException {
        group = new NioEventLoopGroup();

        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new RedisDecoder());
                        p.addLast(new RedisBulkStringAggregator());
                        p.addLast(new RedisArrayAggregator());
                        p.addLast(new RedisEncoder());
                        p.addLast(redisClientHandler);
                    }
                });

        channel = b.connect(host, port).sync().channel();

        if (!StringUtil.isNullOrEmpty(password)) {
            SendCommand sendCommand = new SendCommand(new CompletableFuture<>(), "AUTH " + password);
            channel.writeAndFlush(sendCommand);
            Future<RedisMessage> future = sendCommand.getFuture();
            RedisMessage redisMessage = future.get();
            if (redisMessage instanceof ErrorRedisMessage) {
                logger.error(((ErrorRedisMessage) redisMessage).content());
            } else if (redisMessage instanceof SimpleStringRedisMessage) {
                logger.info(((SimpleStringRedisMessage) redisMessage).content());
            }
        }
    }

    public CompletableFuture<RedisMessage> execute(String command) {
        SendCommand sendCommand = new SendCommand(new CompletableFuture<>(), command);
        channel.writeAndFlush(sendCommand);
        return sendCommand.getFuture();
    }

}
