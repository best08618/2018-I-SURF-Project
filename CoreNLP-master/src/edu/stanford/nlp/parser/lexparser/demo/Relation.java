package edu.stanford.nlp.parser.lexparser.demo;


public class Relation {

	public String govIdx;
	public String depIdx;
	public int wordIdx;
	
	public Relation() {
		
	}
	
	public Relation(String govIdx, String depIdx, int wordIdx) {
		this.govIdx = govIdx;
		this.depIdx = depIdx;
		this.wordIdx = wordIdx;
	}
}
