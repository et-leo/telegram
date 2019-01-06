package controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import model.User;
import model.UsersMongoDB;

public class Test {
	static Logger logger = Logger.getLogger("org.mongodb");

	public static void main(String[] args) {
		// === Create === //
		logger.setLevel(Level.ALL);
		User user1 = new User("@user1", 0);
		User user2 = new User("@user2", 0);

		UsersMongoDB usersMongoDB = UsersMongoDB.createUsersMongoDB();
		// usersMongoDB.drop();
		usersMongoDB.addUser(user1);
		usersMongoDB.addUser(user2);

		// === Read === //

		logger.setLevel(Level.WARNING);
		usersMongoDB.getUser(user1.getUserId());
		for (User user : usersMongoDB.getUsers()) {
			System.out.println(user.toString());
		}

	}

}
