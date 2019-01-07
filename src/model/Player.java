package model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import repo.PlayerRepository;

@Document(collection = PlayerRepository.COLLECTION_NAME)
public class Player {
	@Id
	String userId;
	int counter;

	public Player(String userId, int counter) {
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
		return userId;

	}

}
