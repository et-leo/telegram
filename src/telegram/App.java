package telegram;

import java.util.LinkedList;
import java.util.List;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
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
				printHelpMessage(message);
				break;

			case "/play":
				play(message);
				break;

			case "/stat":
				showStat(message);
				break;
			case "/register":
				addPlayer(message);
				break;
			default:
				sendMsg(message, "default");
				break;
			}
		}
	}

	private void showStat(Message message) {
		// TODO Auto-generated method stub
		sendMsg(message, "stat");
	}

	private void play(Message message) {
		// TODO Auto-generated method stub
		sendMsg(message, "play");
	}

	private void printHelpMessage(Message message) {
		String help = "help";
		sendMsg(message, help);
	}

	private void addPlayer(Message message) {
		// TODO Auto-generated method stub
		sendMsg(message, "add player");

	}

	private void sendMsg(Message message, String text) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.enableMarkdown(true);
		sendMessage.setChatId(message.getChatId());
		sendMessage.setText(text);
		try {
			setButtons(sendMessage);
			execute(sendMessage);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}

	private void setButtons(SendMessage sendMessage) {
		ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(false);

		List<KeyboardRow> keyboardRows = new LinkedList<>();
		KeyboardRow keyboardRow1 = new KeyboardRow();
		KeyboardRow keyboardRow2 = new KeyboardRow();

		keyboardRow1.add(new KeyboardButton("/help"));
		keyboardRow1.add(new KeyboardButton("/register"));
		keyboardRow2.add(new KeyboardButton("/play"));
		keyboardRow2.add(new KeyboardButton("/stat"));

		keyboardRows.add(keyboardRow1);
		keyboardRows.add(keyboardRow2);

		replyKeyboardMarkup.setKeyboard(keyboardRows);
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