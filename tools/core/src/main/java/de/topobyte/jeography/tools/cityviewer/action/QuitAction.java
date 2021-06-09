package de.topobyte.jeography.tools.cityviewer.action;

import java.awt.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.topobyte.jeography.viewer.action.SimpleAction;

/**
 * @author Sebastian Kuerten (sebastian.kuerten@fu-berlin.de)
 * 
 */
public class QuitAction extends SimpleAction
{

	private static final long serialVersionUID = -7110246291534835462L;

	static final Logger logger = LoggerFactory.getLogger(QuitAction.class);

	private static String FILE = "res/images/gtk-quit.png";

	/**
	 * Create a new SearchAction.
	 */
	public QuitAction()
	{
		super("quit", "exit the program");
		setIconFromResource(FILE);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		System.exit(0);
	}

}
