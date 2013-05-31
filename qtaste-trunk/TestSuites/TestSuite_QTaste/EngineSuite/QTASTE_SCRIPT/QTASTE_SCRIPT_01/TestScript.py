#    Copyright 2007-2009 QSpin - www.qspin.be
#
#    This file is part of QTaste framework.
#
#    QTaste is free software: you can redistribute it and/or modify
#    it under the terms of the GNU Lesser General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    QTaste is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU Lesser General Public License for more details.
#
#    You should have received a copy of the GNU Lesser General Public License
#    along with QTaste. If not, see <http://www.gnu.org/licenses/>.

##
# QTaste scripting language test: Check Python syntax error.
# <p>
# The goal of this test case is to verify the QTaste behavior in case of syntax error encountered in the Python script.
# <p>
# Execute a test script containing a Python syntax error. QTaste will set the test result to "Not available" with the details of the python syntax error.
# @preparation None
##

from qtaste import *

def Step1():
	"""
	@step Define a Python script containing the following a syntax error: <p>
		  if myValue eq True:
	@expected Test result is "Not available" with the following reason:<p>
			  <i>Python syntax errorin file Testcase.py at line 18, column 20:
			  if myValue eq True:</i><p>
			  Script call stack is reported.
	"""
	if myValue eq True:
		print "myValue is true"
	else:	
		print "myValue is false"

doStep(Step1)
myValue = testAPI.getEngineTest().throwNoException()
