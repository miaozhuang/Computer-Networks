
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class control {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
	
		//if(IPAddressUtil)
		try {
			InetAddress inet = InetAddress.getLocalHost();
			InetAddress[] ips = InetAddress.getAllByName(inet
					.getCanonicalHostName());
			if (ips != null) {
				for (int i = 0; i < ips.length; i++) {
					System.out.println(ips[i]);
				}
			}
		} catch (UnknownHostException e) {

		}
		InetAddress ia = null;
		ia = InetAddress.getLocalHost();
		String serverHostTemp = ia.getHostAddress().toString();// "192.168.0.15";
		System.out.println("local IP: " + serverHostTemp);
		String serverHost = serverHostTemp;//“192.168.0.11";
		int serverPort = 3344;
		UdpServerSocket udpServerSocket = new UdpServerSocket(serverHost,
				serverPort);
		//ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(500000);
		udpServerSocket.getDs().setReceiveBufferSize(64 * 1024 * 8);
		ExecutorService scheduledThreadPool = Executors.newCachedThreadPool();
		int nPreCount = 0;
		try {
			while (true) {
				String recMsg = udpServerSocket.receive();
				//int nCount = Integer.valueOf(recMsg.substring(0,recMsg.indexOf("aaa")));
				int nCount = Integer.valueOf(recMsg);
				System.out.println("[" + System.currentTimeMillis()
						+ "]recMsg = " + recMsg + " And the count number is "
						+ nCount);
				if ((nCount > nPreCount + 1)||(nCount< nPreCount-15)) {
					System.out.println("Drop "+recMsg);
					continue;
				}
				ResponseSoc resSoc = new ResponseSoc(udpServerSocket, nCount);
				//scheduledThreadPool.schedule(resSoc, 10, TimeUnit.MILLISECONDS);
				scheduledThreadPool.execute(resSoc);
				nPreCount = nCount;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//udpServerSocket.close();
		}
	}
}

class ResponseSoc implements Runnable {
	UdpServerSocket udpServerSocket;
	int recMsgCnt;

	ResponseSoc(UdpServerSocket udpServerSocket, int recMsgCnt) {
		this.udpServerSocket = udpServerSocket;
		this.recMsgCnt = recMsgCnt;
	}

	@Override
	public void run() {
		try {
			Thread.sleep(10);
			//String strRecMsgCnt = String.valueOf(this.recMsgCnt);
			byte[] arbResponseContent = new byte[1];
			arbResponseContent[0] = (byte)this.recMsgCnt;
			System.out.println("[" + System.currentTimeMillis() + "] ACK: "
					+ this.recMsgCnt);// strRecMsgCnt);
			//udpServerSocket.response(strRecMsgCnt);
			udpServerSocket.response(arbResponseContent);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
