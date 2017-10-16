package com.bin.java8.test.lambda;

import com.google.common.collect.Lists;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by shaobin.qin on 2017/7/20.
 * java8 lambda表达式的一般语法
 *<pre>
	 (Type1 param1, Type2 param2, ..., TypeN paramN) -> {
	 statment1;
	 statment2;
	 //.............
	 return statmentM;
	 }
 </pre>
 http://www.cnblogs.com/aoeiuv/p/5911692.html
 */
public class LambdaGrammarTest {

	/**
	 * 单参数写法
	 * <pre>
	 *   param1 -> {
		 statment1;
		 statment2;
		 //.............
		 return statmentM;
		 }
	 * </pre>
	 当lambda表达式的参数个数只有一个，可以省略小括号
	 */
	@Test
	public void testSingleParam()
	{
		//将列表中的字符串转换为全小写
		List<String> sourStrs = Lists.newArrayList("Hello", "Java8", "Lambda");
		sourStrs.forEach(System.out::println);

		System.out.println("-------------------");

		List<String> tarStrs = sourStrs.stream().map(name -> { return name.toLowerCase(); })
				.collect(Collectors.toList());
		tarStrs.forEach(System.out::println);
	}

	/**
	 * 单语句写法
	 * <pre>
		 param1 ->
		 statment;
	 * </pre>
	 * 当lambda表达式只包含一条语句时，可以省略大括号、return和语句结尾的分号
	 */
	@Test
	public void testSingleStatment()
	{
		//将列表中的字符串转换为全小写
		List<String> sourStrs = Lists.newArrayList("Hello", "Java8", "Lambda");
		sourStrs.forEach(System.out::println);

		System.out.println("-------------------");

		List<String> tarStrs = sourStrs.stream().map(name -> name.toLowerCase())
				.collect(Collectors.toList());
		tarStrs.forEach(System.out::println);
	}

	/**
	 * 方法引用写法
	 * <pre>
		 param1 ->
		 statment;
	 * </pre>
	 * Class or instance :: method
	 */
	@Test
	public void testMethodRef()
	{
		//将列表中的字符串转换为全小写
		List<String> sourStrs = Lists.newArrayList("Hello", "Java8", "Lambda");
		sourStrs.forEach(System.out::println);

		System.out.println("-------------------");

		List<String> tarStrs = sourStrs.stream().map(String::toLowerCase)
				.collect(Collectors.toList());
		tarStrs.forEach(System.out::println);
	}

	/**
	 * 构造方法引用
	 * <pre></pre>
	 */
	@Test
	public void testConstructionMethodRef() {
		// 内部类
		Car.create(new Supplier<Car>() {
			@Override
			public Car get() {
				return new Car();
			}
		});

		// 无参构造函数
		Car.create(() -> new Car());

		// 无参构造方法引用lambda表达式
		Car.create(Car::new);

		// 有参构造函数
		Car.create(() -> new Car("1"));

	}

	/**
	 * 静态方法引用
	 * <pre>
	 *     对于静态方法，编译器也不需要推断调用者（类名），当传入参数和静态方法所需参数个数一致时，就不存在歧义：
	 * </pre>
	 */
	@Test
	public void testStaticMethodRef() {
		Car car1 = Car.create(() -> new Car("1"));
		Car car2 = Car.create(() -> new Car("2"));
		Car car3 = Car.create(() -> new Car("3"));

		List<Car> cars = Lists.newArrayList(car1, car2, car3);

		// 内部类
		cars.forEach(new Consumer<Car>() {
			@Override
			public void accept(Car car) {
				Car.staticM(car);
			}
		});

		//
		cars.forEach(c -> Car.staticM(c));

		// 静态方法引用
		cars.forEach(Car::staticM);
	}
}

@Data
class Car {

	private String name;

	public Car() {}

	public Car(String name) {
		this.name = name;
	}

	// 通过Supplier获取Car实例
	public static Car create(Supplier<Car> supplier) {
		return supplier.get();
	}

	// 静态方法，一个入参Car对象
	public static void staticM(final Car car) {
		System.out.println("staticM:" + ToStringBuilder.reflectionToString(car));
	}

	// 非静态方法，一个入参Car对象
	public void nonStaticM(final Car car) {
		System.out.println("nonStaticM:" + ToStringBuilder.reflectionToString(car));
	}

	public void repair() {
		System.out.println("repair:" + ToStringBuilder.reflectionToString(this));
	}
}
