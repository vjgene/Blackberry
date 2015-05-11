package com.ewebcomputing.blackberry;
import java.util.*;

import net.rim.device.api.system.EventLogger;


class UUID	{
	public static long fromString(String uuid)	{			 
	           if (uuid == null) {
	               throw new NullPointerException();
	           }
	   
	           int[] position = new int[5];
	           int lastPosition = 1;
	           int startPosition = 0;
	   
	           int i = 0;
	           for (; i < position.length && lastPosition > 0; i++) {
	               position[i] = uuid.indexOf("-", startPosition); //$NON-NLS-1$
	               lastPosition = position[i];
	               startPosition = position[i] + 1;
	           }
	   
	           // should have and only can have four "-" in UUID
	           if (i != position.length || lastPosition != -1) {
	               //throw new IllegalArgumentException(Messages.getString("luni.47") + uuid); //$NON-NLS-1$
	           }
	   
	           long m1 = Long.parseLong(uuid.substring(0, position[0]), 16);
	           long m2 = Long.parseLong(uuid.substring(position[0] + 1, position[1]),
	                   16);
	           long m3 = Long.parseLong(uuid.substring(position[1] + 1, position[2]),
	                   16);
	   
	           long lsb1 = Long.parseLong(
	                   uuid.substring(position[2] + 1, position[3]), 16);
	           long lsb2 = Long.parseLong(uuid.substring(position[3] + 1), 16);
	   
	           long msb = (m1 << 32) | (m2 << 16) | m3;
	           long lsb = (lsb1 << 48) | lsb2;
	   
	           return  (lsb & 0x0000FFFFFFFFFFFFL);

	       }
	   


	}


public class Logger {	
	
	
	
	static long guid = UUID.fromString("Balive");
	static Logger inst = new Logger();
	
	private Logger()	{
		EventLogger.register(guid, "Balive", EventLogger.VIEWER_STRING);
	}
	
	public static Logger getInstance()	{
		return inst;
	}
	
	public void log(String str)	{
		EventLogger.logEvent(guid,str.getBytes(), EventLogger.WARNING);
	}
	
	public void err(String str)	{
		EventLogger.logEvent(guid,str.getBytes(), EventLogger.ERROR);
	}
}
