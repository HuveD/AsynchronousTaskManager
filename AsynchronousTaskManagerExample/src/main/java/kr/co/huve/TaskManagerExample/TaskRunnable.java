package kr.co.huve.TaskManagerExample;

class TaskRunnable implements Runnable {

    private String responseValue = "";

    @Override
    public void run() {
        try {
            // Sleep
            Thread.sleep(3000);
            responseValue = "3000ms Sleep!";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String getResponseValue() {
        return responseValue;
    }
}
