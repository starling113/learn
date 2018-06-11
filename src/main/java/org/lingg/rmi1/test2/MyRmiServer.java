package org.lingg.rmi1.test2;

import org.lingg.rmi1.test2.MyRmiImpl;

import java.rmi.*;
import java.rmi.registry.*;

//MyRmiServer.java
public class MyRmiServer {
    public static void main(String args[]) {
        try {
            //实例化远程对象，同时导出了该对象
            MyRmiInterface server = new MyRmiImpl();


            //远程对象注册表实例
            LocateRegistry.createRegistry(8888);
            //把远程对象注册到RMI注册服务器上
            Naming.bind("rmi://localhost:8888/Hello", server);

            //通告服务端已准备好了
            System.out.println("System already!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}