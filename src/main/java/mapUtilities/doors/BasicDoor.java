package mapUtilities.doors;

public class BasicDoor extends Door {

    public BasicDoor() {
        super(15, 15, 5000);
        setButtonDoor(false); //In default -> standard, non-button door
    }
}
