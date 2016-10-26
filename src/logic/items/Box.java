package logic.items;

public class Box extends IdentifiableItem {

    private boolean marked;

    public Box() {
        super();
        marked = false;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }
}
