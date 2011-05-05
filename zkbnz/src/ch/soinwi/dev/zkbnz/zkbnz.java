package ch.soinwi.dev.zkbnz;

import android.app.Activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.os.Bundle;

public class zkbnz extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
   
    private checkSms c = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button sender = (Button)findViewById(R.id.hellobutton);
        
       // button_listener bl = new button_listener();
        
        sender.setOnClickListener(this);
        
        c = new checkSms(this);
        
        if( !c.isValidDay() )
        {
        	sender.setText("Kein gültiger Tag, trotzdem senden?");
        }
        
        
    }

    public checkSms getChecker()
    {
    	return c;
    }
    
    public void onClick(View v)
	{
		
		c = new checkSms(this);
    	Button caller = (Button)v;
			
		if(! c.attemptSend() )
		{
			Toast t = Toast.makeText(v.getContext(), "already valid sms available...", Toast.LENGTH_LONG);
			t.show();
			
			TextView text = (TextView)findViewById(R.id.messagetext);
			text.setText(c.getLastText() );
		}
		
		else
		{
			TextView text = (TextView)findViewById(R.id.messagetext);
			text.setText("" );
			caller.setText("SMS gesendet");
		}
	}

}


