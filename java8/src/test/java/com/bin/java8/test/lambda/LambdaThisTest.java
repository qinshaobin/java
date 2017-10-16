package com.bin.java8.test.lambda;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by shaobin.qin on 2017/7/20.
 * java8 lambda表达式this概念
 * <pre>
 *     在lambda中，this不是指向lambda表达式产生的那个SAM对象，而是声明它的外部对象。
 * </pre>
 */
public class LambdaThisTest {

	@Test
	public void testThis()
	{
		//将列表中的字符串转换为全小写
		List<String> sourStrs = Lists.newArrayList("Hello", "Java8", "Lambda");
		sourStrs.forEach(System.out::println);

		System.out.println("-------------------");

		List<String> tarStrs = sourStrs.stream().map(name -> {
			//这里this指的是java8.test.lambda.LambdaThisTest
			String className = this.getClass().getName();
			return name.toLowerCase() + ":" + className;
			}).collect(Collectors.toList());
		tarStrs.forEach(System.out::println);
	}
}
