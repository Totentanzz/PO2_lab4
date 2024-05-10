package edu.app;

import edu.app.view.MainWindow;

public class App {
    public static void main(String[] args) {
        MainWindow.createInstance(700, 600);
        MainWindow.getInstance().setTitle("Figures");
        MainWindow.getInstance().setVisible(true);
        MainWindow.figuresCount = 5;
    }
}
