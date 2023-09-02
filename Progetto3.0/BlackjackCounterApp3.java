import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BlackjackCounterApp3 extends JFrame {

    private static final String[] VALORI = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final int[][] D = { /* ... */ };
    private static final int[][] B = { /* ... */ };
    private static final int[][] C = { /* ... */ };

    private int[][] A;

    private JLabel totalLabel;
    private JLabel suggestionLabel;
    private JTextField[] cardCountFields;

    public BlackjackCounterApp3() {
        A = new int[22][11];
        int[][] z = new int[5][10];
        int[][] u = new int[7][10];
        for (int i = 0; i < 22; i++) {
            for (int j = 0; j < 11; j++) {
                if (i >= 2 && i <= 21) {
                    if (i >= 2 && i <= 12) {
                        A[i][j] = z[i - 2][j];
                    } else if (i >= 13 && i <= 19) {
                        A[i][j] = D[i - 13][j];
                    } else {
                        A[i][j] = u[i - 20][j];
                    }
                }
            }
        }

        initializeUI();
    }

    private void initializeUI() {
        setTitle("Blackjack Card Counter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        JPanel cardCountPanel = new JPanel(new GridLayout(0, 7, 10, 10));
        cardCountPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cardCountFields = new JTextField[VALORI.length];
        for (int i = 0; i < VALORI.length; i++) {
            cardCountFields[i] = new JTextField(5);
            ((AbstractDocument) cardCountFields[i].getDocument()).setDocumentFilter(new IntegerDocumentFilter());
            cardCountPanel.add(new JLabel(VALORI[i]));
            JPanel textFieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            textFieldPanel.add(cardCountFields[i]);
            cardCountPanel.add(textFieldPanel);
        }

        JPanel buttonPanel = new JPanel();
        JButton countButton = new JButton("Count");
        countButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });
        buttonPanel.add(countButton);

        totalLabel = new JLabel("Total Value: ");
        suggestionLabel = new JLabel("");

        add(cardCountPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(totalLabel, BorderLayout.NORTH);
        add(suggestionLabel, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private void calculateTotal() {
        int aceCount = Integer.parseInt(cardCountFields[13].getText());

        int totalValue = 0;
        int subt = 0;
        for (int i = 0; i < VALORI.length; i++) {
            int value = Integer.parseInt(cardCountFields[i].getText());
            totalValue += value * (VALORI[i].equals("A") ? 11 : VALORI[i].equals("J") || VALORI[i].equals("Q") || VALORI[i].equals("K") ? 10 : Integer.parseInt(VALORI[i]));
        }

        for (int i = 0; i < aceCount; i++) {
            if (totalValue > 21) {
                totalValue -= 10;
                subt = 1;
            }
        }
        totalLabel.setText("Somma dei valori: " + totalValue);

        int dealerCard = 0;  // Get the dealer's face-up card

        String suggestion = decideMossa(totalValue, dealerCard, subt);
        suggestionLabel.setText("Mossa suggerita: " + suggestion);
    }

    private String decideMossa(int totalValue, int dealerCard, int subt) {
        if (totalValue > 21) {
            return "sballato (punteggio superiore a 21)";
        }

        int split = B[totalValue / 2 - 1][dealerCard];
        int aceStrategy = C[totalValue - 3 - 10 * subt][dealerCard];
        int hitOrStay = A[totalValue - 2][dealerCard];

        if (canDivide() && split == 1 && twoCards()) {
            return "dividi";
        }

        if (hasAce() && twoCards()) {
            if (aceStrategy == 1) {
                return "nuova carta";
            }
            if (aceStrategy == 2) {
                return "raddoppia";
            } else if (aceStrategy == 0) {
                return "stai";
            }
        }

        if (hitOrStay == 1) {
            return "nuova carta";
        } else if (hitOrStay == 2) {
            return "raddoppia";
        } else if (hitOrStay == 0) {
            return "stai";
        }

        return "";
    }

    private boolean canDivide() {
        for (int i = 0; i < VALORI.length; i++) {
            if (Integer.parseInt(cardCountFields[i].getText()) >= 2) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAce() {
        return Integer.parseInt(cardCountFields[13].getText()) > 0;
    }

    private boolean twoCards() {
        int cardCountSum = 0;
        for (int i = 0; i < VALORI.length; i++) {
            cardCountSum += Integer.parseInt(cardCountFields[i].getText());
        }
        return cardCountSum == 2;
    }

    private class IntegerDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String text, AttributeSet attr) throws BadLocationException {
            if (text.matches("\\d+")) {
                super.insertString(fb, offset, text, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text.matches("\\d+")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BlackjackCounterApp());
    }
}