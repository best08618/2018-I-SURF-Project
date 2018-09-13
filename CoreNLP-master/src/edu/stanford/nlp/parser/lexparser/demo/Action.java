package edu.stanford.nlp.parser.lexparser.demo;


public class Action {
	Noun subj;
	Verb pred;
	Noun[] dobjarr ;
	Modifier[] molarr;
	int dobjIdx;
	int modIdx;
	
	public Action(){
		this.subj = new Noun();
		this.pred = new Verb();
		this.dobjarr = new Noun[10];
		this.molarr = new Modifier[10];
		this.dobjIdx = 0;
		this.modIdx = 0;
	}
	
	public void setSubj(Noun s) {
		this.subj = s;
	}
	
	public void setVerb(Verb v) {
		this.pred = v;
	}
	
	public void setDobj(Noun d) {
		this.dobjarr[dobjIdx++] = d;
		
	}
	public void setModifier(Modifier m) {
		this.molarr[modIdx++] = m;
	}
	
}
