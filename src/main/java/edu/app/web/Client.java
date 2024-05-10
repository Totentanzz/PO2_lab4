package edu.app.web;

import com.thoughtworks.xstream.XStream;
import edu.app.shapes.Circle;
import edu.app.shapes.Figure;
import edu.app.shapes.Rectangle;
import edu.app.utils.Utils;
import edu.app.view.MainWindow;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements Runnable {

    private Socket server;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean isConnected;
    private String command;
    private int idDeliver;

    public Client(String host, int port) {
        try {
            server = new Socket(host, port);
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());
            isConnected = true;

            new Thread(this).start();
        } catch (Exception ex) {
            Utils.showError(MainWindow.getInstance(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Client constructor exception");
        }
    }

    public void disconnect() {
        command = "disconnect";
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void receive(int selectedId) {
        idDeliver = selectedId;
        command = "receive";
    }

    @Override
    public void run() {
        try {
            ArrayList<Integer> clientIds = Utils.castArrayList(in.readObject(), Integer.class);
            int id = in.readInt();
            Utils.deleteValueInList(clientIds, id);
            String idsStr = Utils.join(clientIds);
            MainWindow.getInstance().updateClientsListFromServer(idsStr);

            while (isConnected) {
                if (in.available() == 0) {
                    if (command != null) {
                        switch (command) {
                            case "receive" -> {
                                out.writeUTF("connect");
                                out.writeInt(idDeliver);
                                out.flush();
                            }
                            case "disconnect" -> {
                                out.writeUTF("disconnect");
                                out.flush();
                            }
                            default -> {
                            }
                        }
                        command = null;
                    }
                } else {
                    String commandServer = in.readUTF();
                    switch (commandServer) {
                        case "receive" -> {
                            int idFrom = in.readInt();

                            XStream xstream = new XStream();
                            Utils.setXMLParameters(xstream);

                            String xmlString = xstream.toXML(MainWindow.getInstance().getFigures());
                            out.writeUTF("receive");
                            out.writeObject(xmlString);
                            out.writeInt(idFrom);
                            out.flush();
                        }
                        case "get" -> {
                            String xmlString = (String) in.readObject();

                            XStream xstream = new XStream();
                            Utils.setXMLParameters(xstream);
                            xstream.allowTypes(new Class[]{Circle.class, Rectangle.class});

                            ArrayList<Figure> getFigures = Utils.castArrayList(xstream.fromXML(xmlString), Figure.class);
                            Utils.restoreColor(getFigures);

                            MainWindow.getInstance().loadFigures(getFigures);
                        }
                        case "otherClients" -> {
                            clientIds = Utils.castArrayList(in.readObject(), Integer.class);
                            Utils.deleteValueInList(clientIds, id);
                            idsStr = Utils.join(clientIds);
                            MainWindow.getInstance().updateClientsListFromServer(idsStr);
                        }
                        default -> {
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Utils.showError(MainWindow.getInstance(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Client run method exception 1");
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (server != null) {
                    server.close();
                }
            } catch (Exception ex) {
                Utils.showError(MainWindow.getInstance(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Client run method exception 2");
            }
        }
    }
}
