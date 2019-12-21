package kr.co.huve.AsynchronousTaskManager;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import kr.co.huve.AsynchronousTaskManager.Type.TaskState;

class AtmThread extends Thread {
    private boolean isTaskFinished = false;
    private final AtmTask atmTask;

    AtmThread(@NonNull AtmTask atmTask) {
        super(atmTask.getTask());
        this.atmTask = atmTask;
    }

    @Override
    public synchronized void start() {
        if (atmTask.getCurrentState() == TaskState.READY && !isTaskFinished) {
            atmTask.changeState(TaskState.RUNNING);
            super.start();
        }
    }

    @Override
    public void run() {
        if (atmTask.getCurrentState() == TaskState.RUNNING) {
            super.run();
            isTaskFinished = true;
        }
        // If life cycle of activity is 'Pause', postpone invoking the callback method
        while (TaskState.STOP == atmTask.getCurrentState() || TaskState.SHUTDOWN == atmTask.getCurrentState()) {
            try {
                if (atmTask.getCurrentState() == TaskState.SHUTDOWN) {
                    // If the life cycle of activity is 'Destroy' after 'Stop', this thread stop and doesn't invoke the callback method.
                    interrupt();
                    break;
                } else {
                    // Delay checking thread state.
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                // This thread was interrupted.
                break;
            }
        }
        if (atmTask.getCurrentState() != TaskState.SHUTDOWN && isTaskFinished) {
            // Remove task which was finished.
            AtmThreadPool.getInstance().removeTask(this);

            // Notify task which was finished.
            if (atmTask instanceof AtmUiTask) {
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        atmTask.notifyTaskComplete();
                    }
                });
            } else {
                atmTask.notifyTaskComplete();
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (atmTask.getCurrentState() != TaskState.SHUTDOWN) {
            atmTask.changeState(TaskState.SHUTDOWN);
        }
        // Remove task which was interrupted from AtmThread Pool.
        AtmThreadPool.getInstance().removeTask(this);
    }

    /**
     * Get {@link AtmTask} from {@link AtmThread}.
     *
     * @return {@link AtmTask}
     */
    AtmTask getTaskItem() {
        return atmTask;
    }
}
