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

/**
 * Interface for all Test API singleton components
 * 
 * @author David Ergo
 */
public interface SingletonComponent extends Component {

    /**
     * This component uses a SingletonComponentFactory, i.e. it has only one instance. <br>
     * The constructors of implementing classes must have no argument.
     */
    public static final ComponentFactory factory = SingletonComponentFactory.getInstance();
}
