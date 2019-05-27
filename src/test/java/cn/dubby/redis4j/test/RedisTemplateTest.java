package cn.dubby.redis4j.test;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.template.RedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/30 14:03
 */
public class RedisTemplateTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RedisClient redisClient = new RedisClient("127.0.0.1", 6379, "");
        RedisTemplate redisTemplate = new RedisTemplate(redisClient);

        final String key = "abc";
        Future<Long> del = redisTemplate.del(key);
        System.out.println("del:" + del.get());

        System.out.println("==== LOOP START ====");
        int threadNum = 100;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; ++i) {
            new Thread(() -> {
                for (int j = 0; j < 100; ++j) {
                    try {
                        Future<Long> longFuture = redisTemplate.incr(key);
                        System.out.println(longFuture.get());
                    } catch (Exception e) {
                        System.out.println("error " + e.getMessage());
                        return;
                    }
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println("==== LOOP COMPLETE ====");

        redisTemplate.close();
    }

}
