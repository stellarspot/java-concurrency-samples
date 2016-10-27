package nonatomicoperation;

import java.util.concurrent.CountDownLatch;

/*
 * @test
 * @summary Nonatomic 64 bit operation
 * @run main nonatomicoperation.NonAtomic64BitOperation
 */
public class NonAtomic64BitOperation {

    private static final int N = 10000;
    private static final long VALUE_1 = 0x11111111_22222222L;
    private static final long VALUE_2 = 0x33333333_44444444L;
    private static long value = VALUE_2;
    private static volatile boolean failed = false;
    private static volatile boolean interrupted = false;
    private static volatile String message;

    public static void main(String[] args) throws Exception {

        final CountDownLatch latch1 = new CountDownLatch(1);
        final CountDownLatch latch2 = new CountDownLatch(2);

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch1.await();

                    boolean flag = true;
                    for (int i = 0; i < N; i++) {
                        if (failed || interrupted) {
                            return;
                        }
                        value = flag ? VALUE_1 : VALUE_2;
                        flag = !flag;
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
                        if (interrupted) {
                            return;
                        }
                        long readValue = value;
                        if (readValue != VALUE_1 && readValue != VALUE_2) {
                            message = String.format("Read value: 0x%x does not"
                                    + " equal to 0x%x or 0x%x\n",
                                    readValue, VALUE_1, VALUE_2);
                            failed = true;
                            return;
                        }
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

        if (failed) {
            throw new RuntimeException(message);
        }
    }
}
