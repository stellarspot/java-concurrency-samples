package visibility;

import java.util.concurrent.TimeUnit;

/*
 * @test
 * @summary Use non volatile flag
 * @run main/othervm -server visibility.NonVolatileFlag
 */
public class NonVolatileFlag {

    private static boolean done = false; // use volatile keyword!
    private static volatile boolean finished = false;
    private static volatile boolean interrupted = false;

    public static void main(String[] args) {

        new Thread(() -> {

            while (!done) {
                if (interrupted) {
                    return;
                }
                // do something
                System.out.println("do something!");
            }
            finished = true;
            System.out.println("done: " + done);

        }).start();

        new Thread(() -> {
            done = true;
        }).start();

        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            interrupted = true;
            throw new RuntimeException("Process has been interrupted!", e);
        }

        interrupted = true;

        if (!finished) {
            throw new RuntimeException("Thread is not finished!");
        }
    }
}