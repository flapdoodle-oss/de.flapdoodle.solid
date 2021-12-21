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
package de.flapdoodle.solid.io;

import java.io.File;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import de.flapdoodle.types.Try;

public class PathWatcherTest {

	@Test
	@Ignore
	public void watcherMustNotify() throws InterruptedException, ExecutionException {
		File tempDir = Files.createTempDir();
		
		AtomicBoolean shouldCreateFiles = new AtomicBoolean(true);
		AtomicInteger changeCounter=new AtomicInteger(0);
		
		Future<?> fileChanger = Executors.newSingleThreadExecutor().submit(() -> {
			int i=0;
			while (shouldCreateFiles.get()) {
				int run=i++;
				
				Try.runable(() -> {
						System.out.println("run "+run);
						System.out.flush();
						java.nio.file.Files.write(tempDir.toPath().resolve("test"+run), "".getBytes(Charsets.UTF_8), StandardOpenOption.CREATE_NEW);
						java.nio.file.Files.write(tempDir.toPath().resolve("test"+run+"b"), "".getBytes(Charsets.UTF_8), StandardOpenOption.CREATE_NEW);
						Thread.sleep(100);
					})
					.mapCheckedException(RuntimeException::new)
					.run();
			}
		});
		
		PathWatcher.watch(tempDir.toPath())
		.every(1, TimeUnit.SECONDS)
		.with(changes -> {
					return changeCounter.addAndGet(changes.size()) > 10;
				});
		
		shouldCreateFiles.set(false);
		fileChanger.get();
		
		System.out.println("DONE");
		System.out.flush();
	}
}
