package il.co.topq.mobile.server.interfaces;

import il.co.topq.mobile.server.interfaces.IDataCallback;
import il.co.topq.mobile.server.interfaces.IInstrumentationLauncher;

interface IExecutorService {

//	String executeCommand(String data);
	void registerExecutor(IDataCallback executor);
	void registerInstrumenationLauncher(IInstrumentationLauncher iInstrumentationLauncher);
	void startServerCommunication(int serverPort);
}