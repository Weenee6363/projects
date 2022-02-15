package converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import dcmdon.resourcemanager.IResourceManager;
import dcmdon.resourcemanager.ResourceManagerActivator;
import dcmdon.rpc.protocol.Activator;
import dcmdon.rpc.protocol.IArchiveManagerClient;

public class TestApplication implements IApplication
{

	@Override
	public Object start(IApplicationContext a_context) throws NullPointerException, InterruptedException, IOException 
	{	
		IResourceManager testManager = ResourceManagerActivator.getResourceManager(new File("C:\\Users\\andre\\eclipse\\java-2019-06\\eclipse\\converter\\input\\resources").toURI().toURL());
		IArchiveManagerClient testManagerClient = Activator.getArchiveManagerClient(new URL("file:///C:/Users/andre/eclipse/java-2019-06/eclipse/converter/input/archives"), "STM", 100 );
		ArchiveInfo testInfo = new ArchiveInfo(testManagerClient, testManager, 0,0);
		ChainConversionMethod testChainConversionMethod = new ChainConversionMethod();
		List<ICommand> testArrayList = new ArrayList<>();
		testArrayList=testChainConversionMethod.convert(testInfo);
		File file = new File("C:\\Users\\andre\\Documents\\test.txt");
		file.createNewFile();
		FileWriter writer = new FileWriter(file);
		for(int i=0; i<testArrayList.size(); i++)
		{
			writer.write(testArrayList.get(i).toString()+"\n");
		}
		writer.flush();
		writer.close();
		return null;
	}

	@Override
	public void stop()
	{
		// TODO Auto-generated method stub
		
	}

	

}
