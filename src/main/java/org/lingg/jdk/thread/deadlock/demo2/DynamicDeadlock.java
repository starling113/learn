package org.lingg.jdk.thread.deadlock.demo2;

import org.junit.jupiter.api.Test;

import javax.naming.InsufficientResourcesException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


//https://blog.csdn.net/w1014074794/article/details/51114752
 
/**
 * 动态加锁顺序产生的死锁
 * 
 * 使用模拟转账的例子来说明：
 * 
 * 这个转账操作看起来都是同样的顺序获得锁的，但是事实上锁的顺序取决于传递给transferMoney的参数的顺序，
 * 这个参数的顺序实际上又取决外部输入的顺序。
 * 
 * 如果两个线程同时调用transferMoney,一个从X向Y转账，另一个从Y向X转账，那么就会发生死锁：
 * A:  transferMoney(myAccount,yourAccount,10)
 * B:  transferMoney(yourAccount,myAccount,20)
 * 
 * 为了解决这个问题，我们必须制定锁的执行顺序，使得在整个应用程序中，获得锁都必须始终遵守这个既定的顺序。
 * 
 * 改进方法：
 * 1、通过System.identityHashCode来定义锁的顺序
 * 
 * 2、如果Account对象中有唯一的账号编码信息，可以直接通过账号编码排定对象顺序
 * 
 * @author hadoop
 *
 */
public class DynamicDeadlock {
	
	class Account{
		private String accountCode;//账户编码  
		private int balance;//账户余额
		
		
		public Account(String accountCode, int balance) {
			this.accountCode = accountCode;
			this.balance = balance;
		}
 
		public int getBalance() {
			return balance;
		}
		
		//汇出
		public  void debit(int dollarAmount){//这里犹豫每次都是对整个对象加锁，所以可以不用使用synchronized来同步
			this.balance  = this.balance - dollarAmount;
		}
		
		//汇入
        public void credit(int dollarAmount){
        	this.balance  = this.balance + dollarAmount;
		}
	}

	/**
	 * 转账(安全)？
	 * @param fromAccount   转出账户
	 * @param toAccount     转入账户
	 * @param dollarAmount  金额
	 * @throws InsufficientResourcesException 
	 */
    public void transferMoney(Account fromAccount,Account toAccount,int dollarAmount) throws InsufficientResourcesException {
    		   synchronized(fromAccount){
        		   synchronized(toAccount){
            		     if(fromAccount.getBalance() - dollarAmount < 0){//余额不足
            		    	 throw new InsufficientResourcesException();
            		     }else{
            		    	 System.out.println("开始转账");
            		    	 try {
								Thread.sleep(2*1000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
            		    	 fromAccount.debit(dollarAmount);//汇出
            		    	 toAccount.credit(dollarAmount);//转入
            		    	 System.out.println("转出金额为："+dollarAmount+",转出账户"+fromAccount.accountCode+"余额：" + fromAccount.getBalance()+",转入账户"+toAccount.accountCode+"余额：" + toAccount.getBalance());
            		     }
            	   }
        	   }
    }
	
	/**
	 * 转账(安全)
	 * @param fromAccount   转出账户
	 * @param toAccount     转入账户
	 * @param dollarAmount  金额
	 * @throws InsufficientResourcesException 
	 */
    public void transferMoneyForSafe(Account fromAccount,Account toAccount,int dollarAmount) throws InsufficientResourcesException{
    	 if(Integer.valueOf(fromAccount.accountCode) > Integer.valueOf(toAccount.accountCode)){//根据账号编号指定执行顺序，让多个线程调用的时候，始终是先获取编号大的账号的锁，然后获取编号小的账号的锁
    		   synchronized(fromAccount){
        		   synchronized(toAccount){
            		     if(fromAccount.getBalance() - dollarAmount < 0){//余额不足
            		    	 throw new InsufficientResourcesException();
            		     }else{
            		    	 try {
 								Thread.sleep(2*1000);
 							} catch (InterruptedException e) {
 								// TODO Auto-generated catch block
 								e.printStackTrace();
 							}
            		    	 fromAccount.debit(dollarAmount);//汇出
            		    	 toAccount.credit(dollarAmount);//转入
            		    	 System.out.println("转出金额为："+dollarAmount+",转出账户"+fromAccount.accountCode+"余额：" + fromAccount.getBalance()+",转入账户"+toAccount.accountCode+"余额：" + toAccount.getBalance());
            		     }
            	   }
        	   }
    	   }else if(Integer.valueOf(fromAccount.accountCode) < Integer.valueOf(toAccount.accountCode)){
    		   synchronized(toAccount){
        		   synchronized(fromAccount){
            		     if(fromAccount.getBalance() - dollarAmount < 0){//余额不足
            		    	 throw new InsufficientResourcesException();
            		     }else{
            		    	 try {
 								Thread.sleep(2*1000);
 							} catch (InterruptedException e) {
 								// TODO Auto-generated catch block
 								e.printStackTrace();
 							}
            		    	 fromAccount.debit(dollarAmount);//汇出
            		    	 toAccount.credit(dollarAmount);//转入
            		    	 System.out.println("转出金额为："+dollarAmount+",转出账户"+fromAccount.accountCode+"余额：" + fromAccount.getBalance()+",转入账户"+toAccount.accountCode+"余额：" + toAccount.getBalance());
            		     }
            	   }
        	   }
    	   }else{//如果相等，提示不能转账
    		   System.out.println("不能向自己转账");
    	   }
    	  
    }
    
    
    public static void main(String[] args) {
    	final DynamicDeadlock dynamicDeadlock =new DynamicDeadlock();
    	//这里是模拟，使用共享域作为账户对象，能够让多个线程访问账户数据
    	final Account fromAccount = dynamicDeadlock.new Account("666666", 80000);
    	final Account toAccount  = dynamicDeadlock.new Account("888888", 5000);
    	//模拟多个线程执行对这2个账户进行转账操作
    	ExecutorService executorService = Executors.newCachedThreadPool();
    	for (int i = 0; i < 100; i++) {
    		executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("借钱：");
						dynamicDeadlock.transferMoney(fromAccount,toAccount,100);
						//dynamicDeadlock.transferMoneyForSafe(fromAccount,toAccount,100);
					} catch (InsufficientResourcesException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
    		
    		executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("还钱：");
						dynamicDeadlock.transferMoney(toAccount,fromAccount,50);
					} catch (InsufficientResourcesException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}	
    	  
			
		
	}

	@Test
	public void test333() throws  Exception{
		final DynamicDeadlock dynamicDeadlock =new DynamicDeadlock();
		//这里是模拟，使用共享域作为账户对象，能够让多个线程访问账户数据
		final Account fromAccount = dynamicDeadlock.new Account("666666", 80000);
		final Account toAccount  = dynamicDeadlock.new Account("888888", 5000);

		dynamicDeadlock.transferMoney(fromAccount,toAccount,100);

		dynamicDeadlock.transferMoney(toAccount,fromAccount,66);
	}
	
}