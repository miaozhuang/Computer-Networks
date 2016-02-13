
import java.net.InetAddress;
import java.util.Scanner;

public class ControlMain {

	static int[] nPacketSize = {32, 512, 1400, 8192};
	private static Scanner input;
	public ControlMain() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * The whole program's engrance
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			InetAddress ia = InetAddress.getLocalHost(); 
			System.out.println("IP="+ia.getHostAddress());
			input = new Scanner(System.in);
			System.out.println("Please input the server's IPV4 address");
			String strInput = input.nextLine();
			UdpClientSocket clientUDP = new UdpClientSocket();
			clientUDP.setServerHost(strInput);
			clientUDP.startThread();
			for(int nc = 0; nc < nPacketSize.length; nc++)
			{
				for(int nCnt = 0; nCnt < 9; nCnt++)
				{
					for(int nTryCnt = 0; nTryCnt < 5; nTryCnt ++)
					{
						clientUDP.setPACKET_SIZE(nPacketSize[nc]);
						clientUDP.setMaxwin((int)Math.pow(2,nCnt));
						clientUDP.setnTestCount(nTryCnt+1);
						clientUDP.test();
						Thread.sleep(6000);
					}
				}
			}
			//UdpServerSocket serverUDP = new UdpServerSocket();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
