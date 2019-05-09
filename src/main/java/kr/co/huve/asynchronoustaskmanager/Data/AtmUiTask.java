package kr.co.huve.asynchronoustaskmanager.Data;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;

import kr.co.huve.asynchronoustaskmanager.Type.AtmState;

public class AtmUiTask extends AtmTask {
    /**
     * @param task   The runnable object for background run.
     * @param owner  The life cycle owner of sender.
     * @param taskId The unique identification of the task.
     */
    public AtmUiTask(@NonNull Runnable task, @NonNull LifecycleOwner owner, int taskId) {
        super(task, taskId);
        LifecycleObserver observer = new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            public void onResume() {
                if (getTaskAtmState() == AtmState.STOP) {
                    changeState(AtmState.READY);
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            public void onPause() {
                changeState(AtmState.STOP);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                changeState(AtmState.SHUTDOWN);
            }
        };
        owner.getLifecycle().addObserver(observer);
    }
}
