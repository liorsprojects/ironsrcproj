package il.co.topq.mobile.server.application;

import il.co.topq.mobile.common.client.enums.ClientProperties;
import il.co.topq.mobile.common.server.consts.TcpConsts;
import il.co.topq.mobile.server.impl.ExecutorService;
import il.co.topq.mobile.server.interfaces.IExecutorService;
import il.co.topq.mobile.server.interfaces.IInstrumentationLauncher;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 
 * @author tal ben shabtay the main activity of the tcp server application
 */
public class RobotiumServerActivity extends Activity {
	private static final String TAG = "RobotiumServer";
	private int serverPort;
	private boolean firstLaunch = true;
	private IExecutorService serviceApi;
	private ServiceConnection serviceConnection = new ServiceConnection() {
		/**
		 * set the interface for the service
		 */
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(TAG, "Service connection established");
			serviceApi = IExecutorService.Stub.asInterface(service);
			try {
				serviceApi.startServerCommunication(serverPort);
				serviceApi.registerInstrumenationLauncher(instrumentationLauncherListener);
				// serviceApi.registerInstrumenationLauncher(TcpServerActivity.this);
				// ExecutorService.tcpServer.registerInstrumentationLauncher(TcpServerActivity.this);
			} catch (RemoteException e) {
				Log.e(TAG, "Error while trying to register instrumentation launcher", e);
			}
		}

		/**
		 * destroys the interface to the service
		 */
		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(TAG, "Service connection closed");
			serviceApi = null;
		}
	};

	/**
	 * Get IP address from first non-localhost interface
	 * 
	 * @return first ipv4 external ip address or localhost string
	 */
	private String getIPAddress() {
		try {
			List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress().toUpperCase();
						if (InetAddressUtils.isIPv4Address(sAddr)) {
							return sAddr;
						}
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Execption while getting ip", e);
		}
		return "localhost";
	}

	/**
	 * will update the server details on the screen
	 */
	private void setServerDetailsText() {
		TextView serverDetails = (TextView) findViewById(R.id.server_details);
		Resources res = getResources();
		String str = res.getString(R.string.server_details);
		String text = String.format(str, getIPAddress(), this.serverPort);
		serverDetails.setText(text);
		serverDetails.refreshDrawableState();
	}

	/**
	 * creates the service connection and starts the tcp server communication
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (this.firstLaunch) {
			this.firstLaunch = false;
			readConfiguration();

			setContentView(R.layout.activity_tcp_server);
			setServerDetailsText();

			Intent service = new Intent(ExecutorService.class.getName());
			ComponentName name = startService(service);
			if (null == name) {
				Log.e(TAG, "Failed starting service with intent " + service.toString());
				return;
			}
			if (!bindService(service, this.serviceConnection, 0)) {
				Log.e(TAG, "Failed to bind to service" + service.toString());
				return;

			}
		}
	}

	/**
	 * updates the screen by the selected menu item
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		boolean result = false;
		switch (item.getItemId()) {
		case R.id.menu_settings:
			setNewServerPort();
			result = true;
		default:
			result = super.onOptionsItemSelected(item);
		}
		return result;
	}

	/**
	 * will display the new port dialogue and restart the server with the new
	 * port
	 */
	private void setNewServerPort() {
		final EditText input = new EditText(this);
		new AlertDialog.Builder(this).setTitle("Server Port").setMessage("Set new server port :").setView(input)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					/**
					 * Ok button : restarts the server with the input server
					 * port
					 */
					public void onClick(DialogInterface dialog, int whichButton) {
						Editable value = input.getText();
						try {
							serverPort = Integer.parseInt(value.toString());
							setServerDetailsText();
							serviceApi.startServerCommunication(serverPort);
						} catch (Exception e) {
							Log.e(TAG, "Exception in parse port", e);
						}
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					/**
					 * Cancel button : does nothing
					 */
					public void onClick(DialogInterface dialog, int whichButton) {
						// Do nothing.
					}
				}).show();
	}

	private IInstrumentationLauncher.Stub instrumentationLauncherListener = new IInstrumentationLauncher.Stub() {
		/**
		 * starting the instrumentation defined in the input string
		 */
		public void startInstrumentationServer(String launcherActivityClass) throws RemoteException {
			try {
				Log.i(TAG, "Launching instrumentation for : " + launcherActivityClass);
				Bundle savedInstanceState = new Bundle();
				savedInstanceState.putString("launcherActivityClass", launcherActivityClass);
				startInstrumentation(new ComponentName("il.co.topq.mobile.server.application",
						"il.co.topq.mobile.server.impl.RobotiumExecutor"), null, savedInstanceState);
				Log.i(TAG, "Finished instrumentation launch");
			} catch (Exception e) {
				Log.e(TAG, "Error in command execution", e);
			}
		}
	};

	/**
	 * displays the menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_tcp_server, menu);
		return true;
	}

	/**
	 * reads the configuration from the intent parameters and set the server
	 * with the properties
	 */
	private void readConfiguration() {
		Log.i(TAG, "Reading user configurations");
		String tmpVal = getIntent().getStringExtra(ClientProperties.SERVER_PORT.name());
		if (tmpVal != null && tmpVal.length() != 0) {
			Log.d(TAG, "Recieved server port : " + tmpVal);
			this.serverPort = Integer.parseInt(tmpVal);
		} else {
			Log.d(TAG, "Using default server port");
			this.serverPort = TcpConsts.SERVER_DEFAULT_PORT;
		}
		Log.i(TAG, "Done parsing configurations");
	}

	/**
	 * NOT USED , NEEDED FOR SERVICE CONNECITON
	 */
	public IBinder asBinder() {
		// NOT USED
		return null;
	}
}
