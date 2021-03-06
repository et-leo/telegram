package controller;

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

import model.Player;
import model.PlayersMongoDB;

public class App extends TelegramLongPollingBot {
	static PlayersMongoDB usersMongoDB;

	public static void main(String[] args) {
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new App());
		} catch (Exception e) {
			e.printStackTrace();
		}
		usersMongoDB = PlayersMongoDB.createUsersMongoDB();
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
				break;
			}
		}
	}

	private void showStat(Message message) {
		List<Player> players = (List<Player>) usersMongoDB.getUsers();
		StringBuilder stat = new StringBuilder();
		for (Player player : players) {
			stat.append(player.getUserId() + ": " + player.getCounter() + "\n");
		}
		sendMsg(message, stat.toString());
	}

	private void play(Message message) {
		List<Player> players = (List<Player>) usersMongoDB.getUsers();
		sendMsg(message, "Searching...");
		if (players.isEmpty()) {
			sendMsg(message, "No players");
		} else {
			int nPlayers = players.size();
			int randomPlayer = (int) (Math.random() * (nPlayers));
			players.get(randomPlayer);
			Player winner = players.get(randomPlayer);
			sendMsg(message, "Winner: " + usersMongoDB.getUser(winner.getUserId()).toString());
			usersMongoDB.incUserCounter(winner.getUserId());
		}
	}

	private void printHelpMessage(Message message) {
		String help = "help";
		sendMsg(message, help);
	}

	private void addPlayer(Message message) {
		String userName = message.getFrom().getFirstName() + message.getFrom().getLastName();
		if (usersMongoDB.addUser(new Player(userName, 0))) {
			sendMsg(message, userName + " added");
		} else {
			sendMsg(message, userName + " already registred");
		}

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
		//sendMessage.setReplyMarkup(replyKeyboardMarkup);
		replyKeyboardMarkup.setSelective(true);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(false);

		List<KeyboardRow> keyboardRows = new LinkedList<>();
		KeyboardRow keyboardRow1 = new KeyboardRow();
		// KeyboardRow keyboardRow2 = new KeyboardRow();

		// keyboardRow1.add(new KeyboardButton("/help"));
		keyboardRow1.add(new KeyboardButton("/register"));
		keyboardRow1.add(new KeyboardButton("/play"));
		keyboardRow1.add(new KeyboardButton("/stat"));

		keyboardRows.add(keyboardRow1);
		// keyboardRows.add(keyboardRow2);

		replyKeyboardMarkup.setKeyboard(keyboardRows);
	}

	public String getBotUsername() {
		return "pidor2";
	}

	@Override
	public String getBotToken() {
		return "662562683:AAE_wnataOiCIH8wL5UqcpGx9TAdAbVRvdM";
	}
}