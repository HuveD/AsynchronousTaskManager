package kr.co.huve.asynchronoustaskmanager;

public interface OnErrorListener {
    void onError(String message, int taskId);
}
