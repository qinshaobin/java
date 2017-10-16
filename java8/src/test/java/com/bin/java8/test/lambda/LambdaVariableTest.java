package com.bin.java8.test.lambda;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by shaobin.qin on 2017/7/20.
 * java8 lambda表达式访问变量
 * <pre>
 *    lambda表达式可以访问给它传递的变量，访问自己内部定义的变量，同时也能访问它外部的变量。
	 不过lambda表达式访问外部变量有一个非常重要的限制：变量不可变（只是引用不可变，而不是真正的不可变）。
	 当在表达式内部修改外部变量时，IDE就会提示你：
	 Local variable waibu defined in an enclosing scope must be final or effectively final
	 编译时会报错。因为变量waibu被lambda表达式引用，所以编译器会隐式的把其当成final来处理。
	 以前Java的匿名内部类在访问外部变量的时候，外部变量必须用final修饰。现在java8对这个限制做了优化，可以不用显示使用final修饰，但是编译器隐式当成final来处理。
 * </pre>
 */
public class LambdaVariableTest {

	@Test
	public void testVariable()
	{
		//将为列表中的字符串添加前缀字符串
		//变量可以不用final修饰，但是编译器会隐式当作final的
		String str = "variable:";
		List<String> sourStrs = Lists.newArrayList("Hello", "Java8", "Lambda");
		sourStrs.forEach(System.out::println);

		System.out.println("-------------------");

		List<String> tarStrs = sourStrs.stream().map(s -> {
				Long currentTimeMillis = System.currentTimeMillis();
				//编译报错 str = "";
				return str + s + currentTimeMillis;
			}).collect(Collectors.toList());
		tarStrs.forEach(System.out::println);
	}
}
