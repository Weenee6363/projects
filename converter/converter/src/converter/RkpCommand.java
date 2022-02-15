package converter;

/** Переключение РКП
 * @author andre
 *
 */
public class RkpCommand implements ICommand
{
String rkpName;



/**
 * @param a_rkpName - Название РКП
 */
public RkpCommand(String a_rkpName)
{
	super();
	rkpName = a_rkpName;
}



@Override
	public String toString()
	{
		// TODO Auto-generated method stub
		return "$РКП("+rkpName+")";
	}
}
