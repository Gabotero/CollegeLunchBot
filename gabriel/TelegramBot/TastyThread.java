package gabriel.TelegramBot;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;

import java.lang.Thread;
import java.lang.Exception;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.StringWriter;
import java.io.PrintWriter;

public class TastyThread implements Runnable {

	private PollingBot bot;
	private int today_hour;
	private int today_minute;
	private int tomorrow_hour;
	private int tomorrow_minute;
	final Calendar now = GregorianCalendar.getInstance();

	public TastyThread(){

		this.bot = null;
		this.today_hour = 12;
		this.today_minute = 0;
		this.tomorrow_hour = 20;
		this.tomorrow_minute = 0;

	}

	public TastyThread(PollingBot bot){

		this.bot = bot;
		this.today_hour = 12;
		this.today_minute = 0;
		this.tomorrow_hour = 20;
		this.tomorrow_minute = 0;

	}

	public TastyThread(PollingBot bot, int today_hour, int today_minute, int tomorrow_hour, int tomorrow_minute){

		this.bot = bot;
		this.today_hour = today_hour;
		this.today_minute = today_minute;
		this.tomorrow_hour = tomorrow_hour;
		this.tomorrow_minute = tomorrow_minute;

	}


	public void run(){

		try {

			while(true){

				Calendar calToday = Calendar.getInstance();
				calToday.set(Calendar.HOUR_OF_DAY, this.today_hour);
				calToday.set(Calendar.MINUTE, this.today_minute);
				calToday.set(Calendar.SECOND, 0);
				calToday.set(Calendar.MILLISECOND, 0);

				Calendar calTomorrow = Calendar.getInstance();
				calTomorrow.set(Calendar.HOUR_OF_DAY, this.tomorrow_hour);
				calTomorrow.set(Calendar.MINUTE, this.tomorrow_minute);
				calTomorrow.set(Calendar.SECOND, 0);
				calTomorrow.set(Calendar.MILLISECOND, 0);

				Date current_hour = new Date();

				long until_today = calToday.getTime().getTime() - current_hour.getTime();
				long until_tomorrow = calTomorrow.getTime().getTime() - current_hour.getTime();

				long toSleep = 0;

				if(until_today > 0 && until_tomorrow > 0)
					toSleep = until_today;//Sleep until today
				else if(until_today < 0 && until_tomorrow < 0)
					toSleep = (24*60*60*1000) + until_today;//Sleep until today
				else
					toSleep = until_tomorrow;//Sleep until tomorrow

				System.out.println(toSleep);
				Thread.sleep(toSleep);

				this.bot.sendTastyInfo(!(until_today < 0 && until_tomorrow > 0)); // True means send info about today

			}

		} catch (InterruptedException e) {
			System.out.println("I wasn't done!");
		} catch (Exception e){
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			String exceptionAsString = sw.toString();
			System.out.println("Exception Polling Thread: " + e.getMessage() + exceptionAsString);
		}
	}


}

