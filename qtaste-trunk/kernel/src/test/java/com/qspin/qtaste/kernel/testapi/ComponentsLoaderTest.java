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

package com.qspin.qtaste.kernel.testapi;

import com.qspin.qtaste.kernel.testapi.ComponentsLoader;
import com.qspin.qtaste.config.StaticConfiguration;
import com.qspin.qtaste.config.TestBedConfiguration;
import junit.framework.TestCase;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author lvboque
 */
public class ComponentsLoaderTest extends TestCase {

    public ComponentsLoaderTest(String testName) {
        super(testName);
        // Log4j Configuration
        PropertyConfigurator.configure(StaticConfiguration.CONFIG_DIRECTORY + "/log4j.properties");
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestBedConfiguration.setConfigFile(StaticConfiguration.TESTBED_CONFIG_DIRECTORY + "/enginetest." + StaticConfiguration.TESTBED_CONFIG_FILE_EXTENSION);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
