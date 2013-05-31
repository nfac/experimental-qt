package com.qspin.qtaste.tools.converter.ui;

import static com.qspin.qtaste.tools.converter.ui.UIConstants.COMPONENT_SPACING;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.converter.action.ConversionTask;
import com.qspin.qtaste.tools.converter.model.ComponentNameMapping;
import com.qspin.qtaste.tools.converter.model.EventManager;
import com.qspin.qtaste.tools.converter.ui.action.ImportEventAction;
import com.qspin.qtaste.tools.converter.ui.action.ImportFilterAction;
import com.qspin.qtaste.tools.converter.ui.model.table.AliasTableModel;

public class MainUI extends JFrame {

	public MainUI() {
		super("Converter");
		
		genUI();
		
		setMinimumSize(new Dimension(800,600));
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}

	public void setFilterDefinitionFile(String pPath) {
		mUsedFilterFile.setText("Filter definition file:" + pPath);
	}	
	
	private void genUI()
	{
		setLayout( new BorderLayout() );
		createAndAddMenuBar();
		
		FormLayout layout = new FormLayout( 
				FRAME_BORDER + ", left:pref:grow " + COMPONENT_SPACING + " pref:grow, " + FRAME_BORDER,
				FRAME_BORDER + ", pref" + COMPONENT_SPACING +"pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref, " + FRAME_BORDER);
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 2;
		JButton importXml = new JButton("Import recording file");
		importXml.addActionListener(new ImportEventAction(this));
		builder.add(importXml, cc.xyw(2, rowIndex, 3));
		rowIndex += 2;

		mUsedFilterFile = new JLabel();
		setFilterDefinitionFile("None");
		builder.add(mUsedFilterFile, cc.xy(2, rowIndex));
		JButton importXmlFilter = new JButton("Select filter definition file");
		importXmlFilter.addActionListener(new ImportFilterAction(this));
		builder.add(importXmlFilter, cc.xy(4, rowIndex));
		rowIndex += 2;
		
		JTabbedPane tabbedPanes = new JTabbedPane();
		tabbedPanes.insertTab("Description", null, new EventDescriptionPane(), null, 0);
		tabbedPanes.insertTab("Aliases", null, new JScrollPane(new JTable(new AliasTableModel())), null, 1);
		builder.add(tabbedPanes, cc.xyw(2, rowIndex, 3));
		rowIndex += 2;
		
		mConfigurationPane = new ConversionConfigurationPane();
		EventManager.getInstance().addPropertyChangeListener(mConfigurationPane);
		builder.add(mConfigurationPane, cc.xyw(2, rowIndex, 3));
		rowIndex += 2;
		
		JPanel p = new JPanel();
		JButton launch = new JButton("Launch conversion");
		launch.addActionListener(new LauchConversionAction());
		p.add(launch);
		builder.add(p, cc.xyw(2, rowIndex, 3));
		rowIndex += 2;
		
		add(builder.getPanel(), BorderLayout.CENTER);
	}
	
	private void createAndAddMenuBar() {
		JMenuBar bar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Quit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent pEvent) {
				System.exit(0);
			}
		});
		file.add(exit);
		bar.add(file);
		
		setJMenuBar(bar);
	}
	
	private class LauchConversionAction implements ActionListener
	{
		public void actionPerformed(ActionEvent pEvt)
		{
			try {
				ConversionTask task = new ConversionTask();
				List<Object> selectedComponents = mConfigurationPane.getSelectedComponent();
				for (int i=0; i< selectedComponents.size(); ++i)
				{
					selectedComponents.set(i, ComponentNameMapping.getInstance().getComponentNameFor(selectedComponents.get(i).toString()));
				}
				task.setAcceptedComponentName(selectedComponents);
				task.setAcceptedEventType(mConfigurationPane.getSelectedEventType());
				task.setOutputDirectory(mConfigurationPane.getOutputDirectory());
				new Thread(task).start();
			}
			catch (IOException pExc)
			{
				LOGGER.error(pExc);
				JOptionPane.showConfirmDialog(MainUI.this, pExc.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private ConversionConfigurationPane mConfigurationPane;
	
	private JLabel mUsedFilterFile;
	
	private static final String FRAME_BORDER = "3dlu";
	private static final Logger LOGGER = Logger.getLogger(MainUI.class);
	/** Default serial version UID. */
	private static final long serialVersionUID = 1L;
}
