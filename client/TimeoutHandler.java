/**
  * This class handldes what happens after a timeout expires
**/


import java.net.*;
import java.util.*;
import java.io.*;
import cpsc441.a3.shared.*;
import java.util.TimerTask;

public class TimeoutHandler extends TimerTask
{

	private TxQueue rectQueue;
	private DatagramSocket socket;
	private String sName;
	private int sPort;
	private int[] rtCount;

	/**
	  * Constructor
	**/
	public TimeoutHandler(TxQueue q, DatagramSocket s, String address, int port, int[] count)
	{
		rectQueue = q;
		socket = s;
		sName = address;
		sPort = port;
		rtCount = count;
	}

	/**
	  * Run method override
	**/
	public void run()
	{
		retransmit();
	}

	/**
	  * Retransmits the entire queue when the timer expires
	**/
	public synchronized void retransmit()
	{
		if (!rectQueue.isEmpty()) {
			rtCount[0] += 1;
			Segment[] tArray = rectQueue.toArray();
			//System.out.println("Retransmitting queue...");
			for (Segment s : tArray) {
				try {
					//System.out.println("Sending " + s.getBytes().length + " bytes of data to " + InetAddress.getByName(sName).toString() + ":" + sPort + " with seqnum " + s.getSeqNum());
					DatagramPacket sendPacket = new DatagramPacket(s.getBytes(), s.getBytes().length, InetAddress.getByName(sName), sPort);
					socket.send(sendPacket);
				}
				catch (IOException e) {
					System.out.println("Error during retransmission: " + e);
				}
			}
		}
	}
}