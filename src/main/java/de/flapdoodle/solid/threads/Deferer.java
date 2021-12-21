/*
 * Copyright (C) 2017
 *   Michael Mosmann <michael@mosmann.de>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
