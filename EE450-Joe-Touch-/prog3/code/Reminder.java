
import java.util.Timer;
import java.util.TimerTask;

public class Reminder {
	private Timer timer;
	private int milliSeconds;
	private Integer nFlag = 0;
	private int nValue = Integer.MIN_VALUE;
	
	public Integer getnFlag() {
		return this.nFlag;
	}
	
	public void Cancel()
	{
		timer.cancel();
	}
	
	public Reminder(int milliSeconds) {
		this.milliSeconds 	= 	milliSeconds;
		timer = new Timer();
		timer.schedule(new RemindTask(), milliSeconds);
	}
	
	public Reminder(int milliSeconds, Integer nFlag) {
		this.milliSeconds 	= 	milliSeconds;
		this.nFlag 			= 	nFlag;
		timer = new Timer();
		timer.schedule(new RemindTask(), milliSeconds);
	}
	
	public Reminder(int milliSeconds, Integer nFlag, int nValue) {
		this.milliSeconds 	= 	milliSeconds;
		this.nFlag 			= 	nFlag;
		this.nValue			=	nValue;
		timer = new Timer();
		timer.schedule(new RemindTask(), milliSeconds);
	}

	class RemindTask extends TimerTask {
		@Override
		public void run() {
			System.out.println("["+System.currentTimeMillis()+"]"+milliSeconds + " ms time is up!");
			timer.cancel(); // Terminate the timer thread
			if(nValue == Integer.MIN_VALUE)
			{
				nFlag++;
			}
			else
			{
				nFlag = nValue;
			}
		}
	}
}
