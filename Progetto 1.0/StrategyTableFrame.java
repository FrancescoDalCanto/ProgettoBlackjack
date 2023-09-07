import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StrategyTableFrame extends JFrame {

    private JTextArea strategyTable;

    public StrategyTableFrame() {
        setTitle("Blackjack Strategy Table");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        strategyTable = new JTextArea();
        strategyTable.setFont(new Font("Monospaced", Font.PLAIN, 14));
        strategyTable.setEditable(false);
        strategyTable.setText(getStrategyTableText1());

        JScrollPane scrollPane = new JScrollPane(strategyTable);
        add(scrollPane, BorderLayout.CENTER);

        JButton switchTable1Button = new JButton("Switch to Table 1");
        switchTable1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                strategyTable.setText(getStrategyTableText1());
            }
        });

        JButton switchTable2Button = new JButton("Switch to Table 2");
        switchTable2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                strategyTable.setText(getStrategyTableText2());
            }
        });

        JButton switchTable3Button = new JButton("Switch to Table 3");
        switchTable3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                strategyTable.setText(getStrategyTableText3());
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(switchTable1Button);
        buttonPanel.add(switchTable2Button);
        buttonPanel.add(switchTable3Button);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private String getStrategyTableText1() {
        return
            "+------------------------------------------------------------------------------+\n" +
            "|  Giocata / Carta del Banco |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  9 | 10 | A  |\n" +
            "+----------------------------+----+----+----+----+----+----+----+----+----+--- +\n" +
            "|  17                         | S  | S  | S  | S  | S  | S  | S  | S  | S  | S |\n" +
            "|  16                         | S  | S  | S  | S  | S  | c  | c  | c  | c  | c |\n" +
            "|  15                         | S  | S  | S  | S  | S  | c  | c  | c  | H  | H |\n" +
            "|  13-14                      | S  | S  | S  | S  | S  | c  | c  | c  | H  | H |\n" +
            "|  12                         | H  | H  | S  | S  | S  | c  | c  | c  | H  | H |\n" +
            "|  11                         | D  | D  | D  | D  | D  | c  | c  | c  | D  | H |\n" +
            "|  10                         | D  | D  | D  | D  | D  | D  | D  | D  | H  | H |\n" +
            "|  9                          | H  | D  | D  | D  | D  | H  | H  | H  | H  | H |\n" +
            "|  5-8                        | H  | H  | H  | H  | H  | H  | H  | H  | H  | H |\n" +
            "+------------------------------------------------------------------------------+\n" +
            "Legenda: S = Stand, H = Hit, D = Double if allowed, otherwise Hit";
    }

    private String getStrategyTableText2() {
        return
            "+------------------------------------------------------------------------------+\n" +
            "|  Giocata / Carta del Banco |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  9 | 10 | A  |\n" +
            "+----------------------------+----+----+----+----+----+----+----+----+----+--- +\n" +
            "|  A,8,A,9                    | S  | S  | S  | S  | S  | S  | S  | S  | S  | S |\n" +
            "|  A,7                        | S  | D  | D  | D  | D  | S  | S  | H  | H  | H |\n" +
            "|  A,6                        | H  | D  | D  | D  | D  | H  | H  | H  | H  | H |\n" +
            "|  A,4,A,5                    | H  | H  | D  | D  | D  | H  | H  | H  | H  | H |\n" +
            "|  A,2,A,3                    | H  | H  | H  | D  | D  | H  | H  | H  | H  | H |\n" +
            "+------------------------------------------------------------------------------+\n" +
            "Legenda: S = Stand, H = Hit, D = Double if allowed, otherwise Hit";
    }

    private String getStrategyTableText3() {
        return
            "+------------------------------------------------------------------------------+\n" +
            "|  Giocata / Carta del Banco |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  9 | 10 | A  |\n" +
            "+----------------------------+----+----+----+----+----+----+----+----+----+--- +\n" +
            "|  A,A                       | SP | SP | SP | SP | SP | SP | SP | SP | SP | SP |\n" +
            "|  10,10                     | S  | S  | S  | S  | S  | S  | S  | S  | S  | S  |\n" +
            "|  9,9                       | SP | SP | SP | SP | SP | S  | SP | SP | S  | S  |\n" +
            "|  8,8                       | SP | SP | SP | SP | SP | SP | SP | SP | SP | SP |\n" +
            "|  7,7                       | SP | SP | SP | SP | SP | SP | H  | H  | H  | H  |\n" +
            "|  6,6                       | SP | SP | SP | SP | SP | H  | H  | H  | H  | H  |\n" +
            "|  5,5                       | D  | D  | D  | D  | D  | D  | D  | D  | H  | H  |\n" +
            "|  4,4                       | H  | H  | H  | SP | SP | H  | H  | H  | H  | H  |\n" +
            "|  2,2 3,3                   | SP | SP | Sp | SP | SP | SP | H  | H  | H  | H  |\n" +
            "+------------------------------------------------------------------------------+\n" +
            "Legenda: S = Stand, H = Hit, D = Double if allowed, otherwise Hit";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StrategyTableFrame());
    }
}
