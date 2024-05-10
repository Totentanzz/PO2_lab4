package edu.app.shapes;

import com.thoughtworks.xstream.XStream;

import java.awt.*;

public class Circle extends Figure {

    private int radius;

    public Circle(int x, int y, Color color, int radius) {
        super(x, y, color);
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }

    public static void setXMLParameters(XStream xstream) {
        xstream.alias("circle", Circle.class);
        xstream.useAttributeFor(Circle.class, "radius");
        xstream.aliasAttribute(Circle.class, "radius", "r");
    }

    @Override
    public String toString() {
        return super.toString() + "r = " + radius;
    }
}
