package model;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

import repo.PlayerRepository;

public class PlayersMongoDB {
	MongoTemplate mongoTemplate;
	PlayerRepository users;
	AbstractApplicationContext ctx;
	static PlayersMongoDB usersMongoDB;

	// ========= works with one DB ======= //

	// private PlayersMongoDB() {
	// ctx = new FileSystemXmlApplicationContext(PlayerRepository.BEANS_FILE_NAME);
	// mongoTemplate = (MongoTemplate) ctx.getBean(PlayerRepository.MONGO_TEMPLATE_ID);
	// users = ctx.getBean(PlayerRepository.class);
	// }
	//
	// synchronized public static PlayersMongoDB createUsersMongoDB() {
	// if (usersMongoDB == null) {
	// usersMongoDB = new PlayersMongoDB();
	// }
	// return usersMongoDB;
	// }

	private PlayersMongoDB(String chatId) {
		ctx = new FileSystemXmlApplicationContext(PlayerRepository.BEANS_FILE_NAME);
		String uri = "mongodb://root:root123@ds149344.mlab.com:49344/telegram";
		Mongo mongo = new MongoClient(uri);
		mongoTemplate = new MongoTemplate(mongo, "telegram" + chatId);
		users = ctx.getBean(PlayerRepository.class);
	}

	synchronized public static PlayersMongoDB createUsersMongoDB(String chatId) {
		usersMongoDB = new PlayersMongoDB(chatId);
		return usersMongoDB;
	}

	public void drop() {
		mongoTemplate.dropCollection(PlayerRepository.COLLECTION_NAME);
	}

	public boolean addUser(Player user) {
		boolean res = false;
		users.findOne(user.userId);
		if (!users.exists(user.getUserId())) {
			users.save(user);
			res = true;
		}
		return res;
	}

	public Iterable<Player> getUsers() {
		return users.findAll();
	}

	public Player getUser(String userId) {
		return users.findOne(userId);
	}

	public void incUserCounter(String userId) {
		Map<Integer, Integer> counter = users.findOne(userId).counter;
		int year = Year.now().getValue();
		Integer count = counter.get(year);
		count = count == null ? 1 : count++;
		Map<Integer, Integer> newCounter = new HashMap<Integer, Integer>();
		newCounter.put(year, count);
		users.save(new Player(userId, newCounter));
	}

	//
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
