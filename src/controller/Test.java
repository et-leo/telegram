package controller;

import java.util.List;

import model.Player;
import model.PlayersMongoDB;

public class Test {
	// static Logger logger = Logger.getLogger("org.mongodb");

	public static void main(String[] args) {
		// === Create === //
		// logger.setLevel(Level.ALL);
		Player user1 = new Player("@user1", 1);
		Player user2 = new Player("@user2", 1);

		PlayersMongoDB usersMongoDB = PlayersMongoDB.createUsersMongoDB();
		// usersMongoDB.drop();
		usersMongoDB.addUser(user1);
		usersMongoDB.addUser(user2);

		// === Read === //

		// logger.setLevel(Level.WARNING);
		List<Player> players = (List<Player>) usersMongoDB.getUsers();
		System.out.println(players.toString());
//		for (User user : usersMongoDB.getUsers()) {
//			System.out.println(user.toString());
//		}

	}

}
