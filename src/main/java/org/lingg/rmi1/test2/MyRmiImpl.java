package org.lingg.rmi1.test2;

import java.rmi.*;
import java.rmi.server.*;

//这个类继承 UnicastRemoteObject非常重要
//MyRmiImpl.java
public class MyRmiImpl extends UnicastRemoteObject implements MyRmiInterface {
    //在实例化这个类时，就导出了远程对象，该构造方法必需
    public MyRmiImpl() throws RemoteException {
        super();
    }

    //实现接口中的方法，这个时间不需要显 式抛出RemoteException异常了
    public String sayHello() {
        return "Hello ,world";
    }
}