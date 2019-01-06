package model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import repo.UserRepository;

@Document(collection = UserRepository.COLLECTION_NAME)
public class User {
	@Id
	@Indexed(unique = true)
	String userId;
	int counter;

	public User(String userId, int counter) {
		this.userId = userId;
		this.counter = counter;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	@Override
	public String toString() {
		return "User [userName=" + userId + ", counter=" + counter + "]";
	}

}
