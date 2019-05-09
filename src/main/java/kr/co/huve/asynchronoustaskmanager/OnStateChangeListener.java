package kr.co.huve.asynchronoustaskmanager;

import kr.co.huve.asynchronoustaskmanager.Type.AtmState;

/**
 * Called when state of ATM was changed.
 */
public interface OnStateChangeListener {
    void onTaskStateChange(AtmState atmState, int taskId);
}
