/*
 * Copyright (c) 2011-2015 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor;

import org.reactivestreams.Processor;
import reactor.core.processor.*;
import reactor.core.support.internal.PlatformDependent;
import reactor.fn.Consumer;

/**
 * Main gateway to build various asynchronous {@link Processor} or "pool" services that allow their reuse.
 * Reactor offers a few management API via the subclassed {@link ExecutorPoweredProcessor} for the underlying {@link java.util.concurrent.Executor} in use.
 * 
 * @author Stephane Maldini
 * @since 2.1
 */
public final class Processors {

	/**
	 * Default number of processors available to the runtime on init (min 2)
	 *
	 * @see Runtime#availableProcessors()
	 */
	public static final int DEFAULT_POOL_SIZE = Math.min(Runtime.getRuntime().availableProcessors(), 2);

	/**
	 * Create a new {@link ExecutorPoweredProcessor} using {@link AsyncProcessor#SMALL_BUFFER_SIZE} backlog size, blockingWait Strategy
	 * and auto-cancel.
	 * <p>
	 * A Shared Processor authorizes concurrent onNext calls and is suited for multi-threaded publisher that
	 * will fan-in data.
	 * <p>
	 * A new Cached ThreadExecutorPool will be implicitely created.
	 *
	 * @param <E> Type of processed signals
	 * @return a fresh processor
	 */
	public static <E> ExecutorPoweredProcessor<E, E> async() {
		return async("async", AsyncProcessor.SMALL_BUFFER_SIZE, true);
	}

	/**
	 * Create a new {@link ExecutorPoweredProcessor} using {@link AsyncProcessor#SMALL_BUFFER_SIZE} backlog size, blockingWait Strategy
	 * and the passed auto-cancel setting.
	 * <p>
	 * A Shared Processor authorizes concurrent onNext calls and is suited for multi-threaded publisher that
	 * will fan-in data.
	 * <p>
	 * A new Cached ThreadExecutorPool will be implicitely created.
	 *
	 * @param autoCancel Should this propagate cancellation when unregistered by all subscribers ?
	 * @param <E>        Type of processed signals
	 * @return a fresh processor
	 */
	public static <E> ExecutorPoweredProcessor<E, E> async(boolean autoCancel) {
		return async(Processors.class.getSimpleName(), AsyncProcessor.SMALL_BUFFER_SIZE, autoCancel);
	}

	/**
	 * Create a new {@link ExecutorPoweredProcessor} using {@link AsyncProcessor#SMALL_BUFFER_SIZE} backlog size, blockingWait Strategy
	 * and the passed auto-cancel setting.
	 * <p>
	 * A Shared Processor authorizes concurrent onNext calls and is suited for multi-threaded publisher that
	 * will fan-in data.
	 * <p>
	 * A new Cached ThreadExecutorPool will be implicitely created and will use the passed name to qualify
	 * the created threads.
	 *
	 * @param name       Use a new Cached ExecutorService and assign this name to the created threads
	 * @param bufferSize A Backlog Size to mitigate slow subscribers
	 * @param <E>        Type of processed signals
	 * @return a fresh processor
	 */
	public static <E> ExecutorPoweredProcessor<E, E> async(String name, int bufferSize) {
		return async(name, bufferSize, true);
	}

	/**
	 * Create a new {@link ExecutorPoweredProcessor} using the blockingWait Strategy, passed backlog size,
	 * and auto-cancel settings.
	 * <p>
	 * A Shared Processor authorizes concurrent onNext calls and is suited for multi-threaded publisher that
	 * will fan-in data.
	 * <p>
	 * The passed {@link java.util.concurrent.ExecutorService} will execute as many event-loop
	 * consuming the ringbuffer as subscribers.
	 *
	 * @param name       Use a new Cached ExecutorService and assign this name to the created threads
	 * @param bufferSize A Backlog Size to mitigate slow subscribers
	 * @param autoCancel Should this propagate cancellation when unregistered by all subscribers ?
	 * @param <E>        Type of processed signals
	 * @return a fresh processor
	 */
	public static <E> ExecutorPoweredProcessor<E, E> async(String name, int bufferSize, boolean autoCancel) {
		final ExecutorPoweredProcessor<E, E> processor;

		if (PlatformDependent.hasUnsafe()) {
			processor = RingBufferProcessor.create(name, bufferSize, autoCancel);
		} else {
			throw new UnsupportedOperationException("Pub-Sub async processor not yet supported without Unsafe");
				//			processor = SimpleWorkProcessor.create(name, bufferSize);
		}
		return processor;
	}

	/**
	 * Create a new {@link ExecutorPoweredProcessor} using {@link AsyncProcessor#SMALL_BUFFER_SIZE} backlog size, blockingWait Strategy
	 * and auto-cancel.
	 * <p>
	 * A Shared Processor authorizes concurrent onNext calls and is suited for multi-threaded publisher that
	 * will fan-in data.
	 * <p>
	 * A new Cached ThreadExecutorPool will be implicitely created.
	 *
	 * @param <E> Type of processed signals
	 * @return a fresh processor
	 */
	public static <E> ExecutorPoweredProcessor<E, E> work() {
		return work("worker", AsyncProcessor.SMALL_BUFFER_SIZE, true);
	}

	/**
	 * Create a new {@link ExecutorPoweredProcessor} using {@link AsyncProcessor#SMALL_BUFFER_SIZE} backlog size, blockingWait Strategy
	 * and the passed auto-cancel setting.
	 * <p>
	 * A Shared Processor authorizes concurrent onNext calls and is suited for multi-threaded publisher that
	 * will fan-in data.
	 * <p>
	 * A new Cached ThreadExecutorPool will be implicitely created.
	 *
	 * @param autoCancel Should this propagate cancellation when unregistered by all subscribers ?
	 * @param <E>        Type of processed signals
	 * @return a fresh processor
	 */
	public static <E> ExecutorPoweredProcessor<E, E> work(boolean autoCancel) {
		return work(Processors.class.getSimpleName(), AsyncProcessor.SMALL_BUFFER_SIZE, autoCancel);
	}

	/**
	 * Create a new {@link ExecutorPoweredProcessor} using {@link AsyncProcessor#SMALL_BUFFER_SIZE} backlog size, blockingWait Strategy
	 * and the passed auto-cancel setting.
	 * <p>
	 * A Shared Processor authorizes concurrent onNext calls and is suited for multi-threaded publisher that
	 * will fan-in data.
	 * <p>
	 * A new Cached ThreadExecutorPool will be implicitely created and will use the passed name to qualify
	 * the created threads.
	 *
	 * @param name       Use a new Cached ExecutorService and assign this name to the created threads
	 * @param bufferSize A Backlog Size to mitigate slow subscribers
	 * @param <E>        Type of processed signals
	 * @return a fresh processor
	 */
	public static <E> ExecutorPoweredProcessor<E, E> work(String name, int bufferSize) {
		return work(name, bufferSize, true);
	}

	/**
	 * Create a new {@link ExecutorPoweredProcessor} using the blockingWait Strategy, passed backlog size,
	 * and auto-cancel settings.
	 * <p>
	 * A Shared Processor authorizes concurrent onNext calls and is suited for multi-threaded publisher that
	 * will fan-in data.
	 * <p>
	 * A new Cached ThreadExecutorPool will be implicitely created and will use the passed name to qualify
	 * the created threads.
	 *
	 * @param name       Use a new Cached ExecutorService and assign this name to the created threads
	 * @param bufferSize A Backlog Size to mitigate slow subscribers
	 * @param autoCancel Should this propagate cancellation when unregistered by all subscribers ?
	 * @param <E>        Type of processed signals
	 * @return a fresh processor
	 */
	public static <E> ExecutorPoweredProcessor<E, E> work(String name, int bufferSize, boolean autoCancel) {
		final ExecutorPoweredProcessor<E, E> processor;

		if (PlatformDependent.hasUnsafe()) {
			processor = RingBufferWorkProcessor.create(name, bufferSize, autoCancel);
		} else {
			processor = SimpleWorkProcessor.create(name, bufferSize, autoCancel);
		}
		return processor;
	}

	/**
	 * @param name
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> asyncService(String name) {
		return asyncService(name, AsyncProcessor.MEDIUM_BUFFER_SIZE);
	}

	/**
	 * @param name
	 * @param bufferSize
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> asyncService(String name,
	                                                         int bufferSize) {
		return asyncService(name, bufferSize, null);
	}

	/**
	 * @param name
	 * @param bufferSize
	 * @param uncaughtExceptionHandler
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> asyncService(String name,
	                                                         int bufferSize,
	                                                         Consumer<Throwable> uncaughtExceptionHandler) {
		return asyncService(name, bufferSize, uncaughtExceptionHandler, null);
	}

	/**
	 * @param name
	 * @param bufferSize
	 * @param uncaughtExceptionHandler
	 * @param shutdownHandler
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> asyncService(String name,
	                                                         int bufferSize,
	                                                         Consumer<Throwable> uncaughtExceptionHandler,
	                                                         Consumer<Void> shutdownHandler
	) {
		return asyncService(name, bufferSize, uncaughtExceptionHandler, shutdownHandler, true);
	}

	/**
	 * @param name
	 * @param bufferSize
	 * @param uncaughtExceptionHandler
	 * @param shutdownHandler
	 * @param autoShutdown
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> asyncService(String name,
	                                                         int bufferSize,
	                                                         Consumer<Throwable> uncaughtExceptionHandler,
	                                                         Consumer<Void> shutdownHandler,
	                                                         boolean autoShutdown) {

		return SharedProcessorService.create(
		  PlatformDependent.hasUnsafe()
			? RingBufferProcessor.share(name, bufferSize, SharedProcessorService.DEFAULT_TASK_PROVIDER)
			: SimpleWorkProcessor.create(name, bufferSize),
		  uncaughtExceptionHandler,
		  shutdownHandler,
		  autoShutdown
		);
	}


	/**
	 * @param name
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> workService(String name) {
		return workService(name, AsyncProcessor.MEDIUM_BUFFER_SIZE);
	}

	/**
	 * @param name
	 * @param bufferSize
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> workService(String name,
	                                                        int bufferSize) {
		return workService(name, bufferSize, DEFAULT_POOL_SIZE);
	}

	/**
	 * @param name
	 * @param bufferSize
	 * @param concurrency
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> workService(String name,
	                                                        int bufferSize,
	                                                        int concurrency) {
		return workService(name, bufferSize, concurrency, null, null, true);
	}

	/**
	 * @param name
	 * @param bufferSize
	 * @param concurrency
	 * @param uncaughtExceptionHandler
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> workService(String name,
	                                                        int bufferSize,
	                                                        int concurrency,
	                                                        Consumer<Throwable> uncaughtExceptionHandler) {
		return workService(name, bufferSize, concurrency, uncaughtExceptionHandler, null, true);
	}

	/**
	 * @param name
	 * @param bufferSize
	 * @param concurrency
	 * @param uncaughtExceptionHandler
	 * @param shutdownHandler
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> workService(String name,
	                                                        int bufferSize,
	                                                        int concurrency,
	                                                        Consumer<Throwable> uncaughtExceptionHandler,
	                                                        Consumer<Void> shutdownHandler) {
		return workService(name, bufferSize, concurrency, uncaughtExceptionHandler, shutdownHandler, true);
	}


	/**
	 * @param name
	 * @param bufferSize
	 * @param concurrency
	 * @param uncaughtExceptionHandler
	 * @param shutdownHandler
	 * @param autoShutdown
	 * @param <E>
	 * @return
	 */
	public static <E> SharedProcessorService<E> workService(String name,
	                                                        int bufferSize,
	                                                        int concurrency,
	                                                        Consumer<Throwable> uncaughtExceptionHandler,
	                                                        Consumer<Void> shutdownHandler,
	                                                        boolean autoShutdown) {
		return SharedProcessorService.create(
		  PlatformDependent.hasUnsafe()
		    ? RingBufferWorkProcessor.share(name, bufferSize)
		    : SimpleWorkProcessor.create(name, bufferSize),
		  concurrency,
		  uncaughtExceptionHandler,
		  shutdownHandler,
		  autoShutdown
		);
	}

	/**
	 *
	 * @param processor
	 * @param <I>
	 * @param <O>
	 * @return
	 */
	public <I, O> Processor<I, O> broadcast(Processor<I, O> processor) {
		processor.onSubscribe(Publishers.NOOP_SUBSCRIPTION);
		return processor;
	}

}