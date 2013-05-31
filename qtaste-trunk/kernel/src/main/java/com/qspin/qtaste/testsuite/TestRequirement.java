package com.qspin.qtaste.testsuite;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class TestRequirement {

	public TestRequirement(String pId)
	{
		mDataIdList = new LinkedList<String>();
		mData = new HashMap<String, String>();
		setData(ID, pId);
	}
	
	public TestRequirement(TestRequirement pTestRequirement) {
		this(pTestRequirement.getId());
		for ( String dataId : pTestRequirement.getDataId() )
		{
			setData(dataId, pTestRequirement.getData(dataId));
		}
	}

	public void setRequirementDescription(String pDescription)
	{
		setData(DESCRIPTION, pDescription);
	}
	
	public String getId()
	{
		return getData(ID);
	}
	
	public String getDescription()
	{
		return getData(DESCRIPTION);
	}
	
	public void setData(String pDataId, String pDataValue)
	{
		if ( !mDataIdList.contains(pDataId) )
		{
			mDataIdList.add(pDataId);
		}
		mData.put(pDataId, pDataValue);
	}
	
	public void changeDataId(String pOldId, String pNewId) {
		if ( mDataIdList.contains(pOldId) )
		{
			mDataIdList.add(mDataIdList.indexOf(pOldId), pNewId);
			mData.put(pNewId, mData.get(pOldId));
			
			removeDataId(pOldId);
		}
	}
	public void removeDataId(String pDataId) {
		mDataIdList.remove(pDataId);		
		mData.remove(pDataId);
	}
	
	public String getData(String pDataId)
	{
		return mData.get(pDataId);
	}
	
	public List<String> getDataId()
	{
		return mDataIdList;
	}
	
	private List<String> mDataIdList;
	private Map<String, String> mData;
	
	public static final String ID = "REQ_ID";
	public static final String DESCRIPTION = "REQ_DESCRIPTION";
}
