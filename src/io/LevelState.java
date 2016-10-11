package io;

public class LevelState {

    public static int MIN_ROW_LENGTH = 4;
    public static int MIN_COL_LENGTH = 4;

    private char[][] allItems;

    public LevelState(char[][] allItems) {
        this.allItems = allItems;
    }

    public char[][] getAllItems() {
        return allItems;
    }

    public boolean isSavable() {
        return allItems != null && allItems.length > MIN_ROW_LENGTH
                && allItems[0].length > MIN_COL_LENGTH;
    }

}