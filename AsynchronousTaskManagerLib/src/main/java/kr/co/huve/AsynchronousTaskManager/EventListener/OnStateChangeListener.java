package kr.co.huve.AsynchronousTaskManager.EventListener;

import kr.co.huve.AsynchronousTaskManager.Type.TaskState;

/**
 * Called when state of ATM was changed.
 */
public interface OnStateChangeListener {
    /**
     * @param state  State of the current task.
     *               int RUNNING = 0;
     *               int SHUTDOWN = 1;
     *               int READY = 2;
     *               int STOP = 3;
     *               int FINISH = 4;
     * @param taskId Id of the current task.
     */
    void onTaskStateChange(@TaskState int state, int taskId);
}
