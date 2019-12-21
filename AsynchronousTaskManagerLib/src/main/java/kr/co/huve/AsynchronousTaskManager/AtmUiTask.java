package kr.co.huve.AsynchronousTaskManager;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import kr.co.huve.AsynchronousTaskManager.Type.TaskState;

public class AtmUiTask extends AtmTask {
    /**
     * @param task   The runnable object for background run.
     * @param owner  The life cycle owner of sender.
     * @param taskId The unique identification of the task.
     */
    AtmUiTask(@NonNull Runnable task, @NonNull LifecycleOwner owner, int taskId) {
        super(task, taskId);
        LifecycleObserver observer = new LifecycleObserver() {
            @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
            public void onResume() {
                if (getCurrentState() == TaskState.STOP) {
                    changeState(TaskState.RUNNING);
                }
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            public void onPause() {
                changeState(TaskState.STOP);
            }

            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            public void onDestroy() {
                changeState(TaskState.SHUTDOWN);
            }
        };
        uiHandler = new Handler(Looper.getMainLooper());
        owner.getLifecycle().addObserver(observer);
    }
}
