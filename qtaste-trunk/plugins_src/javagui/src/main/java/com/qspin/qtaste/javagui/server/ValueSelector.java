package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Label;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ListCellRenderer;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

public class ValueSelector extends UpdateComponentCommander {

	@Override
	protected void prepareActions() throws QTasteTestFailException
	{
		String value = mData[2].toString();
		if (component instanceof JCheckBox || component instanceof JRadioButton) {
			new ComponentSelector().executeCommand(mData[0].toString(),Integer.parseInt(mData[1].toString()), Boolean.parseBoolean(value));
		} else if (component instanceof JComboBox) {
			JComboBox combo = (JComboBox) component;
			ListCellRenderer renderer = combo.getRenderer();
			for (int i = 0; i < combo.getItemCount(); i++) { 
				String itemValue = getItemText(combo.getModel().getElementAt(i), renderer);
				System.out.println("compare combo elmt (" + itemValue + ") with '" + value + "'");
				// Use a startsWith instead of equals() as toString() can return more than the value
				if (itemValue.equals(value)) {
					mValueToSelect = i;
				}
			}
		} else if (component instanceof JList) {
			JList list = (JList) component;
			ListCellRenderer renderer = list.getCellRenderer();
			for (int i = 0; i < list.getModel().getSize(); i++) {
				String itemValue = getItemText(list.getModel().getElementAt(i), renderer);
				System.out.println("compare list elmt (" + itemValue + ") with '" + value + "'");
				if (itemValue.equals(value)) {
					mValueToSelect = i;
				}
			}
		} else if (component instanceof JSpinner) {
			mValueToSelect = Double.parseDouble(value);
		} else if (component instanceof JSlider) {
			mValueToSelect = Integer.parseInt(value);
		} else {
			throw new QTasteTestFailException("component '" + component.getName() + "' (" + component.getClass() + ") found but unused");
		}
	}
	@Override
	protected void doActionsInSwingThread()
	{
		if (component instanceof JComboBox) {
			((JComboBox)component).setSelectedIndex(mValueToSelect.intValue());
		} else if (component instanceof JList) {
			((JList)component).setSelectedIndex(mValueToSelect.intValue());
		}
		else if (component instanceof JSpinner) {
			JSpinner spinner = (JSpinner) component;
			spinner.getModel().setValue(mValueToSelect.doubleValue());
		} else if (component instanceof JSlider) {
			JSlider slider = (JSlider) component;
			slider.getModel().setValue(mValueToSelect.intValue());
		}
	}
	
	protected String getItemText(Object item, ListCellRenderer renderer)
	{
		Component c = renderer.getListCellRendererComponent(new JList(), item, 0, false, false);
		if ( c instanceof Label )
		{
			return ((Label)c).getText();
		}
		if ( c instanceof JLabel )
		{
			return ((JLabel)c).getText();
		}
		return item.toString();
	}
	
	protected Number mValueToSelect;

}
