package com.ewebcomputing.blackberry;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MultipartMessage;
import javax.wireless.messaging.TextMessage;

import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.Store;
import net.rim.blackberry.api.sms.SMS;
import net.rim.blackberry.api.sms.SendListener;
import net.rim.device.api.system.Application;

public class SMSForward extends Application implements SendListener {	
	public static void main(String[] args) {
		SMSForward theApp = new SMSForward();
		theApp.enterEventDispatcher();
	}
	
	public boolean sendMessage(Message m)	{		
		if(m instanceof TextMessage)
			sendMessage(m.getAddress(), ((TextMessage)m).getPayloadText());
		else if(m instanceof BinaryMessage){
			sendMessage(m.getAddress(), new String(((BinaryMessage)m).getPayloadData()));
		}
		else if(m instanceof MultipartMessage)	{
			//sendMessage(m.getAddress(), )
		}
		return true;
	}
	
	private volatile String email = "";
	BackgroundApp ba = null;
	DatagramConnection mSMSConnection = null;
	DatagramConnection mSMSConnectionSend = null;

	public SMSForward() {
		email = "9172516044";
		ba = new BackgroundApp();
		ba.start();
		SMS.addSendListener(this);
		try {
			mSMSConnection = (DatagramConnection) Connector.open("sms://:0");
			mSMSConnectionSend = (DatagramConnection) Connector.open("sms://"
					+ email);
			Store store = Session.waitForDefaultSession().getStore();			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public synchronized String getEmail() {
		return email;
	}

	public synchronized void setEmail(String lemail) {
		this.email = lemail;
	}

	public void processMessage(String[] args) {			
		sendMessage(args[0], args[1]);		
	}

	

	public void sendMessage(String addr, String _data) {		
		byte[] data = _data.getBytes();
		try {
			Datagram d = mSMSConnectionSend.newDatagram(mSMSConnectionSend.getMaximumLength());
			d.setData(data, 0, data.length);
			mSMSConnectionSend.send(d);
		} catch (Throwable me) {
			System.err.println(me);
		}
	}

	private String[] getMessage() {
		try {
			Datagram d = mSMSConnection.newDatagram(mSMSConnection
					.getMaximumLength());
			mSMSConnection.receive(d);
			String address = new String(d.getAddress());
			String msg = new String(d.getData());
			return new String[] { address, msg };
		} catch (Throwable e) {

			return new String[] {};
		}
	}

	private class BackgroundApp extends Thread {
		boolean stopThread = false;

		public synchronized void stop() {
			stopThread = true;
		}

		public void run() {
			while (!stopThread) {
				try {
					processMessage(getMessage());
				} catch (Exception e) {
				}
			}
		}
	}

	protected void onExit() {
		ba.stop();
	}

	
}
