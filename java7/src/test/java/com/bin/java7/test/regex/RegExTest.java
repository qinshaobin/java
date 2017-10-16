package com.bin.java7.test.regex;

import org.junit.Test;

import java.util.regex.Pattern;

/**
 * @author shaobin.qin
 *
 * 正则表达式的匹配有三种模式：
 * Greedy（贪婪的）
 * Reluctant（不情愿的）
 * Possessive（独占的）
 */
public class RegExTest {

	final String str = "aaaa5bbbb6";

	/**
	 * 贪婪模式
	 * 匹配最长。在贪婪量词模式下，正则表达式会尽可能长地去匹配符合规则的字符串，且会回溯。
	 */
	@Test
	public void testGreedyRegEx() {
		Pattern pattern = Pattern.compile("^.{3,10}[0-9]$");
		System.out.println(pattern.matcher(str).matches());
	}
}
