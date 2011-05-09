package ch.soinwi.dev.zkbnz;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;

import android.text.Layout;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.*;
import android.os.Bundle;




public class zkbnz extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
   
	static final int FIRST_START_DIAG = 0;
    private checkSms c = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        checkFirstStart();
        
        Button sender = (Button)findViewById(R.id.hellobutton);		//enable events on button        
        sender.setOnClickListener(this);
        
        c = new checkSms(this);				//sms checker instance to privide functionality like daychecking
        
        if( !c.isValidDay() )				//if it's not a valid day (friday, saturday, sunday morning)
        {
        	sender.setText("Kein gültiger Tag, trotzdem senden?");		//Warn user
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
			Toast t = Toast.makeText(v.getContext(), "Es ist bereits ein gültiges Ticket vorhanden", Toast.LENGTH_LONG);		//inform user about that
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
    
    private void checkFirstStart()
    {
    	SharedPreferences settings = getPreferences(0);
    	boolean hasBeenStarted = settings.getBoolean("hasBeenStarted", false);
    	
    	if(!hasBeenStarted)
    	{
    		showDialog(FIRST_START_DIAG);
    	}
  
    }
    
    private void setHasBeenStarted()
    {
    	SharedPreferences settings = getPreferences(0);
    	SharedPreferences.Editor editor = settings.edit();
    	
    	editor.putBoolean("hasBeenStarted", true);
    	
    	editor.commit();
    }

    
    @Override
    protected Dialog onCreateDialog(int id)
    {
    	Dialog diag=null;
    	switch(id)
    	{
    	case FIRST_START_DIAG:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setTitle("Wichtig. Bitte lesen")
    			.setMessage("Achtung: Bei Klick auf den Bestellen-Button wird eine SMS gemäss den einstellungen versendet! Dabei fallen die Kosten der beteiligten Dienstleister an! Der Ersteller dieser App übernimmt keinerlei Haftung für entstandene Kosten. \n\nMit einem Klick auf den Bestätigen-Button bestätige ich, dass ich diesen Hinweis gelesen und Verstanden habe! \n\nWeitere informationen finden sie im wiki unter http://code.google.com/p/zkbnz/")
    			.setCancelable(false)
    			.setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
    				public void onClick(DialogInterface dialog, int id) {
    					
    					setHasBeenStarted();
    					
    					dialog.dismiss();
    	           }
    			});
    		AlertDialog alert = builder.create();
    		diag = (Dialog)alert;
    		break;
    		
    	}
    	return diag;
    
    }
    

}


