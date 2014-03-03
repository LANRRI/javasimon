package org.javasimon.examples.testapp.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Tuple data object.
 *
 * @author Radovan Sninsky
 * @see Tuples
 * @since 2.0
 */
public class Tuple implements Serializable {

	private int unique1;
	private int idx;
	private int one;
	private int ten;
	private int twenty;
	private int twentyFive;
	private int fifty;
	private int evenOnePercent;
	private int oddOnePercent;
	private String stringU1;
	private String stringU2;
	private String string4;

	private long created;

	public Tuple() {
		this.created = System.currentTimeMillis();
	}

	public int getUnique1() {
		return unique1;
	}

	public void setUnique1(int unique1) {
		this.unique1 = unique1;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public int getOne() {
		return one;
	}

	public void setOne(int one) {
		this.one = one;
	}

	public int getTen() {
		return ten;
	}

	public void setTen(int ten) {
		this.ten = ten;
	}

	public int getTwenty() {
		return twenty;
	}

	public void setTwenty(int twenty) {
		this.twenty = twenty;
	}

	public int getTwentyFive() {
		return twentyFive;
	}

	public void setTwentyFive(int twentyFive) {
		this.twentyFive = twentyFive;
	}

	public int getFifty() {
		return fifty;
	}

	public void setFifty(int value) {
		this.fifty = value;
	}

	public int getEvenOnePercent() {
		return evenOnePercent;
	}

	public void setEvenOnePercent(int evenOnePercent) {
		this.evenOnePercent = evenOnePercent;
	}

	public int getOddOnePercent() {
		return oddOnePercent;
	}

	public void setOddOnePercent(int oddOnePercent) {
		this.oddOnePercent = oddOnePercent;
	}

	public String getStringU1() {
		return stringU1;
	}

	public void setStringU1(String stringU1) {
		this.stringU1 = stringU1;
	}

	public String getStringU2() {
		return stringU2;
	}

	public void setStringU2(String stringU2) {
		this.stringU2 = stringU2;
	}

	public String getString4() {
		return string4;
	}

	public void setString4(String string4) {
		this.string4 = string4;
	}

	public long getCreated() {
		return created;
	}

	public Date getCreatedAsDate() {
		return new Date(created);
	}

	public String toString() {
		return "Tuple[unique1=" + unique1 + ",idx=" + idx + ",one=" + one + ",ten=" + ten +
			",twenty=" + twenty + ",twentyFive=" + twentyFive + ",fifty=" + fifty +
			",even=" + evenOnePercent + ",odd=" + oddOnePercent + ",stringU1=" + stringU1 +
			",stringU2=" + stringU2 + ",string4=" + string4 + ",created=" + getCreatedAsDate() + ']';
	}
}