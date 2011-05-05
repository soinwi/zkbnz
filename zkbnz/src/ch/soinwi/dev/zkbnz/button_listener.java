package ch.soinwi.dev.zkbnz;

import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class button_listener implements OnClickListener
{
	private smsSender send = new smsSender();
	
	public void onClick(View v)
	{
		
		Button caller = (Button)v;
		/*	
		checkSms checker = .getChecker();
		if(! checker.attemptSend() )
		{
			Toast t = Toast.makeText(v.getContext(), "already valid sms available...", Toast.LENGTH_LONG);
		}
		//send.send_sms("ZKBNZ","988");
		
		caller.setText("SMS gesendet");*/
	}

}
