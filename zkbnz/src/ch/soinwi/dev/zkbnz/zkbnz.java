package ch.soinwi.dev.zkbnz;

import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.*;
import android.os.Bundle;




public class zkbnz extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
   
	static final int FIRST_START_DIAG = 0;
	static final int ABOUT_DIAG = 1;
    private checkSms c = null;//new checkSms(this);
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //SharedPreferences settings = getSharedPreferences("zkbnz_settings",0); 

        c = new checkSms(this);
        checkFirstStart();
        
        Button sender = (Button)findViewById(R.id.hellobutton);		//enable events on button        
        sender.setOnClickListener(this);
        
        //initCheck();
                
        
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.ad);
                
        AdView adv = new AdView(this, AdSize.BANNER, "a14dc55acae2cdd");
        //adv = new AdView(this, AdSize.BANNER, "a14dc55acae2cdd");
        layout.addView(adv);
        AdRequest adreq = new AdRequest();
        adreq.setTesting(true);
        adv.loadAd(adreq);
    }
    
    private void initCheck()
    {
    	//c = new checkSms(this);
    	c.loadContent();
    	Button sender = (Button)findViewById(R.id.hellobutton);
        
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
    	
    }
   
    @Override
    public void onPause()		//when app gets out of focus
    {
    	super.onPause();
    	
    	//this.finish();			//quit it to ensure that each time it is displayed it refetches the messages
    	
    }
    
    @Override
    public void onResume()
    {
    	super.onResume();
    	initCheck();
    	
    }
    
    public checkSms getChecker()
    {
    	return c;
    }
    
    public void onClick(View v)
	{
		
		//c = new checkSms(this);			//sms checker instance
    	c.loadContent();
		Button caller = (Button)v;
		
		SharedPreferences settings = getSharedPreferences("zkbnz_settings",0); 
		String message_text = settings.getString("provider", "zkbnz");
			
		if(! c.attemptSend(message_text, "988") )			//if sending-attempt returns false, that means ther was no sms sent because there is already an existing ticket
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
    			.setMessage("Achtung: Bei Klick auf den Bestellen-Button wird eine SMS gemäss den einstellungen versendet! Dabei fallen die Kosten der beteiligten Dienstleister an! Der Ersteller dieser App übernimmt keinerlei Haftung für entstandene Kosten. \n\nWichtig: Der Entwickler dieses Apps steht in keinerlei Zusammenhang mit der ZKB oder dem ZVV. Dies ist kein offizielles App einer dieser Firmen! \n\nMit einem Klick auf den Bestätigen-Button bestätige ich, dass ich diesen Hinweis gelesen und Verstanden habe! \n\nWeitere informationen finden sie im wiki unter http://code.google.com/p/zkbnz/")
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
    		
    	case ABOUT_DIAG:
    		AlertDialog.Builder aboutB = new AlertDialog.Builder(this);
    		aboutB.setTitle("Über dieses App")
    		.setMessage("Programmiert von David Sommer, soinwi \ndavid.sommer@gmx.ch info@soinwi.ch \n\nHomepage: www.soinwi.ch")
    		.setCancelable(false)
    		.setPositiveButton("OK",new DialogInterface.OnClickListener(){
    			public void onClick(DialogInterface dialog, int id){
    				dialog.dismiss();
    			}
    		});
    		AlertDialog about = aboutB.create();
    		diag = (Dialog)about;
    		break;
    		
    	}
    	return diag;
    
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.bottommenu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch(item.getItemId())
    	{
    	case R.id.about:
    		
    		showDialog(ABOUT_DIAG);
    		
    		return true;
    	case R.id.settings:
    		
    		Intent i = new Intent(zkbnz.this, settings.class);
    		startActivity(i);
    		
    		return true;
    		
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    	
    	
    }
}


