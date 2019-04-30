package cn.dubby.redis4j.op;

import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/30 11:33
 */
public interface StringOperation {

    /**
     * Get the value of key.
     * If the key does not exist the special value nil is returned.
     * An error is returned if the value stored at key is not a string,
     * because GET only handles string values.
     *
     * @return Bulk string reply: the value of key, or nil when key does not exist.
     */
    Future<String> get(String key);

    /**
     * SET key value [expiration EX seconds|PX milliseconds] [NX|XX]
     * <p>
     * Set key to hold the string value.
     * If key already holds a value, it is overwritten, regardless of its type.
     * Any previous time to live associated with the key is discarded on successful SET operation.
     * <p>
     * Options
     * Starting with Redis 2.6.12 SET supports a set of options that modify its behavior:
     * <p>
     * EX seconds -- Set the specified expire time, in seconds.
     * PX milliseconds -- Set the specified expire time, in milliseconds.
     * NX -- Only set the key if it does not already exist.
     * XX -- Only set the key if it already exist.
     * Note: Since the SET command options can replace SETNX, SETEX, PSETEX, it is possible that in future versions of Redis these three commands will be deprecated and finally removed.
     *
     * @return Simple string reply: OK if SET was executed correctly. Null reply: a Null Bulk Reply is returned if the SET operation was not performed because the user specified the NX or XX option but the condition was not met.
     */
    Future<Boolean> set(String key, String value);

}
