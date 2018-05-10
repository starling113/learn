package org.lingg.learn.netty4all.netty4.udp.demo1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Scanner;

public class UdpUtils implements Runnable {

    //定义Socket数据包服务
    private DatagramSocket socket;

    public UdpUtils() {
        try {
            //创建socket数据包服务
            socket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    //发送
    public void send(String content, String ip, int port) {
        //获取接收端 的IP 和 端口号
        InetSocketAddress address = new InetSocketAddress(ip, port); 
        //创建数据包  并将 消息内容 、地址 传入
        DatagramPacket dp = new DatagramPacket(content.getBytes(),
                content.getBytes().length,address);
        try {
            //发送数据包
            socket.send(dp);
            try {
                Thread.sleep(1);    //防止Udp传输时，包错误。
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //接收
    @Override
    public void run() {
        byte[] buf = new byte[1024];
        //创建数据包 将 发送过来的 消息内容 取出
        DatagramPacket dp = new DatagramPacket(buf, buf.length);
        while (true) {
            try {
                //接收数据包
                socket.receive(dp);
                System.out.println(new String(dp.getData(), 0, dp.getLength()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

//Test 测试
class TestUdpUtils{
    public static void main(String[] args) {
        UdpUtils utils = new UdpUtils();
        Thread thread = new Thread(utils);
        thread.start();
        Scanner input = new Scanner(System.in);
        while(true){
            String msg = input.next();
            if(msg.equals("exit")){
                input.close();
                System.exit(0);
            }
            utils.send("Send:" + msg, "127.0.0.1", 9999);
        }
    }
}