package model;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import repo.PlayerRepository;

public class PlayersMongoDB {
	MongoTemplate mongoTemplate;
	PlayerRepository users;
	AbstractApplicationContext ctx;
	static PlayersMongoDB usersMongoDB;

	private PlayersMongoDB() {
		ctx = new FileSystemXmlApplicationContext(PlayerRepository.BEANS_FILE_NAME);
		mongoTemplate = (MongoTemplate) ctx.getBean(PlayerRepository.MONGO_TEMPLATE_ID);
		users = ctx.getBean(PlayerRepository.class);
	}

	synchronized public static PlayersMongoDB createUsersMongoDB() {
		if (usersMongoDB == null) {
			usersMongoDB = new PlayersMongoDB();
		}
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
		int count = users.findOne(userId).counter;
		users.save(new Player(userId, ++count));
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
