package gabriel.TelegramBot;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;

import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;

import org.json.JSONException;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Calendar;
import java.util.Locale;
import java.util.HashMap;

import java.text.SimpleDateFormat;

public class PollingBot extends BaseBot{

	private Thread pt;
	private Thread tastyThread;
	private Integer initial_day;
	private String[][] menus;
	private HashMap<String, Integer> hmap = new HashMap<String, Integer>();
	private int today_hour;
	private int today_min;
	private int tomorrow_hour;
	private int tomorrow_min;
	private String chatId;

	public PollingBot(String token){

		super(token);
		this.pt = new Thread(new PollingThread(this.getUrl(), this, 0, 0, 60, 2000));
		this.initial_day = 0;
		this.today_hour = 12;
		this.today_min = 0;
		this.tomorrow_hour = 20;
		this.tomorrow_min = 0;
		this.chatId = "";
		this.hmap.put("lunes", 0);
		this.hmap.put("martes", 1);
		this.hmap.put("miércoles", 2);
		this.hmap.put("jueves", 3);
		this.hmap.put("viernes", 4);
	}

	@Override
	public boolean initialiseUpdates(){

		if(this.extractTastyInformation("food.pdf")){

			this.pt.start();
			return true;

		}else
			return false;

	}

	private boolean extractTastyInformation(String path){

		try {
			//URL website = new URL("https://drive.google.com/a/it.uc3m.es/uc?authuser=1&id=1W-r4pYzlpJcwDU-S3oP5HVE5ToewB7ds&export=download");
			URL website = new URL("http://localhost/food.pdf");
			FileOutputStream fos = new FileOutputStream(path);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

			PDDocument doc = PDDocument.load(new File(path));
			PDFTextStripper pdfs = new PDFTextStripper();
			String tasty = new PDFTextStripper().getText(doc);

			this.menus = new String[20][5];

			String[] rows = tasty.split("\n");

			int pointer = 0;

			for(int i = 0; i < rows.length; i++){

				if(i == 0)
					this.initial_day = Integer.parseInt(rows[i].split(" ")[1]);
				else if(i == 11)
					continue;
				else if(i > 21)
					break;
				else{
					this.menus[pointer] = rows[i].split("(?=\\p{Upper})");
					pointer = pointer + 1;
				}

			}

			doc.close();

		}catch(IOException e){

			System.out.println("IOException while downloading or parsing food PDF: " + e.getMessage());
			return false;

		}catch(Exception e){

			System.out.println("Unknown exception while parsing food PDF: " + e.getMessage());
			e.printStackTrace();
			return false;
		}

		


		return true;
	}


	public void sendTastyInfo(boolean today){


		final Calendar now = GregorianCalendar.getInstance();
		SimpleDateFormat simpleDateformat = new SimpleDateFormat("EEEE", new Locale("es", "ES")); // the day of the week spelled out completely
		Date n = new Date();
		if(simpleDateformat.format(n).equals("sábado"))
			return;
		else if((simpleDateformat.format(n).equals("domingo") && today) || (simpleDateformat.format(n).equals("viernes") && !today))
			return;

		if(this.extractTastyInformation("food.pdf")){

			if(!today){

				Calendar c = Calendar.getInstance(); 
				c.setTime(n); 
				c.add(Calendar.DATE, 1);
				n = c.getTime();

			}

			int initial_row = 0;

			if( ((now.get(Calendar.DAY_OF_MONTH) - this.initial_day) >=7)  || ((now.get(Calendar.DAY_OF_MONTH) - this.initial_day) < 0))
				initial_row = 10;

			StringBuilder info = new StringBuilder();

			if(!today)
				info.append("<i>Y para mañana...</i> \n");
			info.append("<b>Primeros</b>: \n");

			for(int i=initial_row; i <= initial_row + 9; i++){

				if(i == initial_row + 3)
					info.append("<b>Segundos</b>: \n");
				else if(i == initial_row + 6)
					info.append("<b>Dieta</b>: \n");
				else if(i == initial_row + 8)
					info.append("<b>Vegetariano</b>: \n");

				info.append(this.menus[i][this.hmap.get(simpleDateformat.format(n))] + "\n");

			}

			try{

				this.sendMessage(this.chatId, info.toString(), "html", false, false, 0, null);

			}catch(IOException e){

				System.out.println("IO Exception at sending tasty information: " + e.getMessage());

			}catch(JSONException e){

				System.out.println("JSON Exception at sending tasty information: " + e.getMessage());

			}

		}

	}


	public void receiveUpdate(Update [] updates){


		for (int i = 0; i < updates.length; i++){

			if(updates[i].getMessage() != null && updates[i].getMessage().getText() != null){

				try{

					if(updates[i].getMessage().getText().equals("Lenny")){
						String picPath = System.getProperty("user.dir") + "/gabriel/TelegramBot/images/lenny.png";
						this.sendPhoto(Long.toString(updates[i].getMessage().getChat().getId()), new File(picPath), "Lenny Approves :)", false, 0, null);

					}else if(updates[i].getMessage().getText().startsWith("/today")){

						if(this.tastyThread != null)
							if(this.tastyThread.isAlive()){
								String temp = updates[i].getMessage().getText().split(" ")[1];
								this.today_hour =  Integer.parseInt(temp.split(":")[0]);
								this.today_min=  Integer.parseInt(temp.split(":")[1]);

								this.tastyThread.interrupt();
								this.tastyThread = new Thread(new TastyThread(this, this.today_hour, this.today_min, this.tomorrow_hour, this.tomorrow_min));
								this.tastyThread.start();

								this.sendMessage(Long.toString(updates[i].getMessage().getChat().getId()), "Daily updates set to: " + this.today_hour + ":" + this.today_min +"h", null, false, false, updates[i].getMessage().getId(), null);
							}else
								this.sendMessage(Long.toString(updates[i].getMessage().getChat().getId()), "Tasty Thread not running ", null, false, false, updates[i].getMessage().getId(), null);

					}else if(updates[i].getMessage().getText().startsWith("/tomorrow")){

						if(this.tastyThread != null)
							if(this.tastyThread.isAlive()){

								String temp = updates[i].getMessage().getText().split(" ")[1];
								this.tomorrow_hour =  Integer.parseInt(temp.split(":")[0]);
								this.tomorrow_min =  Integer.parseInt(temp.split(":")[1]);

								this.tastyThread.interrupt();
								this.tastyThread = new Thread(new TastyThread(this, this.today_hour, this.today_min, this.tomorrow_hour, this.tomorrow_min));
								this.tastyThread.start();

								this.sendMessage(Long.toString(updates[i].getMessage().getChat().getId()), "Tomorrow updates set to: " + this.tomorrow_hour + ":" + this.tomorrow_min +"h", null, false, false, updates[i].getMessage().getId(), null);

							}else
								this.sendMessage(Long.toString(updates[i].getMessage().getChat().getId()), "Tasty Thread not running ", null, false, false, updates[i].getMessage().getId(), null);

					}else if(updates[i].getMessage().getText().equals("/start")){

						if(this.tastyThread == null){
							this.chatId = Long.toString(updates[i].getMessage().getChat().getId());
							this.tastyThread = new Thread(new TastyThread(this));
							this.tastyThread.start();
							this.sendMessage(Long.toString(updates[i].getMessage().getChat().getId()), "Initalising tasty updates " + Emoji.FACE_SAVOURING_DELICIOUS_FOOD + "\n" + "Use: '/today hh:mm' to configure the time of daily alarms" + "\n" + "Use: '/tomorrow hh:mm' to configure tomorrow's reminder time", null, false, false, updates[i].getMessage().getId(), null);

						}else
							this.sendMessage(Long.toString(updates[i].getMessage().getChat().getId()), "Updates already working", null, false, false, updates[i].getMessage().getId(), null);

					}else if(updates[i].getMessage().getText().startsWith("/stop")){

						this.sendMessage(Long.toString(updates[i].getMessage().getChat().getId()), "Stopping Tasty Thread", null, false, false, updates[i].getMessage().getId(), null);
						this.tastyThread.interrupt();
						this.tastyThread = null;

					}else 
						this.sendMessage(Long.toString(updates[i].getMessage().getChat().getId()), updates[i].getMessage().getText(), null, false, false, updates[i].getMessage().getId(), null);

				}catch(IOException e){
					System.out.println("IO Exception at polling thread: " + e.getMessage());
				}catch(JSONException e){
					System.out.println("JSON Exception at polling thread: " + e.getMessage());
				}catch(Exception e){
					System.out.println("Exception at polling thread: " + e.getMessage());
					try{
					this.sendMessage(Long.toString(updates[i].getMessage().getChat().getId()), "Wouldn't you like to cause an exception? -.-", null, false, false, updates[i].getMessage().getId(), null);}catch (Exception e2){System.out.println("Exception at the Exception (Inception): " + e2.getMessage());}
				}

			}
				
		}

		return;
	}

}
