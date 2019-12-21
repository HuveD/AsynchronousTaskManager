package kr.co.huve.AsynchronousTaskManager.EventListener;

public interface OnTaskEndListener {
    /**
     * This method will invoke when the task was finished.
     * But if you add the {@link OnStateChangeListener}, this method will be ignore.
     * Because the {@link OnStateChangeListener} also notify the state whether the task was finished to you.
     *
     * @param taskId
     */
    void onTaskEnd(int taskId);
}
