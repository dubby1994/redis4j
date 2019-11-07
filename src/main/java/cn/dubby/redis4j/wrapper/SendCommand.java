package cn.dubby.redis4j.wrapper;

import io.netty.handler.codec.redis.RedisMessage;

import java.util.concurrent.CompletableFuture;

/**
 * @author dubby
 * @date 2019/11/7 18:01
 */
public class SendCommand {

    public SendCommand(CompletableFuture<RedisMessage> future, String command) {
        this.future = future;
        this.command = command;
    }

    private CompletableFuture<RedisMessage> future;

    private String command;

    public CompletableFuture<RedisMessage> getFuture() {
        return future;
    }

    public String getCommand() {
        return command;
    }

}
