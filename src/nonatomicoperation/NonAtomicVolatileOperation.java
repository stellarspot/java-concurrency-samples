package nonatomicoperation;

import java.util.concurrent.CountDownLatch;

/*
 * @test
 * @summary Nonatomic Volatile operation
 * @run main nonatomicoperation.NonAtomicVolatileOperation
 */
public class NonAtomicVolatileOperation {

    private static final int N = 10000;
    private static volatile int id = 0;
    private static volatile boolean interrupted = false;

    public static void main(String[] args) throws Exception {

        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(2);

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    latch1.await();

                    for (int i = 0; i < N; i++) {
                        id++;
                    }
                } catch (InterruptedException e) {
                    interrupted = true;
                } finally {
                    latch2.countDown();
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    latch1.await();

                    for (int i = 0; i < N; i++) {
                        id--;
                    }
                } catch (InterruptedException e) {
                    interrupted = true;
                } finally {
                    latch2.countDown();
                }
            }
        });
        thread1.start();
        thread2.start();
        latch1.countDown();
        latch2.await();

        if (interrupted) {
            throw new InterruptedException("One of threads has been interrupted!");
        }

        if (id != 0) {
            throw new RuntimeException(
                    String.format("Volatile field id is %d instead of zero!", id));
        }
    }
}
