package com.ewebcomputing.blackberry;

import net.rim.device.api.system.*;
import net.rim.device.api.util.*;

class WakeSMSProperties implements Persistable {

	private boolean wakeEnabled;
	private String email;

	// 16bit Hash of com.ewebcomputing.blackberry.BaliveProperties
	private static final long PERSISTENCE_ID = 0xaca4d70e;

	// Persistent object wrapping the effective properties instance
	private static PersistentObject store;

	// Ensure that an effective properties set exists on startup.
	static {
		store = PersistentStore.getPersistentObject(PERSISTENCE_ID);
		synchronized (store) {
			if (store.getContents() == null) {
				store.setContents(new WakeSMSProperties());
				store.commit();
			}
		}
	}

	// Constructs a properties set with default values.
	private WakeSMSProperties() {
		wakeEnabled = false;
	}

	// Retrieves a copy of the effective properties set from storage.
	public static WakeSMSProperties fetch() {
		synchronized (store) {
			WakeSMSProperties savedProps = (WakeSMSProperties) store
					.getContents();
			return new WakeSMSProperties(savedProps);
		}
	}

	// Causes the values within this instance to become the effective
	// properties for the application by saving this instance to the store.
	public void save() {
		synchronized (store) {
			store.setContents(this);
			store.commit();
		}
	}

	// The greeting is the text that is displayed within the
	// OptionsSample application.

	// If emphasized, the greeting is displayed in more resouding manner.
	public boolean isEnabled() {
		return wakeEnabled;
	}

	public String getEmail() {
		return email;
	}
	
	public void setEmail(String lemail) {
		 email = lemail;
	}

	public void setWakeEnabled(boolean wakeEnabledLocal) {
		wakeEnabled = wakeEnabledLocal;
	}

	private WakeSMSProperties(WakeSMSProperties other) {
		wakeEnabled = other.wakeEnabled;
		email = other.email;
	}
}
