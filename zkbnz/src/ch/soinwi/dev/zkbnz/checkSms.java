package ch.soinwi.dev.zkbnz;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.format.Time;
import android.widget.Toast;
import android.app.Activity;
import android.app.AlertDialog;

import java.util.regex.*;
import java.util.Vector;


public class checkSms {
	 
	private Date lastSmsDate;										//Deprecated
	private String lastSmsBody = null;								//contains last sms's body from given number
	private Calendar lastValidUntil = Calendar.getInstance();		//calendar containt valid until date from last sms if available
	private Activity a;												//activity to set as parent
	private String msgText, phoneNr;
	
	
	public checkSms(Activity a, String msgText, String phoneNr)
	{
		this.a = a;		//set a to activity
		this.msgText = msgText;
		this.phoneNr = phoneNr;
		
		
		//Query the content provider for Sms from 988, get date and body
		loadContent();
	}

	
	public void loadContent()
	{
		Uri inboxSmsUri = Uri.parse("content://sms/inbox");
		String[] columns = {"body","date","read","_id"};
		Cursor inboxCursor = a.getContentResolver().query(inboxSmsUri, columns, "address = '"+ phoneNr + "'",null, "date DESC");
		
		if(inboxCursor.moveToFirst() ) //get first element (the newest one)
		{
			
			int dateCol = inboxCursor.getColumnIndex("date");
			int bodyCol = inboxCursor.getColumnIndex("body");
			int readCol = inboxCursor.getColumnIndex("read");
			int idCol = inboxCursor.getColumnIndex("_id");

			String smsBody = inboxCursor.getString(bodyCol);
			long longDate	= inboxCursor.getLong(dateCol);
			int read = inboxCursor.getInt(readCol);
			int id = inboxCursor.getInt(idCol);
			
			if(read==0)			//set sms to read
			{
				ContentValues values = new ContentValues();
				values.put("read", "1");
				
				//a.getContentResolver().insert(Uri.withAppendedPath(inboxSmsUri, Integer.toString(id) ),values);
				int numbers = a.getContentResolver().update(Uri.withAppendedPath(inboxSmsUri, Integer.toString(id) ), values, null, null);
				//Toast.makeText(a, Integer.toString(numbers), Toast.LENGTH_LONG).show();
				//Toast.makeText(a, Integer.toString(id), Toast.LENGTH_LONG).show();
			}
			
			
			
			lastSmsDate = new Date(longDate);
			lastSmsBody = new String(smsBody);

			
		}
	}
	
	public boolean attemptSend(String msgText, String phoneNr)		//check if should send sms
	{
		
		
		if(! oldSmsValid())				//if no other, older sms exists
		{
			
			smsSender s = new smsSender();
			//s.send_sms("ZKBNZ","988");
			s.send_sms(msgText, phoneNr);
			
			return true;
		}
		
		return false;
	}
	
	public boolean isValidDay()	//returns if today is friday (http://www.youtube.com/watch?v=CD2LRROpph0), saturday or sunday
	{
		Calendar rightNow = Calendar.getInstance();
		
		int day = rightNow.get( Calendar.DAY_OF_WEEK);
		int hour = rightNow.get( Calendar.HOUR_OF_DAY);
		
		if(day==Calendar.FRIDAY || day==Calendar.SATURDAY || (day==Calendar.SUNDAY && hour<6) )
		{
			return true;
		}
		else
		{
			return false;
		}
		
		
	}
	
	public	 boolean oldSmsValid()	//checks if an older sms is still valid
	{
		Pattern matcherPattern = Pattern.compile(".*Bis.*(\\d\\d).(\\d\\d).(\\d\\d)\\s(\\d\\d):(\\d\\d)");	//regex pattern
		if(lastSmsBody==null)	//if there is no older sms
		{
			return false;
		}
		Matcher match = matcherPattern.matcher(lastSmsBody);	//match regex
		Vector<String> date_matches = new Vector<String>();		//contains match results
		
		if( match.find() )		//if there is a match result (happens, if the last sms is a ticket. yet has to be determined wheter it is still valid)
		{
		
			int i=1;		//start at element 1, 0 contains whole match string
			while(i<=5)		//get first 5 elements
			{
				date_matches.add(match.group(i));		//insert to vector
				i++;
				//i=2;
			}

																				//get logical values until when the ticket is valid
			int day = Integer.parseInt(date_matches.elementAt(0) );				//
			int month = Integer.parseInt(date_matches.elementAt(1) ) - 1;		//months in java calendar start with 0, not 1. so substract one...
			int year = Integer.parseInt(date_matches.elementAt(2) ) + 100;		//0 as year means 1900, so add 100 (provides 2 digit year
			int hour = Integer.parseInt(date_matches.elementAt(3) );			//hour
			int min = Integer.parseInt(date_matches.elementAt(4) );				//min


			lastValidUntil.setTime( new Date(year, month, day, hour, min	) );	//define time
			Calendar dateNow = Calendar.getInstance();								//get current time

			//String group0 = null;

			/*long neues = dateNow.get(Calendar.DAY_OF_MONTH);
			long neuesmonth = dateNow.get(Calendar.MONTH);
			long neuesyear = dateNow.get(Calendar.YEAR);

			long altes = lastValidUntil.get(Calendar.DAY_OF_MONTH);
			long altesmonth = lastValidUntil.get(Calendar.MONTH);
			long altesyear = lastValidUntil.get(Calendar.YEAR);*/


			if( ((int)dateNow.getTime().compareTo(lastValidUntil.getTime())) >= 0 ) //if current date/time is bigger (later) than the expired-date of the ticket, return false
			{
				return false;					//no valid ticket available
				//group0 = "ungültig"; 

			}
			else
			{
				return true;					//valid ticket available
				//group0 = "gültig";
			}
		}
		return false;
		
		
		//String dateString = java.text.DateFormat.getInstance().format(longDate);
		/*
		group0 += "   ";
		group0 += Long.toString(altes);
		group0 += Long.toString(altesmonth);
		group0 += Long.toString(altesyear);
		group0 += " ";
		group0 += Long.toString( lastValidUntil.getTimeInMillis() );
		group0 += "  ";
		group0 += Long.toString(neues);
		group0 += Long.toString(neuesmonth);
		group0 += Long.toString(neuesyear);
		group0 += " ";
		group0 += Long.toString( dateNow.getTimeInMillis() );
		
		Toast t = Toast.makeText(a, group0, Toast.LENGTH_LONG);
		t.show();*/
	}
	
	public String getLastText()		//return the text of the last sms...
	{
		return lastSmsBody;
	}

		
}
