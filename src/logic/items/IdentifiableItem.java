package logic.items;

public class IdentifiableItem {

    private static int nextID = 0;

    private final int id;

    public IdentifiableItem() {
        this.id = nextID++;
    }

    public int getId() {
        return id;
    }

}
