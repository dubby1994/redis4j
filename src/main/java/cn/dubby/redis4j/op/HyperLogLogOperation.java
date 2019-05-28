package cn.dubby.redis4j.op;

import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/30 14:16
 */
public interface HyperLogLogOperation {

    Future<Long> pfAdd(String key, String... elements);

    Future<Long> pfCount(String... key);

    /**
     * 把 sourceKey 合并到 destKey
     *
     * @param destKey   目标
     * @param sourceKey 源
     * @return OK
     */
    Future<String> pfMerge(String destKey, String... sourceKey);

}
