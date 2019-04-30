package cn.dubby.redis4j.op;

import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/30 14:19
 */
public interface ServerOperation {

    Future<String> info();

}
