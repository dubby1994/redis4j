# redis4j

>基于Netty的一个极简的Redis Client。

## RedisTemplate

初始化:

```
RedisClient redisClient = new RedisClient("127.0.0.1", 6379, "");
RedisTemplate redisTemplate = new RedisTemplate(redisClient);
```

使用:
```
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
System.out.println(redisTemplate.info().get());
```

关闭连接:
```
redisTemplate.close();
```


## MonitorTemplate

初始化:

```
RedisClient redisClient = new RedisClient("127.0.0.1", 6379, "");
MonitorTemplate monitorTemplate = new MonitorTemplate(redisClient);
```

使用:
```
RedisStream<String> stringRedisStream = monitorTemplate.monitor();
while (true) {
    System.out.println(stringRedisStream.next());
}
```

关闭连接:
```
redisTemplate.close();
```