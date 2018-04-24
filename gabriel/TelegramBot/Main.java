package gabriel.TelegramBot;

import java.lang.Exception;

public class Main{

	public static void main(String[] args){

		try{

			String botToken = "403057023:AAHXmoxOPuRrmWHGBdWxcr2GmR-MMX1Z7yY";
			PollingBot b = new PollingBot(botToken);
			b.initialiseUpdates();

/*
			long duration = 5 * 60 * 1000;

			System.out.println("Starting Telegram Time Thread");

			long startTime = System.currentTimeMillis();
			Thread t = new Thread(new TimeThread());
			t.start();

			System.out.println("Waiting for Time Thread to end");

			while(t.isAlive()){

				t.join(5000);
				if (((System.currentTimeMillis() - startTime) > duration) && t.isAlive()){
					System.out.println("Tired of waiting!");
					t.interrupt();
					t.join();
				} else {
					System.out.println("Waiting for duration...");
				}
			}

			System.out.println("Ending...");
*/
		} catch (Exception e) {
			System.out.println("Exception Main: " + e.getMessage());
		}

	}

}
