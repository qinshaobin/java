package com.bin.java8.test.lambda;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by shaobin.qin on 2017/8/4.
 * https://segmentfault.com/a/1190000007832130
 */
public class LambdaExceptionTest {

	private static final String path = "E:/Programmer/Code/Stu/java/java/java8/src/test/java/com/bin/java8/test/lambda";

	@Test
	public void testLambdaRuntimeException()
	{
		InterfaceA a = (()-> 1/0 + "test");

		System.out.println(a.a());
	}

	@Test
	public void testLambdaException() throws Exception
	{
		InterfaceB b = (() -> "test");
		System.out.println(b.b());
	}

	/**
	 * 统计文本行数
	 * 在lambda中捕获异常
	 */
	@Test
	public void testSolveLambdaException1() throws Exception
	{
		long count = Files.walk(Paths.get(path))
				.filter(file -> !Files.isDirectory(file))
				.filter(file -> file.toString().endsWith(".java"))
				.flatMap(file -> {
					try {
						return Files.lines(file);
					} catch (IOException e) {
						return Stream.empty();
					}})
				.filter(line -> !line.trim().isEmpty())
				.count();
		System.out.println(count);
	}

	/**
	 * 统计文本行数
	 * 把抛异常方法封装为方法
	 */
	@Test
	public void testSolveLambdaException2() throws Exception
	{
		long count = Files.walk(Paths.get(path))
				.filter(file -> !Files.isDirectory(file))
				.filter(file -> file.toString().endsWith(".java"))
				.flatMap(this::getLines)
				.filter(line -> !line.trim().isEmpty())
				.count();
		System.out.println(count);
	}

	private Stream<String> getLines(Path path)
	{
		try {
			return Files.lines(path);
		} catch (Exception e) {
			return Stream.empty();
		}
	}

	/**
	 * 统计文本行数
	 * 将会抛出异常的函数进行包装，使其不抛出受检异常
	 */
	@Test
	public void testSolveLambdaException3() throws Exception
	{
		long count = Files.walk(Paths.get(path))
				.filter(file -> !Files.isDirectory(file))
				.filter(file -> file.toString().endsWith(".java"))
				.flatMap(Try.of(Files::lines))
				.filter(line -> !line.trim().isEmpty())
				.count();
		System.out.println(count);
	}

	/**
	 * 统计文本行数
	 * 将会抛出异常的函数进行包装，如果异常返回默认值
	 */
	@Test
	public void testSolveLambdaException4() throws Exception
	{
		long count = Files.walk(Paths.get(path))
				.filter(file -> !Files.isDirectory(file))
				.filter(file -> file.toString().endsWith(".java"))
				.flatMap(Try.of(Files::lines, Stream.empty()))
				.filter(line -> !line.trim().isEmpty())
				.count();
		System.out.println(count);
	}
}

@FunctionalInterface
interface InterfaceA {

	String a() throws RuntimeException;
}

@FunctionalInterface
interface InterfaceB {

	String b() throws Exception;
}

class Try {

	@FunctionalInterface
	interface UncheckedFunction<T, R> {

		R apply(T t) throws Exception;
	}

	/**
	 * 把受检异常转为非受检异常
	 */
	public static <T, R> Function<T, R> of(UncheckedFunction<T, R> mapper) {
		Objects.requireNonNull(mapper);
		return t -> {
			try {
				return mapper.apply(t);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		};
	}

	/**
	 * 捕获受检异常，返回默认值
	 */
	public static <T, R> Function<T, R> of(UncheckedFunction<T, R> mapper, R defaultR) {
		Objects.requireNonNull(mapper);
		return t -> {
			try {
				return mapper.apply(t);
			} catch (Exception e) {
				return defaultR;
			}
		};
	}
}

