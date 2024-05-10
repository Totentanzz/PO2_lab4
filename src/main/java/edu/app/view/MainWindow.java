package edu.app.view;

import com.thoughtworks.xstream.XStream;
import edu.app.shapes.Figure;
import edu.app.shapes.Rectangle;
import edu.app.shapes.Circle;
import edu.app.utils.RandomFigureFactory;
import edu.app.utils.Utils;
import edu.app.web.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class MainWindow extends JFrame {

    private static MainWindow instance;
    private JButton btnConnect;
    private JButton btnReceive;
    private JButton btnDisconnect;
    private JButton btnNew;
    private FigurePanel figurePanel;
    private Client client;
    private final ArrayList<Figure> figures = new ArrayList<>();
    public static int figuresCount;
    private JList<String> clients;
    private int selectedId;

    private MainWindow(int width, int height) {
        setSize(width, height);
        setLocationRelativeTo(null);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        addMenuBar();
        addPanel(width, height);
        addConnectButton();
        addReceiveButton();
        addDisconnectButton();
        addNewButton();
        addClientsList();
    }

    public static void createInstance(int width, int height) {
        if (instance == null) {
            instance = new MainWindow(width, height);
        }
    }

    public static MainWindow getInstance() {
        return instance;
    }

    public ArrayList<Figure> getFigures() {
        return figures;
    }

    private void addMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuFile = new JMenu("File");

        JMenuItem menuItemSave = new JMenuItem("Save XML");
        menuItemSave.addActionListener(this::saveActionPerformed);
        menuFile.add(menuItemSave);

        JMenuItem menuItemLoad = new JMenuItem("Load XML");
        menuItemLoad.addActionListener(this::loadActionPerformed);
        menuFile.add(menuItemLoad);

        menuBar.add(menuFile);
        setJMenuBar(menuBar);
    }

    private void saveActionPerformed(ActionEvent event) {
        FileDialog fd = createFileDialog(this, "Save file", FileDialog.SAVE);

        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(getPath(fd)), "Unicode")) {
            XStream xstream = new XStream();
            Utils.setXMLParameters(xstream);
            xstream.toXML(figures, osw);
            osw.flush();
        } catch (IOException ex) {
            Utils.showError(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadActionPerformed(ActionEvent event) {
        FileDialog fd = createFileDialog(this, "Load file", FileDialog.LOAD);

        try (FileInputStream fis = new FileInputStream(getPath(fd));
             BufferedReader br = new BufferedReader(new InputStreamReader(fis, "Unicode"))) {

            XStream xstream = new XStream();
            Utils.setXMLParameters(xstream);
            xstream.allowTypes(new Class[]{Circle.class, Rectangle.class});

            /* *
             * This forced conversion is not safe and may throw an exception and cause the program to crash:
             * ArrayList<lab1.Figure> receivedFigures = (ArrayList<lab1.Figure>) xstream.fromXML(br);
             * */
            ArrayList<Figure> receivedFigures = Utils.castArrayList(xstream.fromXML(br), Figure.class);
            Utils.restoreColor(receivedFigures);

            loadFigures(receivedFigures);
        } catch (IOException ex) {
            Utils.showError(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPanel(int width, int height) {
        figurePanel = new FigurePanel(width - 35, height - 200);
        add(figurePanel);
    }

    private void addConnectButton() {
        btnConnect = createButton("Connect", 10, 10, true);
        add(btnConnect);

        btnConnect.addActionListener(e -> {
            DialogWindow dw = new DialogWindow(205, 160, "Input");
            String portStr = dw.showAndWait();
            if (portStr != null) {
                int port = Integer.parseInt(portStr);
                client = new Client("127.0.0.1", port);
                if (client.isConnected()) {
                    btnConnect.setEnabled(false);
                    btnDisconnect.setEnabled(true);
                }
            }
        });
    }

    private void addReceiveButton() {
        btnReceive = createButton("Receive", 190, 40, false);
        add(btnReceive);

        btnReceive.addActionListener(e -> client.receive(selectedId));
    }

    private void addDisconnectButton() {
        btnDisconnect = createButton("Disconnect", 10, 70, false);
        add(btnDisconnect);

        btnDisconnect.addActionListener(e -> {
            client.disconnect();
            updateClientsListFromServer("");
            btnDisconnect.setEnabled(false);
            btnReceive.setEnabled(false);
            btnConnect.setEnabled(true);
        });
    }

    private void addNewButton() {
        btnNew = createButton("New", 575,40, true);
        add(btnNew);

        btnNew.addActionListener(e -> {
            createFigures();
            repaint();
        });
    }

    private FileDialog createFileDialog(JFrame p, String title, int mode) {
        FileDialog fd = new FileDialog(p, title, mode);
        fd.setFile("data.xml");
        fd.setVisible(true);
        return fd;
    }

    private String getPath(FileDialog fd) {
        return fd.getDirectory() + fd.getFile();
    }

    private JButton createButton(String title, int x, int y, boolean isEnabled) {
        JButton button = new JButton(title);
        button.setBounds(x, y, 100, 50);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setEnabled(isEnabled);
        return button;
    }

    public void createFigures() {
        figures.clear();

        for (int i = 0; i < MainWindow.figuresCount; i++) {
            if (i <= MainWindow.figuresCount / 2) {
                figures.add(RandomFigureFactory.createCircle(figurePanel));
            } else {
                figures.add(RandomFigureFactory.createRectangle(figurePanel));
            }
        }
        repaint();
    }

    public void loadFigures(ArrayList<Figure> newFigures) {
        figures.clear();
        figures.addAll(newFigures);
        MainWindow.figuresCount = figures.size();
        repaint();
    }

    private void addClientsList() {
        clients = new JList<>();
        clients.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && clients.getSelectedIndex() >= 0) {
                if (!Objects.equals(clients.getSelectedValue(), "")) {
                    selectedId = Integer.parseInt(clients.getSelectedValue());
                    btnReceive.setEnabled(true);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(clients);
        scroll.setBounds(120, 25, 60, 80);
        scroll.setBackground(null);

        add(scroll);
    }

    public void updateClientsListFromServer(String list) {
        selectedId = 0;
        btnReceive.setEnabled(false);
        clients.setSelectedIndex(-1);
        clients.setListData(list.split(" "));
    }
}
