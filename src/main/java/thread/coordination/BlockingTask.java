package thread.coordination;

public class BlockingTask  implements Runnable{
    public static void main(String[] args) {
        Thread thread = new Thread(new BlockingTask());
        thread.start();
        thread.interrupt();
    }

    @Override
    public void run(){
        try {
            Thread.sleep(500000);
        } catch (InterruptedException e) {
            System.out.println("Exiting blocking thread");
        }
    }

}
