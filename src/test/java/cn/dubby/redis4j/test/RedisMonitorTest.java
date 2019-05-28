package cn.dubby.redis4j.test;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.exception.RedisException;
import cn.dubby.redis4j.stream.RedisStream;
import cn.dubby.redis4j.template.MonitorTemplate;

import java.util.concurrent.ExecutionException;

/**
 * @author dubby
 * @date 2019/5/27 22:00
 */
public class RedisMonitorTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException, RedisException {
        RedisClient redisClient = new RedisClient("127.0.0.1", 6379, "");
        MonitorTemplate monitorTemplate = new MonitorTemplate(redisClient);
        RedisStream<String> stringRedisStream = monitorTemplate.monitor();
        while (true) {
            System.out.println(stringRedisStream.next());
        }
    }

}
