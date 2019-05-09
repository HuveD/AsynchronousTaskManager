package kr.co.huve.asynchronoustaskmanager.Type;

public enum AtmState {
    RUNNING,     // Running.
    SHUTDOWN,    // Force shutdown due to external factors.
    READY,       // Waiting for start.
    STOP,        // Task stop due to activity life cycle.
    FINISH       // Task complete.
}
