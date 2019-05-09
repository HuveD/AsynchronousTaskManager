package kr.co.huve.asynchronoustaskmanager.Util;

import android.arch.lifecycle.LifecycleOwner;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import kr.co.huve.asynchronoustaskmanager.Data.AtmTask;
import kr.co.huve.asynchronoustaskmanager.Data.AtmUiTask;
import kr.co.huve.asynchronoustaskmanager.OnErrorListener;
import kr.co.huve.asynchronoustaskmanager.OnStateChangeListener;
import kr.co.huve.asynchronoustaskmanager.OnTaskEndListener;
import kr.co.huve.asynchronoustaskmanager.Type.AtmState;

/**
 * AsynchronousTaskManager called ATM by omitting is background task helper.
 * ATM is designed for background work that is free from the activity.
 */
public class AsynchronousTaskManager {
    // Event Listener
    private ArrayList <OnStateChangeListener> onStateChangeListener;
    private ArrayList <OnTaskEndListener> onTaskEndListener;
    private ArrayList <OnErrorListener> onErrorListener;

    private AsynchronousTaskManager() {
        // Private constructor
    }

    //region Common method field

    /**
     * Activate specific task
     */
    private void activateTask(@NonNull AtmTask taskItem) {
        AtmThread thread = new AtmThread(taskItem);
        AtmThreadPool.getInstance().addTask(thread);
    }

    /**
     * Deactivate specific task
     */
    public void deactivateTask(int taskId) {
        AtmThread thread = AtmThreadPool.getInstance().getAtmThread(taskId);
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
    }

    /**
     * Create AtmUiTask object
     */
    public void createUiTask(@NonNull Runnable runnable, @NonNull LifecycleOwner owner, int taskId) {
        AtmUiTask item = new AtmUiTask(runnable, owner, taskId);
        activateTask(item);
    }

    /**
     * Create AtmTask object
     */
    public void createTask(@NonNull Runnable runnable, int taskId) {
        AtmTask item = new AtmTask(runnable, taskId);
        activateTask(item);
    }

    /**
     * Check specific task is running.
     */
    public synchronized boolean isTaskRunning(int taskId) {
        AtmThread thread = AtmThreadPool.getInstance().getAtmThread(taskId);
        if (thread != null) {
            return AtmState.SHUTDOWN != thread.getTaskItem().getTaskAtmState() && AtmState.FINISH != thread.getTaskItem().getTaskAtmState();
        } else {
            return false;
        }
    }

    /**
     * Clear all listener item.
     */
    void clearAtmListener() {
        if (onStateChangeListener != null) {
            onStateChangeListener.clear();
        }
        if (onTaskEndListener != null) {
            onTaskEndListener.clear();
        }
        if (onErrorListener != null) {
            onErrorListener.clear();
        }
    }

    //endregion Common method field

    //region Getter & Setter field

    public void addOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        if (!this.onStateChangeListener.contains(onStateChangeListener)) {
            this.onStateChangeListener.add(onStateChangeListener);
        }
    }

    public void addOnTaskEndListener(OnTaskEndListener onTaskEndListener) {
        if (!this.onTaskEndListener.contains(onTaskEndListener)) {
            this.onTaskEndListener.add(onTaskEndListener);
        }
    }

    public void addOnErrorListener(OnErrorListener onErrorListener) {
        if (!this.onErrorListener.contains(onErrorListener)) {
            this.onErrorListener.add(onErrorListener);
        }
    }

    public ArrayList <OnStateChangeListener> getOnStateChangeListener() {
        if (onStateChangeListener == null) {
            onStateChangeListener = new ArrayList <>();
        }
        return onStateChangeListener;
    }

    public ArrayList <OnTaskEndListener> getOnTaskEndListener() {
        if (onTaskEndListener == null) {
            onTaskEndListener = new ArrayList <>();
        }
        return onTaskEndListener;
    }

    public ArrayList <OnErrorListener> getOnErrorListener() {
        if (onErrorListener == null) {
            onErrorListener = new ArrayList <>();
        }
        return onErrorListener;
    }

    //endregion Getter & Setter field

    //region Inner class field

    /**
     * ATM Lazy holder class
     */
    private static class ManagerHolder {
        private static final AsynchronousTaskManager INSTANCE = new AsynchronousTaskManager();
    }

    /**
     * Ui handler Lazy holder class
     */
    private static class UiHandlerHolder {
        private static final Handler INSTANCE = new Handler(Looper.getMainLooper());
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

    /**
     * Get the singleton instance of the ui handler
     *
     * @return ui handler for {@link AtmUiTask}
     */
    public static Handler getUiHandler() {
        return UiHandlerHolder.INSTANCE;
    }

    //endregion Singleton initializer field
}
