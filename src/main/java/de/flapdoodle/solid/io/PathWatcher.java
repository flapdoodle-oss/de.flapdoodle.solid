/**
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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import com.google.common.base.Preconditions;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import de.flapdoodle.solid.exceptions.SomethingWentWrong;
import de.flapdoodle.types.Try;

public class PathWatcher {

	private final WatchService watcher;
	private final Map<WatchKey, Path> keys = Maps.newLinkedHashMap();

	/**
	 * Creates a WatchService and registers the given directory
	 */
	private PathWatcher(Path dir) throws IOException {
		this.watcher = FileSystems.getDefault().newWatchService();
		walkAndRegisterDirectories(dir);
	}

	/**
	 * Register the given directory with the WatchService; This function will be
	 * called by FileVisitor
	 */
	private void registerDirectory(Path dir) throws IOException {
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		keys.put(key, dir);
	}

	/**
	 * Register the given directory, and all its sub-directories, with the
	 * WatchService.
	 */
	private void walkAndRegisterDirectories(final Path start) throws IOException {
		Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				registerDirectory(dir);
				return FileVisitResult.CONTINUE;
			}
		});
	}

	/**
	 * Process all events for keys queued to the watcher
	 * @param eventListener 
	 */
	void processEvents(long durration, TimeUnit unit, Function<Multimap<String, Path>, Boolean> eventListener) {
		for (;;) {
			
			// wait for key to be signalled
			WatchKey key;
			try {
				key = watcher.poll(durration, unit);
			}
			catch (InterruptedException x) {
				return;
			}

			LinkedHashMultimap<String, Path> events = LinkedHashMultimap.create();

			if (key!=null) {
				Path dir = keys.get(key);
				Preconditions.checkNotNull(dir,"got change for unknown directory: %s",key);
//				if (dir == null) {
//					System.err.println("WatchKey not recognized!!");
//					continue;
//				}
				
				for (WatchEvent<?> event : key.pollEvents()) {
					@SuppressWarnings("rawtypes")
					WatchEvent.Kind kind = event.kind();
	
					// Context for directory entry event is the file name of entry
					@SuppressWarnings("unchecked")
					Path name = ((WatchEvent<Path>) event).context();
					Path child = dir.resolve(name);
	
					events.put(event.kind().name(), child);
					
					// print out event
//					System.out.format("%s: %s\n", event.kind().name(), child);
	
					// if directory is created, and watching recursively, then register it
					// and its sub-directories
					if (kind == ENTRY_CREATE) {
						try {
							if (Files.isDirectory(child)) {
								walkAndRegisterDirectories(child);
							}
						}
						catch (IOException x) {
							// do something useful
						}
					}
				}
	
				// reset key and remove from set if directory no longer accessible
				boolean valid = key.reset();
				if (!valid) {
					keys.remove(key);
	
					// all directories are inaccessible
					if (keys.isEmpty()) {
						break;
					}
				}
			}
			
			if (eventListener.apply(events)) {
				return;
			}
		}
	}
	
	public static Watch watch(Path directory) {
		return new Watch(directory);
	}
	
	public static class Watch {

		private final Path directory;

		public Watch(Path directory) {
			this.directory = directory;
		}
		
		public Every every(long duration, TimeUnit unit) {
			return new Every(directory, duration, unit);
		}
	}
	
	public static class Every {

		private final Path directory;
		private final long duration;
		private final TimeUnit unit;

		public Every(Path directory, long duration, TimeUnit unit) {
			this.directory = directory;
			this.duration = duration;
			this.unit = unit;
		}
		
		public Running with(Function<Multimap<String, Path>, Boolean> eventListener) {
			return new Running(directory, duration, unit, eventListener);
		}
		
	}
	
	
	public static class Running {

		public Running(Path directory, long duration, TimeUnit unit, Function<Multimap<String, Path>, Boolean> eventListener) {
			Try.supplier(() -> new PathWatcher(directory))
				.mapCheckedException(SomethingWentWrong::new)
				.get().processEvents(duration, unit, eventListener);
		}
	}
}
