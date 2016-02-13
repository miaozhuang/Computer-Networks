import java.io.*;  
import java.net.*;  
import java.util.ArrayList;
  
public class UdpClientSocket {  

    String serverHost 		= 	"192.168.0.11";  
    
    public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	int serverPort 			= 	3344;
    
    private int nWinSize			=	1;
    // Time out 50ms
    private final int TIME_OUT		=	50;
    // Receive and send buffer 64k
    private final int SOC_BUFFER	=	64*1024*8;
    
    private int PACKET_SIZE 	= 	1400;
    
    public int getPACKET_SIZE() {
		return PACKET_SIZE;
	}

	public void setPACKET_SIZE(int pACKET_SIZE) {
		PACKET_SIZE = pACKET_SIZE;
	}

	private byte[] buffer 			= new byte[PACKET_SIZE];  
    
    private static DatagramSocket ds= null; 
    
    private int MAXWIN = 8; // 1, 2, 4 , 8, 16, 32, 64, 128, 256 
    
    public int getMaxwin() {
		return MAXWIN;
	}

	public void setMaxwin(int maxwin) {
		MAXWIN = maxwin;
	}

	private final static int ONEMIN = 60000;
  
    //this is for main and thread
    private static boolean bStatus 	= false;
    
    //private int nWinStart			=	0;
    
    private int nWinPos				=	0;
    
    private Integer nFiftyMilliSec 	=	0;
    
    private int nSentPackets 		=	0;
    
    private int nLostPackets 		=	0;
    
    private boolean bNewRound 		=	false;
    
    private boolean bReceive		=	true;
    
    private int 	nTestCount		=	0;

    public int getnTestCount() {
		return nTestCount;
	}

	public void setnTestCount(int nTestCount) {
		this.nTestCount = nTestCount;
	}

	private ArrayList<Integer> SentNum;
      
    public UdpClientSocket() throws Exception { 
    	//InetSocketAddress sa = new InetSocketAddress("10.123.75.117", 57609);
        //ds = new DatagramSocket(sa);
    	SentNum = new ArrayList<Integer>();
        ds = new DatagramSocket(21002);
    	ds.setSendBufferSize(SOC_BUFFER);
    	ds.setReceiveBufferSize(SOC_BUFFER);
    }  
    
    public final void setSoTimeout(final int timeout) throws Exception {  
        ds.setSoTimeout(timeout);  
    }  
  
    public final int getSoTimeout() throws Exception {  
        return ds.getSoTimeout();  
    }  
  
    public final DatagramSocket getSocket() {  
        return ds;  
    }
  
    public final DatagramPacket send(final String host, final int port,  
            final byte[] bytes) throws IOException { 
    	String msg = new String(bytes);
    	System.out.println("["+System.currentTimeMillis()+"]"+"Send "+msg);
        DatagramPacket dp = new DatagramPacket(bytes, bytes.length, InetAddress  
                .getByName(host), port);  
        ds.send(dp);  
        return dp;  
    }  
    
    public final String receive(final String lhost, final int lport)  
            throws Exception {  
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);  
        ds.receive(dp);
        //String info = new String(dp.getData(), 0, dp.getLength());
        return String.valueOf((int)dp.getData()[0]);
        //return new String(dp.getData(), 0, dp.getLength()); 
    }  
  
    public final void close() {  
        try {  
            ds.close();  
        } catch (Exception ex) {  
            ex.printStackTrace();  
        }  
    }  
    boolean writeTargetFile(String strContent)
			throws IOException {
		// Init the output file
    	String strOutFile = ".\\"+"output"+PACKET_SIZE+"B"+MAXWIN+"-"+nTestCount+".txt";
		File file = new File(strOutFile);
		// Declare the output bytes
		byte[] contentInBytes;
		// if the output file does not exist, create the output file
		if (!file.exists()) {
			// if the file can not be written, the file should be deleted and
			// return
			if ((file.createNewFile()) && (!file.canWrite())) {
				System.out.println("[ERROR] Output file is not writable.");
				file.delete();
				return false;
			}
		}
		// Init the FileOutputStream
		FileOutputStream fop = new FileOutputStream(file);
		// init the strbContent
		StringBuffer strbContent = new StringBuffer(strContent); 
		// if the content is not null, Set the first character to upper letter and add . to the end
		// get the content in bytes
		contentInBytes = strbContent.toString().getBytes();
		// write to the file
		fop.write(contentInBytes);
		fop.flush();
		// close the FileOutputStream
		fop.close();
		// return true if write output file successfully
		return true;
	}
    public void startThread()
    {
        Runnable r 				= 	new ReceiveRunnable(this, serverHost, serverPort);
        Thread t 				= 	new Thread(r);
        t.setDaemon(true);
        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }
    public void test(){
    	nWinPos				=	0;
    	nSentPackets 		=	0;
    	nLostPackets 		=	0;
    	try
    	{
    		String strPkgTemp = "";
    		for(int nCnt = 0; nCnt < PACKET_SIZE; nCnt++)
    		{
    			strPkgTemp += "a";
    		}
	        int nRound 		= 0;
	        int nLongestWin = 0;
        	long nStartTime = System.currentTimeMillis();
        	System.out.println("startTime = "+nStartTime);
	        while(true)
	        {
	        	synchronized (SentNum)
	        	{
	        		SentNum.clear();
	        	}
	        	System.out.println("************************************Round "+(++nRound)+"*********************************");
	        	bNewRound		=	true;
	        	Integer count 	=	nWinPos+1;
	        	//nWinStart		=	count;
	        	int winCount	=	0;
	        	if(nLongestWin < nWinSize)
	        	{
	        		nLongestWin = nWinSize;
	        	}
	        	bStatus = true;
        		long nCurrentTime =  System.currentTimeMillis();
	        	while(winCount < nWinSize)
	        	{
	        		if(nCurrentTime - nStartTime >= ONEMIN)
	        		{
	        			System.out.println("One min Time is up!!!");
	        			if(!SentNum.isEmpty())
	        			{
	        				Reminder remExit = new Reminder(TIME_OUT, nFiftyMilliSec);
	        				while(remExit.getnFlag() == 0)
	        	        	{
	        	        		Thread.sleep(5);
	        	        		if(SentNum.isEmpty())
	        	        		{
	        	        			remExit.Cancel();
	        	        			break;
	        	        		}
	        	        	}
	        				nLostPackets +=  SentNum.size();
	        			}
	        			double dCostTime	=((double)System.currentTimeMillis() - (double)nStartTime)/1000.0;
	        			int nThroughput 	= (int) (((double)(nSentPackets-nLostPackets)*PACKET_SIZE*8)/dCostTime);
	        			String strContent ="Time Costs = " + dCostTime +" seconds\n\r"+"Longest Window Size is "+nLongestWin+"\n\rLost Packets number is "+nLostPackets+"\n\rLost Bytes number is "+nLostPackets*PACKET_SIZE+"\nTotal Packets number is "+nSentPackets+"\n\rThe Throughput is "+nThroughput+" bit per second";
	        			writeTargetFile(strContent);
	        			System.out.println("Time Costs = " + dCostTime +" seconds");
	        			System.out.println("Longest Window Size is "+nLongestWin);
	        			System.out.println("Lost Packets number is "+nLostPackets);
	        			System.out.println("Lost Bytes number is "+nLostPackets*PACKET_SIZE);
	        			System.out.println("Total Packets number is "+nSentPackets);
	        			System.out.println("The Throughput is "+nThroughput+" bit per second");
	        			//bReceive = false;
	        			return ;
	        		}
		        	byte[] arbSendContent = strPkgTemp.getBytes();
		        	arbSendContent[0] = (byte) (count%256);
		        	synchronized (SentNum)
		        	{
		        		SentNum.add((int)arbSendContent[0]);
		        	}
		        	this.send(serverHost, serverPort, arbSendContent);
		        	nSentPackets++;
		        	count++;
		        	winCount++;
	        	}
	        	Reminder rem = new Reminder(TIME_OUT, nFiftyMilliSec);
	        	boolean bSizeEnlarged = false;
	        	while(rem.getnFlag() == 0)
	        	{
	        		Thread.sleep(5);
	        		if(SentNum.isEmpty())
	        		{
	        			nWinPos = nWinPos+nWinSize;
	        			nWinSize++;
	        			if(nWinSize > MAXWIN)
	        			{
	        				nWinSize = MAXWIN;
	        			}
	        			System.out.println("Window size has been enlarged to "+nWinSize);
	        			bSizeEnlarged = true;
	        			rem.Cancel();
	        			break;
	        		}
	        	}
	        	bStatus = false;
	        	if((!SentNum.isEmpty())&&(!bSizeEnlarged))
	        	{
	        		nLostPackets +=  SentNum.size();
	        		nWinPos = SentNum.get(0)-1;
	        		nWinSize = (int)Math.ceil(nWinSize/2);
	        		if(nWinSize == 0)
	        		{
	        			nWinSize = 1;
	        		}
	        		System.out.println("Window size has been deminished to "+nWinSize);
	        	}
	        }
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    class ReceiveRunnable implements Runnable
    {
    	private UdpClientSocket client;
        private String serverHost;  
        private int serverPort;
        private String info;
        
        public String getInfo() {
    		return info;
    	}
        
    	public ReceiveRunnable(UdpClientSocket client, String serverHost, int serverPort)
    	{
    		this.client = client;
    		this.serverHost = serverHost;
    		this.serverPort = serverPort;
    	}
    	
    	@Override
    	public void run() {
    		try {
    			System.out.println("[thread running]");
    			while(this.client.bReceive)
    			{
    				info = client.receive(serverHost, serverPort);
    				System.out.println("info = "+ info);
    				if(bStatus)
    				{
    					System.out.println("["+System.currentTimeMillis()+"]receive: "+ info);
    					System.out.println("bNewRound = "+String.valueOf(bNewRound));
    					Integer nACKNum = Integer.valueOf(info);
    					if(SentNum.contains(nACKNum))
    					{
    						synchronized (SentNum)
    						{
    							SentNum.remove(nACKNum);
    						}
    					}
						else
						{
							System.out.println("["+System.currentTimeMillis()+"]"+"Data from Server: " + info+"has been dropped because they are not suppose to come");
						}
    				}
    				else
    				{
    					System.out.println("["+System.currentTimeMillis()+"]"+"Data from Server: " + info+"has been dropped because they are coming in inapproriate time");
    				}
    				Thread.sleep(1);
    			}
    		} catch (Exception e) {
    			System.out.println(System.currentTimeMillis());
    			e.printStackTrace();
    		}  
    	}
    }
}