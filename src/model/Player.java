package model;

import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

import repo.PlayerRepository;

@Document(collection = PlayerRepository.COLLECTION_NAME)
public class Player {
	Long chatId;
	String name;
	Map<Integer, Integer> counter; // key - year; value - counter;
	Boolean isWinner;
	String dateOfWin;

	public Player(Long chatId, String name, Map<Integer, Integer> counter, Boolean isWinner, String dateOfWin) {
		this.chatId = chatId;
		this.name = name;
		this.counter = counter;
		this.isWinner = isWinner;
		this.dateOfWin = dateOfWin;
	}

	public Boolean getIsWinner() {
		return isWinner;
	}

	public void setIsWinner(Boolean isWinner) {
		this.isWinner = isWinner;
	}

	public String getDateOfWin() {
		return dateOfWin;
	}

	public void setDateOfWin(String dateOfWin) {
		this.dateOfWin = dateOfWin;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<Integer, Integer> getCounter() {
		return counter;
	}

	public void setCounter(Map<Integer, Integer> counter) {
		this.counter = counter;
	}

	@Override
	public String toString() {
		return name;

	}

}
