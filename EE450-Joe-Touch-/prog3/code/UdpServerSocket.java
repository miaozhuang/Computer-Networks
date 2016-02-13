
import java.io.BufferedReader;
import java.io.IOException;  
import java.io.InputStreamReader;
import java.net.DatagramPacket;  
import java.net.DatagramSocket;  
import java.net.InetAddress;  
import java.net.InetSocketAddress;  
import java.net.SocketException;  
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
  

public class UdpServerSocket {  
    private byte[] buffer = new byte[8192];  
      
    private DatagramSocket ds = null;  
  
    public DatagramSocket getDs() {
		return ds;
	}

    private DatagramPacket packet = null;  
  
    private InetSocketAddress socketAddress = null;  
  
    private String orgIp;
    
    private DatagramPacket dp = null;
    

    public UdpServerSocket(String host, int port) throws Exception {
        packet = new DatagramPacket(buffer, buffer.length); 
        socketAddress = new InetSocketAddress(host, port);  
        ds = new DatagramSocket(socketAddress);  
    	ds.setSendBufferSize(64*1024*8);
    	ds.setReceiveBufferSize(64*1024*8);
        System.out.println("Server Start!");  
    }  
      
    public String getOrgIp() {  
        return orgIp;  
    }  

    public void setSoTimeout(int timeout) throws Exception {  
        ds.setSoTimeout(timeout);  
    }  
   
    public int getSoTimeout() throws Exception {  
        return ds.getSoTimeout();  
    }  

    public void bind(String host, int port) throws SocketException {  
        socketAddress = new InetSocketAddress(host, port);  
        ds = new DatagramSocket(socketAddress);  
    }  

    public String receive() throws IOException {   
        ds.receive(packet);
        if(dp == null)
        {
        	dp = new DatagramPacket(buffer, buffer.length, packet.getAddress(), packet.getPort());
        }
        orgIp = packet.getAddress().getHostAddress();  
        //String info = new String(packet.getData(), 0, packet.getLength());  
        String info = String.valueOf((int)packet.getData()[0]);
        System.out.println("["+System.currentTimeMillis()+"]received msg：" + info);  
        return info;
    }  

    public void response(String info) throws IOException {    
        dp.setData(info.getBytes());  
        ds.send(dp);
        System.out.println("["+System.currentTimeMillis()+"]Client Address : " + packet.getAddress().getHostAddress()  
                + ",port：" + packet.getPort()); 
    }
    
    public void response(byte[] info) throws IOException {    
        dp.setData(info);  
        ds.send(dp);
        System.out.println("["+System.currentTimeMillis()+"]Client Address : " + packet.getAddress().getHostAddress()  
                + ",port：" + packet.getPort()); 
    }  

    public void setLength(int bufsize) {  
        packet.setLength(bufsize);  
    }  

    public InetAddress getResponseAddress() {  
        return packet.getAddress();  
    }  
 
    public int getResponsePort() {  
        return packet.getPort();  
    }  
 
    public void close() {  
        try {  
            ds.close();  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
    }  
/*
    public void go() throws Exception {
    	Random ran = new Random(System.currentTimeMillis());
    	try {
    		  InetAddress inet = InetAddress.getLocalHost();
    		  InetAddress[] ips = InetAddress.getAllByName(inet.getCanonicalHostName());
    		  if (ips  != null ) {
    		    for (int i = 0; i < ips.length; i++) {
    		      System.out.println(ips[i]);
    		    }
    		  }
    		} catch (UnknownHostException e) {

    		}    

    	InetAddress ia = null;
    	ia = InetAddress.getLoopbackAddress();//.getLocalHost();
        String serverHostTemp = ia.getHostAddress().toString();//"192.168.0.15";
        System.out.println("local IP: "+ serverHostTemp);
    	String serverHost = "10.123.91.24";
        int serverPort = 3344;
        UdpServerSocket udpServerSocket = new UdpServerSocket(serverHost, serverPort);
        //ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(50000);  
    	udpServerSocket.ds.setReceiveBufferSize(64*1024*8);
    	int nPreCount = 0;
    	try
    	{
        while (true) {
        	String recMsg = udpServerSocket.receive();
        	int nCount = Integer.valueOf(recMsg.substring(0, recMsg.indexOf("aaa")));
        	System.out.println("["+System.currentTimeMillis()+"]recMsg = "+recMsg+" And the count number is "+ nCount);
        	if(nCount > nPreCount+1)
        	{
        		continue;
        	}
        	//Reminder rem = new Reminder(50, );
            //String strRecMsgCnt = "ACK: "+ nCurrentNo + " MSG";
            //System.out.println(strRecMsgCnt);
        	//udpServerSocket.response(strRecMsgCnt);
        	ResponseSoc resSoc = new ResponseSoc(udpServerSocket, nCount, dp);
        	//scheduledThreadPool.execute(resSoc);
        	//udpServerSocket.response(strRecMsgCnt);
        	scheduledThreadPool.schedule(resSoc,1, TimeUnit.MILLISECONDS);
        	nPreCount = nCount;
        }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
        finally
        {
        	udpServerSocket.close();
        }
    }
*/
}