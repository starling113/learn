package org.lingg.learn.netty4all.netty4.udp.demo1;

import java.net.DatagramPacket;

public class EchoClient {
    public static void main(String[] args) throws Exception {
        // 初始化本地UDP的Socket
        LocalUDPSocketProvider.getInstance().initSocket();
        // 启动本地UDP监听（接收数据用的）
        LocalUDPDataReciever.getInstance().startup();

        //UdpUtils udpUtils = new UdpUtils();
        //udpUtils.run();
// 循环发送数据给服务端
        while (true) {
// 要发送的数据
            String toServer = "Hi，我是客户端，我的时间戳" + System.currentTimeMillis();
            byte[] soServerBytes = toServer.getBytes("UTF-8");

// 开始发送
LocalUDPSocketProvider.getInstance().getLocalUDPSocket().send(new DatagramPacket(soServerBytes, soServerBytes.length));
            //udpUtils.send(toServer, ConfigEntity.serverIP, ConfigEntity.serverUDPPort);
            //boolean ok = UDPUtils.send(soServerBytes, soServerBytes.length);
            //if(ok)
            //Log.d("EchoClient", "发往服务端的信息已送出.");
            //else
            //Log.e("EchoClient", "发往服务端的信息没有成功发出！！！");

// 3000秒后进入下一次循环
            Thread.sleep(3000);
        }
    }
}