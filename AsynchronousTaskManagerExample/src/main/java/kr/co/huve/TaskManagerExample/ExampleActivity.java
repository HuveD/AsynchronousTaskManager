package kr.co.huve.TaskManagerExample;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.huve.AsynchronousTaskManager.AsynchronousTaskManager;
import kr.co.huve.AsynchronousTaskManager.AtmTask;
import kr.co.huve.AsynchronousTaskManager.AtmUiTask;
import kr.co.huve.AsynchronousTaskManager.EventListener.OnErrorListener;
import kr.co.huve.AsynchronousTaskManager.EventListener.OnStateChangeListener;
import kr.co.huve.AsynchronousTaskManager.EventListener.OnTaskEndListener;
import kr.co.huve.AsynchronousTaskManager.Type.TaskState;

public class ExampleActivity extends AppCompatActivity {
    private final static String TAG = ExampleActivity.class.getSimpleName();
    private TaskRunnable taskRunnable = new TaskRunnable();
    private View mainView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainView = findViewById(R.id.mainView);
        mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the task is running.
                checkTaskIsRunning();
            }
        });
    }

    //region Button click event

    public void onClickDefaultUse(View view) {
        // Here is the basic usage
        defaultAtmTaskUse();
    }

    public void onClickChangeUiAfterFinished(View view) {
        // Change the Ui with #AtmTask
        changeUiWithAtmTask();
    }

    public void onClickStateChangeEvent(View view) {
        // Use the event handler.
        useStateChangeEvent();
    }

    public void onClickDetectError(View view) {
        // Use the event handler.
        detectErrorEvent();
    }

    public void onClickGetResultValue(View view) {
        // Get the result value from task.
        returnTaskResult();
    }

    //endregion Button click event

    //region Example Use
    private void defaultAtmTaskUse() {
        // Create the background task. #createTask(Runnable, int)
        // 'Runnable' is about you working in the background thread.
        // 'int' is the unique id of this task.
        AtmTask atmTask = AsynchronousTaskManager.getInstance().createTask(taskRunnable, ExampleType.EXAMPLE_TASK.ordinal());

        // If you invoke '#active()', this task will start.
        atmTask.active();
    }

    private void changeUiWithAtmTask() {
        // Create the background task. #createTask(Runnable, LifecycleOwner, int)
        // 'Runnable' is about you working in the background thread.
        // 'LifecycleOwner' is the 'LifecycleOwner' object of the 'Activity' that includes AtmUiTask
        // 'int' is the unique id of this task.
        AtmUiTask atmUiTask = AsynchronousTaskManager.getInstance().createUiTask(taskRunnable, this, ExampleType.EXAMPLE_TASK.ordinal());

        atmUiTask.setOnTaskEndListener(new OnTaskEndListener() {
            @Override
            public void onTaskEnd(int taskId) {
                // TODO: The access to the main thread is available only the #atmUitask. It can't change the background color in #AtmTask.
                // The background color will be changed after the task finished.
                mainView.setBackgroundColor(Color.YELLOW);
            }
        });

        // If you invoke '#active()', this task will start.
        atmUiTask.active();
    }


    private void useStateChangeEvent() {
        // Create the background task. #createTask(Runnable, LifecycleOwner, int)
        // 'Runnable' is about you working in the background thread.
        // 'LifecycleOwner' is the 'LifecycleOwner' object of the 'Activity' that includes AtmUiTask
        // 'int' is the unique id of this task.
        AtmUiTask atmUiTask = AsynchronousTaskManager.getInstance().createUiTask(taskRunnable, this, ExampleType.EXAMPLE_TASK.ordinal());
        atmUiTask.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onTaskStateChange(int state, int taskId) {
                if (TaskState.READY == state) {
                    /**
                     * @Note This step isn't to call normally in #onTaskStateChange().
                     * Because when #AtmTask was created, its state becomes a 'Ready'.
                     */
                    // #TaskState.READY means the state before execution.
                } else if (TaskState.RUNNING == state) {
                    // #TaskState.RUNNING means that the task is running.
                    // If you use #AtmTask, this step is on the background thread.
                    // If you use #AtmUiTask, this step is on the main thread.

                    mainView.setBackgroundColor(Color.RED);
                } else if (TaskState.STOP == state) {
                    /**
                     * @Note This step isn't to call normally if you are using #AtmTask.
                     * Because the only #AtmUitask can know activity cycle, The #AtmTask can't become 'STOP'
                     */
                    // #TaskState.STOP means that the task was stopped because the activity was paused.
                    // If you use #AtmUiTask, this step is on the main thread.
                    Toast.makeText(ExampleActivity.this, "Task was stopped", Toast.LENGTH_SHORT).show();
                } else if (TaskState.SHUTDOWN == state) {
                    /**
                     * @Note This step isn't to call normally if you are using #AtmTask.
                     * Because the only #AtmUitask can know activity cycle, The #AtmTask can't become 'SHUTDOWN'
                     */
                    // #TaskState.SHUTDOWN means that the task was forced termination because the activity was destroyed.
                    // If you use #AtmUiTask, this step is on the main thread.
                    Log.d(TAG, "onTaskStateChange: Task was shutdown");
                } else if (TaskState.FINISH == state) {
                    // #TaskState.FINISH indicates that the task has been finished.
                    // If you use #AtmTask, this step is on the background thread.
                    // If you use #AtmUiTask, this step is on the main thread.
                    mainView.setBackgroundColor(Color.WHITE);
                }
            }
        });

        // If you invoke '#active()', this task will start.
        atmUiTask.active();
    }

    private void detectErrorEvent() {
        // Create the background task. #createTask(Runnable, int)
        // 'Runnable' is about you working in the background thread.
        // 'int' is the unique id of this task.
        AtmTask atmTask = AsynchronousTaskManager.getInstance().createTask(taskRunnable, ExampleType.EXAMPLE_TASK.ordinal());
        atmTask.setOnTaskEndListener(new OnTaskEndListener() {
            @Override
            public void onTaskEnd(int taskId) {
                mainView.setBackgroundColor(Color.LTGRAY);
                Toast.makeText(ExampleActivity.this, "TaskEnd", Toast.LENGTH_SHORT).show();
            }
        });
        // If a problem occurs, you can check with #OnErrorListener.
        atmTask.setOnErrorListener(new OnErrorListener() {
            @Override
            public void onError(String message, int taskId) {
                // message: Error content
                // taskId: The task id where the error occurred.
                Toast.makeText(ExampleActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });

        // If you invoke '#active()', this task will start.
        atmTask.active();
    }

    private void returnTaskResult() {
        AtmUiTask atmUiTask = AsynchronousTaskManager.getInstance().createUiTask(taskRunnable, this, ExampleType.EXAMPLE_TASK.ordinal());
        atmUiTask.setOnTaskEndListener(new OnTaskEndListener() {
            @Override
            public void onTaskEnd(int taskId) {
                // TODO: The Toast work on #AtmUiTask, because the toast can show only on the main thread
                // You can receive a result from #Runnable after the task finished.
                Toast.makeText(ExampleActivity.this, taskRunnable.getResponseValue(), Toast.LENGTH_SHORT).show();
            }
        });
        atmUiTask.active();
    }

    private void checkTaskIsRunning() {
        // You can check the task is running. #isTaskRunning(int)
        // 'int' is the unique id that you want to check.
        boolean isRunning = AsynchronousTaskManager.getInstance().isTaskRunning(ExampleType.EXAMPLE_TASK.ordinal());
        if (isRunning) {
            // The task is running!
            Toast.makeText(this, "Task is running", Toast.LENGTH_SHORT).show();
        } else {
            // The task is finish or shutdown.
            Toast.makeText(this, "Task is finished", Toast.LENGTH_SHORT).show();
        }
    }

    //endregion Example Use
}
