package com.qspin.qtaste.tools.converter.ui;

import static com.qspin.qtaste.tools.converter.ui.UIConstants.COMPONENT_SPACING;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.qspin.qtaste.tools.converter.factory.ComponentTreeFactory;
import com.qspin.qtaste.tools.converter.factory.EventTreeFactory;
import com.qspin.qtaste.tools.converter.model.event.Event;
import com.qspin.qtaste.tools.converter.ui.action.FactorySelection;
import com.qspin.qtaste.tools.converter.ui.event.EventPane;

class EventDescriptionPane extends JPanel {
	public EventDescriptionPane(){
		super();
		genUI();
	}
	
	private void genUI()
	{
		setLayout( new BorderLayout() );
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		FormLayout layout = new FormLayout( 
				"right:pref:grow" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref",
				"pref" + COMPONENT_SPACING + "pref" + COMPONENT_SPACING + "pref");
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc = new CellConstraints();
		int rowIndex = 1;
		
		builder.addLabel("Group by :", cc.xy(1, rowIndex));
		ButtonGroup group = new ButtonGroup();
		JRadioButton groupByComponent = new JRadioButton("component(s)", false);
		groupByComponent.addItemListener(new FactorySelection(mTree, ComponentTreeFactory.getInstance()));
		group.add(groupByComponent);
		builder.add(groupByComponent, cc.xy(3, rowIndex));
		JRadioButton groupByEvent = new JRadioButton("event(s)", false);
		groupByEvent.addItemListener(new FactorySelection(mTree, EventTreeFactory.getInstance()));
		group.add(groupByEvent);
		builder.add(groupByEvent, cc.xy(5, rowIndex));
		groupByComponent.setSelected(true);
		rowIndex+=2;
		builder.add(new JScrollPane(mTree), cc.xyw(1, rowIndex, 5));
		split.setLeftComponent(builder.getPanel());
		EventPane eventPane = new EventPane();
		mTree.addTreeSelectionListener(eventPane);
		split.setRightComponent(eventPane);
		
		rowIndex += 2;
		add(split, BorderLayout.CENTER);
	}

	private EventTree mTree = new EventTree();
	private List<Event> mEvents;
}
