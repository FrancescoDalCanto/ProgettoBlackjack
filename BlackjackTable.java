import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class BlackjackTable extends JFrame {

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

    private int decimalPrecision = 2;

    public BlackjackTable(int numPlayers, int numDecks) {
        setTitle("Blackjack HI-LO Counter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 650);
        setLayout(new BorderLayout());

        int totalCardsInDecks = 52 * numDecks;

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

        remainingCardsLabel = new JLabel("Remaining Cards: " + (totalCardsInDecks - enteredCardsMap.values().stream().mapToInt(Integer::intValue).sum() + removedCardsMap.values().stream().mapToInt(Integer::intValue).sum()));
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

            final int index = i;
            valueField.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        int newValue = Integer.parseInt(valueField.getText());
                        int oldValue = player.getCardValue(CARD_SYMBOLS[index]);
                        player.setCardValue(CARD_SYMBOLS[index], newValue);
                        enteredCardsMap.put(CARD_SYMBOLS[index], enteredCardsMap.getOrDefault(CARD_SYMBOLS[index], 0) + newValue - oldValue);
                        updateHiLoCount();
                        updateAdviceLabel();
                        updateRemainingCardsLabel();
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

    private void updateHiLoCount() {
        hiloCount = 0;
        for (Player player : players.values()) {
            hiloCount += player.getHiLoCount();
        }
        hiloCountLabel.setText("HI-LO Count: " + hiloCount);
    }

    private void updateAdviceLabel() {
        if (hiloCount >= 2) {
            adviceLabel.setForeground(Color.GREEN);
            adviceLabel.setText("Consiglio: potresti considerare di aumentare la tua scommessa.");
        } else if (hiloCount <= -2) {
            adviceLabel.setForeground(Color.RED);
            adviceLabel.setText("Consiglio: potresti considerare di ridurre la tua scommessa o di non giocare.");
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
        int totalCardsInDecks = 52 * players.size();
        int remainingCards = totalCardsInDecks - enteredCardsMap.values().stream().mapToInt(Integer::intValue).sum() + removedCardsMap.values().stream().mapToInt(Integer::intValue).sum();
        remainingCardsLabel.setText("Remaining Cards: " + remainingCards);
    }

    public static void main(String[] args) {
        int numPlayers = Integer.parseInt(JOptionPane.showInputDialog("Enter number of players:"));
        int numDecks = Integer.parseInt(JOptionPane.showInputDialog("Enter number of decks:"));
        SwingUtilities.invokeLater(() -> new BlackjackTable(numPlayers, numDecks));
    }

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
