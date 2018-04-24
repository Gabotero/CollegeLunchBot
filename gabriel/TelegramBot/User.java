package gabriel.TelegramBot;

import org.json.JSONException;
import org.json.JSONObject;

public class User {

	private int id;
	private String first_name;
	private String last_name;
	private String username;
	private boolean isBot;

	public User(int id) {
		super();
		this.id = id;
	}

	public User(int id, String first_name, String last_name, String username, boolean isBot) {
		super();
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.username = username;
		this.isBot = isBot;
	}

	public User(JSONObject object) {
		try {
			this.id = object.getInt("id");
			this.first_name = object.getString("first_name");
			//this.last_name = object.getString("last_name");
			//this.username = object.getString("username");
			this.isBot = object.getBoolean("is_bot");
		} catch (JSONException e) {
			System.out.println("JSONObjectExcetion creating User" + e.getMessage());
		}
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setIsBot(boolean b) {
		this.isBot = b;
	}

	public boolean getIsBot() {
		return this.isBot;
	}


	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
