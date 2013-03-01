package com.qspin.qtaste.sutuidemo;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

final class Interface extends JFrame {

	
	public Interface()
	{
		super("SUT GUI Demonstration controlled by QTaste");
		setName("MAIN_FRAME");
		
		genUI();
		
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	private void genUI()
	{
		setLayout(new BorderLayout());
		int index = 0;
		mTabbedPane = new JTabbedPane();
		mTabbedPane.setName("TABBED_PANE");
		System.out.println("insert " + DocumentPanel.COMPONENT_NAME + " at " + index);
		mTabbedPane.insertTab(DocumentPanel.COMPONENT_NAME, null, new DocumentPanel(), null, index++);
		System.out.println("insert " + ChoosePanel.COMPONENT_NAME + " at " + index);
		mTabbedPane.insertTab(ChoosePanel.COMPONENT_NAME, null, new ChoosePanel(), null, index++);
		System.out.println("insert " + SelectionPanel.COMPONENT_NAME + " at " + index);
		mTabbedPane.insertTab(SelectionPanel.COMPONENT_NAME, null, new SelectionPanel(), null, index++);
		System.out.println("insert " + Tree_ListComponentsPanel.COMPONENT_NAME + " at " + index);
		mTabbedPane.insertTab(Tree_ListComponentsPanel.COMPONENT_NAME, null, new Tree_ListComponentsPanel(), null, index++);
		System.out.println("insert " + TablePanel.COMPONENT_NAME + " at " + index);
		mTabbedPane.insertTab(TablePanel.COMPONENT_NAME, null, new TablePanel(), null, index++);
		System.out.println("insert UNAMED COMPONENTS at " + index);
		mTabbedPane.insertTab("UNAMED COMPONENTS", null, new UnamedPanel(), null, index++);
		System.out.println("insert " + MiscellaneousPane.COMPONENT_NAME + " at " + index);
		mTabbedPane.insertTab(MiscellaneousPane.COMPONENT_NAME, null, new MiscellaneousPane(), null, index++);
		mTabbedPane.setSelectedIndex(-1);
		
		add(mTabbedPane, BorderLayout.CENTER);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Starting SUT GUI");
				new Interface();
				System.out.println("SUT GUI started");
				System.err.println("not an error, just a simple test");
			}
		});
	}
	
	private JTabbedPane mTabbedPane;

}
