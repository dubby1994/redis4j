package cn.dubby.redis4j.op;

import java.util.concurrent.Future;

/**
 * @author dubby
 * @date 2019/4/30 11:33
 */
public interface StringOperation {

    Future<String> get(String key);

    Future<Boolean> set(String key, String value);

}
