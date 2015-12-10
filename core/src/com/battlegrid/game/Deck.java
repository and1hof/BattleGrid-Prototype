package com.battlegrid.game;

import java.util.Stack;

/*
 * A deck consists of 30 cards.
 */
public class Deck {
	Stack<Card> myDeck;
	int remaining;

	public Deck() {
		// create new card deck
		myDeck = new Stack<Card>();
		addCards();
	}

	/*
	 * Fill the initial deck with random cards.
	 */
	private void addCards() {
		for (int i = 0; i < 30; i++) {
			int range = Math.max(1, (int) (Math.random() * 4));
			int damage = Math.max(50, (int) (Math.random() * 150));
			int element = Math.max(1, (int) (Math.random() * 5));
			myDeck.push(new Card(getElem(element) + ": " + damage + "x" + range, damage, range, element));
		}
	}

	public String getElem(int element) {
		String ret = "Grass"; // base case
		if (element == 2) {
			ret = "Fire";
		} else if (element == 3) {
			ret = "Water";
		} else if (element == 4) {
			ret = "Elec";
		}
		return ret;
	}

	public Card draw() {
		// draw from top of deck
		Card ret = myDeck.pop();
		return ret;
	}

	public int size() {
		return myDeck.size();
	}
}
