import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class BlackjackCounterApp {

    private final String[] VALORI = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

    private int numero_mazzi = 6;

    private int[] A = new int[13];
    private Map<String, JTextField> cardCountMap = new HashMap<>();

    private JFrame frame;
    private JLabel totalLabel;
    private JLabel suggestionLabel;

    public BlackjackCounterApp() {
        frame = new JFrame("Blackjack Card Counter");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        for (String valore : VALORI) {
            cardCountMap.put(valore, new JTextField("0", 5));
            A[getIndexFromValue(valore)] = numero_mazzi * 4;

            JLabel label = new JLabel(valore);
            inputPanel.add(label);

            JTextField textField = cardCountMap.get(valore);
            inputPanel.add(textField);
        }

        JPanel buttonPanel = new JPanel();
        JButton countButton = new JButton("Conta");
        countButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateTotal();
            }
        });
        buttonPanel.add(countButton);

        frame.add(inputPanel, BorderLayout.CENTER);

        totalLabel = new JLabel("Somma dei valori: ");
        frame.add(totalLabel, BorderLayout.NORTH);

        suggestionLabel = new JLabel("");
        frame.add(suggestionLabel, BorderLayout.SOUTH);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    private void calculateTotal() {
        int[] totalPlayers = new int[7];

        for (int i = 0; i < 7; i++) {
            int aceCount = Integer.parseInt(cardCountMap.get("A").getText());

            int totalValue = 0;
            for (String valore : VALORI) {
                int valueCount = Integer.parseInt(cardCountMap.get(valore).getText());
                totalValue += valueCount * (valore.equals("J") || valore.equals("Q") || valore.equals("K") ? 10 : valore.equals("A") ? 11 : Integer.parseInt(valore));
            }

            for (int j = 0; j < aceCount; j++) {
                if (totalValue > 21) {
                    totalValue -= 10;
                }
            }

            totalPlayers[i] = totalValue;

            for (String valore : VALORI) {
                int valoreCount = Integer.parseInt(cardCountMap.get(valore).getText());
                A[getIndexFromValue(valore)] -= valoreCount;
            }
        }

        totalLabel.setText("Somma dei valori: " + totalPlayers[0]); // Aggiorna l'etichetta con il valore desiderato
        // Esegui le operazioni di decisione qui e aggiorna l'etichetta delle suggestioni come richiesto nel tuo codice originale.
    }

    private int getIndexFromValue(String valore) {
        for (int i = 0; i < VALORI.length; i++) {
            if (VALORI[i].equals(valore)) {
                return i;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BlackjackCounterApp();
            }
        });
    }
}
