package cn.dubby.redis4j.test;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.RedisTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author dubby
 * @date 2019/4/30 14:03
 */
public class RedisTemplateTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RedisClient redisClient = new RedisClient("127.0.0.1", 6379, "123456");
        RedisTemplate redisTemplate = new RedisTemplate(redisClient);

        Future<String> infoFuture = redisTemplate.info();
        System.out.println(infoFuture.get());

        final String key = "test-key";

        System.out.println("==== LOOP START ====");
        int threadNum = 3;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; ++i) {
            new Thread(() -> {
                for (int j = 0; j < 10; ++j) {
                    try {
                        Future<Boolean> booleanFuture = redisTemplate.set(key, String.valueOf(Thread.currentThread().getId()) + ":" + j);
                        System.out.println(booleanFuture.get());

                        Future<String> stringFuture = redisTemplate.get(key);
                        System.out.println(stringFuture.get());

                        Thread.sleep(ThreadLocalRandom.current().nextInt(100, 2000));
                    } catch (Exception e) {
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
