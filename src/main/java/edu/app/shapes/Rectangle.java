package edu.app.shapes;

import com.thoughtworks.xstream.XStream;

import java.awt.*;

public class Rectangle extends Figure {

    private int width;
    private int height;

    public Rectangle(int x, int y, Color color, int width, int height) {
        super(x, y, color);
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public static void setXMLParameters(XStream xstream) {
        xstream.alias("rect", Rectangle.class);
        xstream.useAttributeFor(Rectangle.class, "width");
        xstream.useAttributeFor(Rectangle.class, "height");
    }

    @Override
    public String toString() {
        return super.toString() + "width = " + width + ", " + "height = " + height;
    }
}
