package org.lingg.learn.redisInAction.book.util.test;

/**
 * 父类 
 * @author syh 
 * 
 */  
  
public class Parent {  
  
    public String publicField  = "1";  
      
    String defaultField = "2";   
      
    protected String protectedField = "3";  
      
    private String privateField = "4" ;  
      
    public void publicMethod() {  
        System.out.println("Parent public Method...");
    }  
      
    void defaultMethod() {  
        System.out.println("Parent default Method...");
    }  
      
    protected void protectedMethod() {  
        System.out.println("Parent protected Method...");
    }  
      
    private void privateMethod() {  
        System.out.println("Parent private Method...");
    }  
      
}  