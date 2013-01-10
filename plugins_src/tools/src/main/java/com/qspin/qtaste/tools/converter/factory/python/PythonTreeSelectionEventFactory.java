package com.qspin.qtaste.tools.converter.factory.python;

import com.qspin.qtaste.tools.converter.model.event.Event;
import com.qspin.qtaste.tools.converter.model.event.TreeSelectionEvent;

class PythonTreeSelectionEventFactory extends PythonEventFactory {

	@Override
	public String createPythonEvent(Event pEvent, long pPreviousTimestamp)
	{
		TreeSelectionEvent evt = (TreeSelectionEvent)pEvent;
		StringBuilder builder = new StringBuilder();
		
		insertSleep(pEvent, pPreviousTimestamp, builder);
		builder.append(getPythonIndentation(1) + "javaguiMI.selectNode(" + getComponentIdentifier(evt.getComponentName()) + ", ");
		builder.append(												  "\"" + evt.getSelectedPath().substring(1, evt.getSelectedPath().length()-1) + "\", " );
		builder.append(												  "\", \"" + " )" + LINE_BREAK);
		return builder.toString();
	}
	
}
