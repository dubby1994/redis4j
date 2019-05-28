package cn.dubby.redis4j.test;

import cn.dubby.redis4j.RedisClient;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.ArrayRedisMessage;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.CharsetUtil;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/29 19:59
 */
public class RedisClientTest {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        RedisClient redisClient = new RedisClient("127.0.0.1", 6379, "");

        for (int i = 0; i < 3; ++i) {
            new Thread(() -> {
                try {
                    Future<RedisMessage> future = redisClient.execute("INFO");
                    printAggregatedRedisResponse(future.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    private static void printAggregatedRedisResponse(RedisMessage msg) {
        if (msg instanceof SimpleStringRedisMessage) {
            System.out.println(((SimpleStringRedisMessage) msg).content());
        } else if (msg instanceof ErrorRedisMessage) {
            System.out.println(((ErrorRedisMessage) msg).content());
        } else if (msg instanceof IntegerRedisMessage) {
            System.out.println(((IntegerRedisMessage) msg).value());
        } else if (msg instanceof FullBulkStringRedisMessage) {
            System.out.println(getString((FullBulkStringRedisMessage) msg));
        } else if (msg instanceof ArrayRedisMessage) {
            for (RedisMessage child : ((ArrayRedisMessage) msg).children()) {
                printAggregatedRedisResponse(child);
            }
        } else {
            throw new CodecException("unknown message type: " + msg);
        }
    }

    private static String getString(FullBulkStringRedisMessage msg) {
        if (msg.isNull()) {
            return "(null)";
        }
        return msg.content().toString(CharsetUtil.UTF_8);
    }

}
