package kr.co.huve.AsynchronousTaskManager;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import kr.co.huve.AsynchronousTaskManager.EventListener.OnErrorListener;
import kr.co.huve.AsynchronousTaskManager.EventListener.OnStateChangeListener;
import kr.co.huve.AsynchronousTaskManager.EventListener.OnTaskEndListener;
import kr.co.huve.AsynchronousTaskManager.Type.TaskState;

public class AtmTask {
    private String TAG = "AsynchronousTaskManager";

    // The state whether the task was finished.
    protected boolean isTaskFinished = false;
    // The state of current task.
    private int taskAtmState;
    // The runnable object for background run.
    private final Runnable task;
    // The unique identification of the task.
    private final int taskId;

    // Handler to send 'Runnable' object to the main looper.
    protected Handler uiHandler;

    // Event listener
    private OnStateChangeListener onStateChangeListener;
    private OnTaskEndListener onTaskEndListener;
    private OnErrorListener onErrorListener;

    /**
     * @param task   The runnable object for background run.
     * @param taskId The unique identification of the task.
     */
    AtmTask(@NonNull Runnable task, int taskId) {
        this.taskId = taskId;
        this.task = task;
        changeState(TaskState.READY);
    }

    //region Common method field

    /**
     * Change state of the current task.
     *
     * @param currentTaskState The state of current task.
     */
    void changeState(@TaskState final int currentTaskState) {
        if (isTaskFinished) {
            // The task is completely terminated
            return;
        } else if (TaskState.FINISH == currentTaskState || TaskState.SHUTDOWN == currentTaskState) {
            isTaskFinished = true;
        }

        // change state
        if (onStateChangeListener != null) {
            if (this instanceof AtmUiTask) {
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onStateChangeListener.onTaskStateChange(currentTaskState, taskId);
                    }
                });
            } else {
                onStateChangeListener.onTaskStateChange(currentTaskState, taskId);
            }
        }
        taskAtmState = currentTaskState;
    }

    /**
     * Notify Atm task done.
     */
    void notifyTaskComplete() {
        try {
            if (onStateChangeListener == null && onTaskEndListener != null) {
                if (this instanceof AtmUiTask) {
                    uiHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onTaskEndListener.onTaskEnd(taskId);
                        }
                    });
                } else {
                    onTaskEndListener.onTaskEnd(taskId);
                }
            }
            changeState(TaskState.FINISH);
        } catch (RuntimeException e) {
            if (uiHandler == null) {
                uiHandler = new Handler(Looper.getMainLooper());
            }

            final String errorMessage = e.toString();
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (onErrorListener != null) {
                        onErrorListener.onError(errorMessage, taskId);
                    }
                }
            });
            Log.e(TAG, errorMessage);
            changeState(TaskState.SHUTDOWN);
        }
    }

    /**
     * Activate specific task
     */
    public void active() {
        AtmThread existThread = AtmThreadPool.getInstance().getAtmThread(taskId);
        if (existThread != null && !existThread.isInterrupted()) {
            existThread.interrupt();
        }
        if (TaskState.READY == getCurrentState()) {
            AtmThread thread = new AtmThread(this);
            AtmThreadPool.getInstance().addTask(thread);
        } else if (onErrorListener != null) {
            Log.e(TAG, "Create a new instance. The task that has been executed once cannot execute again.");
            onErrorListener.onError("Create a new instance. The task that has been executed once cannot execute again.", taskId);
        }
    }

    /**
     * Deactivate specific task
     */
    public void deactivate() {
        AtmThread thread = AtmThreadPool.getInstance().getAtmThread(taskId);
        if (thread != null && !thread.isInterrupted()) {
            thread.interrupt();
        }
    }

    /**
     * Check specific task is running.
     */
    public boolean isTaskRunning() {
        return !isTaskFinished && TaskState.SHUTDOWN != getCurrentState() && TaskState.FINISH != getCurrentState();
    }
    //endregion Common method field

    //region Getter & Setter field

    Runnable getTask() {
        return task;
    }


    public int getTaskId() {
        return taskId;
    }

    @TaskState
    public int getCurrentState() {
        return taskAtmState;
    }

    public OnStateChangeListener getOnStateChangeListener() {
        return onStateChangeListener;
    }

    public AtmTask setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.onStateChangeListener = onStateChangeListener;
        return this;
    }

    public OnTaskEndListener getOnTaskEndListener() {
        return onTaskEndListener;
    }

    public AtmTask setOnTaskEndListener(OnTaskEndListener onTaskEndListener) {
        this.onTaskEndListener = onTaskEndListener;
        return this;
    }

    public OnErrorListener getOnErrorListener() {
        return onErrorListener;
    }

    public AtmTask setOnErrorListener(OnErrorListener onErrorListener) {
        this.onErrorListener = onErrorListener;
        return this;
    }
    //endregion Getter & Setter field
}