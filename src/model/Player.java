package model;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import repo.PlayerRepository;

@Document(collection = PlayerRepository.COLLECTION_NAME)
public class Player {
	@Id
	String userId;
	Map<Integer, Integer> counter; // key - year; value - counter;

	public Player(String userId, Map<Integer, Integer> counter) {
		this.userId = userId;
		this.counter = counter;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Map<Integer, Integer> getCounter() {
		return counter;
	}

	public void setCounter(Map<Integer, Integer> counter) {
		this.counter = counter;
	}

	@Override
	public String toString() {
		return userId;

	}

}
