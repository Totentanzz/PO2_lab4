package edu.app.web;

import edu.app.utils.Utils;
import edu.app.view.DialogWindow;
import edu.app.view.MainWindow;

import javax.swing.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private static Map<Integer, ClientHandler> clients;
    private static List<Integer> ids;

    @SuppressWarnings("InfiniteLoopStatement")
    public static void main(String[] args) {
        clients = new HashMap<>();
        ids = new ArrayList<>();

        DialogWindow dw = new DialogWindow(205, 160, "Input");
        String portStr = dw.showAndWait();
        if (portStr == null) return;
        int port = Integer.parseInt(portStr);

        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> System.out.println("\nServer is closed.")));

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server is started...");

            int id = 1;
            while (true) {
                Socket client = server.accept();
                System.out.println("Client#" + id + " connected");
                ClientHandler clientThread = new ClientHandler(client, id);

                ids.add(id);
                clients.put(id, clientThread);
                id++;
                clientThread.start();
                sendClients(ids.size() - 1);
            }
        } catch (Exception ex) {
            Utils.showError(MainWindow.getInstance(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println("Server main method exception");
        }
    }

    public static void sendClients(int count) {
        for (int i = 0; i < count; ++i) {
            clients.get(ids.get(i)).sendClientIds();
        }
    }

    private static class ClientHandler extends Thread {

        public Socket client;
        public ObjectOutputStream out;
        public ObjectInputStream in;
        public String command;
        public boolean isConnected = true;
        public int id;

        public ClientHandler(Socket client, int id) {
            this.client = client;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(client.getOutputStream());
                in = new ObjectInputStream(client.getInputStream());

                out.writeObject(ids);
                out.writeInt(id);
                out.flush();

                while (isConnected) {
                    command = in.readUTF();
                    switch (command) {
                        case "connect" -> {
                            int idToSwap = in.readInt();
                            ClientHandler otherClient = clients.get(idToSwap);
                            otherClient.out.writeUTF("receive");
                            otherClient.out.writeInt(id);
                            otherClient.out.flush();
                        }
                        case "receive" -> {
                            String xmlString = (String) in.readObject();
                            int id = in.readInt();
                            ClientHandler client = clients.get(id);
                            client.out.writeUTF("get");
                            client.out.writeObject(xmlString);
                            client.out.flush();
                        }
                        case "disconnect" -> {
                            disconnect();
                            System.out.println("Client#" + id + " disconnected");
                        }
                        default -> {
                        }
                    }
                }
            } catch (Exception ex) {
                Utils.showError(MainWindow.getInstance(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Server run method exception");
            } finally {
                disconnect();
            }
        }

        public void sendClientIds() {
            try {
                out.writeUTF("otherClients");
                out.writeObject(new ArrayList<>(ids));
                out.flush();
            } catch (Exception ex) {
                Utils.showError(MainWindow.getInstance(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Server sendClientIds method exception");
            }
        }

        public void disconnect() {
            try {
                isConnected = false;
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
                if (client != null) {
                    client.close();
                }

                clients.remove(id);

                for (int i = 0; i < ids.size(); ++i) {
                    if (ids.get(i) == id) {
                        ids.remove(i);
                        break;
                    }
                }

                sendClients(ids.size());
            } catch (Exception ex) {
                Utils.showError(MainWindow.getInstance(), ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                System.out.println("Server disconnect method exception");
            }
        }
    }
}
