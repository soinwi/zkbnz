package ch.soinwi.dev.zkbnz;

import android.telephony.SmsManager;

public class smsSender {
	
	public void send_sms(String content, String phone_nr)		//sends sms with given content/receiver
	{
		SmsManager smsman = SmsManager.getDefault();
		
		smsman.sendTextMessage(phone_nr, null, content, null, null);


	}

}
