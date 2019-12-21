package kr.co.huve.AsynchronousTaskManager;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;

import kr.co.huve.AsynchronousTaskManager.Type.TaskState;

/**
 * AsynchronousTaskManager called ATM by omitting is background task helper.
 * ATM is designed for background work that is free from the activity.
 */
public class AsynchronousTaskManager {
    private String TAG = "AsynchronousTaskManager";

    private AsynchronousTaskManager() {
        // Private constructor
    }

    //region Common method field

    /**
     * Create the {@link AtmUiTask}.
     *
     * @param runnable The {@link Runnable} object that you want to run in the background thread.
     * @param owner    The {@link LifecycleOwner} of the 'Activity' that includes AtmUiTask
     * @param taskId   The unique id of the task.
     * @return The new {@link AtmUiTask} object.
     */
    public AtmUiTask createUiTask(@NonNull Runnable runnable, @NonNull LifecycleOwner owner, int taskId) {
        return new AtmUiTask(runnable, owner, taskId);
    }

    /**
     * Create the {@link AtmTask}.
     *
     * @param runnable The {@link Runnable} object that you want to run in the background thread.
     * @param taskId   The unique id of the task.
     * @return The new {@link AtmTask} object.
     */
    public AtmTask createTask(@NonNull Runnable runnable, int taskId) {
        return new AtmTask(runnable, taskId);
    }

    /**
     * Check specific task is running.
     */
    public synchronized boolean isTaskRunning(int taskId) {
        AtmThread thread = AtmThreadPool.getInstance().getAtmThread(taskId);
        if (thread != null) {
            return TaskState.SHUTDOWN != thread.getTaskItem().getCurrentState() && TaskState.FINISH != thread.getTaskItem().getCurrentState();
        } else {
            return false;
        }
    }

    /**
     * Get the {@link AtmTask} from the unique id.
     */
    public synchronized AtmTask getTask(int taskId) {
        AtmThread thread = AtmThreadPool.getInstance().getAtmThread(taskId);
        if (thread != null) {
            return thread.getTaskItem();
        } else {
            return null;
        }
    }

    //endregion Common method field

    //region Inner class field

    /**
     * ATM Lazy holder class
     */
    private static class ManagerHolder {
        private static final AsynchronousTaskManager INSTANCE = new AsynchronousTaskManager();
    }

    //endregion Inner class field

    //region Singleton initializer field

    /**
     * Get the singleton instance of the Atm class
     *
     * @return The instance of {@link AsynchronousTaskManager}
     */
    public static AsynchronousTaskManager getInstance() {
        return ManagerHolder.INSTANCE;
    }

    //endregion Singleton initializer field
}
