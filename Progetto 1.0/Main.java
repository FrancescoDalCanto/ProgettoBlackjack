import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // Mostra la finestra della tabella di strategia
                new StrategyTableFrame();
                
                // Mostra la finestra del gioco del blackjack
                new BlackjackTable(1);

                // Calcolo della probabilitá 
                new BlackjackCounterApp();
            }
        });
    }
}

