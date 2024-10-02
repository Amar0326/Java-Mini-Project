import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PurblePairs extends JFrame {
    private JButton[] buttons;
    private int[] cardValues;
    private boolean[] cardRevealed;
    private int firstCardIndex = -1;
    private int secondCardIndex = -1;
    private int pairsFound = 0;
    private int totalPairs; // Total pairs of cards
    private javax.swing.Timer timer; // Specify the javax.swing.Timer
    private int gridSize; // Size of the grid (4, 5, etc.)

    public PurblePairs() {
        gridSize = 4; // Start with a 4x4 grid
        totalPairs = (gridSize * gridSize) / 2; // Total pairs for the current grid size
        setTitle("Purble Pairs");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(gridSize, gridSize));

        buttons = new JButton[gridSize * gridSize];
        cardValues = new int[gridSize * gridSize];
        cardRevealed = new boolean[gridSize * gridSize];

        initializeCards();
        createButtons();

        timer = new javax.swing.Timer(1000, e -> hideCards());
    }

    private void initializeCards() {
        List<Integer> values = new ArrayList<>();
        for (int i = 0; i < totalPairs; i++) {
            values.add(i);
            values.add(i);
        }
        Collections.shuffle(values);
        for (int i = 0; i < cardValues.length; i++) {
            cardValues[i] = values.get(i); // Ensure this index is within bounds
        }
    }

    private void createButtons() {
        for (int i = 0; i < buttons.length; i++) {
            buttons[i] = new JButton();
            buttons[i].setFont(new Font("Arial", Font.BOLD, 40));
            buttons[i].addActionListener(new CardClickListener(i));
            add(buttons[i]);
        }
    }

    private class CardClickListener implements ActionListener {
        private int index;

        public CardClickListener(int index) {
            this.index = index;
        }

        public void actionPerformed(ActionEvent e) {
            if (cardRevealed[index] || secondCardIndex != -1) {
                return; // Ignore clicks on revealed cards or if two cards are already selected
            }
            revealCard(index);
            if (firstCardIndex == -1) {
                firstCardIndex = index; // Store first card index
            } else {
                secondCardIndex = index; // Store second card index
                timer.start(); // Start timer to check for a match
            }
        }
    }

    private void revealCard(int index) {
        buttons[index].setText(String.valueOf(cardValues[index]));
        cardRevealed[index] = true;
    }

    private void hideCards() {
        if (firstCardIndex != -1 && secondCardIndex != -1) {
            if (cardValues[firstCardIndex] == cardValues[secondCardIndex]) {
                pairsFound++;
                if (pairsFound == totalPairs) {
                    // Ask user if they want to play again or quit
                    int response = JOptionPane.showConfirmDialog(this, 
                        "You found all pairs! Do you want to play again?", 
                        "Game Over", 
                        JOptionPane.YES_NO_OPTION);
                    if (response == JOptionPane.YES_OPTION) {
                        resetGame(true); // Reset the game to initial state
                    } else {
                        System.exit(0); // Exit the game if user chooses not to play again
                    }
                }
            } else {
                buttons[firstCardIndex].setText("");
                buttons[secondCardIndex].setText("");
                cardRevealed[firstCardIndex] = false;
                cardRevealed[secondCardIndex] = false;
            }
            firstCardIndex = -1;
            secondCardIndex = -1;
            timer.stop();
        }
    }

    private void resetGame(boolean isNewGame) {
        if (isNewGame) {
            // Reset to initial 4x4 grid
            gridSize = 4; // Always reset to 4x4 when playing again
        } else {
            // Increment grid size for next game
            gridSize++;
        }
        totalPairs = (gridSize * gridSize) / 2;

        // Reset the JFrame for a new game
        getContentPane().removeAll(); // Remove all components
        setLayout(new GridLayout(gridSize, gridSize)); // Set new layout

        buttons = new JButton[gridSize * gridSize];
        cardValues = new int[gridSize * gridSize];
        cardRevealed = new boolean[gridSize * gridSize];

        initializeCards();
        createButtons();
        
        pairsFound = 0; // Reset pairs found count
        setSize(400 + (gridSize - 4) * 50, 400 + (gridSize - 4) * 50); // Adjust window size for larger grids
        revalidate(); // Revalidate frame to refresh the components
        repaint(); // Repaint the frame
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PurblePairs game = new PurblePairs();
            game.setVisible(true);
        });
    }
}
