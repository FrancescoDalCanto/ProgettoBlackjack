import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class BlackjackTable extends JFrame {

    private static final int NUM_DECKS = 1;
    private static final int TOTAL_CARDS_IN_DECK = 52 * NUM_DECKS;

    private Map<String, Player> players = new HashMap<>();
    private int hiloCount = 0;
    private JLabel hiloCountLabel;
    private JLabel adviceLabel;
    private JLabel remainingCardsLabel;
    private Map<String, Integer> enteredCardsMap = new HashMap<>();
    private Map<String, Integer> removedCardsMap = new HashMap<>();
    private JLabel probabilityLabel;

    private static final String[] CARD_SYMBOLS = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final int[] CARD_VALUES = {1, 1, 1, 1, 1, 0, 0, 0, -1, -1, -1, -1, 0};

    private int decimalPrecision = 2; // Precisione predefinita

    public BlackjackTable(int numPlayers) {
        setTitle("Blackjack HI-LO Counter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLayout(new BorderLayout());

        JPanel cardPanel = new JPanel(new GridLayout(numPlayers + 1, 5, 10, 10));

        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player("Player " + (i + 1));
            players.put(player.getName(), player);
            addCardRow(cardPanel, player);
        }

        Player dealer = new Player("Dealer");
        players.put(dealer.getName(), dealer);
        addCardRow(cardPanel, dealer);

        add(cardPanel, BorderLayout.CENTER);

        JPanel hiloPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        hiloPanel.setBackground(Color.WHITE);
        hiloCountLabel = new JLabel("HI-LO Count: " + hiloCount);
        hiloCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        hiloPanel.add(hiloCountLabel);
        add(hiloPanel, BorderLayout.NORTH);

        JPanel advicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        advicePanel.setBackground(Color.WHITE);
        adviceLabel = new JLabel();
        adviceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        advicePanel.add(adviceLabel);

        remainingCardsLabel = new JLabel("Remaining Cards: " + (TOTAL_CARDS_IN_DECK - enteredCardsMap.values().stream().mapToInt(Integer::intValue).sum() + removedCardsMap.values().stream().mapToInt(Integer::intValue).sum()));
        remainingCardsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        advicePanel.add(remainingCardsLabel);

        add(advicePanel, BorderLayout.SOUTH);

        JPanel probPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        probPanel.setBackground(Color.WHITE);
        probabilityLabel = new JLabel();
        probabilityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        probPanel.add(probabilityLabel);
        add(probPanel, BorderLayout.WEST);

        setVisible(true);
    }

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
    
            final int index = i;  // Need a final variable for ActionListener
            valueField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        int newValue = Integer.parseInt(valueField.getText());
                        int oldValue = player.getCardValue(CARD_SYMBOLS[index]);
                        player.setCardValue(CARD_SYMBOLS[index], newValue);
                        enteredCardsMap.put(CARD_SYMBOLS[index], enteredCardsMap.getOrDefault(CARD_SYMBOLS[index], 0) + newValue - oldValue);
                        updateHiLoCount();
                        updateAdviceLabel(player);
                        updateRemainingCardsLabel();
                    } catch (NumberFormatException ex) {
                        valueField.setText("0");
                    }
                }
            });
    
            cardValuePanel.add(cardSymbolLabel);
            cardValuePanel.add(valueField);
    
            // Aggiungi il campo per la percentuale di vittoria
            JTextField winPercentageField = new JTextField("0%", 5);
            player.setWinPercentage(0);
    
            winPercentageField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String input = winPercentageField.getText();
                        input = input.replaceAll("%", "").trim();
                        int newValue = Integer.parseInt(input);
                        player.setWinPercentage(newValue);
                        updateAdviceLabel(player); // Aggiorna il consiglio quando la percentuale cambia
                    } catch (NumberFormatException ex) {
                        winPercentageField.setText("0%");
                    }
                }
            });
    
            cardValuePanel.add(winPercentageField);
    
            panel.add(cardValuePanel);
        }
    }
    

    private void updateHiLoCount() {
        hiloCount = 0;
        for (Player player : players.values()) {
            hiloCount += player.getHiLoCount();
        }
        hiloCountLabel.setText("HI-LO Count: " + hiloCount);
    }

    private void updateAdviceLabel(Player player) {
        if (hiloCount >= 2) {
            adviceLabel.setForeground(Color.GREEN);
            adviceLabel.setText("Consiglio: potresti considerare di aumentare la tua scommessa. Percentuale di vittoria: " + player.getWinPercentage() + "%");
        } else if (hiloCount <= -2) {
            adviceLabel.setForeground(Color.RED);
            adviceLabel.setText("Consiglio: potresti considerare di ridurre la tua scommessa o di non giocare." + player.getWinPercentage() + "%");
        } else {
            adviceLabel.setForeground(Color.BLACK);
            adviceLabel.setText("Per il momento non fare nulla, aspetta altre carte.");
        }

        StringBuilder probabilityText = new StringBuilder("<html><body>Probabilità di tirare le carte:");
        for (int i = 0; i < CARD_SYMBOLS.length; i++) {
            double probability = calculateCardProbability(CARD_SYMBOLS[i]);
            String formattedProbability = formatProbability(probability);
            probabilityText.append("<br>").append(CARD_SYMBOLS[i]).append(": ").append(formattedProbability);
        }
        probabilityText.append("</body></html>");
        probabilityLabel.setText(probabilityText.toString());
    }

    private String formatProbability(double probability) {
        String format = "##0." + "0".repeat(decimalPrecision) + "%";
        return new DecimalFormat(format).format(probability);
    }

    private double calculateCardProbability(String card) {
        int remainingOfCard = getRemainingCardCount(card);
        int totalCardsInDecks = 52 * players.size();
        int remainingCards = totalCardsInDecks - enteredCardsMap.values().stream().mapToInt(Integer::intValue).sum() + removedCardsMap.values().stream().mapToInt(Integer::intValue).sum();

        if (remainingCards == 0) {
            return 0.0;
        }

        double probability = (double) remainingOfCard / remainingCards;
        return Math.max(0.0, probability);
    }

    private int getRemainingCardCount(String card) {
        int totalOccurrences = 4 * players.size();
        int drawnOccurrences = enteredCardsMap.getOrDefault(card, 0) - removedCardsMap.getOrDefault(card, 0);
        return totalOccurrences - drawnOccurrences;
    }

    private void updateRemainingCardsLabel() {
        int remainingCards = TOTAL_CARDS_IN_DECK - enteredCardsMap.values().stream().mapToInt(Integer::intValue).sum() + removedCardsMap.values().stream().mapToInt(Integer::intValue).sum();
        remainingCardsLabel.setText("Remaining Cards: " + remainingCards);
    }

    public static void main(String[] args) {
        int numPlayers = Integer.parseInt(JOptionPane.showInputDialog("Enter number of players:"));
        SwingUtilities.invokeLater(() -> new BlackjackTable(numPlayers));
    }

    private class Player {
        private String name;
        private Map<String, Integer> cardValues = new HashMap<>();
        private int winPercentage;
        
        public Player(String name) {
            this.name = name;
            this.winPercentage = 0; // Imposta la percentuale di vittoria iniziale a 0.
        }
        
        
        public void setWinPercentage(int percentage) {
            winPercentage = percentage;
        }
        
        public int getWinPercentage() {
            return winPercentage;
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
            return 0;  // Default value for unknown cards
        }

        public void setCardValue(String symbol, int value) {
            cardValues.put(symbol, value);
        }

        public int getCardValue(String symbol) {
            return cardValues.getOrDefault(symbol, 0);
        }
    }
}