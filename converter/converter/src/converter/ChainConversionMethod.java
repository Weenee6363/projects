package converter;

import static org.junit.Assert.assertNotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import dcmdon.eventmanager.IEvent;
import dcmdon.eventmanager.RtsParamEvent;
import dcmdon.events.IParameters;
import dcmdon.railway.events.IEvents;
import dcmdon.railway.resources.IProperties;
import dcmdon.railway.resources.IResources;
import dcmdon.resourcemanager.IResourceManager;
import dcmdon.resourcemanager.objects.ICharPropertyObj;
import dcmdon.resourcemanager.objects.IEnumPropertyObj;
import dcmdon.resourcemanager.objects.IResourceObject;
import dcmdon.rpc.protocol.IArchiveManagerClient;
import dcmdon.rpc.protocol.IContextEvent;

public class ChainConversionMethod implements IConversionMethod
{

	public int firstParPComplexAPPint = 0;
	public int firstParPComplexint = 0;
	IResourceManager resourceManager ;
	IResourceObject chain;
	long key;
	/** Метод конвертирует архив в текстовый файл
	 *
	 */
	public List<ICommand> convert(ArchiveInfo a_archiveInfo) throws InterruptedException, NullPointerException
	{

		IContextEvent actualIContextEvent;
		IArchiveManagerClient m_arcmgr = a_archiveInfo.getArcmgr();
		System.out.println("beginEvTime = "+Date.from(Instant.ofEpochMilli(m_arcmgr.getBegEventTime()/1000000)));
		System.out.println("EndEvTime = "+Date.from(Instant.ofEpochMilli(m_arcmgr.getEndEventTime()/1000000)));
		
		long key;
		String charChainState = "Н";
		long timeStamp = 0;
		long prevEventTime = 0;
		ChainCommand prevEvent = null;
		long firstEvent = a_archiveInfo.getFirstEvent()*1000000;
		long lastEvent = a_archiveInfo.getLastEvent();
		long time=0;
		String chainName=null;
		if (lastEvent == 0)
		{
			lastEvent = Long.MAX_VALUE;
		}
		 resourceManager = a_archiveInfo.getResmgr();
		IResourceObject rkp = null;
		ArrayList<ICommand> commandList = new ArrayList<>();
		IResourceObject prevRkp = null;
		
		//поиск первого объекта chain по resID и eventID 
		actualIContextEvent = m_arcmgr.getEventByResAndEvtId(IResources.RES_ID_CHAIN,
		IEvents.EVT_ID_CHANGE_DIGITAL_STATE, firstEvent, IArchiveManagerClient.NEXT_MODE);	
		IEvent actualEvent = actualIContextEvent.getEvent();
		RtsParamEvent firstParPcomplex= actualEvent.getParam(IParameters.PAR_ID_ID_PCOMPLEX);
		RtsParamEvent firstParPcomplexAPP= actualEvent.getParam(IParameters.PAR_ID_ID_PCOMPLEX_APP );
		RtsParamEvent actualParPcomplex=null;
		
		if(firstParPcomplex!=null && firstParPcomplexAPP!=null)
		{
			firstParPComplexAPPint=firstParPcomplexAPP.getInt();
			firstParPComplexint = firstParPcomplex.getInt();
		}
		timeStamp = actualEvent.getIssueTimestampMillis();
		while(actualIContextEvent != null && (lastEvent == 0 || timeStamp <= lastEvent)) 
		{
			try
			{
				actualEvent = actualIContextEvent.getEvent();
				actualParPcomplex= actualEvent.getParam(IParameters.PAR_ID_ID_PCOMPLEX);
				
				//отсеивание повторных ивентов
				if(checkPComplexAndAPP(actualEvent)==true)
				{
					//находим объект chain
					key = actualEvent.getParam(IParameters.PAR_ID_KEY).getLong();
					chain = resourceManager.getResourceObject(IResources.RES_ID_CHAIN, key);
					
					//находим имя объекта chain
					chainName=getChainName();
					
					//находим состояние объекта chain
					String chainState = getChainState(actualEvent);
	
					// Проверка сосотояния объекта chain
					charChainState = chainStateToChar(chainState);
					
					// Проверка РКП
					rkp = resourceManager.getOwnerByPath(chain, IResources.RES_ID_RKP);
					if (rkp != null && rkp != prevRkp)
					{
						ICharPropertyObj rkpName = (ICharPropertyObj) rkp.getProperty(IProperties.PROP_ID_CODE);
						commandList.add(new RkpCommand(rkpName.getStringValue()));
						prevRkp = rkp;
					}
					if (rkp!=null)
					{
						timeStamp = actualEvent.getIssueTimestampMillis();
						if (prevEvent!=null)
						{
							prevEvent.setTimeout(timeStamp-prevEventTime);
						}
										
						ChainCommand m_chainObj = new ChainCommand(chainName, charChainState, 0);
						System.out.println("PcomplexAPP"+actualParPcomplex.getInt()+"  "+chainName);
						commandList.add(m_chainObj);
						prevEventTime=timeStamp;
						prevEvent= m_chainObj;
					}
				}
				if(m_arcmgr.getNextEvent(actualIContextEvent, lastEvent)==null)
				{
					break;
				}
				actualIContextEvent = m_arcmgr.getNextEvent(actualIContextEvent, lastEvent);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			
		}
		return commandList;
	}
	
	/** проверка по PAR_ID_ID_PCOMPLEX и PAR_ID_ID_PCOMPLEX_APP для отсеивания повторений ивентов
	 * @param event
	 * @return true/false
	 */
	private boolean checkPComplexAndAPP(IEvent event)
	{
		RtsParamEvent ParPcomplex= event.getParam(IParameters.PAR_ID_ID_PCOMPLEX);
		RtsParamEvent ParPcomplexAPP= event.getParam(IParameters.PAR_ID_ID_PCOMPLEX_APP );
		if(firstParPComplexAPPint==0 || (ParPcomplex.getInt()==firstParPComplexint && ParPcomplexAPP.getInt()==firstParPComplexAPPint))
			return true;
		else
			 return false;			
	}
	
	
	/**возвращает состояние объекта chain полностью (active/passive/blink/unknown/error)
	 * @param event
	 * @return
	 */
	public String getChainState(IEvent event)
	{
		byte state = event.getParam(IParameters.PAR_ID_STATE).getByte();
		IEnumPropertyObj propState = (IEnumPropertyObj) chain.getProperty(IProperties.PROP_ID_STATE);
		String chainState = propState.getAliasByValue(state);
		return chainState;
	}
	
	/** метод возвращает имя объекта chain
	 * @param event
	 * @return имя объекта
	 */
	private String getChainName()
	{
		ICharPropertyObj propName = (ICharPropertyObj) chain.getProperty(IProperties.PROP_ID_NAME);
		String propNameString = propName.getStringValue();
		return propNameString;
	}

	/** Конвертирует состояние объекта Chain (active = A)
	 * @param chainState
	 * @return состояние объекта буквой
	 */
	private String chainStateToChar(String chainState)
	{
		String charChainState;
		switch (chainState)
		{
		case "active":
			charChainState = "А";
			break;

		case "passive":
			charChainState = "П";
			break;

		case "blink":
			charChainState = "М";
			break;

		case "error":
			charChainState = "О";
			break;

		case "unknown":
			charChainState = "Н";
			break;

		default:
			charChainState = "Н";
			break;
		}
		return charChainState;
	}
		
}
