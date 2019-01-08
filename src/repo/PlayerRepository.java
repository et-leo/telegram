package repo;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.CrudRepository;

import model.Player;

public interface PlayerRepository extends CrudRepository<Player, String> {
	String COLLECTION_NAME = "telegram";
	String BEANS_FILE_NAME = "beans.xml";
	String MONGO_TEMPLATE_ID = "mongoTemplate";

	@Query("{'chatId':{'$eq':?0}}")
	Iterable<Player> findByChat(Long chatId);

	@Query("{'$and':[{'chatId':{'$eq':?0}},{'isWinner':{'$eq':?1}}]}")
	Player findByChatIdIsWinner(long chatId, boolean b);

	@Query("{'$and':[{'chatId':{'$eq':?0}},{'name':{'$eq':?1}}]}")
	Iterable<Player> findByChatIdName(Long chatId, String name);

	// ============================= //

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
