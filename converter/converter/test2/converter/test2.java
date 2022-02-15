package converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import chainConversionMethodTest.FakeEnumPropertyObj;
import chainConversionMethodTest.FakeEvent;
import dcmdon.eventmanager.RtsParamEvent;
import dcmdon.events.IParameters;
import dcmdon.railway.resources.IProperties;
import dcmdon.resourcemanager.objects.IPropertyObject;
import dinputUnitConversionMethodTest.FakeResourceObject;

class test2
{
	
	@Test
	void testgetChainState()
	{
		byte b = 127;
		
		Map<Short, RtsParamEvent> map = new HashMap<Short, RtsParamEvent>();
		RtsParamEvent rts = new RtsParamEvent(IParameters.PAR_ID_STATE,b,1);
		map.put(IParameters.PAR_ID_STATE,rts );
		
		FakeEvent event = new FakeEvent(map);
		byte state = event.getParam(IParameters.PAR_ID_STATE).getByte();
		
		Map<Short, IPropertyObject> prop = new HashMap<Short, IPropertyObject>();
		FakeEnumPropertyObj enumPropertyObj = new FakeEnumPropertyObj();
		prop.put(IProperties.PROP_ID_STATE,enumPropertyObj );
		
		FakeResourceObject chain= new FakeResourceObject(prop);
		FakeEnumPropertyObj propState = (FakeEnumPropertyObj) chain.getProperty(IProperties.PROP_ID_STATE);
		ChainConversionMethod aChainConversionMethod = new ChainConversionMethod();	
		String actual=aChainConversionMethod.getChainState(event, chain);
		String expected = "127";
		assertEquals(expected, actual);
	}

}
