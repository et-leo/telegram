package repo;

import org.springframework.data.repository.CrudRepository;

import model.User;

public interface UserRepository extends CrudRepository<User, String> {
	String COLLECTION_NAME = "telegram";
	String BEANS_FILE_NAME = "beans.xml";
	String MONGO_TEMPLATE_ID = "mongoTemplate";

	// Iterable<User> findByAuthorLike(String author);
	//
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
