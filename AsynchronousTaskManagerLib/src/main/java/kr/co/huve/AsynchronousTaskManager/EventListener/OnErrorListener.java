package kr.co.huve.AsynchronousTaskManager.EventListener;

public interface OnErrorListener {
    void onError(String message, int taskId);
}
