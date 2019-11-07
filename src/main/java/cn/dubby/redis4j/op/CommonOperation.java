package cn.dubby.redis4j.op;

import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/5/27 21:08
 */
public interface CommonOperation {

    Future<Long> del(String key);

}
