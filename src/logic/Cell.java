package logic;

public class Cell {

    public static enum Type {
        WALL, BSPACE, BOX, MARKED_BOX, FIELD, EMPTY, PLAYER, PLAYER_ON_BSPACE, BOX_ON_BSPACE, MARKED_BOX_ON_BSPACE;

        boolean containsBoxSpace() {
            return this == BSPACE || this == PLAYER_ON_BSPACE
                    || this == BOX_ON_BSPACE|| this == MARKED_BOX_ON_BSPACE;
        }

        boolean containsBox() {
            return this == BOX || this == MARKED_BOX || this == BOX_ON_BSPACE || this == MARKED_BOX_ON_BSPACE;
        }

        boolean containsMarkedBox() {
            return this == MARKED_BOX || this == MARKED_BOX_ON_BSPACE;
        }

        boolean containsPlayer() {
            return this == PLAYER || this == PLAYER_ON_BSPACE;
        }
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

    void setType(Type type) {
        if (type == Type.FIELD)
            throw new IllegalArgumentException("Field type can only be set to along with the field value!");
        if (this.type == type)
            return;
        if (this.type == Type.FIELD) {
            fieldValue = null;
        }
        this.type = type;
    }

    public Integer getFieldValue() {
        return fieldValue;
    }

    void setToFieldType(Integer fieldValue) {
        this.type = Type.FIELD;
        this.fieldValue = fieldValue;
    }
}
