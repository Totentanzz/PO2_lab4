package edu.app.utils;

import edu.app.shapes.Circle;
import edu.app.shapes.Rectangle;
import edu.app.view.FigurePanel;

import java.awt.*;
import java.util.Random;

public final class RandomFigureFactory {

    private static final Random RAND = new Random();

    private RandomFigureFactory() {
    }

    public static Circle createCircle(FigurePanel panel) {
        int radius = RAND.nextInt(30, 100);
        int x = RAND.nextInt(panel.getWidth() - radius);
        int y = RAND.nextInt(panel.getHeight() - radius);
        int r = RAND.nextInt(255);
        int g = RAND.nextInt(255);
        int b = RAND.nextInt(255);
        Color color = new Color(r, g, b);
        return new Circle(x, y, color, radius);
    }

    public static Rectangle createRectangle(FigurePanel panel) {
        int x = RAND.nextInt(panel.getWidth());
        int y = RAND.nextInt(panel.getHeight());
        int r = RAND.nextInt(255);
        int g = RAND.nextInt(255);
        int b = RAND.nextInt(255);
        Color color = new Color(r, g, b);
        int width = RAND.nextInt(panel.getWidth() - x);
        int height = RAND.nextInt(panel.getHeight() - y);
        return new Rectangle(x, y, color, width, height);
    }
}
