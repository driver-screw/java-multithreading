package thread.creation;

import java.util.ArrayList;
import java.util.List;

public class CreateThread {
    public static void main(String[] args) {
        List<Runnable> taskList = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            int finalI = i;
            taskList.add(() -> System.out.println("Task "+ finalI));
        }

        MultiExecutor multiExecutor = new MultiExecutor(taskList);
        multiExecutor.executeAll();
    }
}
