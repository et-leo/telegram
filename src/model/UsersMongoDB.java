package model;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import repo.UserRepository;

public class UsersMongoDB {
	MongoTemplate mongoTemplate;
	UserRepository users;
	AbstractApplicationContext ctx;
	static UsersMongoDB usersMongoDB;

	private UsersMongoDB() {
		ctx = new FileSystemXmlApplicationContext(UserRepository.BEANS_FILE_NAME);
		mongoTemplate = (MongoTemplate) ctx.getBean(UserRepository.MONGO_TEMPLATE_ID);
		users = ctx.getBean(UserRepository.class);
	}

	synchronized public static UsersMongoDB createUsersMongoDB() {
		if (usersMongoDB == null) {
			usersMongoDB = new UsersMongoDB();
		}
		return usersMongoDB;

	}

	public void drop() {
		mongoTemplate.dropCollection(UserRepository.COLLECTION_NAME);
	}

	public boolean addUser(User user) {
		boolean res = false;
		users.findOne(user.userId);
		if (!users.exists(user.getUserId())) {
			users.save(user);
			res = true;
		}
		return res;
	}

	public Iterable<User> getUsers() {
		return users.findAll();
	}

	public User getUser(String userId) {
		return users.findOne(userId);
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
