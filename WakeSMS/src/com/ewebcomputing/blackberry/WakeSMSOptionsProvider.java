package com.ewebcomputing.blackberry;
import net.rim.blackberry.api.options.OptionsProvider;
import net.rim.device.api.system.ApplicationManager;
import net.rim.device.api.ui.component.BasicEditField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.ObjectChoiceField;
import net.rim.device.api.ui.container.MainScreen;

class WakeSMSOptionsProvider implements OptionsProvider {

	private ObjectChoiceField styleChoice;
	private EditField email;
	private WakeSMSProperties optionProperties;
	private WakeSMS app = null;

	public WakeSMSOptionsProvider(WakeSMS theApp) {
		app = theApp;
	}

	public String getTitle() {
		return "WakeSMS";
	}

	public void populateMainScreen(MainScreen mainScreen) {
		optionProperties = WakeSMSProperties.fetch();
		Object[] choices = { "On", "Off!!" };
		int selection = optionProperties.isEnabled() ? 0 : 1;
		styleChoice = new ObjectChoiceField("Wake On SMS: ", choices, selection);
		mainScreen.add(styleChoice);
		email = new EditField("Forward Email: ", optionProperties.getEmail(),
				BasicEditField.DEFAULT_MAXCHARS, BasicEditField.NO_NEWLINE);
		mainScreen.add(email);
	}

	public void save() {
		optionProperties.setWakeEnabled(styleChoice.getSelectedIndex() != 1);
		optionProperties.setEmail(email.getText());
		optionProperties.save();
		app.setWakeEnabled(styleChoice.getSelectedIndex() != 1);
		app.setEmail(email.getText());
		styleChoice = null;
		optionProperties = null;
	}
}