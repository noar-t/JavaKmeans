import javax.imageio.IIOException;
import java.io.IOException;

public class Main {
    public static void main(String args[]) throws IOException {
        KMeans kMeans = new KMeans("input/input.txt", 4, 2);
    }

    static void threadsTest() {
        Thread t = Thread.currentThread();

        System.out.println("Thread name:" + t.getName());

        ThreadDemo td = new ThreadDemo();
        td.start();

        try {
            td.join();
        } catch (Exception e) {
            System.out.println(e);
        }


    }

    static class ThreadDemo extends Thread {
        public void run() {
            System.out.println("Im a thread!");
            System.out.println("Thread name:" + this.getName());
        }
    }
}
