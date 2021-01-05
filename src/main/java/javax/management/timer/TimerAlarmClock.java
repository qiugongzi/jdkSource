
    public void run() {

        try {

            TimerAlarmClockNotification notif = new TimerAlarmClockNotification(this);
            listener.notifyAlarmClock(notif);
        } catch (Exception e) {
            TIMER_LOGGER.logp(Level.FINEST, Timer.class.getName(), "run",
                    "Got unexpected exception when sending a notification", e);
        }
    }
}
