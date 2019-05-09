package kr.co.huve.asynchronoustaskmanager.Data;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import kr.co.huve.asynchronoustaskmanager.OnErrorListener;
import kr.co.huve.asynchronoustaskmanager.OnStateChangeListener;
import kr.co.huve.asynchronoustaskmanager.OnTaskEndListener;
import kr.co.huve.asynchronoustaskmanager.Type.AtmState;
import kr.co.huve.asynchronoustaskmanager.Util.AsynchronousTaskManager;

public class AtmTask {
    private String TAG = "AsynchronousTaskManager";
    // The state of task.
    private AtmState taskAtmState;
    // The runnable object for background run.
    private Runnable task;
    // The unique identification of the task.
    private int taskId;

    /**
     * @param task   The runnable object for background run.
     * @param taskId The unique identification of the task.
     */
    public AtmTask(@NonNull Runnable task, int taskId) {
        this.taskId = taskId;
        this.task = task;
        changeState(AtmState.READY);
    }

    /**
     * Change atmState of the task.
     *
     * @param atmState {@link AtmState}
     */
    public void changeState(AtmState atmState) {
        ArrayList <OnStateChangeListener> onStateChangeListeners = AsynchronousTaskManager.getInstance().getOnStateChangeListener();
        for (OnStateChangeListener listener : onStateChangeListeners) {
            listener.onTaskStateChange(atmState, taskId);
        }
        taskAtmState = atmState;
    }

    /**
     * Notify Atm task done.
     */
    public void notifyTaskComplete() {
        try {
            ArrayList <OnTaskEndListener> onTaskEndListeners = AsynchronousTaskManager.getInstance().getOnTaskEndListener();
            for (OnTaskEndListener listener : onTaskEndListeners) {
                listener.onTaskEnd(taskId);
            }
            changeState(AtmState.FINISH);
        } catch (RuntimeException e) {
            if (e.getClass().getSimpleName().equals("CalledFromWrongThreadException")) {
                ArrayList <OnErrorListener> onErrorListeners = AsynchronousTaskManager.getInstance().getOnErrorListener();
                for (final OnErrorListener listener : onErrorListeners) {
                    AsynchronousTaskManager.getUiHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError("'createTask()' is unavailable to access to ui controller. Use 'createUiTask()'.", taskId);
                        }
                    });
                }
                changeState(AtmState.SHUTDOWN);
            } else {
                throw e;
            }
        }
    }

    /**
     * Get task id
     */
    public int getTaskId() {
        return taskId;
    }

    public Runnable getTask() {
        return task;
    }

    /**
     * Get task state.
     *
     * @return {@link AtmState}
     */
    public AtmState getTaskAtmState() {
        return taskAtmState;
    }
}