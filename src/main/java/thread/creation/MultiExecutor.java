package thread.creation;

import java.util.List;

public class MultiExecutor {
    private List<Runnable> taskList;

    public MultiExecutor(List<Runnable> taskList) {
        this.taskList = taskList;
    }

    public void executeAll() {
        for (Runnable r : taskList) {
            Thread t = new Thread(r);
            t.start();
        }
    }
}
