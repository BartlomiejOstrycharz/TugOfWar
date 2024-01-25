import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame implements KeyListener {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;

    private DataInputStream input;
    private DataOutputStream output;
    private JProgressBar progressBar;
    private String team;

    public Client() {
        initializeUI();
        connectToServer();
    }

    private void initializeUI() {
        setTitle("Przeciąganie liny");
        setSize(350, 150);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        progressBar = new JProgressBar(0, 40);
        progressBar.setValue(20);
        progressBar.setStringPainted(true);

        JLabel instructionLabel = new JLabel("Naciśnij LEWĄ lub PRAWĄ strzałkę, aby przeciągać linę");

        setLayout(new BorderLayout());
        add(progressBar, BorderLayout.CENTER);
        add(instructionLabel, BorderLayout.SOUTH);

        addKeyListener(this);
        setFocusable(true);
    }

    private void connectToServer() {
        try {
            Socket serverSocket = new Socket(SERVER_IP, SERVER_PORT);
            System.out.println("Połączono z serwerem: " + serverSocket);

            input = new DataInputStream(serverSocket.getInputStream());
            output = new DataOutputStream(serverSocket.getOutputStream());

            // Prompt the client to choose their team
            String teamChoice = JOptionPane.showInputDialog(this, "Wybierz drużynę: 'L' (LEWA) lub 'P' (PRAWA)");
            if (teamChoice != null && (teamChoice.equalsIgnoreCase("l") || teamChoice.equalsIgnoreCase("p"))) {
                team = teamChoice.toLowerCase();
                output.writeUTF(team);
                output.flush();

                new Thread(this::setProgressBar).start();
            } else {
                JOptionPane.showMessageDialog(this, "Wybrano niepoprawną drużynę.");
                System.exit(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProgressBar() {
        try {
            while (true) {
                int counter = input.readInt();
                setProgressBarValue(counter);
                if (counter <= 0) {
                    System.out.println("Drużyna LEWA wygrała! Koniec gry!");
                    break;
                }
                else if(counter>=40){
                    System.out.println("Drużyna PRAWA wygrała! Koniec gry!");
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setProgressBarValue(int counter) {
        SwingUtilities.invokeLater(() -> progressBar.setValue(counter));
    }

    private void sendKeystroke(String keystroke) {
        try {
            output.writeUTF(keystroke);
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (keyCode == KeyEvent.VK_LEFT && team.equals("l")) {
            sendKeystroke("decrement");
        } else if (keyCode == KeyEvent.VK_RIGHT && team.equals("p")) {
            sendKeystroke("increment");
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client().setVisible(true));
    }
}
