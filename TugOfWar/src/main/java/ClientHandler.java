import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket clientSocket;
    private DataInputStream input;
    private DataOutputStream output;
    private String team; // Team chosen by the client

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            // Receive the team choice from the client
            team = input.readUTF();
            String teamInfo = team;

            if(teamInfo=="l"){
                teamInfo="LEWEJ";
            }
            else if(teamInfo=="p"){
                teamInfo="PRAWEJ";
            }

            System.out.println("Klient: " + clientSocket + ", Dołączył do drużyny: " + teamInfo);

            while (true) {
                String message = input.readUTF();
                if (message.equals("increment") && team.equals("p")) {
                    Server.incrementCounter();
                } else if (message.equals("decrement") && team.equals("l")) {
                    Server.decrementCounter();
                }
            }
        } catch (IOException e) {
            // Client disconnected
            System.out.println("Klient: " + clientSocket + " się rozłączył");
            Server.removeClient(this);
        }
    }


    public void sendCounter(int counter) {
        try {
            output.writeInt(counter);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
