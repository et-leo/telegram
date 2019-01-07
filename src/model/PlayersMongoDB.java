package model;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;

import repo.PlayerRepository;

@EnableMongoRepositories(basePackages = "repo")
public class PlayersMongoDB {
	MongoTemplate mongoTemplate;
	static PlayersMongoDB usersMongoDB;
	// AbstractApplicationContext ctx;

	@Autowired
	PlayerRepository playersRepo;

	// ========= works with one DB ======= //

	// private PlayersMongoDB() {
	// ctx = new FileSystemXmlApplicationContext(PlayerRepository.BEANS_FILE_NAME);
	// mongoTemplate = (MongoTemplate) ctx.getBean(PlayerRepository.MONGO_TEMPLATE_ID);
	// users = ctx.getBean(PlayerRepository.class);
	// }

	// synchronized public static PlayersMongoDB createUsersMongoDB() {
	// if (usersMongoDB == null) {
	// usersMongoDB = new PlayersMongoDB();
	// }
	// return usersMongoDB;
	// }

	private PlayersMongoDB(String chatId) {
		String uri = "mongodb://root:root123@ds149344.mlab.com:49344/telegram";
		// Mongo mongo = new MongoClient(uri);
		@SuppressWarnings("deprecation")
		Mongo mongo = new Mongo(uri);
		mongoTemplate = new MongoTemplate(mongo, "telegram" + chatId);
	}

	synchronized public static PlayersMongoDB createUsersMongoDB(String chatId) {
		usersMongoDB = new PlayersMongoDB(chatId);
		return usersMongoDB;
	}

	public void drop() {
		mongoTemplate.dropCollection(PlayerRepository.COLLECTION_NAME);
	}

	public boolean addPlayer(Player user) {
		boolean res = false;
		playersRepo.findOne(user.userId);
		if (!playersRepo.exists(user.getUserId())) {
			playersRepo.save(user);
			res = true;
		}
		return res;
	}

	public Iterable<Player> getPlayers() {
		return playersRepo.findAll();
	}

	public Player getPlayer(String userId) {
		return playersRepo.findOne(userId);
	}

	public void incPlayerCounter(String userId) {
		Map<Integer, Integer> counter = playersRepo.findOne(userId).counter;
		int year = Year.now().getValue();
		Integer count = counter.get(year);
		count = count == null ? 1 : count++;
		Map<Integer, Integer> newCounter = new HashMap<Integer, Integer>();
		newCounter.put(year, count);
		playersRepo.save(new Player(userId, newCounter));
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
