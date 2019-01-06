package telegram;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class App extends TelegramLongPollingBot {

	public static void main(String[] args) {
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new App());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onUpdateReceived(Update update) {
		Message message = update.getMessage();
		if (message != null && message.hasText()) {
			switch (message.getText()) {
			case "/help":
				sendMsg(message, "help");
				break;

			default:
				sendMsg(message, "default");
				break;
			}
		}
	}

	private void sendMsg(Message message, String text) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);
		sendMessage.setChatId(message.getChatId());
		sendMessage.setText(text);
		try {
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	public String getBotUsername() {
		return "pidor2";
	}

	@Override
	public String getBotToken() {
		return "662562683:AAGtst2EDKRIBps-QUAEDBwSX7lTuGgfEu0";
		// return "756085460:AAG8Idohwk1Rp7WRt1_Z_zxEAzI7AMxdv48";
	}
}