package se.lantz.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import se.lantz.model.InfoModel;

public class InfoBackgroundPanel extends JPanel
{
	private InfoModel model;
	private ScreenshotsPanel screensPanel;
	private InfoPanel infoPanel2;

	public InfoBackgroundPanel(InfoModel model)
	{
		this.model = model;
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		add(getInfoPanel2(), BorderLayout.WEST);
		add(getScreensPanel(), BorderLayout.CENTER);
		
	}

	private InfoPanel getInfoPanel2()
	{
		if (infoPanel2 == null)
		{
			infoPanel2 = new InfoPanel(model);
		}
		return infoPanel2;
	}

	public ScreenshotsPanel getScreensPanel()
	{
		if (screensPanel == null)
		{
			screensPanel = new ScreenshotsPanel(this.model);
		}
		return screensPanel;
	}

	void focusTitleField()
	{
		getInfoPanel2().focusTitleField();
	}

	public void selectEnDescriptionTab()
	{
		getInfoPanel2().selectEnDescriptionTab();
	}
}
