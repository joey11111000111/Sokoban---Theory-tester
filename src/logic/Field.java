package logic;

public class Field {

    public static enum FieldTypes {
        PLAYER_FIELD, BOX_FIELD;
    }

    public static Field createFieldTemplate(Integer itemID, FieldTypes fieldType) {
        return new Field(itemID, fieldType);
    }

    public static Field createFieldTemplate(Field field) {
        return new Field(field.getItemID(), field.getFieldType());
    }

    private Integer itemID;
    private FieldTypes fieldType;
    private Integer value;

    private Field(Integer itemID, FieldTypes fieldType) {
        this.itemID = itemID;
        this.fieldType = fieldType;
        this.value = null;
    }

    public Field(Integer itemID, FieldTypes fieldType, Integer value) {
        if (itemID == null || fieldType == null || value == null)
            throw new IllegalArgumentException("No constructor argument is allowed to be null!");
        this.itemID = itemID;
        this.fieldType = fieldType;
        this.value = value;
    }

    public boolean isSameTypeAndSource(Field field) {
        return itemID.equals(field.getItemID()) && fieldType == field.getFieldType();
    }

    public Integer getItemID() {
        return itemID;
    }

    public FieldTypes getFieldType() {
        return fieldType;
    }

    public Integer getValue() {
        if (isTemplateField())
            throw new IllegalAccessError("A template field doesn't have a field value!");
        return value;
    }

    public boolean isTemplateField() {
        return value == null;
    }
}
