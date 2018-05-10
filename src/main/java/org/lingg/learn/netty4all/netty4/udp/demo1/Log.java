package org.lingg.learn.netty4all.netty4.udp.demo1;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Log {
    public static void d(String tag, String msg){
        log.debug(msg);
    }

    public static void w(String tag, String msg){
        log.warn(msg);
    }

    public static void w(String tag, String msg,Throwable e){
        log.warn(msg,e);
    }

    public static void e(String tag, String msg){
        log.error(msg);
    }
}
