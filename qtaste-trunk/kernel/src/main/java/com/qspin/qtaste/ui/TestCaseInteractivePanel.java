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

package com.qspin.qtaste.ui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import com.qspin.qtaste.datacollection.collection.ProbeManager;
import com.qspin.qtaste.kernel.testapi.TestAPI;
import com.qspin.qtaste.kernel.testapi.TestAPIImpl;
import com.qspin.qtaste.reporter.testresults.TestResult;
import com.qspin.qtaste.testsuite.QTasteDataException;
import com.qspin.qtaste.testsuite.TestData;
import com.qspin.qtaste.testsuite.TestRequirement;
import com.qspin.qtaste.testsuite.impl.JythonTestScript;
import com.qspin.qtaste.testsuite.impl.TestDataInteractive;
import com.qspin.qtaste.ui.csveditor.TestDataEditor;
import com.qspin.qtaste.ui.log4j.Log4jPanel;
import com.qspin.qtaste.ui.log4j.TextAreaAppender;
import com.qspin.qtaste.ui.tools.ResourceManager;
import com.qspin.qtaste.ui.tools.SpringUtilities;

/**
 *
 * @author vdubois
 */
@SuppressWarnings("serial")
public class TestCaseInteractivePanel extends TestAPIPanel {

    private TestDataEditor testDataView;
    private Log4jPanel log4jPane;
    public JTextField mInteractiveText;
    public JButton mExecuteButton;
    private JButton mStartInteractiveTestButton;
    private JButton mStopInteractiveTestButton;
    private InteractiveLogPanel mLogPanel = new InteractiveLogPanel(this);
    protected Map<String, Integer> testCases = new HashMap<String, Integer>();
    protected ImageIcon passedImg,  failedImg,  runningImg,  snapShotImg,  naImg;
    private boolean isStarted = false;
    private TestDataInteractive m_testData;
    private int commandId = 0;

    private void initIcons() {
        passedImg = ResourceManager.getInstance().getImageIcon("icons/passed");
        failedImg = ResourceManager.getInstance().getImageIcon("icons/failed");
        runningImg = ResourceManager.getInstance().getImageIcon("icons/running_32");
        naImg = ResourceManager.getInstance().getImageIcon("icons/na");
        snapShotImg = ResourceManager.getInstance().getImageIcon("icons/snapshot");
    }

    public void init() {
        try {
            initIcons();
            JPanel topPanel = new JPanel(new BorderLayout());

            mStartInteractiveTestButton = new JButton();
            mStartInteractiveTestButton.setText("Start Interactive mode");

            StartButtonAction startButtonListener = new StartButtonAction();
            mStartInteractiveTestButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/running_32"));
            mStartInteractiveTestButton.addActionListener(startButtonListener);

            mStopInteractiveTestButton = new JButton();
            mStopInteractiveTestButton.setText("Stop Interactive mode");
            mStopInteractiveTestButton.setIcon(ResourceManager.getInstance().getImageIcon("icons/stop"));
            StopInterractiveButtonAction stopButtonListener = new StopInterractiveButtonAction();
            mStopInteractiveTestButton.setEnabled(false);
            mStopInteractiveTestButton.addActionListener(stopButtonListener);

            JPanel northP = new JPanel(new SpringLayout());
            northP.add(mStartInteractiveTestButton);
            northP.add(mStopInteractiveTestButton);
            northP.add(new CommonShortcutsPanel());

            try {

                SpringUtilities.makeCompactGrid(northP, 1, 3, 6, 6, 6, 6); //xPad, yPad
            } catch (Exception exc) {
            }
            topPanel.add(northP, BorderLayout.PAGE_START);

            mInteractiveText = new JTextField();
            mExecuteButton = new JButton();
            mExecuteButton.setText("Execute");
            mExecuteButton.setEnabled(false);
            ExecuteButtonAction executeButtonListener = new ExecuteButtonAction();
            mExecuteButton.addActionListener(executeButtonListener);
            mInteractiveText.addActionListener(executeButtonListener);

            mInteractiveText.setEnabled(false);

            topPanel.add(mInteractiveText, BorderLayout.CENTER);
            topPanel.add(mExecuteButton, BorderLayout.EAST);
            add(topPanel, BorderLayout.NORTH);

            log4jPane = new Log4jPanel();

            JSplitPane bottomSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            bottomSplitPane.setResizeWeight(0.5);
            bottomSplitPane.setDividerSize(4);

            bottomSplitPane.setBottomComponent(log4jPane);

            mLogPanel.init();
            testDataView = new TestDataEditor(true);

            JSplitPane topSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            topSplitPane.setResizeWeight(0.8);
            topSplitPane.setDividerSize(4);
            topSplitPane.setTopComponent(mLogPanel);

            topSplitPane.setBottomComponent(testDataView);
            bottomSplitPane.setTopComponent(topSplitPane);
            this.add(bottomSplitPane);

            this.apiTree.addTreeSelectionListener(new TCTreeListener());

            // initialize testData
            m_testData = new TestDataInteractive("QTaste_interactive", 1, null, null);
            m_testData.setGUIMonitoring(true, this);
            String defaultInstance = null;
            try {
                TestBedConfiguration testbedConfig = TestBedConfiguration.getInstance();
                if (testbedConfig != null) {
                    defaultInstance = testbedConfig.getDefaultInstanceId();
                }
            } catch (Exception e) {
            }
            m_testData.setValue("INSTANCE_ID", defaultInstance);
            testDataView.setTestData(m_testData);

            TestBedConfiguration.registerConfigurationChangeHandler(new TestBedConfiguration.ConfigurationChangeHandler() {

                public void onConfigurationChange() {
                    String defaultInstance = null;
                    try {
                        TestBedConfiguration testbedConfig = TestBedConfiguration.getInstance();
                        if (testbedConfig != null) {
                            defaultInstance = testbedConfig.getDefaultInstanceId();
                        }
                    } catch (Exception e) {
                    }
                    try {
                        m_testData.setValue("INSTANCE_ID", defaultInstance);
                        testDataView.setTestData(m_testData);
                    } catch (QTasteDataException ex) {
                    }
                }
            });
        } catch (QTasteDataException ex) {
        }
    }

    protected void startInteractiveMode() {
        if (!isStarted) {
            // listen to events 
            TextAreaAppender.addTextArea(log4jPane);
            mLogPanel.startTestCaseListener();
            mExecuteButton.setEnabled(true);
            mInteractiveText.setEnabled(true);
            mStopInteractiveTestButton.setEnabled(true);
            mStartInteractiveTestButton.setEnabled(false);

            TestBedConfiguration.reloadConfigFileIfModified();

            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Thread thread = new Thread() {

                public void run() {
                    ProbeManager.getInstance().start();
                    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            };
            thread.start();

            isStarted = true;
        }
    }

    protected void stopInteractiveMode() {
        if (isStarted) {
            ProbeManager.getInstance().stop();

            mExecuteButton.setEnabled(false);
            mInteractiveText.setEnabled(false);
            mStopInteractiveTestButton.setEnabled(false);
            mStartInteractiveTestButton.setEnabled(true);

            mLogPanel.stopTestCaseListener();
            TextAreaAppender.removeTextArea(log4jPane);

            isStarted = false;
        }
    }

    public synchronized void executeCommand(String command) {
        // define a testcase id
        String tcId = command + " - " + ++commandId;
        executeCommand(testDataView.getTestData(), null, command, tcId);
    }

    protected static void executeCommand(TestData data, List<TestRequirement> requirements, String command, String tcId) {
        String sTempDir = System.getProperty("java.io.tmpdir");
        File tempDir = new File(sTempDir);
        //create a subdirectory called " QTaste_Interactive"
        if (tempDir.isDirectory()) {
            File interactiveQTasteDir = new File(tempDir + "/QTaste_interactive");
            interactiveQTasteDir.mkdir();
            PrintWriter interactiveQTasteFile = null;
            try {
                File iQTasteDirectory = new File(tempDir + "/QTaste_interactive");
                File iQTasteFile = new File(iQTasteDirectory + File.separator + StaticConfiguration.TEST_SCRIPT_FILENAME);
                interactiveQTasteFile = new PrintWriter(new BufferedWriter(new FileWriter(iQTasteFile)));
                interactiveQTasteFile.println("from qtaste import *");
                interactiveQTasteFile.println("def interactiveCommand():");
                interactiveQTasteFile.println("\t" + command);
                interactiveQTasteFile.println("result = interactiveCommand()");
                interactiveQTasteFile.close();
                ArrayList<LinkedHashMap<String, String>> l = new ArrayList<LinkedHashMap<String, String>>();
                l.add(data.getDataHash());
                // checks must be added to ask specific data
                JythonTestScript ts = new JythonTestScript(l, requirements, iQTasteFile, iQTasteDirectory, null);
                ts.setName(tcId);
                ts.execute(false);
                TestResult testResult = ts.getTestResults().get(0);
                testResult.setName(tcId);
                testResult.setComment("");
                Bindings globalContext = JythonTestScript.getEngine().getBindings(ScriptContext.ENGINE_SCOPE);
                if (globalContext.containsKey("result")) {
                    Object returnObject = globalContext.get("result");
                    String returnValue = "None";
                    if (returnObject != null) {
                        returnValue = returnObject.toString();
                    }
                    if (!ts.getTestDataSet().getData().isEmpty()) {
                        testResult.setReturnValue(returnValue);
                    }
                }
            } catch (IOException ex) {
                //
            } finally {
                interactiveQTasteFile.close();
            }
        }
    }

    protected class StartButtonAction implements ActionListener {

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            startInteractiveMode();
        }
    }

    protected class StopInterractiveButtonAction implements ActionListener {

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            stopInteractiveMode();
        }
    }

    protected class ExecuteButtonAction implements ActionListener {

        /**
         * Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            executeCommand(mInteractiveText.getText());
        }
    }

    public class TCTreeListener implements TreeSelectionListener {

        public void valueChanged(TreeSelectionEvent e) {

            TreePath path = e.getNewLeadSelectionPath();
            if (path != null) {
                if (path.getParentPath() != null && path.getParentPath().getParentPath() != null) {
                    DefaultMutableTreeNode tn = (DefaultMutableTreeNode) path.getLastPathComponent();
                    String methodName = (String) tn.getUserObject();
                    String componentName = (String) ((DefaultMutableTreeNode) path.getParentPath().getLastPathComponent()).getUserObject();
                    TestAPI testAPI = TestAPIImpl.getInstance();
                    Method method = testAPI.getMethod(componentName, methodName);

                    String text = "testAPI.get" + componentName + "()." + methodName + "(";
                    boolean argumentAdded = false;
		    if (method != null) {
                    	for (Class<?> parameterType : method.getParameterTypes()) {
                        	text += parameterType.getSimpleName() + ", ";
                        	argumentAdded = true;
                    	}
		    }
                    if (argumentAdded) { // remove last ", " characters
                        text = text.substring(0, text.length() - 2);
                    }
                    text += ")";
                    mInteractiveText.setText(text);
                }
            }
        }
    }
}
