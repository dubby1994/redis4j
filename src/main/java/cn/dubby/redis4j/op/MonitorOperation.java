package cn.dubby.redis4j.op;

import cn.dubby.redis4j.stream.RedisStream;

import java.util.concurrent.ExecutionException;

/**
 * @author dubby
 * @date 2019/5/27 21:34
 */
public interface MonitorOperation {

    RedisStream<String> monitor() throws ExecutionException, InterruptedException;

}
