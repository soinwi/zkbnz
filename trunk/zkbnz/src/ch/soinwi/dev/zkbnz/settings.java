package ch.soinwi.dev.zkbnz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import android.preference.Preference.OnPreferenceChangeListener;
import android.widget.Toast;




public class settings extends PreferenceActivity {

	
	static final int ZVV_WARNING_DIAG = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		ListPreference listPref = (ListPreference)findPreference("choose");
		
		SharedPreferences settings = getSharedPreferences("zkbnz_settings",0); 
		String value = settings.getString("provider", "zkbnz");
		//Toast.makeText(getBaseContext(), value, Toast.LENGTH_LONG).show();
		listPref.setValue(value);
		
		listPref.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// TODO Auto-generated method stub
				
				SharedPreferences settings = getSharedPreferences("zkbnz_settings",0);
				SharedPreferences.Editor editor = settings.edit();
				
				editor.putString("provider", (String)newValue );
				editor.commit();
				
				((ListPreference)preference).setValue((String)newValue);
				
				//Toast t = Toast.makeText(getBaseContext(), (String)newValue, Toast.LENGTH_LONG);
				//t.show();
				
				if(newValue.equals("zvvnz") )
				{
					showDialog(ZVV_WARNING_DIAG);
					
				}
				
				return true;
			} } );
		
	}
	
	@Override
    public void onPause()		//when app gets out of focus
    {
    	super.onPause();
    	
    	this.finish();			//quit it to ensure that each time it is displayed it refetches the messages
    	
    	
    }
	
	protected Dialog onCreateDialog(int id)
	{
		Dialog diag=null;
		switch(id)
		{
		case ZVV_WARNING_DIAG:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Wichtig. Bitte lesen")
			.setMessage("Warnung. Mit dieser Einstellung wird der Dienst genutzt den ZVV Nachtzuschlag zum Regulären Preis via SMS zu lösen. Die Kosten werden ihrer Handyrechnung belastet")
			.setCancelable(false)
			.setPositiveButton("Bestätigen", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {

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
