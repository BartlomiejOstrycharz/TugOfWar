import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static final int PORT = 8888;
    private static List<ClientHandler> clients = new ArrayList<>();
    private static int counter = 20;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Serwer wystartował na porcie: " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Połączył się nowy klient: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void incrementCounter() {
        counter++;
        sendCounterUpdate();
    }

    public static void decrementCounter() {
        counter--;
        sendCounterUpdate();
    }

    public static void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    private static void sendCounterUpdate() {
        for (ClientHandler client : clients) {
            client.sendCounter(counter);
        }
    }
}
