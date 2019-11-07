package cn.dubby.redis4j.test;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.template.RedisTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/30 14:03
 */
public class RedisTemplateOrderTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RedisClient redisClient = new RedisClient("127.0.0.1", 6379, "");
        RedisTemplate redisTemplate = new RedisTemplate(redisClient);

        final String key = "abc";
        Future<Long> del = redisTemplate.del(key);
        System.out.println("del:" + del.get());

        long startTime = System.currentTimeMillis();
        System.out.println("==== LOOP START ====");
        int loopNum = 4;

        for (int i = 0; i < loopNum; ++i) {
            Future<Long> longFuture = redisTemplate.incr(key);
            System.out.println(longFuture.get());
        }
        System.out.println("==== LOOP COMPLETE ====");
        System.out.println("COST:" + (System.currentTimeMillis() - startTime));

        redisTemplate.close();
    }

}
