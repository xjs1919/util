package com.github.xjs.util;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author jiashuai.xujs
 * @date 2022/4/14 10:46
 */
public class ContextPassingThreadPool<CTX> {

    private int corePoolSize;
    private int maximumPoolSize;
    private int keepAliveTime;
    private TimeUnit timeUnit;
    private BlockingQueue<Runnable> workQueue;
    private String threadPrefix;
    private boolean makeDaemon;
    private RejectedExecutionHandler rejectedExecutionHandler;

    /**
     * one pool one handler for all pass-ctx
     */
    private ThreadLocalHandler<CTX> threadLocalHandler;

    private ContextPassingThreadPool() {
    }

    public ThreadPoolExecutor createThreadPool() {
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                this.corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                //保活时间是毫秒时间！
                timeUnit,
                workQueue,
                new NamedPoolThreadFactory(threadPrefix, makeDaemon),
                rejectedExecutionHandler
        ) {
            @Override
            public Future<?> submit(Runnable task) {
                return super.submit(new ContextPassingRunnable(task));
            }

            @Override
            public void execute(Runnable runnable) {
                super.execute(new ContextPassingRunnable(runnable));
            }
        };
        /**
         * just start all core threads for lower latency when request incoming.
         */
        threadPoolExecutor.prestartAllCoreThreads();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("shutdown this pool " + threadPoolExecutor);
                threadPoolExecutor.shutdown();
                try {
                    threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    System.err.println(e);
                    Thread.currentThread().interrupt();
                    threadPoolExecutor.shutdownNow();
                }
            }
        });
        return threadPoolExecutor;
    }

    public class ContextPassingRunnable implements Runnable {

        Runnable runnable;
        /**
         * mark main thread info
         */
        final Thread mainThread = Thread.currentThread();

        /**
         * keep a ctx info
         */
        private volatile CTX ctx;

        public ContextPassingRunnable(Runnable runnable) {
            this.runnable = runnable;
            if (threadLocalHandler != null) {
                ctx = threadLocalHandler.preInit();
            }
        }

        @Override
        public void run() {
            try {
                if (threadLocalHandler != null && ctx != null && mainThread != Thread.currentThread()) {
                    threadLocalHandler.passCtx(ctx);
                    ctx = null;
                }
                runnable.run();
            } finally {
                /**
                 * please refer to policy info with {@link CallerRunsPolicy}
                 */
                if (mainThread != Thread.currentThread()) {
                    if (threadLocalHandler != null) {
                        threadLocalHandler.clearCtx();
                    }
                }
            }
        }
    }

    public static final class NamedPoolThreadFactory implements ThreadFactory {
        private final AtomicInteger counter = new AtomicInteger(0);
        private final String prefix;
        private final boolean makeDaemons;
        private final ThreadGroup group;

        public NamedPoolThreadFactory(String prefix, boolean makeDaemons) {
            this.prefix = prefix;
            this.makeDaemons = makeDaemons;
            this.group = new ThreadGroup(Thread.currentThread().getThreadGroup(), prefix);
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(group, r, String.format(
                    "AsyncThreadPool-%s-%d",
                    prefix,
                    counter.incrementAndGet()
            ));
            thread.setDaemon(makeDaemons);
            if (thread.getPriority() != Thread.NORM_PRIORITY) {
                thread.setPriority(Thread.NORM_PRIORITY);
            }
            return thread;
        }
    }

    public static final class AsyncThreadPoolBuilder<CTX> {
        private int corePoolSize;
        private int maximumPoolSize;
        private int keepAliveTime;
        private TimeUnit timeUnit;
        private BlockingQueue<Runnable> workQueue;
        private String threadPrefix;
        private Boolean makeDaemon;
        private RejectedExecutionHandler rejectedExecutionHandler;
        private ThreadLocalHandler<CTX> threadLocalHandler;

        private AsyncThreadPoolBuilder() {
        }

        public static AsyncThreadPoolBuilder anAsyncThreadPool() {
            return new AsyncThreadPoolBuilder();
        }

        public AsyncThreadPoolBuilder corePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public AsyncThreadPoolBuilder maximumPoolSize(int maximumPoolSize) {
            this.maximumPoolSize = maximumPoolSize;
            return this;
        }

        public AsyncThreadPoolBuilder keepAliveTime(int keepAliveTime) {
            this.keepAliveTime = keepAliveTime;
            return this;
        }

        public AsyncThreadPoolBuilder timeUnit(TimeUnit timeUnit) {
            this.timeUnit = timeUnit;
            return this;
        }

        public AsyncThreadPoolBuilder workQueue(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
            return this;
        }

        public AsyncThreadPoolBuilder threadPrefix(String threadPrefix) {
            this.threadPrefix = threadPrefix;
            return this;
        }

        public AsyncThreadPoolBuilder makeDaemon(boolean makeDaemon) {
            this.makeDaemon = makeDaemon;
            return this;
        }

        /**
         * please use different policy for your actual scene.
         *
         * @param rejectedExecutionHandler
         * @return
         * @see ThreadPoolExecutor.DiscardPolicy
         * @see ThreadPoolExecutor.CallerRunsPolicy
         * @see ThreadPoolExecutor.DiscardOldestPolicy
         * @see ThreadPoolExecutor.AbortPolicy
         * @see
         */
        public AsyncThreadPoolBuilder rejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler) {
            this.rejectedExecutionHandler = rejectedExecutionHandler;
            return this;
        }

        public AsyncThreadPoolBuilder threadLocalHandler(ThreadLocalHandler<CTX> threadLocalHandler) {
            this.threadLocalHandler = threadLocalHandler;
            return this;
        }

        public ContextPassingThreadPool build() {
            ContextPassingThreadPool asyncThreadPool = new ContextPassingThreadPool();
            /**
             * default as CPU core size + 1
             */
            if (this.corePoolSize <= 0) {
                this.corePoolSize = Runtime.getRuntime().availableProcessors() + 1;
            }
            /**
             * default as 4 * CPU core size + 1
             */
            if (this.maximumPoolSize <= 0) {
                this.maximumPoolSize = 4 * Runtime.getRuntime().availableProcessors() + 1;
            }
            asyncThreadPool.corePoolSize = this.corePoolSize;
            asyncThreadPool.maximumPoolSize = this.maximumPoolSize;
            /**
             * default as a SynchronousQueue with non-fair access policy
             */
            if (this.workQueue == null) {
                this.workQueue = new SynchronousQueue<>();
            }
            asyncThreadPool.workQueue = this.workQueue;
            if (this.threadPrefix == null || this.threadPrefix.trim().length() == 0) {
                throw new IllegalArgumentException("please set a valid thread prefix");
            }
            asyncThreadPool.threadPrefix = this.threadPrefix;

            if (this.makeDaemon == null) {
                this.makeDaemon = true;
            }
            asyncThreadPool.makeDaemon = this.makeDaemon;

            if (this.keepAliveTime <= 0) {
                this.keepAliveTime = 60000;
            }
            asyncThreadPool.keepAliveTime = this.keepAliveTime;
            if (this.timeUnit == null) {
                this.timeUnit = TimeUnit.MICROSECONDS;
            }
            asyncThreadPool.timeUnit = this.timeUnit;
            /**
             * default policy for this thread pool
             */
            if (rejectedExecutionHandler == null) {
                rejectedExecutionHandler = new ThreadPoolExecutor.CallerRunsPolicy();
            }
            asyncThreadPool.rejectedExecutionHandler = this.rejectedExecutionHandler;
            asyncThreadPool.threadLocalHandler = threadLocalHandler;
            return asyncThreadPool;
        }
    }

    /**
     * execute sequence：
     * 1, caller thread ctx with: preInit()
     * 2, thread of pool with: passCtx()
     * 3, thread of pool with: clear()
     *
     * @see ThreadLocal
     */
    public interface ThreadLocalHandler<CTX> {

        /**
         * before actually execute a {@link Runnable}, call this method
         */
        public CTX preInit();

        /**
         * pass ctx to current thread which is same T as preInit.
         *
         * @param ctx
         */
        public void passCtx(CTX ctx);

        /**
         * after actually a {@link Runnable} exit, call this method.
         */
        public void clearCtx();
    }

    public static class ContextPassingThreadPoolTest {

        static ThreadLocal<String> threadLocal = new ThreadLocal<>();

        ThreadPoolExecutor asyncExecutor;

        public void init() {
            asyncExecutor = ContextPassingThreadPool.AsyncThreadPoolBuilder.anAsyncThreadPool()
                    .threadPrefix("test")
                    .threadLocalHandler(new ContextPassingThreadPool.ThreadLocalHandler<String>() {
                        @Override
                        public String preInit() {
                            String referer = threadLocal.get();
                            return referer;
                        }
                        @Override
                        public void passCtx(String ctx) {
                            threadLocal.set(ctx);
                        }
                        @Override
                        public void clearCtx() {
                            threadLocal.remove();
                        }
                    })
                    .build()
                    .createThreadPool();
        }

        public void testCtx() throws Exception {
            String actualValue = "test ctx with 0";
            threadLocal.set(actualValue);
            asyncExecutor.execute(() -> {
                ThreadLocal<String> refThreadLocal = ContextPassingThreadPoolTest.threadLocal;
                System.out.println("worker thread:" + Thread.currentThread().getName());
                System.out.println("worker thread with local value ==> " + threadLocal.get());
                System.out.println(actualValue == refThreadLocal.get());

            });
            Future<String> submit = asyncExecutor.submit(() -> {
                ThreadLocal<String> refThreadLocal = ContextPassingThreadPoolTest.threadLocal;
                System.out.println("worker thread:" + Thread.currentThread().getName());
                System.out.println("worker thread with local value ==> " + threadLocal.get());
                System.out.println(actualValue == refThreadLocal.get());
                return refThreadLocal.get();
            });
            System.out.println("submit result with thread:" + submit.get());
            Thread.sleep(3000);
            System.out.println("current thread:" + Thread.currentThread().getName());
            System.out.println("thread local value in main thread before ==> " + threadLocal.get());
            threadLocal.remove();
            System.out.println("thread local value in main thread after ==> " + threadLocal.get());
            Thread.sleep(3000);
        }
    }
}
