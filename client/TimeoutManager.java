/**
  * This class manages timeouts
**/



import java.net.*;
import java.util.*;
import java.io.*;
import cpsc441.a3.shared.*;
import java.util.TimerTask;


public class TimeoutManager implements Runnable
{
	private TxQueue rectQueue;
	private DatagramSocket socket;
	private String sName;
	private int sPort;
	private int toTime;
	private Segment cSeg = null;
	private Timer timer;
	private int[] rtCount;


	/**
	  * Constructor
	**/
	public TimeoutManager(TxQueue q, DatagramSocket s, String address, int port, int tO, Timer t, int[] count)
	{
		rectQueue = q;
		socket = s;
		sName = address;
		sPort = port;
		toTime = tO;
		timer = t;
		rtCount = count;
	}

	/**
	  * Run method override
	**/
	public void run()
	{
		manageTimer();
	}

	/**
	  * Detects when the front of the queue changes and updates the timout timer accordingly.
	**/
	public synchronized void manageTimer()
	{
		while(true)
		{
			if ((cSeg == null) && (rectQueue.size() != 0) || (cSeg != rectQueue.element() && rectQueue.element() != null)) {
				cSeg = rectQueue.element();
				timer.cancel();
				timer = new Timer(true);
				timer.schedule(new TimeoutHandler(rectQueue, socket, sName, sPort, rtCount), toTime);
				//System.out.println("Timeout started");
			}
		}
	}
}