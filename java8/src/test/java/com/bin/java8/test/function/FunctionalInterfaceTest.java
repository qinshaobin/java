package com.bin.java8.test.function;

/**
 * Created by shaobin.qin on 2017/8/4.
 * <pre>
 *     标注为FunctionalInterface的接口被称为函数式接口，该接口只能有一个自定义方法，但是可以包括从object类继承而来的方法。
 *     如果一个接口只有一个方法，则编译器会认为这就是一个函数式接口。
 *     在一个接口中定义两个自定义的方法，就会产生Invalid '@FunctionalInterface' annotation; FunctionalInterfaceTest is not a functional interface错误：
 *     http://blog.csdn.net/englishsname/article/details/54174541
 * </pre>
 */
public class FunctionalInterfaceTest {
}

@FunctionalInterface
interface FunctionlInterfaceA {

	String a();

	//可以覆盖object类的方法
	@Override
	String toString();
}

@FunctionalInterface
interface FunctionlInterfaceB {

	String b();

	//编译报错
	//String c();
}