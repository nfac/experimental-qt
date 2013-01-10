package com.qspin.qtaste.tools.converter.factory.python;

import com.qspin.qtaste.tools.converter.model.event.Event;


public interface PythonEventFactoryInterface {

	String createPythonEvent(Event pEvent, long pPreviousTimestamp);
}
