package com.battlegrid.game;

/*
 * A card represents a single ability that the player can use.
 */
public class Card {
	public String name;
	public int damage;
	public int range;
	public int element;

	public Card(String theName, int theDamage, int theRange, int theElement) {
		name = theName;
		damage = theDamage;
		range = theRange;
		element = theElement;
	}

	public Card(Card theCard) {
		name = theCard.name;
		damage = theCard.damage;
		range = theCard.range;
		element = theCard.element;
	}
}
