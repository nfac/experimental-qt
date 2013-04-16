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

/*
 */
package com.qspin.qtaste.ui.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.io.CSVFile;
import com.qspin.qtaste.util.FileUtilities;

public class FileNode extends TreeNodeImpl{
	
	private PythonTestScript m_PythonTestScript;
	private String m_TestSuiteDir;

    public boolean isTestcaseDir() {
        if (f.isDirectory()) {
        	
            File[] childFiles = FileUtilities.listSortedFiles(f);
            for (int i = 0; i < childFiles.length; i++) {
                if (childFiles[i].getName().equalsIgnoreCase(StaticConfiguration.TEST_SCRIPT_FILENAME)) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * method to be improved
     * @return
     */

    public String getTestcaseHeader()  {
        BufferedReader br = null;
        try {
            File testscriptFile = new File(f + File.separator + StaticConfiguration.TEST_SCRIPT_DOC_HTML_FILENAME);
            br = new BufferedReader(new FileReader(testscriptFile));
            String descriptionLine="";
            String line;
            while ((line = br.readLine()) != null) {
                descriptionLine += line;
            }
            descriptionLine = descriptionLine.replaceAll("<(br/?|/?p)>", "\n");
            descriptionLine = descriptionLine.replaceAll("</?(i|b)>", "");
            final String startDescription = "<h3>Description</h3>";
            final String startVersion = "<h3>Version</h3>";
            final String startPreparation = "<h3>Preparation</h3>";
            final String startRequiredData = "<h3>Required data</h3>";
            int startIndex = descriptionLine.indexOf(startDescription);
            int endIndex = descriptionLine.indexOf(startVersion);
            if (endIndex == -1) {
                endIndex = descriptionLine.indexOf(startPreparation);
            }
            if (endIndex == -1) {
                endIndex = descriptionLine.indexOf(startRequiredData);
            }
            return descriptionLine.substring(startIndex + startDescription.length(), endIndex);
        } catch (FileNotFoundException ex) {
            //
        } catch (IOException ex) {
            //
        } finally {
            try {
            	if (br!=null) br.close();
            } catch (IOException ex) {
                //
            }
        }
        return "";

    }
    public int getTestcaseCount() {
        File testDataFileName = new File(f + File.separator + StaticConfiguration.TEST_DATA_FILENAME);
        try {
            CSVFile testDataFile = new CSVFile(testDataFileName);
            return testDataFile.getCSVDataSet().size();
        } catch (FileNotFoundException ex) {
            //
            return -1;
        } catch (IOException ex) {
            //
            return -1;
        }
    }

    public boolean isTestcaseCheckOk() {
        if (!f.isDirectory()) {
            return false;
        }
        File testScriptFile = new File(f + File.separator + StaticConfiguration.TEST_SCRIPT_FILENAME);
        File testDataFile = new File(f + File.separator + StaticConfiguration.TEST_DATA_FILENAME);
        if (!testScriptFile.canRead() || !testDataFile.canRead()) {
            return false;
        }
        try {
            CSVFile csvFile = new CSVFile(testDataFile);
            return !csvFile.getCSVDataSet().isEmpty();
        } catch (Exception ex) {
            return false;
        }
    }    

    public File getTestcaseFile() {
        if (isTestcaseDir()) {
            File[] childFiles = FileUtilities.listSortedFiles(f);
            for (int i = 0; i < childFiles.length; i++) {
                if (childFiles[i].getName().equalsIgnoreCase(StaticConfiguration.TEST_SCRIPT_FILENAME)) {
                    return childFiles[i];
                }
            }
        }
        return null;
    }

    public String getId() {
        return this.f.getPath().replace("\\", "/");
    }
    
    public FileNode(File f, String displayValue, String testSuiteDir) {
        super(f, displayValue);
        m_TestSuiteDir = testSuiteDir;
        if (this.isTestcaseDir()) {
            File testScriptFile = new File(f + File.separator + StaticConfiguration.TEST_SCRIPT_FILENAME);
        	m_PythonTestScript = new PythonTestScript (testScriptFile,testSuiteDir);
        }
    }
    
    public PythonTestScript getPythonTestScript() {
    	return m_PythonTestScript;
    }
    protected boolean checkIfDirectoryContainsTestScriptFile(File file) {
        File[] childFiles = FileUtilities.listSortedFiles(file);
        if (childFiles==null) return false;
        for (int i = 0; i < childFiles.length; i++) {
            if (childFiles[i].isDirectory()) {
                FileNode childNode = new FileNode(childFiles[i], childFiles[i].getName(), m_TestSuiteDir);
                if (childNode.isTestcaseDir()) {
                    return true;
                } else {
                    // go recursively into its directory
                    boolean result = checkIfDirectoryContainsTestScriptFile(childFiles[i]);
                    if (result) {
                        return true;
                    }
                }
            }

        }
        return false;
    }
    
    /**
     * Loads the children, caching the results in the children ivar.
     */
    public Object[] getChildren() {
	if (children != null) {
	    return children; 
	}
        if (this.isTestcaseDir()) {
            try {
                ArrayList<TestDataNode> arrayDataNode = new ArrayList<TestDataNode>();
                // load test case data
                File tcDataFile = this.getPythonTestScript().getTestcaseData();
                if (tcDataFile== null) return new Object[]{};
                CSVFile csvDataFile = new CSVFile(tcDataFile);
                List<LinkedHashMap<String, String>> data = csvDataFile.getCSVDataSet();
                Iterator<LinkedHashMap<String, String>> it = data.iterator();
                int rowIndex = 1;
                while (it.hasNext()) {
                    LinkedHashMap<String, String> dataRow = it.next();
                    if (dataRow.containsKey("COMMENT")) {
                        String comment = dataRow.get("COMMENT");
                        arrayDataNode.add(new TestDataNode(tcDataFile, comment,rowIndex));
                    }
                    rowIndex++;
                }
                children = arrayDataNode.toArray();
                return  children;
            } catch (FileNotFoundException ex) {
                // no data found
            } catch (IOException ex) {
                // unable to read data file
                
            }
            
          
        }
        else {
            ArrayList<FileNode> arrayFileNode = new ArrayList<FileNode>();
            if (f.isDirectory()) {
                File[] childFiles = FileUtilities.listSortedFiles(f);
                for (int i = 0; i < childFiles.length; i++) {
                    FileNode fn = new FileNode(childFiles[i], childFiles[i].getName(), m_TestSuiteDir);
                    boolean nodeToAdd = fn.isTestcaseDir();
                    if (!fn.isTestcaseDir()) {
                        // go recursilvely to its child and check if it must be added
                        nodeToAdd = checkIfDirectoryContainsTestScriptFile(childFiles[i]);
                    }
                    if (nodeToAdd && !childFiles[i].isHidden()) {

                        arrayFileNode.add(fn);
                    }
                }
            }
            children = arrayFileNode.toArray();
        }
        if (children==null)
            return new Object[]{};
        else return children; 
    }
}
