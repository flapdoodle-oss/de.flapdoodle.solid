package de.flapdoodle.solid.threads;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.Test;

import com.google.common.collect.Lists;

public class DefererTest {

	@Test
	public void defererShouldNotBeCalledIfConsumerIsNotCalled() throws InterruptedException {
		AtomicInteger counter=new AtomicInteger(0);
		Consumer<String> consumer = Deferer.<String>call(t -> {
				counter.incrementAndGet();
			})
			.onInactivityFor(10, TimeUnit.MILLISECONDS);
		
		assertNotNull(consumer);
		Thread.sleep(50);
		assertEquals(0,counter.get());
	}

	@Test
	public void defererShouldNotBeCalledIfConsumerGotNullValue() throws InterruptedException {
		AtomicInteger counter=new AtomicInteger(0);
		Consumer<String> consumer = Deferer.<String>call(t -> {
				counter.incrementAndGet();
			})
			.onInactivityFor(10, TimeUnit.MILLISECONDS);
		
		consumer.accept(null);
		Thread.sleep(50);
		assertEquals(0,counter.get());
	}
	
	@Test
	public void defererShouldBeCalledOnlyOnNewValues() throws InterruptedException {
		List<String> recorder=Lists.newArrayList();
		Consumer<String> consumer = Deferer.<String>call(t -> {
				recorder.add(t);
			})
			.onInactivityFor(10, TimeUnit.MILLISECONDS);
		
		Thread.sleep(20);
		consumer.accept("One");
		Thread.sleep(20);
		consumer.accept("Two");
		Thread.sleep(20);
		
		assertEquals(2, recorder.size());
		assertEquals("[One, Two]", recorder.toString());
	}
	
	@Test
	public void defererShouldBeCalledOnlyOnLatestNewValues() throws InterruptedException {
		List<String> recorder=Lists.newArrayList();
		Consumer<String> consumer = Deferer.<String>call(t -> {
				recorder.add(t);
			})
			.onInactivityFor(100, TimeUnit.MILLISECONDS);
		
		for (int i=0;i<10;i++) {
			consumer.accept("some"+i);
		}
		consumer.accept("Latest");
		Thread.sleep(200);
		
		assertEquals(1, recorder.size());
		assertEquals("[Latest]", recorder.toString());
	}
}
