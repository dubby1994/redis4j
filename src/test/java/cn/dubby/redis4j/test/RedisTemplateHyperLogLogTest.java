package cn.dubby.redis4j.test;

import cn.dubby.redis4j.RedisClient;
import cn.dubby.redis4j.template.RedisTemplate;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/5/28 10:31
 */
public class RedisTemplateHyperLogLogTest {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        RedisClient redisClient = new RedisClient("127.0.0.1", 6379, "");
        RedisTemplate redisTemplate = new RedisTemplate(redisClient);

        final String key = "hll";
        Future<Long> del = redisTemplate.del(key);
        System.out.println("del:" + del.get());

        System.out.println("==== TEST START ====");
        int threadNum = 2;
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        for (int i = 0; i < threadNum; ++i) {
            new Thread(() -> {
                for (int j = 0; j < 2; ++j) {
                    try {
                        Future<Long> longFuture = redisTemplate.pfAdd(key, System.currentTimeMillis() + ":" + UUID.randomUUID().toString());
                        //这里可以不同步等待返回
                        //System.out.println(longFuture.get());
                    } catch (Exception e) {
                        System.out.println("error " + e.getMessage());
                        return;
                    }
                }
                countDownLatch.countDown();
            }).start();
        }
        countDownLatch.await();
        System.out.println("==== TEST COMPLETE ====");

        System.out.println(redisTemplate.pfCount(key).get());

        redisTemplate.close();
    }

}
