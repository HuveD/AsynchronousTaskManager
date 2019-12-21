package kr.co.huve.AsynchronousTaskManager;

import androidx.annotation.NonNull;

import java.util.ArrayList;

class AtmThreadPool extends Thread {
    private String TAG = "AsynchronousTaskManager";
    private final ArrayList<AtmThread> threadList = new ArrayList<>();

    private int amountOfChangedIndex;
    private int lastCheckedIndex;
    private boolean isDataChanged;
    private boolean isRoleOver;
    private boolean isRunning;

    @Override
    public synchronized void start() {
        if (threadList.size() > 0 && !isRunning) {
            isRunning = true;
            super.start();
        }
    }

    @Override
    public void run() {
        int cursorIdx = 0;
        while (threadList.size() > 0) {
            synchronized (threadList) {
                if (isDataChanged) {
                    isDataChanged = false;
                    cursorIdx += amountOfChangedIndex;
                    amountOfChangedIndex = 0;
                }
                if (threadList.size() <= cursorIdx) {
                    cursorIdx = 0;
                } else if (threadList.size() == 0) {
                    break;
                }
                try {
                    threadList.get(cursorIdx).start();
                } catch (IndexOutOfBoundsException e) {
                    if (threadList.size() > 0) {
                        cursorIdx = threadList.size() - 1;
                    } else {
                        break;
                    }
                }
                lastCheckedIndex = cursorIdx;
                cursorIdx++;
            }

            try {
                sleep(100);
            } catch (InterruptedException e) {
                // The thread was interrupted.
            }
        }
        isRoleOver = true;
        isRunning = false;
    }

    @Override
    public void interrupt() {
        super.interrupt();
        isRoleOver = true;
    }

    /**
     * Remove thread to thread pool.
     */
    void removeTask(@NonNull AtmThread thread) {
        synchronized (threadList) {
            int indexOfItem = threadList.indexOf(thread);
            if (indexOfItem != -1) {
                if (indexOfItem < lastCheckedIndex) {
                    amountOfChangedIndex -= 1;
                }
                threadList.remove(indexOfItem);
                isDataChanged = true;
            }
        }
    }

    /**
     * Add thread to thread pool.
     */
    void addTask(@NonNull AtmThread thread) {
        synchronized (threadList) {
            threadList.add(thread);
            isDataChanged = true;
        }

        if (State.NEW == getState() && !isRunning) {
            start();
        }
    }

    AtmThread getAtmThread(int id) {
        synchronized (threadList) {
            for (AtmThread atmThread : threadList) {
                if (id == atmThread.getTaskItem().getTaskId()) {
                    return atmThread;
                }
            }
            return null;
        }
    }

    /**
     * ATM Thread pool Lazy holder class
     */
    private static class ThreadPoolHolder {
        private static AtmThreadPool THREAD_POOL = new AtmThreadPool();

        private static synchronized AtmThreadPool getInitializedInstance() {
            if (THREAD_POOL != null && THREAD_POOL.isAlive()) {
                return THREAD_POOL;
            } else return THREAD_POOL = new AtmThreadPool();
        }
    }

    /**
     * Get the singleton instance of the Atm thread pool
     *
     * @return {@link AtmThreadPool}
     */
    static AtmThreadPool getInstance() {
        AtmThreadPool pool = ThreadPoolHolder.THREAD_POOL;
        if (pool.isRoleOver) {
            return ThreadPoolHolder.getInitializedInstance();
        } else {
            return pool;
        }
    }
}
