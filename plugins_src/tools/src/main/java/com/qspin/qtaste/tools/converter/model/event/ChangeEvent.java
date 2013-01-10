package com.qspin.qtaste.tools.converter.model.event;

public class ChangeEvent extends Event {
	
	public ChangeEvent(Event pEvent) {
		super();
		setComponentName(pEvent.getComponentName());
		setSourceClass(pEvent.getSourceClass());
		setTimeStamp(pEvent.getTimeStamp());
		setType(pEvent.getType());
	}

	public int getTabIndex() {
		return mTabIndex;
	}
	public void setTabIndex(int pTabIndex) {
		mTabIndex = pTabIndex;
	}
	public Class<?> getSourceEventClass()
	{
		return javax.swing.event.ChangeEvent.class;
	}

	private int mTabIndex;
}
