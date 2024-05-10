package edu.app.shapes;

import com.thoughtworks.xstream.XStream;

import java.awt.*;

public abstract class Figure {

    private final int x;
    private final int y;
    private Color color;
    private final String rgbColor;

    public Figure(int x, int y, Color color) {
        this.x = x;
        this.y = y;
        this.color = color;
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        rgbColor = String.format("rgb(%d,%d,%d)", r, g, b);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getRgbColor() {
        return rgbColor;
    }

    public static void setXMLParameters(XStream xstream) {
        xstream.useAttributeFor(Figure.class, "x");
        xstream.useAttributeFor(Figure.class, "y");
        xstream.omitField(Figure.class, "color");
        xstream.useAttributeFor(Figure.class, "rgbColor");
        xstream.aliasAttribute(Figure.class, "rgbColor", "fill");
    }

    public String toString() {
        return "x = " + x + ", " + "y = " + y + ", " + "fill = " + rgbColor + ", ";
    }
}
