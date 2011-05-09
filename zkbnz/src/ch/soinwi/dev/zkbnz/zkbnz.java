package ch.soinwi.dev.zkbnz;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;

import android.text.Layout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.*;
import android.os.Bundle;

public class zkbnz extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
   
    private checkSms c = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button sender = (Button)findViewById(R.id.hellobutton);		//enable events on button        
        sender.setOnClickListener(this);
        
        c = new checkSms(this);				//sms checker instance to privide functionality like daychecking
        
        if( !c.isValidDay() )				//if it's not a valid day (friday, saturday, sunday morning)
        {
        	sender.setText("Kein g�ltiger Tag, trotzdem senden?");		//Warn user
        }
        
        if(c.oldSmsValid() )				//if there already is a valid message int the inbox
        {
        	TextView text = (TextView)findViewById(R.id.messagetext);		//show it
        	text.setText(c.getLastText() );
        	
        	sender.setEnabled(false);										//and disable button
        }
        
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.ad);
                
        AdView adv = new AdView(this, AdSize.BANNER, "a14dc55acae2cdd");
        //adv = new AdView(this, AdSize.BANNER, "a14dc55acae2cdd");
        layout.addView(adv);
        AdRequest adreq = new AdRequest();
        adreq.setTesting(true);
        adv.loadAd(adreq);
    }
    
    @Override
    public void onPause()		//when app gets out of focus
    {
    	super.onPause();
    	
    	this.finish();			//quit it to ensure that each time it is displayed it refetches the messages
    	
    }

    public checkSms getChecker()
    {
    	return c;
    }
    
    public void onClick(View v)
	{
		
		c = new checkSms(this);			//sms checker instance
    	Button caller = (Button)v;
			
		if(! c.attemptSend() )			//if sending-attempt returns false, that means ther was no sms sent because there is already an existing ticket
		{
			Toast t = Toast.makeText(v.getContext(), "Es ist bereits ein g�ltiges Ticket vorhanden", Toast.LENGTH_LONG);		//inform user about that
			t.show();
			
			TextView text = (TextView)findViewById(R.id.messagetext);
			text.setText(c.getLastText() );
		}
		
		else
		{
			TextView text = (TextView)findViewById(R.id.messagetext);		//otherwise change button text
			text.setText("" );
			caller.setText("SMS gesendet");
			
			this.finish();							//and finish activity
		}
	}

}


