package com.shepherd.mallproduct.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.Random;
import java.util.concurrent.*;

/**
 * @author fjzheng
 * @version 1.0
 * @date 2021/6/24 16:07
 */
public class ThreadTest {

    public static ExecutorService executor = Executors.newFixedThreadPool(10);
    public static ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
            .setNameFormat("test-pool-%d").build();

    public static ExecutorService fixedThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors()*2,
            Runtime.getRuntime().availableProcessors() * 40,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(Runtime.getRuntime().availableProcessors() * 20),
            namedThreadFactory);

    public static void main(String[] args) throws Exception {
        // System.out.println("main......start.....");
        // Thread thread = new Thread01();
        // thread.start();
        // System.out.println("main......end.....");

        // Runnable01 runnable01 = new Runnable01();
        // new Thread(runnable01).start();

        // FutureTask<Integer> futureTask = new FutureTask<>(new Callable01());
        // new Thread(futureTask).start();
        // System.out.println(futureTask.get());

        // service.execute(new Runnable01());
        // Future<Integer> submit = service.submit(new Callable01());
        // submit.get();

        System.out.println("main......start.....");
        System.out.println("主线程线程：" + Thread.currentThread().getName() + ": "+ Thread.currentThread().getId());
//        testRunAsync();
//        testSupplyAsync();
//        testWhenCompleteAndExceptionally();
//        testHandle();
//        testThenApplyAsync();
        testNotExecute();
        thenCombine();
        System.out.println("main......end.....");

    }


    /**
     * 测试方法CompletableFuture.runAsync：无返回值,
     */
    private static void testRunAsync() {
        CompletableFuture.runAsync(() ->{
            System.out.println("<======当前线程:" + Thread.currentThread().getName() + "=====线程id： " + Thread.currentThread().getId());
            System.out.println("supplyAsync 是否为守护线程 " + Thread.currentThread().isDaemon());
            int result = 10/2;
            // 这里并不会报错
            String s = String.valueOf(null);
            System.out.println("计算结果为："+ result);
        }, fixedThreadPool);
    }

    /**
     * 测试方法CompletableFuture.supplyAsync：有返回值
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private static void testSupplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("<======当前线程:" + Thread.currentThread().getName() + "=====线程id： " + Thread.currentThread().getId());
            int result = 10 / 2;
            return result;
        }, fixedThreadPool);
        Integer res = future.get();
        System.out.println("返回结果值为："+res);
    }

    /**
     * 测试whenComplete和exceptionally: 异步方法执行完的处理
     */
    private static void testWhenCompleteAndExceptionally() throws ExecutionException, InterruptedException {
         CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
             System.out.println("<======当前线程:" + Thread.currentThread().getName() + "=====线程id： " + Thread.currentThread().getId());
             Integer num = 10;
             int i = num / 2;
             String s = String.valueOf(null);
             System.out.println("运行结果：" + i);
             return i;
         }, executor).whenComplete((res,exception) -> {
             //虽然能得到异常信息，但是没法修改返回数据
             System.out.println("<=====异步任务成功完成了=====结果是：" + res + "=======异常是：" + exception);
         }).exceptionally(throwable -> {
             //可以感知异常，同时返回默认值
             System.out.println("<=====异步任务成功发生异常了======"+throwable);
             return 10;
         });
        Integer result = future.get();
        System.out.println("<=====最终返回结果result=" + result + "======>");

    }

    /**
     * 测试handle方法：它是whenComplete和exceptionally的结合
     */
    private static void testHandle() {
         CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
             System.out.println("<======当前线程:" + Thread.currentThread().getName() + "=====线程id： " + Thread.currentThread().getId());
             int i = 10 / 2;
             System.out.println("运行结果：" + i);
             return i;
         }, executor).handle((result,thr) -> {
             if (result != null) {
                 return result * 2;
             }
             if (thr != null) {
                 System.out.println("异步任务成功完成了...结果是：" + result + "异常是：" + thr);
                 return 0;
             }
             return 0;
         });
    }


    /**
     * 线程串行化
     * 1、thenRunL：不能获取上一步的执行结果
     * 2、thenAcceptAsync：能接受上一步结果，但是无返回值
     * 3、thenApplyAsync：能接受上一步结果，有返回值
     *
     */
    private static void testThenApplyAsync() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("<======当前线程:" + Thread.currentThread().getName() + "=====线程id： " + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return i;
        }, executor);
        CompletableFuture<String> future2 = future1.thenApplyAsync(res -> {
            System.out.println("======任务2启动了..." + res*20);
            return "Hello" + res;
        }, executor);

        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> {
            System.out.println("======任务3执行了");
        }, executor);

        CompletableFuture.allOf(future1, future2, future3).get();
        System.out.println("=======测试结束");

    }

    private static void testNotExecute() {
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("<======当前线程:" + Thread.currentThread().getName() + "=====线程id： " + Thread.currentThread().getId());
            System.out.println("supplyAsync 是否为守护线程 " + Thread.currentThread().isDaemon());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("return之前的打印");
            return i;
        });
    }

    private static void thenCombine() throws Exception {
        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> "hello1", fixedThreadPool);
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> "hello2", fixedThreadPool);
        CompletableFuture<String> result = future1.thenCombine(future2, (t, u) -> t+" "+u);
        System.out.println(result.get());
    }

    private static void thenAcceptBoth() throws Exception {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f1="+t);
            return t;
        },fixedThreadPool);

        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f2="+t);
            return t;
        },fixedThreadPool);
    }

    private static void applyToEither() throws Exception {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f1="+t);
            return t;
        },fixedThreadPool);
        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f2="+t);
            return t;
        },fixedThreadPool);

        CompletableFuture<Integer> result = f1.applyToEither(f2, t -> {
            System.out.println("applyEither:"+t);
            return t * 2;
        });

    }

    private static void acceptEither() throws Exception {
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f1="+t);
            return t;
        },fixedThreadPool);
        CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> {
            int t = new Random().nextInt(3);
            try {
                TimeUnit.SECONDS.sleep(t);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("f2="+t);
            return t;
        },fixedThreadPool);

        CompletableFuture<Void> result = f1.acceptEither(f2, t -> {
            System.out.println("acceptEither:"+t);
        });

    }











    private static void threadPool() {

        ExecutorService threadPool = new ThreadPoolExecutor(
                200,
                10,
                10L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<Runnable>(10000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );

        //定时任务的线程池
        ExecutorService service = Executors.newScheduledThreadPool(2);
    }


    public static class Thread01 extends Thread {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }


    public static class Runnable01 implements Runnable {
        @Override
        public void run() {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
        }
    }


    public static class Callable01 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("当前线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("运行结果：" + i);
            return i;
        }
    }

}

