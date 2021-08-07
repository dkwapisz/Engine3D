package mapUtilities;

import java.util.Timer;
import java.util.TimerTask;

public class Door extends StaticObjects {

    private int doorProgress; // 0 -> fully closed, 100 -> fully opened
    private boolean openStarted;
    private boolean closeStarted;
    private boolean opened;

    public void open() {
        if (!openStarted) {
            Timer openingTimer = new Timer();
            TimerTask openDoorTask = new TimerTask() {
                @Override
                public void run() {
                    if (doorProgress == 100) {
                        opened = true;
                        closeStarted = false;
                        close();
                        this.cancel();
                    }
                    doorProgress++;
                }
            };

            openingTimer.scheduleAtFixedRate(openDoorTask, 0, 15);
            openStarted = true;
        }
    }

    private void close() {
        if (!closeStarted) {
            Timer closingTimer = new Timer();
            TimerTask closeDoorTask = new TimerTask() {
                @Override
                public void run() {
                    opened = false;
                    if (doorProgress == 0) {
                        openStarted = false;
                        this.cancel();
                    }
                    doorProgress--;
                }
            };

            closingTimer.scheduleAtFixedRate(closeDoorTask, 5000, 15);
            closeStarted = true;
        }
    }


    public int getDoorProgress() {
        return doorProgress;
    }
    public boolean isOpenStarted() {
        return openStarted;
    }
    public boolean isCloseStarted() {
        return closeStarted;
    }
    public boolean isOpened() {
        return opened;
    }
}
