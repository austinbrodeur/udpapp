/**
  * This class handles incoming ACKs
**/


import java.net.*;
import java.util.*;
import java.io.*;
import cpsc441.a3.shared.*;
	

public class ReceiverThread extends Thread {

	private DatagramSocket socket;
	private TxQueue rectQueue;
	private int sPort;
	private int lastSeg;
	private String sName;
	private Thread toM;
	private volatile boolean stop = false;

	public ReceiverThread(DatagramSocket s, TxQueue queue, int lchunk, String name, int port)
	{
		socket = s; // Uses UDP socket from main class
		rectQueue = queue; // Queue address from main class
		lastSeg = lchunk;
		sName = name;
		sPort = port;
	}

	/**
	  * Starts waiting for ACKs
	**/
	public void run()
	{
		byte[] receiveData = new byte[1000]; // Max size of segment payload

		try {
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			while (stop == false) {
				socket.receive(receivePacket);
				Segment ackseg = new Segment(receiveData);
				int acknum = ackseg.getSeqNum();
				//System.out.println("ACK " + acknum + " received");
				deQueue(acknum);
			}
		}
		catch (Exception e) {
			System.out.println("Error in receiver thread: " + e);
		}
	}




	/**
	  * Dequeues segment(s) when an ack is received
	  *
	  * @param seqnum 	The sequence number of the ACK received
	**/
	public synchronized void deQueue(int seqnum) throws InterruptedException
	{
		if (rectQueue.element() != null) {
			int end = seqnum - 1;
			int first = rectQueue.element().getSeqNum();
			if (rectQueue.element().getSeqNum() == end) {
				rectQueue.remove();
			}
			else {
				for (int f = rectQueue.element().getSeqNum(); f <= end; f++) {
					rectQueue.remove();
				}
			}
			//System.out.println("Queue size: " + rectQueue.size());
		}
	}


	/**
	  * Stops this thread gracefully when called
	**/
	public void requestStop()
	{
		stop = true;
	}
}