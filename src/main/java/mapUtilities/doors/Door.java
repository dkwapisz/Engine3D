package mapUtilities.doors;

import mapUtilities.ButtonWall;
import mapUtilities.StaticObjects;

import java.util.Timer;
import java.util.TimerTask;

public abstract class Door extends StaticObjects {

    private int doorProgress = 0; // 1 -> fully closed, 100 -> fully opened
    private boolean openStarted;
    private boolean closeStarted;
    private boolean opened;
    private boolean buttonDoor;
    private final int OPENING_PERIOD;
    private final int CLOSING_PERIOD;
    private final int OPEN_TIME;
    private ButtonWall actualButtonWall;

    public Door(int OPENING_PERIOD, int CLOSING_PERIOD, int OPEN_TIME) {
        this.OPENING_PERIOD = OPENING_PERIOD;
        this.CLOSING_PERIOD = CLOSING_PERIOD;
        this.OPEN_TIME = OPEN_TIME;
    }

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
            openingTimer.scheduleAtFixedRate(openDoorTask, 0, OPENING_PERIOD);
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
                    if (doorProgress == 1) {
                        openStarted = false;
                        closeStarted = false;
                        if (actualButtonWall != null) {
                            actualButtonWall.setClicked(false);
                        }
                        this.cancel();
                    }
                    doorProgress--;
                }
            };

            closingTimer.scheduleAtFixedRate(closeDoorTask, OPEN_TIME, CLOSING_PERIOD);
            closeStarted = true;
        }
    }

    public void setActualButtonWall(ButtonWall actualButtonWall) {
        this.actualButtonWall = actualButtonWall;
    }

    public boolean isMoving() {
        return (this.isCloseStarted() || this.isOpenStarted() || this.isOpened());
    }

    public int getDoorProgress() {
        return doorProgress;
    }
    public void setDoorProgress(int doorProgress) {
        this.doorProgress = doorProgress;
    }

    public boolean isOpenStarted() {
        return openStarted;
    }
    public void setOpenStarted(boolean openStarted) {
        this.openStarted = openStarted;
    }

    public boolean isCloseStarted() {
        return closeStarted;
    }
    public void setCloseStarted(boolean closeStarted) {
        this.closeStarted = closeStarted;
    }

    public boolean isOpened() {
        return opened;
    }
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isButtonDoor() {
        return buttonDoor;
    }
    public void setButtonDoor(boolean buttonDoor) {
        this.buttonDoor = buttonDoor;
    }
}
