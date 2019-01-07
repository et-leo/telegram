package controller;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Player;
import model.PlayersMongoDB;

public class Test {
	// static Logger logger = Logger.getLogger("org.mongodb");

	public static void main(String[] args) {
		// === Create === //
		// logger.setLevel(Level.ALL);
		int year = Year.now().getValue();
		Map<Integer, Integer> counter = new HashMap<Integer, Integer>();
		counter.put(year, 0);
		Player user1 = new Player("@user1", counter);
		Player user2 = new Player("@user2", counter);

		PlayersMongoDB usersMongoDB = PlayersMongoDB.createUsersMongoDB("_dev");
		// usersMongoDB.drop();
		usersMongoDB.addPlayer(user1);
		usersMongoDB.addPlayer(user2);

		// === Read === //

		// logger.setLevel(Level.WARNING);
		List<Player> players = (List<Player>) usersMongoDB.getPlayers();
		System.out.println(players.toString());
		// for (User user : usersMongoDB.getUsers()) {
		// System.out.println(user.toString());
		// }

	}

}
