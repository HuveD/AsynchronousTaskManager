package kr.co.huve.AsynchronousTaskManager.Type;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

@Retention(SOURCE)
@IntDef({TaskState.RUNNING, TaskState.SHUTDOWN, TaskState.READY, TaskState.STOP, TaskState.FINISH})
public @interface TaskState {
    int RUNNING = 0;
    int SHUTDOWN = 1;
    int READY = 2;
    int STOP = 3;
    int FINISH = 4;
}
