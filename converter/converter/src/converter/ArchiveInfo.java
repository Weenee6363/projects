package converter;

import dcmdon.resourcemanager.IResourceManager;
import dcmdon.rpc.protocol.IArchiveManagerClient;

/**
 * @author andre
 *  Конфигурационный объект
 */
public class ArchiveInfo {
	private IArchiveManagerClient m_arcmgr;
	private IResourceManager m_resmgr;
	private long m_firstEvent;
	private long m_lastEvent;
	
	/**
	 * @param a_arcmgr - архив 
	 * @param a_resmgr - бд
	 * @param a_firstEvent - начальный момент времени в мс
	 * @param a_lastEvent - конечный момент времени в мс
	 */
	public ArchiveInfo(IArchiveManagerClient a_arcmgr, IResourceManager a_resmgr, long a_firstEvent, long a_lastEvent)
	{
		super();
		m_arcmgr = a_arcmgr;
		m_resmgr = a_resmgr;
		m_firstEvent = a_firstEvent;
		m_lastEvent = a_lastEvent;
	}
	
	public IArchiveManagerClient getArcmgr()
	{
		return m_arcmgr;
	}

	public void setArcmgr(IArchiveManagerClient a_arcmgr)
	{
		m_arcmgr = a_arcmgr;
	}

	public IResourceManager getResmgr()
	{
		return m_resmgr;
	}

	public void setResmgr(IResourceManager a_resmgr)
	{
		m_resmgr = a_resmgr;
	}

	/**
	 * @return начальный момент времени в мс
	 */
	public long getFirstEvent()
	{
		return m_firstEvent;
	}

	/**
	 * @param a_firstEvent - начальный момент времени в мс
	 */
	public void setFirstEvent(long a_firstEvent)
	{
		m_firstEvent = a_firstEvent;
	}

	/**
	 * @return конечный момент времени в мс
	 */
	public long getLastEvent()
	{
		return m_lastEvent;
	}

	/**
	 * @param a_lastEvent - конечный момент времени в мс
	 */
	public void setLastEvent(long a_lastEvent)
	{
		m_lastEvent = a_lastEvent;
	}

}
