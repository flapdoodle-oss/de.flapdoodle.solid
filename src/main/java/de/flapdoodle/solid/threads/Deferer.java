package de.flapdoodle.solid.threads;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class Deferer {
	
	public static <T> Builder<T> call(Consumer<T> consumer) {
		return new Builder<T>(consumer);
	}
	
	public static class Builder<T> {

		private final Consumer<T> consumer;

		public Builder(Consumer<T> consumer) {
			this.consumer = consumer;
		}
		
		public Consumer<T> onInactivityFor(long duration, TimeUnit unit) {
			return new InactivityConsumerDelegate<T>(duration,unit,consumer);
		}
		
	}
	
	private static class InactivityConsumerDelegate<T> implements Consumer<T> {

		private final long waitInMs;
		private final AtomicReference<T> lastValue=new AtomicReference<>();
		private final AtomicLong lastUpdated=new AtomicLong(System.currentTimeMillis());
		private final Consumer<T> consumer; 
		
		public InactivityConsumerDelegate(long duration, TimeUnit unit, Consumer<T> consumer) {
			this.consumer = consumer;
			this.waitInMs = unit.toMillis(duration);
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					long timeSinceLastUpdate = System.currentTimeMillis()-lastUpdated.get();
					if (timeSinceLastUpdate>waitInMs) {
						T value = lastValue.getAndSet(null);
						if (value!=null) {
							InactivityConsumerDelegate.this.consumer.accept(value);
						}
					}
				}
			}, waitInMs, waitInMs/2);
		}

		@Override
		public void accept(T t) {
			this.lastValue.set(t);
			this.lastUpdated.set(System.currentTimeMillis());
		}
		
	}
}
