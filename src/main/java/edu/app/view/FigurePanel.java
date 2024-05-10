package edu.app.view;

import edu.app.shapes.Circle;
import edu.app.shapes.Figure;
import edu.app.shapes.Rectangle;

import javax.swing.*;
import java.awt.*;

public class FigurePanel extends JPanel {

    public FigurePanel(int width, int height) {
        setSize(width, height);
        setLocation(10, 70 + 60);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }

    @Override
    public void paintComponent(Graphics g) {
        if (MainWindow.getInstance().getFigures().size() == 0) {
            MainWindow.getInstance().createFigures();
        }

        for (Figure figure : MainWindow.getInstance().getFigures()) {
            g.setColor(figure.getColor());
            if (figure instanceof Rectangle) {
                drawRectangle(g, figure);
            } else if (figure instanceof Circle) {
                drawCircle(g, figure);
            }
        }
    }

    private void drawRectangle(Graphics g, Figure figure) {
        int x = figure.getX();
        int y = figure.getY();
        int width = ((Rectangle) figure).getWidth();
        int height = ((Rectangle) figure).getHeight();
        g.fillRect(x, y, width, height);
    }

    private void drawCircle(Graphics g, Figure figure) {
        int x = figure.getX();
        int y = figure.getY();
        int width = ((Circle) figure).getRadius();
        int height = ((Circle) figure).getRadius();
        g.fillOval(x, y, width, height);
    }
}
