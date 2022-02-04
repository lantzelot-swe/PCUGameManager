package se.lantz.gui.gameview;

import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;

import se.lantz.model.data.GameView;
import se.lantz.model.data.ViewFilter;

import java.awt.Insets;

public class ViewNamePanel extends JPanel
{
  private JLabel nameLabel;
  private JTextField textField;
  private GameView gameView;

  public ViewNamePanel(GameView gameView)
  {
    this.gameView = gameView;
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[]{0, 0};
    gridBagLayout.rowHeights = new int[]{0, 0, 0};
    gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);
    GridBagConstraints gbc_nameLabel = new GridBagConstraints();
    gbc_nameLabel.anchor = GridBagConstraints.WEST;
    gbc_nameLabel.insets = new Insets(10, 5, 0, 5);
    gbc_nameLabel.gridx = 0;
    gbc_nameLabel.gridy = 0;
    add(getNameLabel(), gbc_nameLabel);
    GridBagConstraints gbc_textField = new GridBagConstraints();
    gbc_textField.insets = new Insets(0, 5, 5, 5);
    gbc_textField.anchor = GridBagConstraints.WEST;
    gbc_textField.fill = GridBagConstraints.HORIZONTAL;
    gbc_textField.gridx = 0;
    gbc_textField.gridy = 1;
    add(getTextField(), gbc_textField);
    
    getTextField().setText(gameView.getName());
  }
  private JLabel getNameLabel() {
    if (nameLabel == null) {
    	nameLabel = new JLabel("Gamelist view name");
    }
    return nameLabel;
  }
  protected JTextField getTextField() {
    if (textField == null) {
    	textField = new JTextField();
    	textField.setColumns(10);
    }
    return textField;
  }
  
  public void updateGameView()
  {
    //Set name in game view from UI
    this.gameView.setName(getTextField().getText());
  }
}
