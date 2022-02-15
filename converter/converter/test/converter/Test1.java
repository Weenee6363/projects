package converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dcmdon.railway.resources.IProperties;
import dcmdon.resourcemanager.objects.IPropertyObject;
import dcmdon.resourcemanager.objects.IRelationPropertyObj;
import dinputUnitConversionMethodTest.FakeLinkPropertyObj;
import dinputUnitConversionMethodTest.FakePropertyObject;
import dinputUnitConversionMethodTest.FakeRelationPropertyObj;
import dinputUnitConversionMethodTest.FakeResourceObject;
import dinputUnitConversionMethodTest.FakeResourceObjectIterator;

class Test1
{


	@Test
	void test_getBitState()
	{
		byte[] dinPutUnit_state = new byte[5];
		dinPutUnit_state[0]=50;
		dinPutUnit_state[1]=84;
		dinPutUnit_state[2]=121;
		dinPutUnit_state[3]=48;
		dinPutUnit_state[4]=6;
		byte[] actual = SecondConversionMethod.getBitState(dinPutUnit_state);
		byte[] expected = new byte [20];
		expected[0]=2;
		expected[1]=0;
		expected[2]=3;
		expected[3]=0;
		
		expected[4]=0;
		expected[5]=1;
		expected[6]=1;
		expected[7]=1;
		
		expected[8]=1;
		expected[9]=2;
		expected[10]=3;
		expected[11]=1;
		
		expected[12]=0;
		expected[13]=0;
		expected[14]=3;
		expected[15]=0;
		
		expected[16]=2;
		expected[17]=1;
		expected[18]=0;
		expected[19]=0;
		assertArrayEquals(expected, actual);
	}
	
	@Test
	void test_getChainState()
	{
		byte[] dinPutUnit_state = new byte[] {-64,-128,64,0};
		byte[] actual = new byte [4];
		actual[0]= SecondConversionMethod.getChainState(dinPutUnit_state[0]);
		actual[1]= SecondConversionMethod.getChainState(dinPutUnit_state[1]);
		actual[2]= SecondConversionMethod.getChainState(dinPutUnit_state[2]);
		actual[3]= SecondConversionMethod.getChainState(dinPutUnit_state[3]);
		byte[] expected = new byte[] {3,2,1,0};
		assertArrayEquals(expected, actual);
	}
	@Test
	void test_getPortNum()
	{
		byte[] dinPutUnit_state = new byte[] {-13,50};
		byte[] actual = new byte [2];
		actual[0]=SecondConversionMethod.getPortNum(dinPutUnit_state[0]);
		actual[1]=SecondConversionMethod.getPortNum(dinPutUnit_state[1]);
		byte[] expected = new byte[] {19,18};
		assertArrayEquals(expected, actual);
	}
	
	@Test
	void test_getUpdatedObj()
	{
		byte[] bitStates = new byte[] {3,2,1,0};
		
		Map<Short, IPropertyObject> chainProp = new HashMap<Short, IPropertyObject>();
		chainProp.put(IProperties.PROP_ID_NAME, new FakePropertyObject("qwerty"));
		FakeResourceObject chain = new FakeResourceObject(chainProp);
				
		Map<Short, IPropertyObject> prop = new HashMap<Short, IPropertyObject>();
		prop.put(IProperties.PROP_ID_CHAIN, new FakeLinkPropertyObj(chain));
		FakeResourceObject port= new FakeResourceObject(prop);
		
		IRelationPropertyObj ports = new FakeRelationPropertyObj(new FakeResourceObjectIterator(port),null);
		
		SecondConversionMethod test = new SecondConversionMethod();
		ArrayList<ChainCommand> actual = test.getUpdatedObj(bitStates, ports);
		ArrayList<ChainCommand> expexted= new ArrayList<ChainCommand>();
		expexted.add(new ChainCommand("qwerty","М", 0));
		assertIterableEquals(expexted, actual);
	}
	
	@Test
	void test_getChangedObj()
	{
		byte[] dinpuunit_state = new byte[] {-13};
		Map<Short, IPropertyObject> chainProp = new HashMap<Short, IPropertyObject>();
		chainProp.put(IProperties.PROP_ID_NAME, new FakePropertyObject("qwerty"));
		FakeResourceObject chain = new FakeResourceObject(chainProp);
				
		Map<Short, IPropertyObject> prop = new HashMap<Short, IPropertyObject>();
		prop.put(IProperties.PROP_ID_CHAIN, new FakeLinkPropertyObj(chain));
		FakeResourceObject port= new FakeResourceObject(prop);
		
		IRelationPropertyObj ports = new FakeRelationPropertyObj(new FakeResourceObjectIterator(port),port);
		
		SecondConversionMethod test = new SecondConversionMethod();
		ArrayList<ChainCommand> actual = test.getChangedObj(dinpuunit_state, ports);
		ArrayList<ChainCommand> expexted= new ArrayList<ChainCommand>();
		expexted.add(new ChainCommand("qwerty","М", 0));
		assertIterableEquals(expexted, actual);
	}
}

