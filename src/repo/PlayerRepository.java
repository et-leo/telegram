package repo;

import org.springframework.data.repository.CrudRepository;

import model.Player;

public interface PlayerRepository extends CrudRepository<Player, String> {
	String COLLECTION_NAME = "telegram";
	String BEANS_FILE_NAME = "beans.xml";
	String MONGO_TEMPLATE_ID = "mongoTemplate";

	Iterable<Player> findByChatIdLike(Long chatId);

	Player findByChatIdIsWinnerLike(Long chatId, boolean b);

	Iterable<Player> findByChatIdNameLike(Long chatId, String name);

	// Iterable<User> findByCostBetween(float f, float g);
	//
	// Iterable<User> findByPublisherCountryLike(String country);
	//
	// @Query("{'edition':{'$lte':?0}}")
	// Iterable<User> findByEditionLessThan(int edition);
	//
	// @Query("{'$and':[{'edition':{'$lte':?0}},{'subject':{'$regex':?1}}]}")
	// Iterable<User> findByEditionSubject(int edition, String subject);
	//
	// @Query("{'_class':{'$regex':?0}}")
	// Iterable<User> findByType(String type);
}
