package gabriel.TelegramBot;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.lang.Thread;
import java.lang.Exception;

import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.StringWriter;
import java.io.PrintWriter;
public class PollingThread implements Runnable {

	private String pollingURL;
	private PollingBot bot;
	private int pollingRate;
	private int offset;
	private int limit;
	private int timeout;

	public PollingThread(){
		this.pollingURL = "";
		this.bot = null;
		this.pollingRate = 3;
		this.offset = 0;
		this.limit = 0;
		this.timeout = 0;

	}

	public PollingThread(String url, PollingBot bot, int initOffset, int initLimit, int initTimeout, int pollrate){

		this.pollingURL = url;
		this.bot = bot;
		this.pollingRate = pollrate;
		this.offset = initOffset;
		this.limit = initLimit;
		this.timeout = initTimeout;

	}


	public void run(){

		try {

			while(true){

				Update[] updates = null;
				String temp;
				String inputLine = "";
				JSONObject result;
				JSONArray updatesArray;
				JSONObject object;
				JSONObject element;

				// Create URL
				URL updateUrl = new URL(this.pollingURL + "/getUpdates?" + (this.offset <= 0 ? "" : "offset=" + this.offset) + (this.limit <= 0 ? "" : "&limit=" + this.limit) + (this.timeout <= 0 ? "" : "timeout=" + this.timeout));
				BufferedReader in = new BufferedReader(new InputStreamReader(updateUrl.openStream()));

				// create the results string
				while ((temp = in.readLine()) != null) {
					inputLine += temp;
				}

				in.close();
				if (inputLine.compareTo("") == 0) {
					throw new Exception("No updates");
				}
				result = new JSONObject(inputLine);

				// if the "ok" parameter in JSON results is set to false, it throws an
				// exception
				if (!result.getBoolean("ok")) {
					throw new Exception("Not ok update");
				}

				updatesArray = result.getJSONArray("result");

				updates = new Update[updatesArray.length()];

				System.out.println(updatesArray);


				for (int i = 0; i < updates.length; i++) {

					Message msg = null;
					InlineQuery inlQuery = null;
					Chosen_inline_result inl_res = null;
					element = updatesArray.getJSONObject(i);
					CallbackQuery callbackQuery = null;

					try {
						object = element.getJSONObject("channel_post");
						msg = new Message(object);
					} catch (JSONException e) {
						System.out.println("JSON Exception. Message");
					}

					try {
						object = element.getJSONObject("message");
						msg = new Message(object);
					} catch (JSONException e) {
						System.out.println("JSON Exception. Message");
					}

					try {

						object = element.getJSONObject("inline_query");
						inlQuery = new InlineQuery(object);

					} catch (JSONException e) {
						System.out.println("JSON Exception. Inline query");
					}

					try {
						object = element.getJSONObject("chosen_inline_result");
						inl_res = new Chosen_inline_result(object);
					} catch (JSONException e) {
						System.out.println("JSON Exception. Inline Result");
					}

					try {
						object = element.getJSONObject("callback_query");
						callbackQuery = new CallbackQuery(object);
					} catch (JSONException e) {
						System.out.println("JSON Exception. Callback query");
					}
					updates[i] = new Update((int) element.get("update_id"), msg, inlQuery, inl_res, callbackQuery);

				}

				if(updates.length > 0){

					this.offset = updates[updates.length-1].getId() + 1;

					this.bot.receiveUpdate(updates);

				}

				Thread.sleep(this.pollingRate);

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

