package ch.soinwi.dev.zkbnz;

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
	 
	private Date lastSmsDate;
	private String lastSmsBody = null;
	private Calendar lastValidUntil = Calendar.getInstance();
	private Activity a;
	
	
	public checkSms(Activity a)
	{
		this.a = a;
		
		Uri inboxSmsUri = Uri.parse("content://sms/inbox");
		String[] columns = {"body","date"};
		Cursor inboxCursor = a.getContentResolver().query(inboxSmsUri, columns, "address = '988'",null, "date DESC");
		
		if(inboxCursor.moveToFirst() )
		{
			
			int dateCol = inboxCursor.getColumnIndex("date");
			int bodyCol = inboxCursor.getColumnIndex("body");
			

			String smsBody = inboxCursor.getString(bodyCol);
			long longDate	= inboxCursor.getLong(dateCol);
			
			lastSmsDate = new Date(longDate);
			lastSmsBody = new String(smsBody);
			
			
		}
	}
	
	public boolean attemptSend()
	{
		
		
		if(! oldSmsValid())
		{
			Toast t = Toast.makeText(a, "send sms", Toast.LENGTH_LONG);
			t.show();
			
			smsSender s = new smsSender();
			s.send_sms("ZKBNZ","988");
			
			return true;
		}
		
		return false;
	}
	
	public boolean isValidDay()
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
	
	private boolean oldSmsValid()
	{
		Pattern matcherPattern = Pattern.compile(".*Bis.*(\\d\\d).(\\d\\d).(\\d\\d)\\s(\\d\\d):(\\d\\d)");
		if(lastSmsBody==null)
		{
			return false;
		}
		Matcher match = matcherPattern.matcher(lastSmsBody);
		Vector<String> date_matches = new Vector<String>();
		
		if( match.find() )
		{
		
			int i=1;
			while(i<=5)
			{
				date_matches.add(match.group(i));
				i++;
				//i=2;
			}


			int day = Integer.parseInt(date_matches.elementAt(0) );
			int month = Integer.parseInt(date_matches.elementAt(1) ) - 1;
			int year = Integer.parseInt(date_matches.elementAt(2) ) + 100;
			int hour = Integer.parseInt(date_matches.elementAt(3) );
			int min = Integer.parseInt(date_matches.elementAt(4) );


			lastValidUntil.setTime( new Date(year, month, day, hour, min	) );
			Calendar dateNow = Calendar.getInstance();

			//String group0 = null;

			long neues = dateNow.get(Calendar.DAY_OF_MONTH);
			long neuesmonth = dateNow.get(Calendar.MONTH);
			long neuesyear = dateNow.get(Calendar.YEAR);

			long altes = lastValidUntil.get(Calendar.DAY_OF_MONTH);
			long altesmonth = lastValidUntil.get(Calendar.MONTH);
			long altesyear = lastValidUntil.get(Calendar.YEAR);


			if( ((int)dateNow.getTime().compareTo(lastValidUntil.getTime())) >= 0 )
			{
				return false;
				//group0 = "ungültig";

			}
			else
			{
				return true;
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
	
	public String getLastText()
	{
		return lastSmsBody;
	}

		
}
