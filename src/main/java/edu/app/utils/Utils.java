package edu.app.utils;

import com.thoughtworks.xstream.XStream;
import edu.app.shapes.Circle;
import edu.app.shapes.Figure;
import edu.app.shapes.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Utils {

    private Utils() {
    }

    public static <T> ArrayList<T> castArrayList(Object obj, Class<T> tClass) {
        ArrayList<T> result = new ArrayList<>();

        if (obj instanceof ArrayList<?>) {
            for (Object o : (ArrayList<?>) obj) {
                result.add(tClass.cast(o));
            }
            return result;
        }

        return null;
    }

    public static int[] extractRGB(String color) {
        String regex = "(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher m = pattern.matcher(color);

        int[] rgb = new int[3];
        int i = 0;
        while (m.find() && i < 3) {
            rgb[i] = Integer.parseInt(m.group());
            i++;
        }
        return rgb;
    }

    public static void restoreColor(ArrayList<Figure> figures) {
        for (Figure figure : figures) {
            int[] rgb = extractRGB(figure.getRgbColor());
            figure.setColor(new Color(rgb[0], rgb[1], rgb[2]));
        }
    }

    public static void showError(JFrame parent, String message, String title, int error) {
        JOptionPane.showMessageDialog(parent, message, title, error);
    }

    public static void setXMLParameters(XStream xstream) {
        Figure.setXMLParameters(xstream);
        Circle.setXMLParameters(xstream);
        Rectangle.setXMLParameters(xstream);
    }

    public static void deleteValueInList(ArrayList<?> list, int value) {
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i).equals(value)) {
                list.remove(i);
                break;
            }
        }
    }

    public static <T> String join(List<T> list) {
        StringBuilder result = new StringBuilder();

        for (T t : list) {
            result.append(t).append(" ");
        }

        return result.toString();
    }
}
