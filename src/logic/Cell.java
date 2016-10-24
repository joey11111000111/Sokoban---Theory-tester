package logic;

/**
 * Created by joey on 2016.10.05..
 */
public class Cell {

    public static enum Type {
        WALL, BSPACE, BOX, MARKED_BOX, FIELD, EMPTY, PLAYER, PLAYER_ON_BSPACE, BOX_ON_BSPACE, MARKED_BOX_ON_BSPACE
    }

    private Type type;
    private Integer fieldValue;

    public Cell() {
        type = Type.EMPTY;
        fieldValue = null;
    }

    public Type getType() {
        return type;
    }

    void setType(Type typee) {
        if (this.type == typee)
            return;
        // If previously this was a field, it has a fieldValue which must be "nulled" by the type change
        if (this.type == Type.FIELD) {
            fieldValue = null;
        }
        this.type = typee;

    }

    public Integer getFieldValue() {
        return fieldValue;
    }

    void setFieldValue(Integer fieldValue) {
        if (type != Type.FIELD)
            throw new IllegalStateException("Only FIELD type cells can have a field value!");
        this.fieldValue = fieldValue;
    }
}
