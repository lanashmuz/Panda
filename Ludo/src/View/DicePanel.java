package View;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import Model.StepsDie;
import java.util.function.IntConsumer;

public class DicePanel extends JPanel {
	private final JButton rollButton = new JButton("Roll Dice");
	private final JLabel resultLabel = new JLabel("Result: -");
	private final StepsDie stepsDie = new StepsDie();
	private IntConsumer onRoll;
	
	public DicePanel() {
	    setLayout(new FlowLayout());

	    add(rollButton);
	    add(resultLabel);
	    
	    // Rolls the die and displays the result
		rollButton.addActionListener(e -> {
		    
			// Lock the button until the roll is handled.
		    rollButton.setEnabled(false);
		    int result = stepsDie.roll();
		    resultLabel.setText("Result: " + result);
		    if (onRoll != null) {
		        onRoll.accept(result);
		    }
		});
	}
	
	// Sets the action to perform after rolling the die
	public void setOnRoll(IntConsumer onRoll) {
	    this.onRoll = onRoll;
	}
	
	// give controller option to restart the dice
	public void setRollEnabled(boolean enabled) {
	    rollButton.setEnabled(enabled);
	}

}
