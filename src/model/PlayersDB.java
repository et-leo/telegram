package model;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import repo.PlayerRepository;

public class PlayersDB {

	private static PlayersDB playersDB;
	private MongoTemplate mongoTemplate;
	private PlayerRepository playersRepo;
	private AbstractApplicationContext ctx;

	synchronized public static PlayersDB createPlayersDb() {
		if (playersDB == null) {
			playersDB = new PlayersDB();
		}
		return playersDB;
	}

	private PlayersDB() {
		ctx = new FileSystemXmlApplicationContext(PlayerRepository.BEANS_FILE_NAME);
		mongoTemplate = (MongoTemplate) ctx.getBean(PlayerRepository.MONGO_TEMPLATE_ID);
		playersRepo = ctx.getBean(PlayerRepository.class);
	}

	public void drop() {
		mongoTemplate.dropCollection(PlayerRepository.COLLECTION_NAME);
	}

	public Iterable<Player> getPlayersByChatId(Long chatId) {
		return playersRepo.findByChatIdLike(chatId);
	}

	public Player getWinnerByChatId(Long chatId) {
		return playersRepo.findByChatIdIsWinnerLike(chatId, true);
	}

	public Iterable<Player> getPlayerByChatId(Long chatId, String name) {
		return playersRepo.findByChatIdNameLike(chatId, name);
	}

	public boolean addPlayer(Player player) {
		boolean res = false;
		List<Player> newPlayer = (List<Player>) getPlayerByChatId(player.chatId, player.name);
		if (newPlayer.isEmpty()) {
			playersRepo.save(newPlayer);
			res = true;
		}
		return res;
	}

	public void updateWinner(Player player, boolean isWinner) {
		player.setIsWinner(isWinner);
		if (isWinner) {
			Map<Integer, Integer> counter = player.getCounter();
			Integer currentWins = counter.get(getYear());
			currentWins = currentWins == null ? 1 : currentWins++;
			counter.put(getYear(), currentWins);
			player.setCounter(counter);
			player.setDateOfWin(getDate());
			playersRepo.save(player);
		}
	}

	public String getDate() {
		return new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
	}

	public Integer getYear() {
		return Year.now().getValue();
	}
	////////////////////////////////////////////////////////////////////////

	// public Iterable<Player> getPlayers(int year) {
	// Sort sort = new Sort(Sort.Direction.DESC, "<>");
	// return playersRepo.findAll();
	// }

	// public Player getPlayer(String userId) {
	// return playersRepo.findOne(userId);
	// }

	public void incPlayerCounter(String userId) {
		// TODO
		// Map<Integer, Integer> counter = playersRepo.findOne(userId).counter;
		// int year = Year.now().getValue();
		// Integer count = counter.get(year);
		// count = count == null ? 1 : count++;
		// Map<Integer, Integer> newCounter = new HashMap<Integer, Integer>();
		// newCounter.put(year, count);
		// playersRepo.save(new Player(0L, userId, newCounter, false, null));
	}

	// public Iterable<Book> getBooksAuthor(String author) {
	// return books.findByAuthorLike(author);
	// }
	//
	// public Iterable<Book> getBooksCost(float f, float g) {
	// return books.findByCostBetween(f, g);
	// }
	//
	// public Iterable<Book> getBooksPublishedCountry(String country) {
	// return books.findByPublisherCountryLike(country);
	// }
	//
	// public Iterable<Book> getBooksEdition(int edition) {
	// return books.findByEditionLessThan(edition);
	// }
	//
	// public Iterable<Book> getBooksEditionSubject(int edition, String subject) {
	// return books.findByEditionSubject(edition, subject);
	// }
	//
	// public Iterable<Book> getBooksType(String type) {
	// return books.findByType(type);
	// }

}
