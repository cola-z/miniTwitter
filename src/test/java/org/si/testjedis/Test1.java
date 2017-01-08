package org.si.testjedis;

class A {
	static void m1() {
		System.out.println(" method_1 in A.");
	}
	final static void m2() {
		System.out.println(" method_2 in A.");
	}
}

class B extends A {
	//可以覆写，但 m1 是类方法，当用 B 实例化 A（A a = new B();）时，
	//a.m1()调用的仍然是 A 中的m1
	//实际上因为 m1 是类方法，所以最好通过类调用它，即 A.m1()
	static void m1() {
		System.out.println(" method_1 of B.");
	}
	/*
	 * 覆写 A 中的 m2 方法会报错
	 * static void m2() {
		System.out.println(" method_2 of B.");
	}*/
}

class C {
	public C() {
    }
	public C(int i) {
		a = i;
	}
	int a = 5;
}


public class Test1 {
	
	static void method(C c) {
		c.a++;
	}
	
	static void swap(C c1, C c2) {
		C temp = c1;
		c1 = c2;
		c2 = temp;
	}

	public static void main(String[] args) {
		/*A a = new B();
		a.m1();*/
		/*C c = new C();
		System.out.println(c.a);
		method(c);
		System.out.println(c.a);*/
		
		C c1 = new C(2);
		C c2 = new C(9);
		System.out.println(c1.a + " " + c2.a);
		swap(c1, c2);
		System.out.println(c1.a + " " + c2.a);
	}

}
