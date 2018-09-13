package edu.stanford.nlp.parser.lexparser.demo;


public class Noun {
	String name;
	Modifier[] molarr;
	String depIdx;
	int modIdx;
	
	public Noun () {
		this.name = "";
		this.molarr = new Modifier[10];
		this.depIdx = "";
		this.modIdx = 0;
	}
	
	public void setName(String s) {
		this.name = s;
	}
	
	public void setModifier(Modifier m) {
		this.molarr[modIdx++]=m;
	}
	
	public void setDepIdx (String idx) {
		this.depIdx = idx;
	}
}
