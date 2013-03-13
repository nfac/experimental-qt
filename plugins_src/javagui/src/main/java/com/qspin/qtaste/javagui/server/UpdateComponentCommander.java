package com.qspin.qtaste.javagui.server;

import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Window;

import javax.swing.SwingUtilities;

import com.qspin.qtaste.testsuite.QTasteTestFailException;

abstract class UpdateComponentCommander extends ComponentCommander implements Runnable {

	@Override
	public Boolean executeCommand(Object... data) throws QTasteTestFailException {
		setData(data);
		String componentName = mData[0].toString();
		component = getComponentByName(componentName);
		if (component == null )
		{
			throw new QTasteTestFailException("The component \"" + componentName + "\" is not found.");
		}
		if (!component.isEnabled()) {
			throw new QTasteTestFailException("The component \"" + componentName + "\" is not enabled.");
		}
		if (! checkComponentIsVisible(component))
			throw new QTasteTestFailException("The component \"" + componentName + "\" is not visible!");
		prepareActions();
		SwingUtilities.invokeLater(this);
		return true;
	}
	
	public void run()
	{
		try {
			doActionsInSwingThread();
		}
		catch (QTasteTestFailException e) {
			LOGGER.fatal(e.getMessage(), e);
		}
	}
	
	protected Component getComponentByName(String name) throws QTasteTestFailException {
		mFoundComponent = null;
		mFindWithEqual = false;
		LOGGER.debug("try to find a component with the name : " + name);
		// TODO: Think about several component having the same names!
		for (int w = 0; w < Frame.getWindows().length && !mFindWithEqual; w++) {
			Window window = Frame.getWindows()[w];
			if ( !checkName(name, window) || !mFindWithEqual ) {
				LOGGER.debug("parse window");
				lookForComponent(name, window.getComponents());
			}
			if  ( checkComponentIsVisible(mFoundComponent) )
			{
				break;
			}
		}
		if ( mFoundComponent != null )
		{
			mFoundComponent.requestFocus();
			return mFoundComponent;
		}
		throw new QTasteTestFailException("The component \"" + name + "\" is not found.");
	}

	protected Component lookForComponent(String name, Component[] components) {
		for (int i = 0; i < components.length && !mFindWithEqual; i++) {
			//String componentName = ComponentNamer.getInstance().getNameForComponent(components[c]);
			Component c = components[i];
			checkName(name, c);
			if ( mFindWithEqual && !checkComponentIsVisible(mFoundComponent))
			{
				mFindWithEqual = false;
			}
			if ( !mFindWithEqual )
			{
				if (c instanceof Container) {
					Component result = lookForComponent(name, ((Container) c).getComponents());
					if (result != null && checkComponentIsVisible(result)) {
						return result;
					}
				}
			}
		}
		return null;
	}
	
	protected boolean checkComponentIsVisible(Component c)
	{
		Component currentComponent = c;
		while (currentComponent != null)
		{
			if ( !currentComponent.isVisible() )
			{
				return false;
			}
			currentComponent = currentComponent.getParent();
		}
		return true;
	}
	
	protected abstract void prepareActions() throws QTasteTestFailException;
	protected abstract void doActionsInSwingThread()throws QTasteTestFailException;

	private void setData(Object[] data)
	{
		this.mData = data;
	}
	
	protected Object[] mData;
	protected Component component;
}
