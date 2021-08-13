package mapUtilities.doors;

import dev.Game;
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
    private int openingPeriod;
    private int closingPeriod;
    private int openTime;
    private ButtonWall actualButtonWall;

    public Door(int openingPeriod, int closingPeriod, int openTime) {
        this.openingPeriod = openingPeriod;
        this.closingPeriod = closingPeriod;
        this.openTime = openTime;
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
            openingTimer.scheduleAtFixedRate(openDoorTask, 0, openingPeriod);
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
                        actualButtonWall.setClicked(false);
                        this.cancel();
                    }
                    doorProgress--;
                }
            };

            closingTimer.scheduleAtFixedRate(closeDoorTask, openTime, closingPeriod);
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
