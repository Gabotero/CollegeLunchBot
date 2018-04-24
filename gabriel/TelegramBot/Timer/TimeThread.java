package gabriel.TelegramBot;

import java.io.File;

import java.lang.Thread;
import java.lang.Exception;

import java.util.Date;

import java.text.SimpleDateFormat;


public class TimeThread implements Runnable {

	public void run() {

		try {

			while(true){

				String botToken = "351325875:AAEvWwHSJYtzFeh1Lm7uMDppKvVVwVd55sM";

				String chatId = "-1001149406746";

				String picPath = "/home/adscom/Escritorio/Bot/gabriel/TelegramBot/images/lenny.png";

				TelegramBot b = new TelegramBot(botToken);

				SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
				Date dateobj = new Date();
				Message result = b.sendMessage(chatId, "Hola, son las: " + df.format(dateobj));
				System.out.println(b.sendPhoto(chatId, new File(picPath), "Lenny Approves :)", false, 0, null));

				Thread.sleep(30000);

			}

		} catch (InterruptedException e) {
			System.out.println("I wasn't done!");
		} catch (Exception e){
			System.out.println("Exception: " + e.getMessage());
		}
	}

}

