# redis4j

>基于Netty的一个极简的Redis Client

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

![](https://www.dubby.cn/upload/2019-11-07/3b68cf8a-8697-4824-afba-c81288aaec62.png)

![](https://www.dubby.cn/upload/2019-11-07/8a97fcdc-1241-4b81-b555-252711454a7a.png)

![](https://www.dubby.cn/upload/2019-11-07/5e6e3f42-bdb6-466b-8f21-dd051834c95e.png)

![](https://www.dubby.cn/upload/2019-11-07/cbd2ff10-a97f-4479-85fc-08dca88a4b56.png)
