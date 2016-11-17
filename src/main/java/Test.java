package main.java;

public class Test {
	private int a = 0;
	private int b = 1;
	public void fun1(){
		int m = fun2(a, b);
		System.out.println(m);
	}
	
	private int fun2(int a,int b){
		return a+b;
	}
}
