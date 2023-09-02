import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main3 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int numPlayers = Integer.parseInt(JOptionPane.showInputDialog("Inserisci il numero di giocatori:"));
            int numDecks = Integer.parseInt(JOptionPane.showInputDialog("Inserisci il numero di mazzi:"));
            
            // Mostra la finestra del gioco del blackjack
            BlackjackTable3 blackjackTable = new BlackjackTable3(numPlayers, numDecks);
            blackjackTable.setVisible(true);

            // Mostra la finestra della tabella di strategia
            BlackjackCounterApp blackjackCounterApp = new BlackjackCounterApp();
            blackjackCounterApp.setVisible(true);
        });
    }
}