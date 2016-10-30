package util;

public class Size {
    private int width;
    private int height;

    public static Size smallerSize(Size size1, Size size2) {
        int area1 = size1.getWidth() * size1.getHeight();
        int area2 = size2.getWidth() * size2.getHeight();
        return (area1 < area2) ? size1 : size2;
    }

    public Size() {
        width = -1;
        height = -1;
    }

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
