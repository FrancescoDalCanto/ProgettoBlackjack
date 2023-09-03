import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Mostra la finestra del gioco del blackjack
            BlackjackTable blackjackTable = new BlackjackTable(1);
            blackjackTable.setVisible(true);

            // Mostra la finestra della tabella di strategia
            BlackjackCounterApp blackjackCounterApp = new BlackjackCounterApp();
            blackjackCounterApp.setVisible(true);
        });
    }
}
