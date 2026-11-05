package View;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import Model.StepsDie;

public class DicePanel extends JPanel {
	private final JButton rollButton = new JButton("Roll Dice");
	private final JLabel resultLabel = new JLabel("Result: -");
	private final StepsDie stepsDie = new StepsDie();
	
	public DicePanel() {
	    setLayout(new FlowLayout());

	    add(rollButton);
	    add(resultLabel);
	    
	    // Rolls the die and displays the result
	    rollButton.addActionListener(e -> {
	    	int result = stepsDie.roll();
	    	resultLabel.setText("Result: " + result);
	    });
	}

}
