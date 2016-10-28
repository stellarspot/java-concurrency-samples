package deadlock;

import java.util.concurrent.TimeUnit;

/*
 * @test
 * @summary Two threads deadlock
 * @run main deadlock.Deadlock
 */
public class Deadlock {

    private static final Object LOCK1 = new Object();
    private static final Object LOCK2 = new Object();
    private static volatile boolean thread1PassLock1 = false;
    private static volatile boolean thread1PassLock2 = false;
    private static volatile boolean thread2PassLock1 = false;
    private static volatile boolean thread2PassLock2 = false;

    public static void main(String[] args) throws InterruptedException {

        new Thread(() -> {
            System.out.println("Thread 1: try to acquery lock1...");
            synchronized (LOCK1) {
                thread1PassLock1 = true;
                System.out.println("Thread 1: wait for the thread 2...");
                while (!thread2PassLock2);
                System.out.println("Thread 1: try to acquery lock2...");
                synchronized (LOCK2) {
                    thread1PassLock2 = true;
                    System.out.println("Thread 1: passed!");
                }
            }

        }).start();

        new Thread(() -> {
            System.out.println("Thread 2: try to acquery lock2...");
            synchronized (LOCK2) {
                thread2PassLock2 = true;
                System.out.println("Thread 2: wait for the thread 1...");
                while (!thread1PassLock1);
                System.out.println("Thread 2: try to acquery lock1...");
                synchronized (LOCK1) {
                    thread2PassLock1 = true;
                    System.out.println("Thread 2: passed!");
                }
            }
        }).start();

        TimeUnit.MICROSECONDS.sleep(300);

        if (thread1PassLock1 && !thread1PassLock2) {
            throw new RuntimeException("Deadlock is detected for the Thread 1");
        }

        if (thread2PassLock2 && !thread2PassLock1) {
            throw new RuntimeException("Deadlock is detected for the Thread 2");
        }
    }
}
