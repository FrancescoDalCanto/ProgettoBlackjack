import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class BlackjackTable extends JFrame {

    // Costanti per il numero di mazzi e il numero totale di carte nel mazzo
    private static final int NUM_DECKS = 1;
    private static final int TOTAL_CARDS_IN_DECK = 52 * NUM_DECKS;

    // Mappa dei giocatori
    private Map<String, Player> players = new HashMap<>();

    // Conteggio HI-LO
    private int hiloCount = 0;

    // Etichette per il conteggio HI-LO, i consigli, le carte rimanenti e le probabilità
    private JLabel hiloCountLabel;
    private JLabel adviceLabel;
    private JLabel remainingCardsLabel;
    private JLabel probabilityLabel;

    // Mappa delle carte inserite dai giocatori
    private Map<String, Integer> enteredCardsMap = new HashMap<>();

    // Simboli delle carte e valori per il conteggio HI-LO
    private static final String[] CARD_SYMBOLS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final int[] CARD_VALUES = {1, 1, 1, 1, 1, 0, 0, 0, -1, -1, -1, -1, 0};

    // Costruttore
    public BlackjackTable(int numPlayers) {
        setTitle("Blackjack HI-LO Counter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Pannello delle carte per i giocatori e il dealer
        JPanel cardPanel = new JPanel(new GridLayout(numPlayers + 1, 5, 10, 10));

        // Creazione e aggiunta delle righe delle carte per i giocatori
        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player("Player " + (i + 1));
            players.put(player.getName(), player);
            addCardRow(cardPanel, player);
        }

        // Creazione e aggiunta delle carte per il dealer
        Player dealer = new Player("Dealer");
        players.put(dealer.getName(), dealer);
        addCardRow(cardPanel, dealer);

        add(cardPanel, BorderLayout.CENTER);

        // Pannello per il conteggio HI-LO
        JPanel hiloPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hiloPanel.setBackground(Color.WHITE);
        hiloCountLabel = new JLabel("HI-LO Count: " + hiloCount);
        hiloCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        hiloPanel.add(hiloCountLabel);
        add(hiloPanel, BorderLayout.NORTH);

        // Pannello per i consigli, le carte rimanenti e le probabilità
        JPanel advicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        advicePanel.setBackground(Color.WHITE);
        adviceLabel = new JLabel();
        adviceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        advicePanel.add(adviceLabel);

        remainingCardsLabel = new JLabel("Remaining Cards: " + (TOTAL_CARDS_IN_DECK - enteredCardsMap.values().stream().mapToInt(Integer::intValue).sum()));
        remainingCardsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        advicePanel.add(remainingCardsLabel);

        add(advicePanel, BorderLayout.SOUTH);

        // Pannello per le probabilità
        JPanel probPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        probPanel.setBackground(Color.WHITE);
        probabilityLabel = new JLabel();
        probabilityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        probPanel.add(probabilityLabel);
        add(probPanel, BorderLayout.WEST);

        setVisible(true);
    }

    // Metodo per aggiungere una riga di carte per un giocatore
    private void addCardRow(JPanel panel, Player player) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JLabel cardLabel = new JLabel(player.getName());
        labelPanel.add(cardLabel);
        panel.add(labelPanel);

        for (int i = 0; i < CARD_SYMBOLS.length; i++) {
            JPanel cardValuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            JLabel cardSymbolLabel = new JLabel(CARD_SYMBOLS[i]);
            JTextField valueField = new JTextField("0", 5);
            player.setCardValue(CARD_SYMBOLS[i], 0);

            final int index = i;  // Variabile finale necessaria per l'ActionListener
            valueField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        int newValue = Integer.parseInt(valueField.getText());
                        int oldValue = player.getCardValue(CARD_SYMBOLS[index]);
                        player.setCardValue(CARD_SYMBOLS[index], newValue);
                        enteredCardsMap.put(CARD_SYMBOLS[index], enteredCardsMap.getOrDefault(CARD_SYMBOLS[index], 0) - oldValue + newValue);
                        updateHiLoCount();
                        updateAdviceLabel();
                        updateRemainingCardsLabel();
                        updateProbabilityLabel(); // Aggiorna la probabilità quando il valore cambia
                    } catch (NumberFormatException ex) {
                        valueField.setText("0");
                    }
                }
            });

            cardValuePanel.add(cardSymbolLabel);
            cardValuePanel.add(valueField);
            panel.add(cardValuePanel);
        }
    }

    // Metodo per aggiornare il conteggio HI-LO
    private void updateHiLoCount() {
        hiloCount = 0;
        for (Player player : players.values()) {
            hiloCount += player.getHiLoCount();
        }
        hiloCountLabel.setText("HI-LO Count: " + hiloCount);
    }

    // Metodo per aggiornare l'etichetta dei consigli
    private void updateAdviceLabel() {
        if (hiloCount >= 2) {
            adviceLabel.setForeground(Color.GREEN);
            adviceLabel.setText("Consiglio: potresti considerare di aumentare la tua scommessa.");
        } else if (hiloCount <= -2) {
            adviceLabel.setForeground(Color.RED);
            adviceLabel.setText("Consiglio: potresti considerare di ridurre la tua scommessa o di non giocare.");
        } else {
            adviceLabel.setForeground(Color.BLACK);
            // ... (altri consigli)
        }
    }

    // Metodo per calcolare la probabilità di pescare una carta specifica
    private double calculateCardProbability(String card) {
        int remainingOfCard = getRemainingCardCount(card);
        int remainingCards = TOTAL_CARDS_IN_DECK;

        if (remainingCards == 0) {
            return 0.0;
        }

        double probability = (double) remainingOfCard / remainingCards;
        return Math.max(0.0, probability);
    }

    // Metodo per ottenere il numero rimanente di una carta specifica nel mazzo
    private int getRemainingCardCount(String card) {
        int totalOccurrences = NUM_DECKS * 4;
        int drawnOccurrences = enteredCardsMap.getOrDefault(card, 0);
        return totalOccurrences - drawnOccurrences;
    }

    // Metodo per aggiornare l'etichetta delle carte rimanenti
    private void updateRemainingCardsLabel() {
        int remainingCards = TOTAL_CARDS_IN_DECK - enteredCardsMap.values().stream().mapToInt(Integer::intValue).sum();
        remainingCardsLabel.setText("Remaining Cards: " + remainingCards);
    }

    // Metodo per aggiornare l'etichetta delle probabilità
    private void updateProbabilityLabel() {
        // Aggiornamento dell'etichetta delle probabilità
        StringBuilder probabilityText = new StringBuilder("<html><body>Probabilità di pescare le carte:");
        for (int i = 0; i < CARD_SYMBOLS.length; i++) {
            double probability = calculateCardProbability(CARD_SYMBOLS[i]);
            String probabilityFormatted = String.format("%.2f", probability * 100);
            probabilityText.append("<br>").append(CARD_SYMBOLS[i]).append(": ").append(probabilityFormatted).append("%");
        }
        probabilityText.append("</body></html>");
        probabilityLabel.setText(probabilityText.toString());
    }

    // Metodo main per avviare l'applicazione
    public static void main(String[] args) {
        int numPlayers = Integer.parseInt(JOptionPane.showInputDialog("Enter number of players:"));
        SwingUtilities.invokeLater(() -> new BlackjackTable(numPlayers));
    }

    // Classe interna Player rappresenta un giocatore
    private class Player {
        private String name;
        private Map<String, Integer> cardValues = new HashMap<>();

        public Player(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public int getHiLoCount() {
            int count = 0;
            for (Map.Entry<String, Integer> entry : cardValues.entrySet()) {
                int value = getValue(entry.getKey());
                count += value * entry.getValue();
            }
            return count;
        }

        private int getValue(String cardValue) {
            int index = java.util.Arrays.asList(CARD_SYMBOLS).indexOf(cardValue);
            if (index != -1) {
                return CARD_VALUES[index];
            }
            return 0;  // Valore predefinito per le carte sconosciute
        }

        public void setCardValue(String symbol, int value) {
            cardValues.put(symbol, value);
        }

        public int getCardValue(String symbol) {
            return cardValues.getOrDefault(symbol, 0);
        }
    }
}
