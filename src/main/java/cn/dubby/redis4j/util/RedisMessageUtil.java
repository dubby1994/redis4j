package cn.dubby.redis4j.util;

import cn.dubby.redis4j.exception.RedisException;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.handler.codec.redis.FullBulkStringRedisMessage;
import io.netty.handler.codec.redis.IntegerRedisMessage;
import io.netty.handler.codec.redis.RedisMessage;
import io.netty.handler.codec.redis.SimpleStringRedisMessage;
import io.netty.util.CharsetUtil;

/**
 * @author dubby
 * @date 2019/4/30 14:21
 */
public class RedisMessageUtil {

    public static String parseString(RedisMessage msg) throws RedisException {
        if (msg instanceof SimpleStringRedisMessage) {
            return ((SimpleStringRedisMessage) msg).content();
        } else if (msg instanceof IntegerRedisMessage) {
            return String.valueOf(((IntegerRedisMessage) msg).value());
        } else if (msg instanceof FullBulkStringRedisMessage) {
            return getString((FullBulkStringRedisMessage) msg);
        } else if (msg instanceof ErrorRedisMessage) {
            throw new RedisException(((ErrorRedisMessage) msg).content());
        } else {
            throw new RedisException("unknown message type: " + msg);
        }
    }

    private static String getString(FullBulkStringRedisMessage msg) {
        if (msg.isNull()) {
            return "(null)";
        }
        return msg.content().toString(CharsetUtil.UTF_8);
    }

}
