package converter;

import java.util.List;

public interface IConversionMethod 
{
	List<ICommand> convert (ArchiveInfo a_archiveInfo)throws InterruptedException;
}
