package converter;

/** 
 * @author andre
 *
 */
public class ChainCommand implements ICommand
{

	private String m_chainName;
	private String m_chainValue;
	private long m_timeout;
	
	/**
	 * @param a_chainName - имя чейна
	 * @param a_chainValue - состояние чейна
	 * @param a_timeout - задержка между событиями
	 */
	public ChainCommand(String a_chainName, String a_chainValue, long a_timeout)
	{
		super();
		m_chainName = a_chainName;
		m_chainValue = a_chainValue;
		m_timeout = a_timeout;
	}

	@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return m_chainName+"="+m_chainValue+" ("+m_timeout +")";
	}

	public void setTimeout(long a_timeout)
	{
		m_timeout = a_timeout;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((m_chainName == null) ? 0 : m_chainName.hashCode());
		result = prime * result + ((m_chainValue == null) ? 0 : m_chainValue.hashCode());
		result = prime * result + (int) (m_timeout ^ (m_timeout >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChainCommand other = (ChainCommand) obj;
		if (m_chainName == null)
		{
			if (other.m_chainName != null)
				return false;
		} else if (!m_chainName.equals(other.m_chainName))
			return false;
		if (m_chainValue == null)
		{
			if (other.m_chainValue != null)
				return false;
		} else if (!m_chainValue.equals(other.m_chainValue))
			return false;
		if (m_timeout != other.m_timeout)
			return false;
		return true;
	}
	
	
	
}
