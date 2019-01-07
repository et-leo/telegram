package controller;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

import model.BotConfig;
import model.Player;
import model.PlayersMongoDB;

public class App extends TelegramLongPollingBot {
	String enter = System.getProperty("line.separator");
	// static PlayersMongoDB usersMongoDB;
	// static Map<Long, PlayersMongoDB> usersMongoDB = new HashMap<>();
	static Player winner;
	static Map<Player, LocalDateTime> currentWinner;
	long chatId;

	public static void main(String[] args) {
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new App());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// usersMongoDB = PlayersMongoDB.createUsersMongoDB();
	}

	public synchronized void onUpdateReceived(Update update) {
		Message message = update.getMessage();
		chatId = message.getChatId();
		createMongoTable(message);
		if (message != null && message.hasText()) {
			String text = message.getText();
			if (text.equals("/help")) {
				printHelpMessage(message);
			} else {
				if (text.equals("/play")) {
					play(message);
				} else {
					if (text.equals("/register")) {
						addPlayer(message);
					} else {
						if (text.startsWith("/stat")) {
							showStat(message, text);
						} else {
							// ignore -> nothing to do
						}
					}
				}
			}
		}
	}

	private void createMongoTable(Message message) {
//		if (!usersMongoDB.containsKey(chatId)) {
//			usersMongoDB.put(chatId, PlayersMongoDB.createUsersMongoDB(String.valueOf(chatId)));
//		}
		PlayersMongoDB.createUsersMongoDB(String.valueOf(chatId));
	}

	private void showStat(Message message, String text) {
		List<Player> players = (List<Player>) PlayersMongoDB.usersMongoDB.getPlayers();
		String textToSend = "No players!";
		if (!players.isEmpty()) {
			if (text.equalsIgnoreCase("/statAll")) {
				textToSend = getSortedPlayers(players, null);
			} else {
				try {
					String yearStr = text.replace("/stat", "");
					textToSend = getSortedPlayers(players, Integer.parseInt(yearStr));
				} catch (Exception e) {
					// no such year -> nothing to do
				}
			}
		}
		sendMsg(message, textToSend);
	}

	private String getSortedPlayers(List<Player> players, Integer year) {
		StringBuilder stat = new StringBuilder("Statistic for " + year == null ? "all time" : (year + " year"));
		Map<String, Integer> statMap = new HashMap<>();
		if (year == null) {
			for (Player player : players) {
				int counter = 0;
				for (Integer value : player.getCounter().values()) {
					counter += value;
				}
				statMap.put(player.getUserId(), counter);
			}
		} else {
			for (Player player : players) {
				Integer count = player.getCounter().get(year);
				statMap.put(player.getUserId(), count == null ? 0 : count);
			}
		}

		Map<String, Integer> sortedPlayers = statMap.entrySet().stream()
				.sorted((Map.Entry.<String, Integer>comparingByValue().reversed()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

		for (Entry<String, Integer> player : sortedPlayers.entrySet()) {
			stat.append(player.getKey() + ": " + player.getValue() + enter);
		}
		return stat.toString();
	}

	private void play(Message message) {
		if (currentWinner == null) {
			List<Player> players = (List<Player>) PlayersMongoDB.usersMongoDB.getPlayers();
			sendSpam(message);
			if (players.isEmpty()) {
				sendMsg(message, "No players");
			} else {
				int nPlayers = players.size();
				int randomPlayer = (int) (Math.random() * (nPlayers));
				players.get(randomPlayer);
				winner = players.get(randomPlayer);
				sendMsg(message, "Winner: " + PlayersMongoDB.usersMongoDB.getPlayer(winner.getUserId()).toString());
				PlayersMongoDB.usersMongoDB.incPlayerCounter(winner.getUserId());
				currentWinner.put(winner, LocalDateTime.now());
			}
		} else {
			if (currentWinner.get(winner) == LocalDateTime.now()) {
				sendMsg(message, "Current winner: " + winner.toString());
			} else {
				currentWinner = null;
				play(message);
			}
		}
	}

	private void sendSpam(Message message) {
		// TODO - generate random messages
		sendMsg(message, "search... ");
		sendMsg(message, "spam... ");
		sendMsg(message, "joke... ");
	}

	private void printHelpMessage(Message message) {
		String help = "/help - to see this message" + enter + "/register - registration" + enter + "/play - play round"
				+ enter + "/statAll - stats for all time" + "/stat2019 - stats for selected year";
		sendMsg(message, help);
	}

	private void addPlayer(Message message) {
		int year = Year.now().getValue();
		Map<Integer, Integer> counter = new HashMap<Integer, Integer>();
		counter.put(year, 0);
		String userName = message.getFrom().getFirstName() + message.getFrom().getLastName();
		userName.replaceAll("_", "");
		// userName.replaceAll("*", "");
		if (PlayersMongoDB.usersMongoDB.addPlayer(new Player(userName, counter))) {
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

		sendMessage.setReplyMarkup(replyKeyboardMarkup);
		replyKeyboardMarkup.setSelective(false);
		replyKeyboardMarkup.setResizeKeyboard(true);
		replyKeyboardMarkup.setOneTimeKeyboard(true);

		List<KeyboardRow> keyboardRows = new LinkedList<>();
		KeyboardRow keyboardRow1 = new KeyboardRow();
		KeyboardRow keyboardRow2 = new KeyboardRow();

		keyboardRow1.add(new KeyboardButton("/play"));
		keyboardRow1.add(new KeyboardButton("/register"));
		keyboardRow1.add(new KeyboardButton("/help"));
		keyboardRow2.add(new KeyboardButton("/statAll"));
		keyboardRow2.add(new KeyboardButton("/stat" + Year.now().getValue()));

		keyboardRows.add(keyboardRow1);
		keyboardRows.add(keyboardRow2);

		replyKeyboardMarkup.setKeyboard(keyboardRows);
	}

	public String getBotUsername() {
		return BotConfig.NAME;
	}

	@Override
	public String getBotToken() {
		return BotConfig.TOKEN;
	}
}