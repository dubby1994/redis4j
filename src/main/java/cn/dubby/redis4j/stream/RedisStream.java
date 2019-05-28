package cn.dubby.redis4j.stream;

import cn.dubby.redis4j.exception.RedisException;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author dubby
 * @date 2019/5/27 21:38
 */
public class RedisStream<T> {

    private LinkedTransferQueue<T> redisMessageQueue;

    private AtomicBoolean isClose = new AtomicBoolean(false);

    public RedisStream(LinkedTransferQueue<T> redisMessageQueue) {
        this.redisMessageQueue = redisMessageQueue;
    }

    public T next() throws InterruptedException, RedisException {
        return redisMessageQueue.take();
    }

    public void close() {
        isClose.set(true);
    }

    public boolean isClosed() {
        return isClose.get();
    }

}
