package controller;

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
import model.PlayersDB;

public class App extends TelegramLongPollingBot {
	private String enter = System.getProperty("line.separator");
	private static PlayersDB playersDB;
	private long chatId;

	public static void main(String[] args) {
		ApiContextInitializer.init();
		TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
		try {
			telegramBotsApi.registerBot(new App());
		} catch (Exception e) {
			e.printStackTrace();
		}
		playersDB = PlayersDB.createPlayersDb();
	}

	public synchronized void onUpdateReceived(Update update) {
		Message message = update.getMessage();
		chatId = message.getChatId();
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
							if (text.equals("/start")) {
								sendMsg(message, "Let's start!");
							}
							// ignore -> nothing to do
						}
					}
				}
			}
		}
	}

	private void showStat(Message message, String text) {
		List<Player> players = (List<Player>) playersDB.getPlayersByChatId(chatId);
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
		StringBuilder stat = new StringBuilder(
				"Statistic for " + (year == null ? "all time" : (year + " year")) + enter);
		Map<String, Integer> statMap = new HashMap<>();
		if (year == null) {
			for (Player player : players) {
				int counter = player.getCounter().values().stream().reduce(0, (acc, value) -> acc + value);
				statMap.put(player.getName(), counter);
			}
		} else {
			for (Player player : players) {
				Integer count = player.getCounter().get(year);
				statMap.put(player.getName(), count == null ? 0 : count);
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
		Player currentWinner = playersDB.getWinnerByChatId(chatId);
		Player newWinner;
		if (currentWinner == null) {
			List<Player> players = (List<Player>) playersDB.getPlayersByChatId(chatId);
			if (players.isEmpty()) {
				sendMsg(message, "No players :(");
			} else {
				sendSpam(message);
				newWinner = players.get((int) (Math.random() * (players.size())));
				sendMsg(message, "Winner: " + newWinner.getName());
				playersDB.updateWinner(newWinner, true);
			}
		} else {
			if (currentWinner.getDateOfWin().equals(playersDB.getDate())) {
				sendMsg(message, "Current winner: " + currentWinner.getName());
			} else {
				playersDB.updateWinner(currentWinner, false);
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
				+ enter + "/statAll - stats for all time" + enter + "/stat2019 - stats for selected year";
		sendMsg(message, help);
	}

	private void addPlayer(Message message) {
		String name = (message.getFrom().getFirstName() + message.getFrom().getLastName()).replace("_", "");
		Map<Integer, Integer> counter = new HashMap<Integer, Integer>();
		counter.put(playersDB.getYear(), 0);
		if (playersDB.addPlayer(new Player(chatId, name, counter, false, ""))) {
			sendMsg(message, name + " added");
		} else {
			sendMsg(message, name + " already registred");
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
		keyboardRow2.add(new KeyboardButton("/stat" + playersDB.getYear()));

		keyboardRows.add(keyboardRow1);
		keyboardRows.add(keyboardRow2);

		replyKeyboardMarkup.setKeyboard(keyboardRows);
		sendMessage.setReplyMarkup(replyKeyboardMarkup);
	}

	public String getBotUsername() {
		return BotConfig.NAME;
	}

	@Override
	public String getBotToken() {
		return BotConfig.TOKEN;
	}
}