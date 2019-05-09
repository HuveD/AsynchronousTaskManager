package kr.co.huve.asynchronoustaskmanager.Util;

import android.support.annotation.NonNull;

import kr.co.huve.asynchronoustaskmanager.Data.AtmTask;
import kr.co.huve.asynchronoustaskmanager.Data.AtmUiTask;
import kr.co.huve.asynchronoustaskmanager.Type.AtmState;

class AtmThread extends Thread {
    private boolean isTaskFinished = false;
    private AtmTask atmTask;

    AtmThread(@NonNull AtmTask atmTask) {
        super(atmTask.getTask());
        this.atmTask = atmTask;
    }

    @Override
    public synchronized void start() {
        if (atmTask.getTaskAtmState() == AtmState.READY && !isTaskFinished) {
            atmTask.changeState(AtmState.RUNNING);
            super.start();
        }
    }

    @Override
    public void run() {
        if (atmTask.getTaskAtmState() == AtmState.RUNNING) {
            super.run();
            isTaskFinished = true;
        }
        // If life cycle of activity is 'Pause', to invoke callback method is postponed.
        while (AtmState.STOP == atmTask.getTaskAtmState() || AtmState.SHUTDOWN == atmTask.getTaskAtmState()) {
            try {
                if (atmTask.getTaskAtmState() == AtmState.SHUTDOWN) {
                    // If life cycle of activity is 'Destroy' after 'Stop', this thread stop and doesn't invoke callback method.
                    interrupt();
                } else {
                    // Delay checking thread state.
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                // This thread was interrupted.
                break;
            }
        }
        if (isTaskFinished) {
            // Notify task which was finished
            if (atmTask instanceof AtmUiTask) {
                AsynchronousTaskManager.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        atmTask.notifyTaskComplete();
                    }
                });
            } else {
                atmTask.notifyTaskComplete();
            }
            // Remove task which was finished
            AtmThreadPool.getInstance().removeTask(this);
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        if (atmTask.getTaskAtmState() != AtmState.SHUTDOWN) {
            atmTask.changeState(AtmState.SHUTDOWN);
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
