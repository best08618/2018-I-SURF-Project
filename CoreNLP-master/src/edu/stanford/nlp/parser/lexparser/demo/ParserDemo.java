package edu.stanford.nlp.parser.lexparser.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.io.StringReader;
import java.io.FileNotFoundException;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.util.ScoredObject;

class ParserDemo {

	/**
	 * The main method demonstrates the easiest way to load a parser. Simply call
	 * loadModel and specify the path of a serialized grammar model, which can be a
	 * file, a resource on the classpath, or even a URL. For example, this
	 * demonstrates loading a grammar from the models jar file, which you therefore
	 * need to include on the classpath for ParserDemo to work.
	 *
	 * Usage: {@code java ParserDemo [[model] textFile]} e.g.: java ParserDemo
	 * edu/stanford/nlp/models/lexparser/chineseFactored.ser.gz
	 * data/chinese-onesent-utf8.txt
	 * 
	 * @throws IOException
	 *
	 */
	public static void main(String[] args) throws IOException {
		String parserModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";

		if (args.length > 0) {
			parserModel = args[0];
		}
		LexicalizedParser lp = LexicalizedParser.loadModel(parserModel);

		if (args.length == 0) {
			demoAPI(lp);
		} else {
			String textFile = "/Users/zoey/git/2018-I-SURF-Project/CoreNLP-master/src/edu/stanford/nlp/parser/lexparser/demo/input.txt";
			demoDP(lp, textFile);
		}
	}

	/**
	 * demoDP demonstrates turning a file into tokens and then parse trees. Note
	 * that the trees are printed by calling pennPrint on the Tree object. It is
	 * also possible to pass a PrintWriter to pennPrint if you want to capture the
	 * output. This code will work with any supported language.
	 * 
	 * @throws IOException
	 */
	public static void demoDP(LexicalizedParser lp, String filename) throws IOException {

		/*
		 * original demoDP code Tree parse = lp.apply(sentence); parse.pennPrint();
		 * System.out.println();
		 * 
		 * if (gsf != null) { GrammaticalStructure gs =
		 * gsf.newGrammaticalStructure(parse); Collection tdl =
		 * gs.typedDependenciesCCprocessed(); System.out.println(tdl);
		 * System.out.println(); }
		 */

		// This option shows loading, sentence-segmenting and tokenizing
		// a file using DocumentPreprocessor.
		// You could also create a tokenizer here (as below) and pass it
		// to DocumentPreprocessor
		File file = new File(filename);
		FileReader fileReader = new FileReader(file);
		BufferedReader bufReader = new BufferedReader(fileReader);
		BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));

		String line = "";
		while ((line = bufReader.readLine()) != null) {

			List<ScoredObject<Tree>> parses;
			Tree final_tree = null;
			List<TypedDependency> final_tdl = null;

			// This option shows loading and using an explicit tokenizer

			String sent2 = line;

			TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
			Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sent2));
			List<CoreLabel> rawWords2 = tok.tokenize();
			parses = lp.kparse(rawWords2);
			int ii = 0;
			for (ScoredObject<Tree> parse : parses) {
				ii++;
				Tree t = parse.object();
				TreebankLanguagePack tlp1 = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
				GrammaticalStructureFactory gsf1 = tlp1.grammaticalStructureFactory();
				GrammaticalStructure gs = gsf1.newGrammaticalStructure(t);
				List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
				for (int i = 0; i < tdl.size(); i++) {
					String extractElement = tdl.get(i).reln().toString();
					if (extractElement.equals("dobj")) {
						writer.write("Number " + ii + " parse has dobj.");
						writer.newLine();
						writer.write("Sentence : " + line);
						writer.newLine();
						writer.newLine();
						/*
						 * System.out.println("Number " + ii + " parse has dobj.");
						 * System.out.println(""); System.out.println("Sentence : " + line);
						 */
						final_tree = t;
						final_tdl = tdl;
						break;
					}
				}
				if (final_tree != null)
					break;
			}
			if (ii == 5) {
				writer.append("Sentence : " + line);
				writer.newLine();
				writer.append("There are no dobj in 5 parses");
				writer.newLine();
				writer.append("===========================================================================");
				writer.newLine();
				/*
				 * System.out.println("Sentence : " + line);
				 * System.out.println("There are no dobj in 5 parses"); System.out.println(
				 * "==========================================================================="
				 * );
				 */
				continue;
			}

			TreePrint tp = new TreePrint("penn,typedDependencies");
			/*
			 * tp.printTree(final_tree); System.out.println();
			 */

			StringBuilder nsbj = new StringBuilder("");
			StringBuilder dobj = new StringBuilder("");
			StringBuilder verb = new StringBuilder("");

			for (int i = 0; i < final_tdl.size(); i++) {
				String extractElement = final_tdl.get(i).reln().toString();
				if (extractElement.equals("nsubj")) {
					String n = final_tdl.get(i).dep().originalText().toLowerCase();
					nsbj.append(n).append(" ");
				}
				if (extractElement.equals("dobj")) {
					String v = final_tdl.get(i).gov().originalText().toLowerCase();
					verb.append(v).append(" ");
				}
				if (extractElement.equals("dobj")) {
					String d = final_tdl.get(i).dep().originalText().toLowerCase();
					dobj.append(d).append(" ");
				}
			}

			writer.write("SUBJECT :  " + nsbj + "\r\n");
			writer.newLine();
			writer.write("VERB : " + verb + "\r\n");
			writer.newLine();
			writer.write("DOBJECT : " + dobj + "\r\n");
			writer.newLine();
			writer.newLine();

			/*
			 * System.out.println("SUBJECT :  " + nsbj + "\r\n");
			 * System.out.println("VERB : " + verb + "\r\n");
			 * System.out.println("DOBJECT : " + dobj + "\r\n"); System.out.println(
			 * "==========================================================================="
			 * );
			 */

		}

		writer.close();
	}

	/**
	 * demoAPI demonstrates other ways of calling the parser with already tokenized
	 * text, or in some cases, raw text that needs to be tokenized as a single
	 * sentence. Output is handled with a TreePrint object. Note that the options
	 * used when creating the TreePrint can determine what results to print out.
	 * Once again, one can capture the output by passing a PrintWriter to
	 * TreePrint.printTree. This code is for English.
	 */

	public static void demoAPI(LexicalizedParser lp) {
		// This option shows parsing a list of correctly tokenized words
		// String[] sent = { "The", "machine", "checks", "how",
		// "much","money","has","been","deposited", "." };
		// List<CoreLabel> rawWords = SentenceUtils.toCoreLabelList(sent);

		List<ScoredObject<Tree>> parses;
		Tree final_tree = null;
		List<TypedDependency> final_tdl = null;

		// This option shows loading and using an explicit tokenizer

		String sent2 = "The registrar inputs the name, address, and phone number of the applicant.";
		System.out.println(sent2);

		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		Tokenizer<CoreLabel> tok = tokenizerFactory.getTokenizer(new StringReader(sent2));
		List<CoreLabel> rawWords2 = tok.tokenize();
		// System.out.println("rawWords:" + rawWords2);
		parses = lp.kparse(rawWords2);
		int ii = 0;
		for (ScoredObject<Tree> parse : parses) {
			ii++;
			Tree t = parse.object();
			TreebankLanguagePack tlp = lp.treebankLanguagePack(); // PennTreebankLanguagePack for English
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure(t);
			List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
			for (int i = 0; i < tdl.size(); i++) {
				String extractElement = tdl.get(i).reln().toString();
				if (extractElement.equals("dobj")) {
					System.out.println("Number " + ii + " parse has dobj.");
					System.out.println("");
					final_tree = t;
					final_tdl = tdl;
					break;
				}
			}
			if (final_tree != null)
				break;
		}
		if (ii == 5) {
			System.out.println("There are no dobj in 5 parses");
			return;
		}

		TreePrint tp = new TreePrint("penn,typedDependencies"); // penn -> seg tree ,
		// typedDependencies -> Dependecy in TreePrint function
		// System.out.println("printTree function \n");
		tp.printTree(final_tree);

		StringBuilder nsbj = new StringBuilder("");
		StringBuilder dobj = new StringBuilder("");
		StringBuilder verb = new StringBuilder("");

		Relation reln = new Relation();

		Vector<Relation> nsbjIdx = new Vector<Relation>();
		Vector<Relation> dobjIdx = new Vector<Relation>();
		Vector<Relation> verbIdx = new Vector<Relation>();
		Vector<Relation> compIdx = new Vector<Relation>();
		Vector<Relation> nmodIdx = new Vector<Relation>();

		for (int i = 0; i < final_tdl.size(); i++) {
			String extractElement = final_tdl.get(i).reln().toString();
			if (extractElement.equals("compound")) {
				reln = new Relation(final_tdl.get(i).gov().toCopyIndex(), final_tdl.get(i).dep().toCopyIndex(), i);
				compIdx.add(reln);
			}
			if (extractElement.equals("nsubj")) {
				reln = new Relation(final_tdl.get(i).gov().toCopyIndex(), final_tdl.get(i).dep().toCopyIndex(), i);
				nsbjIdx.add(reln);
			}
			if (extractElement.equals("dobj")) {
				reln = new Relation(final_tdl.get(i).gov().toCopyIndex(), final_tdl.get(i).dep().toCopyIndex(), i);
				dobjIdx.add(reln);
			}
			if (extractElement.contains("nmod")) {
				reln = new Relation(final_tdl.get(i).gov().toCopyIndex(),final_tdl.get(i).dep().toCopyIndex(),i);
				nmodIdx.add(reln);
			}
		}

		int wordIdx = 0;
		for (int i = 0; i < compIdx.size(); i++) {
			System.out.println(compIdx.get(i).govIdx);
		}
		for (int i = 0; i < dobjIdx.size(); i++) {
			System.out.println(dobjIdx.get(i).depIdx);
		}
		for (int compNum = 0; compNum < compIdx.size(); compNum++) {
			for (int dobjNum = 0; dobjNum < dobjIdx.size(); dobjNum++) {
				if (compIdx.get(compNum).govIdx.equals(dobjIdx.get(dobjNum).depIdx)) {
					wordIdx = compIdx.get(compNum).wordIdx;
					dobj.append(final_tdl.get(wordIdx).dep().originalText().toLowerCase() + "-"
							+ final_tdl.get(wordIdx).gov().originalText().toLowerCase() + " / ");
				} else {
					wordIdx = dobjIdx.get(dobjNum).wordIdx;
					dobj.append(final_tdl.get(wordIdx).dep().originalText().toLowerCase() + " / ");
				}
			}
			for (int nsbjNum = 0; nsbjNum < nsbjIdx.size(); nsbjNum++)
			{
				if(compIdx.get(compNum).govIdx.equals(nsbjIdx.get(nsbjNum).depIdx)) {
					wordIdx = compIdx.get(compNum).wordIdx;
					nsbj.append(final_tdl.get(wordIdx).dep().originalText().toLowerCase() + "-"
							+ final_tdl.get(wordIdx).gov().originalText().toLowerCase() + " / ");
				} else {
					wordIdx = nsbjIdx.get(nsbjNum).wordIdx;
					nsbj.append(final_tdl.get(wordIdx).dep().originalText().toLowerCase() + " / ");
				}
			}
		}
		for (int n = 0; n < nmodIdx.size(); n++) {
			System.out.println(nmodIdx.get(n).wordIdx);
		}
		for (int nmodNum = 0; nmodNum < nmodIdx.size(); nmodNum++) {
			String nmodText;
			String prep; 
			for (int dobjNum = 0; dobjNum < dobjIdx.size(); dobjNum++) {
				if (nmodIdx.get(nmodNum).govIdx.equals(dobjIdx.get(dobjNum).depIdx)) {
					wordIdx = nmodIdx.get(nmodNum).wordIdx;
					nmodText = final_tdl.get(wordIdx).reln().toString();
					prep = nmodText.substring(5);
					dobj.append(final_tdl.get(wordIdx).gov().originalText().toLowerCase() + "-" + prep + 
						 "-" + final_tdl.get(wordIdx).dep().originalText().toLowerCase() + " / ");
				} else {
					wordIdx = dobjIdx.get(dobjNum).wordIdx;
					dobj.append(final_tdl.get(wordIdx).dep().originalText().toLowerCase() + " / ");
				}
			}
			for (int nsbjNum = 0; nsbjNum < nsbjIdx.size(); nsbjNum++)
			{
				if(nmodIdx.get(nmodNum).govIdx.equals(nsbjIdx.get(nsbjNum).depIdx)) {
					wordIdx = nmodIdx.get(nmodNum).wordIdx;
					nmodText = final_tdl.get(wordIdx).reln().toString();
					prep = nmodText.substring(5);
					nsbj.append(final_tdl.get(wordIdx).gov().originalText().toLowerCase() + "-" + prep + "-" + final_tdl.get(wordIdx).dep().originalText().toLowerCase() + " / ");
				} else {
					wordIdx = nsbjIdx.get(nsbjNum).wordIdx;
					nsbj.append(final_tdl.get(wordIdx).dep().originalText().toLowerCase() + " / ");
				}
			}
		}
		System.out.println("SUBJECT :  " + nsbj + "\r\n");
		System.out.println("VERB : " + verb + "\r\n");
		System.out.println("DOBJECT : " + dobj + "\r\n");
		System.out.println("=========================================================");
		// System.out.println("Type dependency list" +tdl);
		System.out.println();
		// System.out.println("after dependency " + parse);
		// You can also use a TreePrint object to print trees and dependencies tree and
		// dependency both are printed
		// TreePrint tp = new TreePrint("penn,typedDependencies"); // penn -> seg tree ,
		// typedDependencies -> Dependecy in TreePrint function
		// System.out.println("printTree function \n");
	}

	private ParserDemo() {
	} // static methods only

}