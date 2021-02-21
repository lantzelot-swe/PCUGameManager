package se.lantz.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import se.lantz.model.InfoModel;
import se.lantz.model.MainViewModel;

public class InfoBackgroundPanel extends JPanel
{
	private InfoModel infoModel;
	private ScreenshotsPanel screensPanel;
	private InfoPanel infoPanel2;
  private MainViewModel model;

	public InfoBackgroundPanel(MainViewModel model)
	{
		this.model = model;
    this.infoModel = model.getInfoModel();
		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);
		add(getInfoPanel2(), BorderLayout.WEST);
		add(getScreensPanel(), BorderLayout.CENTER);
		
	}

	private InfoPanel getInfoPanel2()
	{
		if (infoPanel2 == null)
		{
			infoPanel2 = new InfoPanel(infoModel);
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
