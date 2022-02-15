package converter;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import dcmdon.eventmanager.IEvent;
import dcmdon.eventmanager.RtsParamEvent;
import dcmdon.railway.resources.IProperties;
import dcmdon.railway.resources.IResources;
import dcmdon.resourcemanager.IResourceManager;
import dcmdon.resourcemanager.objects.IBytePropertyObj;
import dcmdon.resourcemanager.objects.ICharPropertyObj;
import dcmdon.resourcemanager.objects.IFilterHandler;
import dcmdon.resourcemanager.objects.ILinkPropertyObj;
import dcmdon.resourcemanager.objects.IRelationPropertyObj;
import dcmdon.resourcemanager.objects.IResourceObject;
import dcmdon.resourcemanager.objects.IResourceObjectIterator;
import dcmdon.rpc.protocol.IArchiveManagerClient;
import dcmdon.rpc.protocol.IContextEvent;
import dcmdon.railway.events.*;
public class SecondConversionMethod implements IConversionMethod
{
	public int firstParPComplexAPPint = 0;
	public int firstParPComplexint = 0;
	public ChainCommand prevChain = null;
	public long timeStamp = 0;
	public long prevEventTime = 0;
	public long time=0;
	
	/** Получает юнит
	 * @param obj пулл юнитов, из которого необходимо найти подходящий
	 * @param num - параметр ивента, по которому отбираем юнит
	 * @return юнит
	 */
	private IResourceObject getUnit(IRelationPropertyObj obj, byte num)
	{
		IResourceObject actualObject=  obj.getRelation( new IFilterHandler()
		{
			
			@Override
			public boolean hasFiltered(IResourceObject a_obj) throws Exception
			{
				IBytePropertyObj unit_num = (IBytePropertyObj)a_obj.getProperty(IProperties.PROP_ID_NUM);
				if (unit_num==null)
					return false;
				return unit_num.getValue()==num;
			}
		});
		return actualObject;
	}
	
	/**Получает порт из пула портов по его номеру
	 * @param obj пул портов
	 * @param num номер порта
	 * @return порт 
	 */
	private IResourceObject getPort(IRelationPropertyObj obj, byte num) 
	{
		IResourceObject actualObject=  obj.getRelation( new IFilterHandler()
		{
			
			@Override
			public boolean hasFiltered(IResourceObject a_obj) throws Exception
			{
				IBytePropertyObj port_num = (IBytePropertyObj)a_obj.getProperty(IProperties.PROP_ID_NUM);
				if (port_num==null)
					return false;
				return port_num.getValue()==num;
			}
		});
		return actualObject;
	}
	
	/**
	 * @param dinputunit_state
	 * @return массив байтов с состояниями (расположены последовательно, от первого к последнему порту)
	 */
	public static byte[] getBitState(byte[] dinputunit_state)
	{   
		BitSet arrayBitSet = BitSet.valueOf(dinputunit_state);
		byte[] chainStates = new byte[dinputunit_state.length*4];
		chainStates[0]=0;
		byte[] state = new byte[1];
		int j=0;
		for(int i=0; i<dinputunit_state.length*8; i+=2)
		{
			state =  arrayBitSet.get(i,i+2).toByteArray();
			if (state.length<1)
			{
				chainStates[j]=0;
			}
			else 
			chainStates[j]=state[0];
			j++;
		}
	    return chainStates;
	}
	
	/**Берёт 5 первых бит, в которых находится номер порта
	 * @param dinPutUnit_state
	 * @return
	 */
	public static byte getPortNum(byte dinPutUnit_state)
	{
		return  (byte) (dinPutUnit_state & 0x1F);
	}
	
	/**Берёт 2 последних бита, в которых состояние объекта
	 * @param dinPutUnit_state
	 * @return
	 */
	public static byte getChainState(byte dinPutUnit_state)
	{
		byte[] bt = new byte [1];
		bt[0]=dinPutUnit_state;  
		BitSet bitSet = BitSet.valueOf(bt);
		bt = bitSet.get(6, 8).toByteArray();
		if (bt.length<1)
			return 0;
		else
		return bt[0];
	}
	
	/**Приводит состояние объекта из byte в string
	 * @param chainState
	 * @return
	 */
	private String getCharState(byte chainState)
	{
		String stringState;
		switch (chainState)
		{
		case 0:
			stringState="Н";
			break;
			
		case 1:
			stringState="П";
			break;
			
		case 2:
			stringState="А";
			break;
			
		case 3:
			stringState="М";
			break;
			
		default:
			stringState="О";
			break;
		}
		return stringState;
	}
	
	/** проверка по PAR_ID_ID_PCOMPLEX и PAR_ID_ID_PCOMPLEX_APP для отсеивания повторений ивентов
	 * @param event
	 * @return true/false
	 */
	private boolean check(IEvent event)
	{
		RtsParamEvent ParPcomplex= event.getParam(IParameters.PAR_ID_ID_PCOMPLEX);
		RtsParamEvent ParPcomplexAPP= event.getParam(IParameters.PAR_ID_ID_PCOMPLEX_APP );
		if(firstParPComplexAPPint==0 || (ParPcomplex.getInt()==firstParPComplexint && ParPcomplexAPP.getInt()==firstParPComplexAPPint))
			{
				return true;
			}
			else
			{
				 return false;
			}			
	}
	
	/**Метод поиска РКП по параметру: PAR_ID_RKP_CODE
	 * @param a_resManager
	 * @param a_rkpCode - код РКП, который необходимо найти
	 * @return объект РКП или null, если он не найден
	 */
	private IResourceObject getRKP(IResourceManager a_resManager, String a_rkpCode)
	{
		IResourceObjectIterator rkpIterator = a_resManager.getResourceObjectFirst(IResources.RES_ID_RKP);
		IResourceObject rkp = null;
		while (rkpIterator!=null)
		{
			rkp=rkpIterator.getResourceObject();
			String code = rkp.getProperty(IProperties.PROP_ID_CODE).stringOfValue();
			if (!(code.equals(a_rkpCode)))
			{
				rkpIterator = rkpIterator.next();
			}
			else 
			{
				break;
			}
		}
		return rkp;
	}
	
	/** Метод возвращает пул портов
	 * @param a_rkp
	 * @param unitNum - номер юнита, которому принадлежит ивент
	 * @return
	 */
	private IRelationPropertyObj getPorts(IResourceObject a_rkp, byte unitNum )
	{
		ILinkPropertyObj linkPropertyObj = (ILinkPropertyObj) a_rkp.getProperty(IProperties.PROP_ID_MATRIXTS);
		IRelationPropertyObj units = (IRelationPropertyObj) linkPropertyObj.getLink().getProperty(IProperties.PROP_ID_UNITS);
		IResourceObject actualUnit = getUnit(units, unitNum);//получаем один юнит из пула юнитов по параметру NUM из ивента
		IRelationPropertyObj ports = (IRelationPropertyObj) actualUnit.getProperty(IProperties.PROP_ID_PORTS);//получаем пул портов юнита
		return ports;
	}
	
	/**Метод получает номер Юнита, к которому он относится
	 * @param event
	 * @return
	 */
	private byte getUnitNum(IEvent event)
	{
		byte unitNum=0;
		if (event.getParam(IParameters.PAR_ID_NUM)!=null) 
		{
			unitNum = event.getParam(IParameters.PAR_ID_NUM).getByte(); 
		}
		else 
		if (event.getParam(IParameters.PAR_ID_ADDR)!=null) 
		{
			unitNum = event.getParam(IParameters.PAR_ID_ADDR).getByte();
		}
		
		return unitNum;
	}
	
	/**Метод получает лист объектов чейн ( для ивентов с EvtId = IEvents.EVT_ID_STATE_UPDATE_PUBLISHER )
	 * @param bitStates состояния портов
	 * @param portsIterator
	 * @return
	 */
	public ArrayList<ChainCommand> getUpdatedObj(byte[] bitStates, IRelationPropertyObj ports)
	{
		IResourceObjectIterator portsIterator = ports.getRelationFirst();
		if(portsIterator!=null)
		{
			ArrayList<ChainCommand> chainlist = new ArrayList<ChainCommand>();
			int i=0;
			while (portsIterator!=null)
			{
				ILinkPropertyObj chainLinkPropertyObj = (ILinkPropertyObj) portsIterator.getResourceObject().getProperty(IProperties.PROP_ID_CHAIN);
				String chainName = chainLinkPropertyObj.getLink().getProperty(IProperties.PROP_ID_NAME).stringOfValue();//получаем имя чейна
				String chainState = getCharState(bitStates[i]);//получаем состояние чейна
				chainlist.add(new ChainCommand(chainName,chainState,0));
				System.out.println(chainName+"  "+ chainState);
				i++;
				portsIterator = portsIterator.next();
			} 
				return chainlist;
		}
		else
		return null;
	}
	
	/**Выставляет таймаут для предыдущего ивента относительно времени текущего
	 * @param chainObj
	 */
	private void setPrevEvTime()
	{
		if(!(prevChain==null))
		{
			prevChain.setTimeout(timeStamp-prevEventTime);
		}
		if (prevEventTime!=0)
		time+=timeStamp-prevEventTime;
		prevEventTime=timeStamp;
	}
	
	/**Метод получает лист объектов чейн ( для ивентов с EvtId = IEvents.EVT_ID_STATE_CHANGE_PUBLISHER )
	 * @param dinPutUnit_state массив байтов с состояниями объектов
	 * @param ports пул портов
	 * @return Возвращает лист с чейнами
	 */
	public ArrayList<ChainCommand> getChangedObj(byte[] dinPutUnit_state, IRelationPropertyObj ports)
	{
		ArrayList<ChainCommand> chainlist = new ArrayList<ChainCommand>();
		for(int j=0; j<dinPutUnit_state.length; j++)
		{
			byte portNum = getPortNum(dinPutUnit_state[j]);
			IResourceObject actualPort = getPort(ports,portNum);
			ILinkPropertyObj chainLinkPropertyObj = (ILinkPropertyObj) actualPort.getProperty(IProperties.PROP_ID_CHAIN);
			String chainName = chainLinkPropertyObj.getLink().getProperty(IProperties.PROP_ID_NAME).stringOfValue();//получаем имя чейна
			String chainState = getCharState( getChainState(dinPutUnit_state[j]) );//получаем состояние чейна
			chainlist.add(new ChainCommand(chainName,chainState,0));
			System.out.println(chainName+"  "+ chainState);
		}
		return chainlist;
	}
	
	public List<ICommand> convert(ArchiveInfo a_archiveInfo) throws InterruptedException, NullPointerException
	{
		
		long firstEvent = a_archiveInfo.getFirstEvent()*1000000;
		long lastEvent = a_archiveInfo.getLastEvent();
		
		if (lastEvent == 0)
		{
			lastEvent = Long.MAX_VALUE;
		}
		IResourceManager resourceManager = a_archiveInfo.getResmgr();
		IResourceObject rkp = null;
		ArrayList<ICommand> commandList = new ArrayList<>();
		IResourceObject prevRkp = null;
		
		//поиск первого объекта chain по resID и eventID 
		short[] evtIDs = new short[2];
		evtIDs[0]= IEvents.EVT_ID_STATE_UPDATE_PUBLISHER;
		evtIDs[1]= IEvents.EVT_ID_STATE_CHANGE_PUBLISHER;
		short[] resIDs = new short [1];
		resIDs[0]= IResources.RES_ID_DINPUTUNIT;
		IArchiveManagerClient m_arcmgr = a_archiveInfo.getArcmgr();
		IContextEvent actualIContextEvent = m_arcmgr.getEventByResAndEvtIds(resIDs, evtIDs, firstEvent,IArchiveManagerClient.NEXT_MODE);
		IEvent actualEvent = actualIContextEvent.getEvent();
		RtsParamEvent firstParPcomplex= actualEvent.getParam(IParameters.PAR_ID_ID_PCOMPLEX);
		RtsParamEvent firstParPcomplexAPP= actualEvent.getParam(IParameters.PAR_ID_ID_PCOMPLEX_APP );
		if(firstParPcomplex!=null && firstParPcomplexAPP!=null)
		{
			firstParPComplexAPPint=firstParPcomplexAPP.getInt();
			firstParPComplexint = firstParPcomplex.getInt();
		}
		
		while (actualEvent!=null)
		{	
			if(check(actualEvent)==true)
			{
				byte num = getUnitNum(actualEvent); 
				
				String rkpCode = actualEvent.getParam(IParameters.PAR_ID_RKP_CODE).getString();  
				byte [] dinPutUnit_state =  actualEvent.getParam(IParameters.PAR_ID_DINPUT_STATE).getBytes();//массив байтов в котором находиться состояния чейнов
				if (dinPutUnit_state.length>1)
				{
					System.out.println();
				}
				
				//поиск РКП, которому принадлежит actualEvent
				rkp=getRKP(resourceManager, rkpCode);
				
				//проверка на появление нового РКП
				if(prevRkp!=rkp)
				{
					ICharPropertyObj rkpName = (ICharPropertyObj) rkp.getProperty(IProperties.PROP_ID_CODE);
					commandList.add(new RkpCommand(rkpName.getStringValue()));
					prevRkp = rkp;
					System.out.println(rkpName.getStringValue());
				}
				
				//переход от РКП к пулу портов
				IRelationPropertyObj ports = getPorts(rkp, num);
				
				timeStamp=actualEvent.getIssueTimestampMillis();//время ивента
				if(actualEvent.getEventId()==IEvents.EVT_ID_STATE_UPDATE_PUBLISHER) // EventId = EVT_ID_STATE_UPDATE_PUBLISHER т.е. берём все порты
				{
					byte [] bitStates = getBitState(dinPutUnit_state);
					
					//получаем лист чейнов
					ArrayList<ChainCommand> chainObjs = getUpdatedObj(bitStates, ports);
					
					//выставляем таймаут предыдущему чейну
					ChainCommand tempoChainCommand =(ChainCommand) chainObjs.get(chainObjs.size()-1);
					setPrevEvTime();
					prevChain=tempoChainCommand;
					
					//добавляем чейны в лист
					commandList.addAll(chainObjs);
				}
				
				else // EventId = EVT_ID_STATE_CHANG_PUBLISHER т.е. берём номера портов и соответсвующие им состояния из dinputunit_state 
				{
						//получаем лист чейнов
						ArrayList<ChainCommand> chainObjs = getChangedObj(dinPutUnit_state, ports);
						
						//выставляем таймаут для предыдущего ивента 
						ChainCommand tempoChainCommand =(ChainCommand) chainObjs.get(chainObjs.size()-1);
						setPrevEvTime();
						prevChain=tempoChainCommand;
						
						//добавляем чейны в лист
						commandList.addAll(chainObjs);
				}
			
			}
			//переход к следущему ивенту
			actualIContextEvent = m_arcmgr.getNextEvent(actualIContextEvent);
			if(actualIContextEvent != null)
			{
				actualEvent = actualIContextEvent.getEvent();
			}
			else
			{
				break;
			}
		}
		System.out.println(time/(1000*60));
		return commandList;
}
}