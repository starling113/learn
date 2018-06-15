package org.lingg.rmi1.test2;//MyRmiInterface.java

import java.rmi.Remote;
import java.rmi.RemoteException;

//这个接口必需继承Remote 接口
public interface MyRmiInterface extends Remote {
    //声明方法 时，必需显式地抛出RemoteException异常
    public String sayHello() throws RemoteException;
}