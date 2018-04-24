package gabriel.TelegramBot;


import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.File;

import java.lang.Exception;

import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONObject;
import org.json.JSONException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public abstract class BaseBot {

	private String url;
	private String token;

	public BaseBot(){

		this.url = "";
		this.token = "";

	}


	public BaseBot (String token){

		this.token = token;
		this.url = "https://api.telegram.org/bot" + token;

	}

	abstract boolean initialiseUpdates();

	public String getToken(){

		return this.token;

	}

	public String getUrl(){

		return this.url;

	}

	public Message sendMessage(String chatId, String text) throws IOException, JSONException{


		URL methodURL = new URL(this.url + "/sendMessage?chat_id=" + chatId + "&text=" + URLEncoder.encode(text, "utf-8"));

		String temp, response = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(methodURL.openStream()));

		// Wait for result
		while ((temp = in.readLine()) != null) {
			response += temp;
		}

		in.close();

		JSONObject resp = new JSONObject(response);
		if (resp.getBoolean("ok")) {
			System.out.println(resp);
			return new Message(resp.getJSONObject("result"));
		} else {
			return null;
		}

	}

	public Message sendMessage(String chat_id, String text, String parse_mode, boolean disable_web_page_preview, boolean disable_notification, int reply_to_message_id, ReplyMarkup reply_markup) throws IOException, JSONException {
		JSONObject respObject;
		URL updateUrl = new URL(url + "/sendMessage?chat_id=" + chat_id + "&text=" + URLEncoder.encode(text, "utf-8") + (parse_mode != null ? "&parse_mode=" + parse_mode : "")
				+ (disable_web_page_preview != false ? "&disable_web_page_preview=" + Boolean.toString(disable_web_page_preview) : "")
				+ (disable_notification != false ? "&disable_notification=" + Boolean.toString(disable_notification) : "")
				+ (reply_to_message_id > 0 ? "&reply_to_message_id=" + reply_to_message_id : "") + (reply_markup != null ? "&reply_markup=" + URLEncoder.encode(reply_markup.toJSONString(), "utf-8") : ""));
		String temp, response = "";

		BufferedReader in = new BufferedReader(new InputStreamReader(updateUrl.openStream()));
		// create the results string
		while ((temp = in.readLine()) != null) {
			response += temp;
		}

		in.close();

		respObject = new JSONObject(response);
		if (respObject.getBoolean("ok")) {
			return new Message(respObject.getJSONObject("result"));
		} else {
			return null;
		}
	}

	/**
	 * Sends a file.
	 * 
	 * @param chat_id
	 *            Recipient's id or channel's id
	 * @param file
	 *            the file to send
	 * @param type
	 *            the file type, allowed types:
	 *            <ul>
	 *            <li>photo</li>
	 *            <li>document</li>
	 *            <li>audio</li>
	 *            <li>video</li>
	 *            <li>sticker</li>
	 *            <li>voice</li>
	 *            </ul>
	 * @throws IOException
	 * 
	 */
	public String sendFile(String chat_id, File file, String type) throws IOException {

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("chat_id", new StringBody(chat_id, ContentType.DEFAULT_TEXT));
		builder.addPart(type, new FileBody(file, ContentType.DEFAULT_BINARY));
		HttpEntity entity = builder.build();
		HttpPost post = new HttpPost(url + "/send" + type);

		post.setEntity(entity);

		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpResponse response = httpclient.execute(post);

		return response.toString();

	}

	/**
	 * Sends a file.
	 * 
	 * @param chat_id
	 *            Recipient's id or channel's id
	 * @param file_id
	 *            file's id to send
	 * @param type
	 *            the file type, allowed types:
	 *            <ul>
	 *            <li>photo</li>
	 *            <li>document</li>
	 *            <li>audio</li>
	 *            <li>video</li>
	 *            <li>sticker</li>
	 *            <li>voice</li>
	 *            </ul>
	 * @throws IOException
	 *             thrown when there is an exception executing the request
	 */
	public String sendFile(String chat_id, String file_id, String type) throws IOException {
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("chat_id", new StringBody(chat_id, ContentType.DEFAULT_TEXT));
		builder.addPart(type, new StringBody(file_id, ContentType.DEFAULT_TEXT));
		HttpEntity entity = builder.build();
		HttpPost post = new HttpPost(url + "/send" + type);

		post.setEntity(entity);

		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpResponse response = httpclient.execute(post);

		return response.toString();

	}

	private String sendFile(String chat_id, File file, String type, String caption, boolean disable_notification,
			int reply_to_message_id, ReplyMarkup reply_markup, int duration, String performer, String title, int width,
			int height) throws IOException {

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("chat_id", new StringBody(chat_id, ContentType.DEFAULT_TEXT));
		if (caption != null)
			builder.addPart("caption", new StringBody(caption, ContentType.DEFAULT_TEXT));
		if (disable_notification != false)
			builder.addPart("disable_notification", new StringBody(Boolean.toString(disable_notification), ContentType.DEFAULT_TEXT));
		if (reply_to_message_id > 0)
			builder.addPart("reply_to_message_id", new StringBody(Integer.toString(reply_to_message_id), ContentType.DEFAULT_TEXT));
		if (reply_markup != null)
			builder.addPart("reply_markup", new StringBody(URLEncoder.encode(reply_markup.toJSONString(), "utf-8"), ContentType.DEFAULT_TEXT));
		if (duration > 0)
			builder.addPart("duration", new StringBody(Integer.toString(duration), ContentType.DEFAULT_TEXT));
		if (performer != null)
			builder.addPart("performer", new StringBody(performer, ContentType.DEFAULT_TEXT));
		if (title != null)
			builder.addPart("title", new StringBody(title, ContentType.DEFAULT_TEXT));
		if (width > 0)
			builder.addPart("width", new StringBody(Integer.toString(width), ContentType.DEFAULT_TEXT));
		if (height > 0)
			builder.addPart("height", new StringBody(Integer.toString(height), ContentType.DEFAULT_TEXT));
		builder.addPart(type, new FileBody(file, ContentType.DEFAULT_BINARY));
		HttpEntity entity = builder.build();
		HttpPost post = new HttpPost(url + "/send" + type);

		post.setEntity(entity);

		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpResponse response = httpclient.execute(post);

		return response.toString();

	}

	private String sendFile(String chat_id, String file_id, String type, String caption, boolean disable_notification,
			int reply_to_message_id, ReplyMarkup reply_markup, int duration, String performer, String title, int width,
			int height) throws IOException {

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
		builder.addPart("chat_id", new StringBody(chat_id, ContentType.DEFAULT_TEXT));
		if (caption != null)
			builder.addPart("caption", new StringBody(caption, ContentType.DEFAULT_TEXT));
		if (disable_notification != false)
			builder.addPart("disable_notification", new StringBody(Boolean.toString(disable_notification), ContentType.DEFAULT_TEXT));
		if (reply_to_message_id > 0)
			builder.addPart("reply_to_message_id",
					new StringBody(Integer.toString(reply_to_message_id), ContentType.DEFAULT_TEXT));
		if (reply_markup != null)
			builder.addPart("reply_markup", new StringBody(URLEncoder.encode(reply_markup.toJSONString(), "utf-8"), ContentType.DEFAULT_TEXT));
		if (duration > 0)
			builder.addPart("duration", new StringBody(Integer.toString(duration), ContentType.DEFAULT_TEXT));
		if (performer != null)
			builder.addPart("performer", new StringBody(performer, ContentType.DEFAULT_TEXT));
		if (title != null)
			builder.addPart("title", new StringBody(title, ContentType.DEFAULT_TEXT));
		if (width > 0)
			builder.addPart("width", new StringBody(Integer.toString(width), ContentType.DEFAULT_TEXT));
		if (height > 0)
			builder.addPart("height", new StringBody(Integer.toString(height), ContentType.DEFAULT_TEXT));
		builder.addPart(type, new StringBody(file_id, ContentType.DEFAULT_TEXT));
		HttpEntity entity = builder.build();
		HttpPost post = new HttpPost(url + "/send" + type);

		post.setEntity(entity);

		CloseableHttpClient httpclient = HttpClients.createMinimal();
		HttpResponse response = httpclient.execute(post);

		return response.toString();

}

	public String sendPhoto(String chat_id, File file, String caption, boolean disable_notification,
			int reply_to_message_id, ReplyMarkup reply_markup) throws IOException {
		return sendFile(chat_id, file, "photo", caption, disable_notification, reply_to_message_id, reply_markup, -1, null, null, -1, -1);
	}

}
