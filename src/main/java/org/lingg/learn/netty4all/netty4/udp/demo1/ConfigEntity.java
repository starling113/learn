package org.lingg.learn.netty4all.netty4.udp.demo1;

public class ConfigEntity {

    public static String serverIP = "127.0.0.1";
    public static int serverUDPPort = 9999;

    public static int localUDPPort = 0;// UDP本地监听端口（如果为0将表示由系统分配，否则使用指定端口）
}
