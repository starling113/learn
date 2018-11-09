package org.lingg.spring.cache;

import lombok.Data;

@Data
public class User {

	
	private String userName;
	private int age;


    public void show(){

        System.out.println(this.userName+"\t"+this.age);
    }
}
