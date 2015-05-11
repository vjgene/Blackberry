package com.ewebcomputing.blackberry;
import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
//import javax.wireless.messaging.Message;
import javax.wireless.messaging.TextMessage;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;

import net.rim.blackberry.api.invoke.Invoke;
import net.rim.blackberry.api.invoke.MessageArguments;
import net.rim.blackberry.api.mail.Folder;
import net.rim.blackberry.api.mail.Session;
import net.rim.blackberry.api.mail.Store;
import net.rim.blackberry.api.mail.event.FolderEvent;
import net.rim.blackberry.api.mail.event.FolderListener;
import net.rim.blackberry.api.options.OptionsManager;
import net.rim.device.api.system.Application;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.system.Backlight;
import net.rim.blackberry.api.mail.*;


import net.rim.device.api.ui.component.Dialog;

public class WakeSMS extends Application	implements /*MessageListener,*/ FolderListener	{
    /**
     * Entry point for application
     * @param args Command line arguments (not used)
     */ 
    public static void main(String[] args){
    	WakeSMS theApp = new WakeSMS();       
    	WakeSMSOptionsProvider provider = new WakeSMSOptionsProvider(theApp);            
    	OptionsManager.registerOptionsProvider(provider);
        theApp.enterEventDispatcher();
    }
    private volatile boolean wakeEnabled = false;
    private volatile String email = "";
    
    public synchronized void enableOrDisableWake()	{
    	wakeEnabled = !wakeEnabled;
    }
    
    public synchronized void setWakeEnabled(boolean e)	{
    	this.wakeEnabled = e;
    }
    
    BackgroundApp ba = null;
    //MessageConnection mSMSConnection = null;
    DatagramConnection mSMSConnection = null;
    public WakeSMS()	{ 
    	WakeSMSProperties props = WakeSMSProperties.fetch();
    	wakeEnabled = props.isEnabled();
    	email = props.getEmail();
    	ba = new BackgroundApp();
    	ba.start();
    	try	{
    	mSMSConnection = (DatagramConnection) Connector.open("sms://:0");
    	//mSMSConnection.setMessageListener(this);   
    	Store store = Session.waitForDefaultSession().getStore();
    	Folder inbox = store.getFolder("Inbox");    	
    	inbox.addFolderListener(this);
    	} catch(Exception e){
    		e.printStackTrace();
    	}    	
    }
    
    public synchronized boolean  isWakeEnabled()	{
    	return wakeEnabled;
    }
    
    public synchronized String  getEmail()	{
    	return email;
    }
    
    public synchronized void setEmail(String lemail)	{
    	this.email = lemail;
    }
    
    public void processMessage(String [] args) {	
    	if(isWakeEnabled())	notifyIncomingMessage(args.length == 0 ? new MessageArguments() : new MessageArguments());
    	if(email != null && email.trim().length() > 0)	{
   		   sendMessage(args[0], args[1]);
   	   }
	}

    //public void notifyIncomingMessage(MessageConnection arg) {		
    public void notifyIncomingMessage(MessageArguments arg) {	
    	try	{   	
     	ApplicationManager.getApplicationManager().unlockSystem();
    	Backlight.enable(true, 30);    	
    	Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, arg);    	
    	}
    	catch(Exception e)	{    		
    		e.printStackTrace();
    	}
    	catch(Error e)	{    		
    		e.printStackTrace();
    	}		
	}    
  //Get the Store from the default mail Session.
    public void sendMessage(String addr, String data)	{
	    /*Store store = Session.getDefaultInstance().getStore();
	    Folder[] folders = store.list(Folder.SENT);
	    Folder sentfolder = folders[0];*/
	    net.rim.blackberry.api.mail.Message msg = new net.rim.blackberry.api.mail.Message(/*sentfolder*/);
	    
	    Address recipients[] = new Address[1];
	    try {
	         recipients[0]= new Address(email, "");
	         msg.addRecipients(Message.RecipientType.TO, recipients);
	         msg.setSubject("SMS from:"+ addr);
	         msg.setContent(data);
	         msg.setPriority(Message.Priority.HIGH);
	         Transport.send(msg);
	     }
	    catch (Throwable me) {
	         System.err.println(me);
	    }
    }
    
    //private MessageArguments getMessage()	{
    private String [] getMessage()	{
    	try	{
    	 Datagram d = mSMSConnection.newDatagram(mSMSConnection.getMaximumLength());
    	 mSMSConnection.receive(d);    	 
         String address = new String(d.getAddress());
         String msg = new String(d.getData());         
         return new String[] {address, msg};
         //return new MessageArguments(MessageArguments.ARG_DEFAULT, address, "", msg);
    	}catch(Throwable e){
    		//return new MessageArguments();
    		return new String [] {};
    	}
    }
	
	 //The thread that will run in the background.
    private class BackgroundApp extends Thread
    {
         boolean stopThread = false;
         public synchronized void stop() 
         { 
              stopThread = true;
         }

         public void run() 
         {
              while (!stopThread) 
              {                    
                   try 
                   {               	   
                	//String [] args = getMessage();
                	//MessageArguments args = getMessage();
                	//if(isWakeEnabled())	{                		   
                		processMessage(getMessage());
                						//new MessageArguments(MessageArguments.ARG_DEFAULT, args[0], "", args[1]));
                	 //}                   
                	   /*String email = getEmail();
                	   if(email != null && email.trim().length() > 0)	{
                  		   sendMessage(args[0], args[1]);
                  	   }  */              	   
                   } 
                   catch (Exception e)
                   {                         
                   } 
              } 
         } 
    } 
    //Stop the thread on exit. 
    protected void onExit() 
    { 
         ba.stop(); 
    }

	public void messagesAdded(FolderEvent e) {		
		ApplicationManager.getApplicationManager().unlockSystem();
    	Backlight.enable(true, 30);
    	Invoke.invokeApplication(Invoke.APP_TYPE_MESSAGES, new MessageArguments());
		
	}

	public void messagesRemoved(FolderEvent e) {
		// TODO Auto-generated method stub
		
	} 
}
