package kr.co.huve.asynchronoustaskmanager.Util;

import android.support.annotation.NonNull;

import java.util.ArrayList;

import kr.co.huve.asynchronoustaskmanager.Data.AtmTask;

class AtmThreadPool extends Thread {
    private final ArrayList <AtmThread> threadList = new ArrayList <>();
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
            if (threadList.size() == cursorIdx) {
                cursorIdx = 0;
            }
            if (isDataChanged) {
                isDataChanged = false;
                cursorIdx = 0;
            }
            synchronized (this) {
                if (threadList.size() == 0) {
                    break;
                } else if (cursorIdx >= threadList.size()) {
                    cursorIdx = 0;
                }
                threadList.get(cursorIdx).start();
            }
            cursorIdx++;
        }
        AsynchronousTaskManager.getInstance().clearAtmListener();
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
    synchronized void removeTask(@NonNull AtmThread thread) {
        threadList.remove(thread);
        isDataChanged = true;
    }

    /**
     * Add thread to thread pool.
     */
    synchronized void addTask(@NonNull AtmThread thread) {
        threadList.add(thread);
        isDataChanged = true;
        if (State.NEW == getState() && !isRunning) {
            start();
        }
    }

    synchronized AtmThread getAtmThread(AtmTask item) {
        for (AtmThread atmThread : threadList) {
            if (item == atmThread.getTaskItem()) {
                return atmThread;
            }
        }
        return null;
    }

    synchronized AtmThread getAtmThread(int id) {
        for (AtmThread atmThread : threadList) {
            if (id == atmThread.getTaskItem().getTaskId()) {
                return atmThread;
            }
        }
        return null;
    }

    ArrayList <AtmThread> getThreadList() {
        return threadList;
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
