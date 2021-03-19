package se.lantz.gui;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class BaseDialog extends JDialog
{
  private JPanel backgroundPanel;
  private JPanel buttonPanel;
  private JButton okButton;
  private JButton cancelButton;

  KeyStroke escKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);

  Action escAction = new AbstractAction()
    {
      private static final long serialVersionUID = 1756396251349970052L;

      @Override
      public void actionPerformed(ActionEvent e)
      {
        okPressed = false;
        setVisible(false);
      }
    };
  private boolean okPressed = false;

  public BaseDialog(Frame owner)
  {
    super(owner, true);
    this.setIconImage(new ImageIcon(getClass().getResource("/se/lantz/FrameIcon.png")).getImage());
    getContentPane().add(getBackgroundPanel(), BorderLayout.CENTER);
    this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    //Register esc as closing the window
    getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escKeyStroke, "closeDialog");
    getRootPane().getActionMap().put("closeDialog", escAction);
    getRootPane().setDefaultButton(getOkButton());
  }

  protected void addContent(JPanel panel)
  {
    getBackgroundPanel().add(panel, BorderLayout.CENTER);
  }

  private JPanel getBackgroundPanel()
  {
    if (backgroundPanel == null)
    {
      backgroundPanel = new JPanel();
      backgroundPanel.setLayout(new BorderLayout(0, 0));
      backgroundPanel.add(getButtonPanel(), BorderLayout.SOUTH);
    }
    return backgroundPanel;
  }

  private JPanel getButtonPanel()
  {
    if (buttonPanel == null)
    {
      buttonPanel = new JPanel();
      GridBagLayout gbl_buttonPanel = new GridBagLayout();
      gbl_buttonPanel.columnWidths = new int[] { 0, 0, 0 };
      gbl_buttonPanel.rowHeights = new int[] { 0, 0 };
      gbl_buttonPanel.columnWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
      gbl_buttonPanel.rowWeights = new double[] { 0.0, Double.MIN_VALUE };
      buttonPanel.setLayout(gbl_buttonPanel);
      GridBagConstraints gbc_okButton = new GridBagConstraints();
      gbc_okButton.anchor = GridBagConstraints.EAST;
      gbc_okButton.weightx = 0.5;
      gbc_okButton.insets = new Insets(5, 5, 5, 5);
      gbc_okButton.gridx = 0;
      gbc_okButton.gridy = 0;
      buttonPanel.add(getOkButton(), gbc_okButton);
      GridBagConstraints gbc_cancelButton = new GridBagConstraints();
      gbc_cancelButton.weightx = 0.5;
      gbc_cancelButton.insets = new Insets(5, 5, 5, 5);
      gbc_cancelButton.anchor = GridBagConstraints.WEST;
      gbc_cancelButton.gridx = 1;
      gbc_cancelButton.gridy = 0;
      buttonPanel.add(getCancelButton(), gbc_cancelButton);
    }
    return buttonPanel;
  }

  public JButton getOkButton()
  {
    if (okButton == null)
    {
      okButton = new JButton("OK");
      okButton.addActionListener(e -> {
        okPressed = true;
        setVisible(false);
      });
      okButton.setPreferredSize(getCancelButton().getPreferredSize());
    }
    return okButton;
  }

  protected JButton getCancelButton()
  {
    if (cancelButton == null)
    {
      cancelButton = new JButton("Cancel");
      cancelButton.addActionListener(e -> {
        okPressed = false;
        setVisible(false);
      });
    }
    return cancelButton;
  }

  public boolean showDialog()
  {
    okPressed = false;
    this.setVisible(true);
    return okPressed;
  }
}
