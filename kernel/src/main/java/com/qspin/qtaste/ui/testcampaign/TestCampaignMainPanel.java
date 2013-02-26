/*
    Copyright 2007-2009 QSpin - www.qspin.be

    This file is part of QTaste framework.

    QTaste is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    QTaste is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with QTaste. If not, see <http://www.gnu.org/licenses/>.
*/

package com.qspin.qtaste.ui.testcampaign;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.python.core.PyException;
import org.python.util.PythonInterpreter;

import com.qspin.qtaste.config.GUIConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.kernel.campaign.Campaign;
import com.qspin.qtaste.kernel.campaign.CampaignManager;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.ui.treetable.JTreeTable;
import com.qspin.qtaste.util.Log4jLoggerFactory;
import com.qspin.qtaste.util.ThreadManager;
import com.qspin.qtaste.ui.MainPanel;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestCampaignMainPanel extends JPanel {

    private MainPanel parent;
    private JTreeTable treeTable;
    private JComboBox metaCampaignComboBox = new JComboBox();
    private JButton addNewMetaCampaignButton = new JButton("Add");
    private JButton saveMetaCampaignButton = new JButton("Save");
    private JButton runMetaCampaignButton = new JButton("Run");
    private boolean isExecuting = false;
    private CampaignExecutionThread testExecutionHandler = null;
    private MetaCampaignFile selectedCampaign = null;
    private static Logger logger = Log4jLoggerFactory.getLogger(TestCampaignMainPanel.class);
    private final List<ActionListener> campaignActionListeners = Collections.synchronizedList(new LinkedList<ActionListener>());
    public static final int RUN_ID = 0;
    public static final String STARTED_CMD = "STARTED";
    public static final String STOPPED_CMD = "STOPPED";
    private static final String LAST_SELECTED_CAMPAIGN_PROPERTY = "last_selected_campaign";

    public TestCampaignMainPanel(MainPanel parent) {
        super(new BorderLayout());
        this.parent = parent;
        genUI();
    }

    public void genUI() {
        TestCampaignTreeModel model = new TestCampaignTreeModel("Test Campaign");
        treeTable = new JTreeTable(model);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Campaign:");
        saveMetaCampaignButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/save_32"));
        saveMetaCampaignButton.setToolTipText("Save campaign");
        saveMetaCampaignButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                treeTable.save(selectedCampaign.getFileName(), selectedCampaign.getCampaignName());
            }
        });
        addNewMetaCampaignButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/add"));
        addNewMetaCampaignButton.setToolTipText("Define a new campaign");
        addNewMetaCampaignButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String newCampaign = JOptionPane.showInputDialog(null,
                        "New campaign creation:",
                        "Campaign name:", JOptionPane.QUESTION_MESSAGE);
                if (newCampaign != null && newCampaign.length() > 0) {
                    int index = addTestCampaign(newCampaign);
                    metaCampaignComboBox.setSelectedIndex(index);
                }
            }
        });

        // get last selected campaign
        GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
        String lastSelectedCampaign = guiConfiguration.getString(LAST_SELECTED_CAMPAIGN_PROPERTY);

        // add campaigns found in the list
        MetaCampaignFile[] campaigns = populateCampaignList();
        topPanel.add(label);
        topPanel.add(metaCampaignComboBox);

        metaCampaignComboBox.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                MetaCampaignFile currentSelectedCampaign = (MetaCampaignFile) metaCampaignComboBox.getSelectedItem();
                if (currentSelectedCampaign != selectedCampaign) {
                    selectedCampaign = currentSelectedCampaign;
	                if (selectedCampaign != null) {
	                    treeTable.removeAll();
	                    if (new File(selectedCampaign.getFileName()).exists()) {
	                        treeTable.load(selectedCampaign.getFileName());
	                    }
	                    
	                    GUIConfiguration guiConfiguration = GUIConfiguration.getInstance();
	                    guiConfiguration.setProperty(LAST_SELECTED_CAMPAIGN_PROPERTY, selectedCampaign.getCampaignName());
	                    try {
	                        guiConfiguration.save();
	                    } catch (ConfigurationException ex) {
	                        logger.error("Error while saving GUI configuration: " + ex.getMessage(), ex);
	                    }
	                }
                }
            }
        });

    	boolean setLastSelectedCampaign = false;
        if (lastSelectedCampaign != null) {
            // select last selected campaign
            for (int i = 0; i < campaigns.length; i++) {
                if (campaigns[i].getCampaignName().equals(lastSelectedCampaign)) {
                    metaCampaignComboBox.setSelectedIndex(i);
                    setLastSelectedCampaign = true;
                    break;
                }
            }
        }
        if (!setLastSelectedCampaign && metaCampaignComboBox.getItemCount() > 0) {
            metaCampaignComboBox.setSelectedIndex(0);
        }
        
        runMetaCampaignButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/running_32"));
        runMetaCampaignButton.setToolTipText("Run campaign");
        runMetaCampaignButton.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    // first save the current campaign if needed
                    if (treeTable.hasChanged()) {
                        treeTable.save(selectedCampaign.getFileName(), selectedCampaign.getCampaignName());
                    }

                    // set SUT version
                    TestBedConfiguration.setSUTVersion(parent.getSUTVersion());
                    
                    testExecutionHandler = new CampaignExecutionThread(selectedCampaign.getFileName());
                    Thread t = new Thread(testExecutionHandler);
                    t.start();
                // set the window to test result
                // TO DO
                } catch (Exception ex) {
                    //
                    logger.error(ex.getMessage(), ex);
                }
            }
        });
       

  

        topPanel.add(addNewMetaCampaignButton);
        topPanel.add(saveMetaCampaignButton);
        topPanel.add(runMetaCampaignButton);
        topPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        JScrollPane sp = new JScrollPane(treeTable);
        this.add(topPanel, BorderLayout.NORTH);
        this.add(sp);
    }

    private MetaCampaignFile[] populateCampaignList() {
    	// clear the list
    	metaCampaignComboBox.removeAllItems();
        MetaCampaignFile[] campaigns = MetaCampaignFile.getExistingCampaigns();
        for (int i = 0; i < campaigns.length; i++) {
            metaCampaignComboBox.addItem(campaigns[i]);
        }
        return campaigns;
		
	}

	public JTreeTable getTreeTable() {
        return treeTable;
    }
    
    public CampaignExecutionThread getExecutionThread() {
        return testExecutionHandler;
    }

    public class UpdateButtons implements Runnable {

        public void run() {
            runMetaCampaignButton.setEnabled(!isExecuting);
        }
    }

    public void addTestCampaignActionListener(ActionListener listener) {
        campaignActionListeners.add(listener);
    }

    public void removeTestCampaignActionListener(ActionListener listener) {
        campaignActionListeners.remove(listener);
    }

    /**
     * Add a test campaign to the test campaigns combobox.
     * 
     * @param campaignName test campaign name
     * @return index of the added test campaign in the combobox or -1 if not added
     */
    public int addTestCampaign(String campaignName) {
      // add the campaign in the list, keeping sorted order
        int index = 0;
        while (index < metaCampaignComboBox.getItemCount()) {
            String elementCampaignName = ((MetaCampaignFile)metaCampaignComboBox.getItemAt(index)).getCampaignName();
            int comparison = campaignName.compareToIgnoreCase(elementCampaignName);
            if (comparison < 0) {
                // insert new campaign at this index
                break;
            } else if (comparison == 0) {
                // error: a campaign with the same name already exists
                JOptionPane.showMessageDialog(null, "A test campaign named '" + elementCampaignName + "' already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                index = -1;
                break;
            }
            index++;
        }
        if (index >= 0) {
            MetaCampaignFile newItem = new MetaCampaignFile(campaignName);
            metaCampaignComboBox.insertItemAt(newItem, index);
        }
        return index;
    }

    public class GenerateDocumentActionListener extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            MetaCampaignFile selectedCampaign = (MetaCampaignFile) metaCampaignComboBox.getSelectedItem();
            try {
                StringWriter output = new StringWriter();
                PythonInterpreter interp = new PythonInterpreter(new org.python.core.PyStringMap(), new org.python.core.PySystemState());
                interp.setOut(output);
                interp.setErr(output);
                interp.cleanup();
                String args = "import sys;sys.argv[1:]= [r'" + selectedCampaign.getFileName() + "']";
                interp.exec(args);
                interp.exec("__name__ = '__main__'");
                String s =   "execfile(r'tools/TestProcedureDoc/generateTestCampaignDoc.py')";
                interp.exec(s);
                interp.cleanup();
                interp = null;
                File campaingFile = new File(selectedCampaign.getFileName());
                if (campaingFile.exists()) {
                	File resultsFile = new File(campaingFile.getParentFile().getCanonicalPath() + "/" + selectedCampaign.getCampaignName() + "-doc.html");
                	if (resultsFile.exists()) {
	                	if (Desktop.isDesktopSupported()) {
	                		Desktop.getDesktop().open(resultsFile);
	                	} else {
	                		logger.error("Feature not supported by this platform");
	                	}
                	}
                	else {
                		JOptionPane.showMessageDialog(null, 
                				"Error during generation of TPO file", 
                				"Error", JOptionPane.ERROR_MESSAGE);
                	
                	}
                }
                
            } catch (PyException ex) {
                logger.error(ex.getMessage(), ex);
        		JOptionPane.showMessageDialog(null, 
        				"Error during generation of TPO file\n" + ex.getMessage(), 
        				"Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
        		JOptionPane.showMessageDialog(null, 
        				"Error during generation of TPO file\n" + ex.getMessage(), 
        				"Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }
    
    public class CampaignExecutionThread implements Runnable {

        private String xmlFileName;

        public CampaignExecutionThread(final String xmlFileName) {
            this.xmlFileName = xmlFileName;
        }

        public void stop() {
            ThreadGroup root = Thread.currentThread().getThreadGroup();
            ThreadManager.stopThread(root, 0);
        }

        public void run() {
            // listen to events 
            SwingUtilities.invokeLater(new UpdateButtons());
            isExecuting = true;

            try {
                synchronized (campaignActionListeners) {
                    Iterator<ActionListener> it = campaignActionListeners.iterator();
                    ActionListener al;
                    while (it.hasNext()) {
                        al = it.next();
                        //
                        al.actionPerformed(new ActionEvent(TestCampaignMainPanel.this, RUN_ID, STARTED_CMD));
                    }
                }
                CampaignManager campaignManager = CampaignManager.getInstance();
                Campaign campaign = campaignManager.readFile(xmlFileName);
                campaignManager.execute(campaign);
            } catch (Exception e) {
                logger.fatal(e.getMessage(), e);
            } finally {
                isExecuting = false;
                SwingUtilities.invokeLater(new UpdateButtons());
                testExecutionHandler = null;
                synchronized (campaignActionListeners) {
                    Iterator<ActionListener> it = campaignActionListeners.iterator();
                    ActionListener al;
                    while (it.hasNext()) {
                        al = it.next();
                        //
                        al.actionPerformed(new ActionEvent(TestCampaignMainPanel.this, RUN_ID, STOPPED_CMD));
                    }
                }
            }
        }
    }
        
}
