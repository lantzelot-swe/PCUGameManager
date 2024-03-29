package se.lantz.gui.carousel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.Beans;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import se.lantz.model.carousel.CarouselPreviewModel;
import se.lantz.model.data.GameDetails;
import se.lantz.util.GenreMap;

public class TextPanel extends JPanel
{
  private JLabel titleLabel;
  private JTextPane textPane;
  private JLabel authorLabel;
  private JLabel composerLabel;
  private JLabel genreLabel;
  private JLabel yearLabel;
  private CarouselPreviewModel model;
  private GenreMap genreMap = new GenreMap();

  public TextPanel(final CarouselPreviewModel model)
  {
    this.model = model;
    setFont(new Font("Microsoft Sans Serif", Font.BOLD, 20));
    setOpaque(false);
    GridBagLayout gridBagLayout = new GridBagLayout();
    setLayout(gridBagLayout);
    GridBagConstraints gbc_titleLabel = new GridBagConstraints();
    gbc_titleLabel.gridwidth = 2;
    gbc_titleLabel.weightx = 1.0;
    gbc_titleLabel.insets = new Insets(17, 20, 6, 5);
    gbc_titleLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_titleLabel.fill = GridBagConstraints.HORIZONTAL;
    gbc_titleLabel.gridx = 0;
    gbc_titleLabel.gridy = 0;
    add(getTitleLabel(), gbc_titleLabel);
    GridBagConstraints gbc_textArea = new GridBagConstraints();
    gbc_textArea.gridwidth = 2;
    gbc_textArea.anchor = GridBagConstraints.NORTHWEST;
    gbc_textArea.insets = new Insets(5, 25, 5, 40);
    gbc_textArea.fill = GridBagConstraints.BOTH;
    gbc_textArea.gridx = 0;
    gbc_textArea.gridy = 1;
    add(getTextPane(), gbc_textArea);
    GridBagConstraints gbc_authorLabel = new GridBagConstraints();
    gbc_authorLabel.gridwidth = 2;
    gbc_authorLabel.insets = new Insets(6, 145, 0, 0);
    gbc_authorLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_authorLabel.gridx = 0;
    gbc_authorLabel.gridy = 2;
    add(getAuthorLabel(), gbc_authorLabel);
    GridBagConstraints gbc_composerLabel = new GridBagConstraints();
    gbc_composerLabel.gridwidth = 2;
    gbc_composerLabel.insets = new Insets(-2, 145, 0, 0);
    gbc_composerLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_composerLabel.gridx = 0;
    gbc_composerLabel.gridy = 3;
    add(getComposerLabel(), gbc_composerLabel);
    GridBagConstraints gbc_genreLabel = new GridBagConstraints();
    gbc_genreLabel.weightx = 1.0;
    gbc_genreLabel.insets = new Insets(-2, 145, 0, 0);
    gbc_genreLabel.weighty = 1.0;
    gbc_genreLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_genreLabel.gridx = 0;
    gbc_genreLabel.gridy = 4;
    add(getGenreLabel(), gbc_genreLabel);
    GridBagConstraints gbc_yearLabel = new GridBagConstraints();
    gbc_yearLabel.anchor = GridBagConstraints.NORTHWEST;
    gbc_yearLabel.insets = new Insets(-2, 0, 0, 45);
    gbc_yearLabel.gridx = 1;
    gbc_yearLabel.gridy = 4;
    add(getYearLabel(), gbc_yearLabel);
    if (!Beans.isDesignTime())
    {
      model.addPropertyChangeListener(CarouselPreviewModel.SELECTED_GAME, e -> selectedGameChanged());
      //trigger once at startup
      selectedGameChanged();
    }
  }

  private void selectedGameChanged()
  {
    GameDetails selectedGame = model.getSelectedGame();
    if (selectedGame != null)
    {
      SwingUtilities.invokeLater(() -> {
        getTitleLabel().setText(truncateText(getTitleLabel(), selectedGame.getTitle(), 600));
        getTextPane().setText(selectedGame.getDescription());
        getAuthorLabel().setText(selectedGame.getAuthor().isEmpty()
          ? "-"
          : truncateText(getAuthorLabel(), selectedGame.getAuthor(), 460));
        getComposerLabel().setText(selectedGame.getComposer().isEmpty()
          ? "-"
          : truncateText(getComposerLabel(), selectedGame.getComposer(), 460));
        getGenreLabel().setText(genreMap.get(selectedGame.getGenre()));
        getYearLabel().setText(selectedGame.getYear() + "");
      });

    }
  }

  private String truncateText(JLabel label, String text, int width)
  {
    String returnText = text;
    Graphics graphics = label.getGraphics();
    if (graphics != null)
    {
      int length = graphics.getFontMetrics().stringWidth(text);
      while (length > width)
      {
        returnText = returnText.substring(0, returnText.length() - 1);
        length = graphics.getFontMetrics().stringWidth(returnText + "...");
        if (length <= (width))
        {
          returnText = returnText + "...";
        }
      }
    }
    return returnText;
  }

  private JLabel getTitleLabel()
  {
    if (titleLabel == null)
    {
      titleLabel = new JLabel(" ");
      titleLabel.setBackground(Color.ORANGE);
      titleLabel.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 36));
    }
    return titleLabel;
  }

  private JTextPane getTextPane()
  {
    if (textPane == null)
    {
      textPane = new JTextPane();
      textPane.setForeground(Color.WHITE);
      textPane.setFont(new Font("Verdana", Font.PLAIN, 21));
      textPane.setOpaque(false);
      //      String text512 = "In Monty on the run the intrepid coal thief Monty Mole fled to the rocky island of Gibraltar. However, the Intermole agency is on to him, and his only hope of escape is to trek across Europe, collecting enough cash to buy the Greek island of Montos and live there in luxury.This is Monty's third game, and the structure is similar to the previous three. There are 80 screens each representing some area of Europe.There are many items to collect, the most important are Eurocheques for money and airplane tickets.";
      //      String textCalGames = "Welcome to California. Hit the beaches, parks and streets, and go for trophies in half-pipe skateboarding, footbag, roller skating, surfing, BMX, bike racing and flying disk throwing. Read the full online instructions on how to compete in the most totally awesome games in the world.";
      textPane.setText(" ");
      changeLineSpacing(textPane, -0.12f, true);
      textPane.setEditable(false);
      textPane.setFocusable(false);
      textPane.setPreferredSize(new Dimension(100, 275));
    }
    return textPane;
  }

  /**
   * Select all the text of a <code>JTextPane</code> first and then set the line spacing.
   * 
   * @param the <code>JTextPane</code> to apply the change
   * @param factor the factor of line spacing. For example, <code>1.0f</code>.
   * @param replace whether the new <code>AttributeSet</code> should replace the old set. If set to <code>false</code>,
   *          will merge with the old one.
   */
  private void changeLineSpacing(JTextPane pane, float factor, boolean replace)
  {
    pane.selectAll();
    MutableAttributeSet set = new SimpleAttributeSet(pane.getParagraphAttributes());
    StyleConstants.setLineSpacing(set, factor);
    pane.setParagraphAttributes(set, replace);
  }

  private JLabel getAuthorLabel()
  {
    if (authorLabel == null)
    {
      authorLabel = new JLabel();
      authorLabel.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 22));
    }
    return authorLabel;
  }

  private JLabel getComposerLabel()
  {
    if (composerLabel == null)
    {
      composerLabel = new JLabel(" ");
      composerLabel.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 22));
    }
    return composerLabel;
  }

  private JLabel getGenreLabel()
  {
    if (genreLabel == null)
    {
      genreLabel = new JLabel(" ");
      genreLabel.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 22));
    }
    return genreLabel;
  }

  private JLabel getYearLabel()
  {
    if (yearLabel == null)
    {
      yearLabel = new JLabel(" ");
      yearLabel.setFont(new Font("Microsoft Sans Serif", Font.BOLD, 22));
    }
    return yearLabel;
  }
}
