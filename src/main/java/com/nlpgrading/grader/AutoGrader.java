package com.nlpgrading.grader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.Relatedness;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import static com.nlpgrading.grader.PartOfSpeech.CC;
import static com.nlpgrading.grader.PartOfSpeech.COLON;
import static com.nlpgrading.grader.PartOfSpeech.COMMA;
import static com.nlpgrading.grader.PartOfSpeech.DOLLAR;
import static com.nlpgrading.grader.PartOfSpeech.DOT;
import static com.nlpgrading.grader.PartOfSpeech.FW;
import static com.nlpgrading.grader.PartOfSpeech.LBRACKET;
import static com.nlpgrading.grader.PartOfSpeech.LQUOTE;
import static com.nlpgrading.grader.PartOfSpeech.NN;
import static com.nlpgrading.grader.PartOfSpeech.NNS;
import static com.nlpgrading.grader.PartOfSpeech.NP;
import static com.nlpgrading.grader.PartOfSpeech.POUND;
import static com.nlpgrading.grader.PartOfSpeech.RBRACKET;
import static com.nlpgrading.grader.PartOfSpeech.RQUOTE;
import static com.nlpgrading.grader.PartOfSpeech.S;
import static com.nlpgrading.grader.PartOfSpeech.SBAR;
import static com.nlpgrading.grader.PartOfSpeech.TOP;
import static com.nlpgrading.grader.PartOfSpeech.VB;
import static com.nlpgrading.grader.PartOfSpeech.VBD;
import static com.nlpgrading.grader.PartOfSpeech.VBG;
import static com.nlpgrading.grader.PartOfSpeech.VBN;
import static com.nlpgrading.grader.PartOfSpeech.VBP;
import static com.nlpgrading.grader.PartOfSpeech.VBZ;
import static com.nlpgrading.grader.PartOfSpeech.VP;
import static com.nlpgrading.grader.PartOfSpeech.getGenderType;
import static com.nlpgrading.grader.PartOfSpeech.getNounTypes;
import static com.nlpgrading.grader.PartOfSpeech.getNumberType;
import static com.nlpgrading.grader.PartOfSpeech.getPersonType;
import static com.nlpgrading.grader.PartOfSpeech.getPronounTypes;
import static com.nlpgrading.grader.PartOfSpeech.getTenseType;
import static com.nlpgrading.grader.PartOfSpeech.getVerbTypes;
import static com.nlpgrading.grader.PartOfSpeech.valueOf;
import static com.nlpgrading.grader.PartOfSpeech.values;
import static java.util.Arrays.asList;

public class AutoGrader {

	private MaxentTagger stanfordTagger = null;
	private ParserModel pmodel = null;
	private InputStream parsermodelIn = null;
	private InputStream smodel = null;
	private String QID;

	/**
	 * @param qID
	 */
	public AutoGrader(String qID) {
		QID = qID;
	}

	public ArrayList<PosTag> getStanfordPosTags(String text) {

		ArrayList<PosTag> posTags = new ArrayList<>();
		String word;
		String posText;
		int separatorIndex;

		try {
			if ( stanfordTagger == null ) {
				stanfordTagger = new MaxentTagger(
						System.getProperty( "user.dir" )
								+ "/Models/left3words-wsj-0-18.tagger"
				);
			}

			String tagged = stanfordTagger.tagString( text );
			//	System.out.println(tagged);
			for ( String wordTag : tagged.split( " " ) ) {
				separatorIndex = wordTag.indexOf( '/' );
				word = wordTag.substring( 0, separatorIndex );
				posText = wordTag.substring( separatorIndex + 1 );

				posTags.add( new PosTag( word, getPOS( posText ) ) );
			}

			return posTags;
		}
		catch (Exception e) {
			e.printStackTrace();
			System.out.println( "[Error] Part of speech tagging error" );
			return new ArrayList<>();
		}
	}

	public Parse getParseTree(String sentence) {

		try {

			if ( pmodel == null || parsermodelIn == null ) {

				parsermodelIn = new FileInputStream(
						System.getProperty( "user.dir" )
								+ "/Models/en-parser-chunking.bin"
				);
				pmodel = new ParserModel( parsermodelIn );
			}

			Parser parser = ParserFactory.create( pmodel );

			Parse topParses[] = ParserTool.parseLine( sentence, parser, 1 );

			return topParses[0];
		}
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		finally {
			if ( parsermodelIn != null ) {
				try {
					parsermodelIn.close();
				}
				catch (IOException ignored) {
				}
			}
		}
	}

	/**
	 * grade the essay text based on punctuation, capitalization, finite verb
	 * hypothesis
	 */

	public void gradeEssayLength(Essay essay) {

		int length;

		int lines = essay.getSentences().size();
		int verbs = 0;
		int sentencebreaks = 0;
		int localverbs;
		int conjunctions;

		if ( lines == 0 ) {
			essay.getEssayScore().setEssayLengthScore( 0 );
		}

		ArrayList<PosTag> tags;

		for ( int i = 0; i < essay.getSentences().size(); i++ ) {

			localverbs = 0;
			conjunctions = 0;

			if ( essay.getSentences().get( i ).equals( "" ) ) {
				continue;
			}

			tags = essay.getPosTags().get( i );

			for ( PosTag pos : tags ) {

				if ( pos.getPartOfSpeech() == VB
						|| pos.getPartOfSpeech() == VBD
						|| pos.getPartOfSpeech() == VBG
						|| pos.getPartOfSpeech() == VBN
						|| pos.getPartOfSpeech() == VBP
						|| pos.getPartOfSpeech() == VBZ ) {

					localverbs = localverbs + 1;
				}

				if ( pos.getPartOfSpeech() == CC ) {
					conjunctions = conjunctions + 1;
				}

				if ( pos.getPartOfSpeech() == DOT ) {
					sentencebreaks = sentencebreaks + 1;
				}
			}

			if ( localverbs > conjunctions ) {
				verbs = verbs + (localverbs - conjunctions);
			}
			else {
				verbs = verbs + localverbs;
			}
		}

		length = (lines > verbs) ? (lines > sentencebreaks)
				? (lines)
				: (sentencebreaks) : (verbs > sentencebreaks)
				? (verbs)
				: (sentencebreaks);
		essay.setLength( length );

		if ( length >= 6 ) {
			essay.getEssayScore().setEssayLengthScore( 5 );
		}
		else if ( length == 5 ) {
			essay.getEssayScore().setEssayLengthScore( 4 );
		}
		else if ( length == 4 ) {
			essay.getEssayScore().setEssayLengthScore( 3 );
		}
		else if ( length == 3 ) {
			essay.getEssayScore().setEssayLengthScore( 2 );
		}
		else if ( length < 3 && length > 0 ) {
			essay.getEssayScore().setEssayLengthScore( 1 );
		}
		else {
			essay.getEssayScore().setEssayLengthScore( 0 );
		}

		// return essay;
	}

	public PartOfSpeech getPOS(String posText) {
		PartOfSpeech pos = FW;
		try {
			switch ( posText ) {
				case ".":
					pos = DOT;
					break;
				case ",":
					pos = COMMA;
					break;
				case ":":
					pos = COLON;
					break;
				case "$":
					pos = DOLLAR;
					break;
				case "#":
					pos = POUND;
					break;
				case "(":
					pos = LBRACKET;
					break;
				case ")":
					pos = RBRACKET;
					break;
				case "``":
					pos = LQUOTE;
					break;
				case "''":
					pos = RQUOTE;
					break;
				default:
					for ( PartOfSpeech partOfSpeech : values() ) {
						if ( valueOf( posText ).equals( partOfSpeech ) ) {
							pos = partOfSpeech;
						}
					}
					break;
			}

			return pos;
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			pos = FW;
			System.out.println( "[Error] No such part of speech, using default FW" );
			return pos;
		}
	}

	public void segmentEssay(Essay essay) {
		try {
			smodel = new FileInputStream(
					System.getProperty( "user.dir" )
							+ "/Models/en-sent.bin"
			);
			SentenceModel model = new SentenceModel( smodel );
			SentenceDetectorME sentenceDetector = new SentenceDetectorME( model );
			ArrayList<String> newsent = new ArrayList<>();
			for ( int i = 0; i < essay.getSentences().size(); i++ ) {
				newsent.addAll( asList( sentenceDetector.sentDetect( essay.getSentences().get( i ) ) ) );
			}

			essay.setDetectedSentences( newsent );
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if ( smodel != null ) {
				try {
					smodel.close();
				}
				catch (IOException ignored) {
				}
			}
		}
	}

	public void gradeSyntax(Essay essay) {
		try {
			PosTag subject;
			PosTag mainVerb;
			PosTag object;
			Parse node;
			Parse tag;
			Parse top;
			int berrorcount = 0;
			int cerrorcount = 0;
			int aerrorcount = 0;
			int derrorcount = 0;
			int dtotalcount = 0;

			ArrayList<Parse> allS;
			ArrayList<Parse> allVerbs = new ArrayList<>();
			ArrayList<Parse> parse = essay.getParsedSentences();

			int sCount = parse.size();

			ArrayList<String> nouns = getNounTypes();
			ArrayList<String> pronouns = getPronounTypes();
			ArrayList<String> verbs = getVerbTypes();

			for ( Parse aParse : parse ) {
				subject = new PosTag( null, null );
				mainVerb = new PosTag( null, null );
				object = new PosTag( null, null );

				allS = getAllBFS( aParse, S.toString() );
				if ( allS.size() > 1 ) {
					for ( Parse all : allS ) {
						dtotalcount = dtotalcount + 1; // subject,verb,object
						subject = new PosTag( null, null );
						mainVerb = new PosTag( null, null );
						object = new PosTag( null, null );

						node = BFS( all, NP.toString() );

						if ( node != null ) {
							tag = BFS( node, nouns );
							if ( tag == null ) {
								tag = BFS( node, pronouns );
							}
							if ( tag != null ) {
								subject = new PosTag(
										tag.toString(),
										getPOS( tag.getType() )
								);
							}
						}

						node = BFS( all, VP.toString() );
						if ( node != null ) {
							tag = BFS( node, verbs );
							if ( tag != null ) {
								mainVerb = new PosTag(
										tag.toString(),
										getPOS( tag.getType() )
								);
							}

							tag = BFS( node, nouns );
							if ( tag != null ) {
								object = new PosTag(
										tag.toString(),
										getPOS( tag.getType() )
								);
							}
						}

						if ( mainVerb.getPartOfSpeech() != VB ) {
							if ( getPersonType(
									subject.getPartOfSpeech(), subject.getString()
							) !=
									getPersonType(
											mainVerb.getPartOfSpeech(),
											mainVerb.getString()
									) ) {
								berrorcount = berrorcount + 1;
								sCount = sCount + 1;
							}
						}

						// 1d

						if ( subject.getString() == null ) {
							derrorcount = derrorcount + 1;
						}
						if ( object.getString() == null ) {
							derrorcount = derrorcount + 1;
						}
					}
				}
				else {

					dtotalcount = dtotalcount + 1; // subject,verb,object

					node = BFS( aParse, NP.toString() );

					if ( node != null ) {

						tag = BFS( node, nouns );

						if ( tag == null ) {
							tag = BFS( node, pronouns );
						}

						if ( tag != null ) {
							subject = new PosTag(
									tag.toString(),
									getPOS( tag.getType() )
							);
						}
					}

					node = BFS( aParse, VP.toString() );

					if ( node != null ) {

						tag = BFS( node, verbs );

						if ( tag != null ) {
							mainVerb = new PosTag(
									tag.toString(),
									getPOS( tag.getType() )
							);
						}

						tag = BFS( node, nouns );

						if ( tag != null ) {
							object = new PosTag(
									tag.toString(),
									getPOS( tag.getType() )
							);
						}
					}

					// evaluation 1b
					if ( mainVerb.getPartOfSpeech() != VB ) {
						if ( getPersonType(
								subject.getPartOfSpeech(), subject.getString()
						) !=
								getPersonType(
										mainVerb.getPartOfSpeech(),
										mainVerb.getString()
								) ) {
							berrorcount = berrorcount + 1;
						}
					}

					// 1d
					if ( subject.getString() == null ) {
						derrorcount = derrorcount + 1;
					}

					if ( object.getString() == null ) {
						derrorcount = derrorcount + 1;
					}
				}

				// evaluation 1c
				allVerbs = getAllBFS( aParse, verbs );
				int[] tensecounts = new int[4];

				if ( allVerbs.size() > 0 ) {

					for ( int c = 0; c < allVerbs.size(); c++ ) {

						if ( getTenseType(
								getPOS(
										allVerbs.get( c )
												.getType()
								)
						) == Tense.PAST ) {
							tensecounts[0] = tensecounts[0] + 1;
						}
						else if ( getTenseType(
								getPOS(
										allVerbs.get(
												c
										).getType()
								)
						) == Tense.PRESENT_PARTICIPLE ) {
							tensecounts[1] = tensecounts[1] + 1;
						}
						else if ( getTenseType(
								getPOS(
										allVerbs.get(
												c
										).getType()
								)
						) == Tense.PRESENT ) {
							tensecounts[2] = tensecounts[2] + 1;
						}
						else if ( getTenseType(
								getPOS(
										allVerbs.get(
												c
										).getType()
								)
						) == Tense.PAST_PARTICIPLE ) {
							tensecounts[3] = tensecounts[3] + 1;
						}

						int max = 0;

						for ( int tensecount : tensecounts ) {
							if ( tensecount >= max ) {
								max = tensecount;
							}
						}

						cerrorcount = allVerbs.size() - max;
					}

				}
				else {
					cerrorcount = cerrorcount + 1;
					derrorcount = derrorcount + 1;
				}

				// 1d
				top = BFS( aParse, TOP.toString() );
				if ( top != null ) {
					for ( Parse child : top.getChildren() ) {
						dtotalcount = dtotalcount + 1;
						if ( !child.getType().equals( S.toString() ) ) {
							// System.out.println("e " + child.toString() + "-"
							// +
							// child.getType());
							derrorcount = derrorcount + 1;
						}
					}
				}

				assert top != null;
				if ( top.toString().contains( "because" )
						&& BFS( aParse, SBAR.toString() ) != null ) {
					derrorcount = derrorcount + 1;
				}
			}

			// evaluation 1a
			ArrayList<Rule> rules = Rule.getSyntaxRules();

			for ( int p = 0; p < essay.getPosTags().size(); p++ ) {

				if ( essay.getPosTags().get( p ).get( 0 ).getPartOfSpeech() == VB
						|| essay.getPosTags().get( p ).get( 0 ).getPartOfSpeech() == VBD
						|| essay.getPosTags().get( p ).get( 0 ).getPartOfSpeech() == VBG
						|| essay.getPosTags().get( p ).get( 0 ).getPartOfSpeech() == VBN
						|| essay.getPosTags().get( p ).get( 0 ).getPartOfSpeech() == VBP
						|| essay.getPosTags().get( p ).get( 0 ).getPartOfSpeech() == VBZ ) {
					aerrorcount = aerrorcount + 1;
				}

				for ( int q = 0; q < essay.getPosTags().get( p ).size() - 1; q++ ) {

					for ( Rule rule : rules ) {
						if ( essay.getPosTags().get( p ).get( q ).getPartOfSpeech() == rule.getPos1()
								&& essay.getPosTags().get( p ).get( q + 1 )
								.getPartOfSpeech() == rule
								.getPos2() ) {
							aerrorcount = aerrorcount + 1;
						}

					}
				}
			}

			// System.out.println(derrorcount / (float)dtotalcount);
			essay.getEssayScore().setSubjectVerbAgreementScore(
					essay.getEssayScore().computeScore1b( berrorcount, sCount )
			);
			essay.getEssayScore().setVerbUsageScore(
					essay.getEssayScore().computeScore1c(
							cerrorcount,
							allVerbs.size()
					)
			);
			essay.getEssayScore().setWordOrderScore(
					essay.getEssayScore().computeScore1a(
							aerrorcount,
							essay.getLength()
					)
			);
			essay.getEssayScore().setSentenceFormationScore(
					essay.getEssayScore().computeScore1d(
							derrorcount,
							dtotalcount
					)
			);

		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Parse BFS(Parse graph, String searchText) {
		Queue<Parse> queue = new LinkedList<>();
		queue.add( graph );
		while ( !queue.isEmpty() ) {
			Parse node = queue.remove();
			if ( node.getType().equals( searchText ) ) {
				return node;
			}

			for ( int i = 0; i < node.getChildCount(); i++ ) {
				queue.add( node.getChildren()[i] );
			}
		}

		return null;
	}

	public ArrayList<Parse> getAllBFS(Parse graph, String searchText) {
		Queue<Parse> queue = new LinkedList<>();
		ArrayList<Parse> ret = new ArrayList<>();
		queue.add( graph );
		while ( !queue.isEmpty() ) {
			Parse node = queue.remove();
			if ( node.getType().equals( searchText ) ) {
				ret.add( node );
			}

			for ( int i = 0; i < node.getChildCount(); i++ ) {

				queue.add( node.getChildren()[i] );
			}
		}

		return ret;
	}

	public ArrayList<Parse> getAllBFS(Parse graph, ArrayList<String> searchText) {

		Queue<Parse> queue = new LinkedList<>();
		ArrayList<Parse> ret = new ArrayList<>();

		queue.add( graph );

		while ( !queue.isEmpty() ) {

			Parse node = queue.remove();

			if ( searchText.contains( node.getType() ) ) {
				ret.add( node );
			}

			for ( int i = 0; i < node.getChildCount(); i++ ) {

				queue.add( node.getChildren()[i] );
			}
		}

		return ret;
	}

	public Parse BFS(Parse graph, ArrayList<String> searchText) {

		Queue<Parse> queue = new LinkedList<>();

		queue.add( graph );

		while ( !queue.isEmpty() ) {

			Parse node = queue.remove();

			if ( searchText.contains( node.getType() ) ) {
				return node;
			}

			for ( int i = 0; i < node.getChildCount(); i++ ) {

				queue.add( node.getChildren()[i] );
			}
		}

		return null;
	}

	public void gradeTopicCoherence(Essay essay) {
		int b2ErrorCount = 0;

		ArrayList<String> commonNounList = new ArrayList<>();

		ArrayList<String> commonNounTypes = new ArrayList<>();
		commonNounTypes.add( NN.toString() );
		commonNounTypes.add( NNS.toString() );

		ArrayList<Parse> parse = essay.getParsedSentences();

		ArrayList<Parse> allCommonNouns;

		for ( Parse aParse : parse ) {
			allCommonNouns = getAllBFS( aParse, commonNounTypes );
			for ( Parse allCommonNoun : allCommonNouns ) {
				commonNounList.add( allCommonNoun.toString() );
			}
		}

		ArrayList<String> storeWordList = new ArrayList<>();
		try {
			String wordListDirPath = System.getProperty( "user.dir" )
					+ "/data/Keywords/" + QID + ".txt";
			FileInputStream fstream = new FileInputStream( wordListDirPath );
			// Get the words from file
			DataInputStream in = new DataInputStream( fstream );
			BufferedReader br = new BufferedReader( new InputStreamReader( in ) );
			String strLine;
			// Read File Line By Line
			while ( (strLine = br.readLine()) != null ) {
				storeWordList.add( strLine );
			}
			in.close();
		}
		catch (Exception e) {
			// Catch exception if any
			System.err.println( "Error: " + e.getMessage() );
		}

		// Search for common nouns in file
		for ( String key : commonNounList ) {

			key = key.replaceAll( "[^A-Za-z]+", "" ).trim().toLowerCase();

			if ( isTopicCoherent( storeWordList, key ) ) {

				b2ErrorCount++;
			}
		}

		// System.out.println(b2ErrorCount / (float)commonNounList.size());
		essay.getEssayScore().setTopicAdherenceScore(
				essay.getEssayScore().computeScore2b(
						b2ErrorCount,
						commonNounList.size()
				)
		);

	}

	private boolean isTopicCoherent(ArrayList<String> list, String text) {

		// regular expression to handle aprostrophes and period.
		/*
		 * String wordnet_dir = System.getProperty("user.dir") +
		 * "/Models/wordnet3.0/"; System.setProperty("wordnet.database.dir",
		 * wordnet_dir); WordNetDatabase wn = WordNetDatabase.getFileInstance();
		 * 
		 * Synset[] synsets = wn.getSynsets("relative",SynsetType.NOUN);
		 * NounSynset nounSynset = (NounSynset) synsets[0];
		 * 
		 * for (NounSynset hypernym : nounSynset.getHypernyms()) {
		 * //System.out.println(hypernym.); System.out.println(); }
		 */

		text = text.replaceAll( "[^A-Za-z]+", "" );
		// list.contains(text) || !hypernymCheck(text) ||
		// return !(hypernymCheck(text)); //

		return !(list.contains( text ) || !hypernymCheck( text ) || !meronymCheck( text ) || isKeywordPresent(
				text,
				list
		));
	}

	private boolean isKeywordPresent(String key, ArrayList<String> list) {
		for ( String t : list ) {
			ILexicalDatabase db = new NictWordNet();
			WS4JConfiguration.getInstance().setMFS( true );
			RelatednessCalculator rc = new Lin( db );

			List<POS[]> posPairs = rc.getPOSPairs();
			double maxScore = -1D;

			for ( POS[] posPair : posPairs ) {
				List<Concept> synsets1 = (List<Concept>) db.getAllConcepts( key, posPair[0].toString() );
				List<Concept> synsets2 = (List<Concept>) db.getAllConcepts( t, posPair[1].toString() );

				for ( Concept synset1 : synsets1 ) {
					for ( Concept synset2 : synsets2 ) {
						Relatedness relatedness = rc.calcRelatednessOfSynset( synset1, synset2 );
						double score = relatedness.getScore();
						if ( score > maxScore ) {
							maxScore = score;
						}
					}
				}
			}
			if ( maxScore > 0.7 ) {
				return true;
			}
		}
		return false;
	}

	private boolean hypernymCheck(String text) {

		String wordnet_dir = System.getProperty( "user.dir" )
				+ "/Models/wordnet3.0/";
		System.setProperty( "wordnet.database.dir", wordnet_dir );
		WordNetDatabase wn = WordNetDatabase.getFileInstance();
		int counter = 0;

		edu.smu.tspell.wordnet.Synset[] textSynsets = wn.getSynsets(
				text,
				SynsetType.NOUN
		);

		if ( textSynsets.length <= 0 ) {
			return true;
		}

		NounSynset nounSynset = (NounSynset) textSynsets[0];

		if ( nounSynset.getHypernyms().length <= 0 ) {
			return true;
		}

		String[] wordforms = nounSynset.getHypernyms()[0].getWordForms();

		if ( wordforms.length <= 0 ) {
			return true;
		}

		while ( !wordforms[0].equals( "entity" ) ) {

			counter = counter + 1;

			if ( counter >= 6 ) {
				break;
			}

			String wordform = wordforms[0];
			// System.out.println(wordform);


			textSynsets = wn.getSynsets( wordform, SynsetType.NOUN );

			if ( textSynsets.length <= 0 ) {
				break;
			}

			nounSynset = (NounSynset) textSynsets[0];

			if ( nounSynset.getHypernyms().length <= 0 ) {
				break;
			}

			wordforms = nounSynset.getHypernyms()[0].getWordForms();

			if ( wordforms.length <= 0 ) {
				break;
			}
		}

		return true;
	}

	private boolean meronymCheck(String text) {

		String wordnet_dir = System.getProperty( "user.dir" )
				+ "/Models/wordnet3.0/";
		System.setProperty( "wordnet.database.dir", wordnet_dir );
		WordNetDatabase wn = WordNetDatabase.getFileInstance();
		ArrayList<String> allmeronyms = new ArrayList<>();
		boolean error = true;

		Synset[] textSynsets = wn.getSynsets( "person", SynsetType.NOUN );

		if ( textSynsets.length <= 0 ) {
			return true;
		}

		NounSynset nounSynset = (NounSynset) textSynsets[0];

		if ( nounSynset.getPartMeronyms().length <= 0 ) {
			return true;
		}

		allmeronyms.addAll(
				asList(
						nounSynset.getPartMeronyms()[0]
								.getWordForms()
				)
		);

		textSynsets = wn.getSynsets( "family", SynsetType.NOUN );

		if ( textSynsets.length <= 0 ) {
			return true;
		}

		nounSynset = (NounSynset) textSynsets[0];

		if ( nounSynset.getPartMeronyms().length <= 0 ) {
			return true;
		}

		allmeronyms.addAll(
				asList(
						nounSynset.getPartMeronyms()[0]
								.getWordForms()
				)
		);

		for ( String word : allmeronyms ) {

			if ( text.equals( word ) ) {
				// System.out.println(text);
				// System.out.println(word);
				error = false;
				break;
			}
		}
		// System.out.println("--");
		return error;
	}

	public void gradeTextCoherence(Essay essay) {

		// 2a
		ArrayList<Parse> parse = essay.getParsedSentences();

		// get all pronouns in the sentence
		ArrayList<String> pronounTypes = getPronounTypes();
		ArrayList<Parse> allPronouns;
		ArrayList<Parse> allNouns;

		float errorcount_2a = 0;
		int bonus_2a = 0;
		int size = 0;
		boolean conjunction = false;

		ConcurrentLinkedQueue<EntityGen> entities = new ConcurrentLinkedQueue<>();

		ArrayList<String> nounTypes = new ArrayList<>();
		// nounTypes.add(PartOfSpeech.NNP.toString());
		// nounTypes.add(PartOfSpeech.NNPS.toString());
		nounTypes.add( NN.toString() );
		nounTypes.add( NNS.toString() );

		for ( Parse aParse : parse ) {

			allPronouns = getAllBFS( aParse, pronounTypes );
			allNouns = getAllBFS( aParse, nounTypes );

			if ( BFS( aParse, CC.toString() ) != null ) {
				conjunction = true;
			}

			size = size + allPronouns.size();

			for ( int j = 0; j < allPronouns.size(); j++ ) {

				PosTag pronoun = new PosTag(
						allPronouns.get( j ).toString(),
						getPOS( allPronouns.get( j ).getType() )
				);

				if ( getPersonType(
						pronoun.getPartOfSpeech(),
						pronoun.getString()
				) == Person.THIRD ) {
					// bonus
					bonus_2a = bonus_2a + 1;
				}
				else if ( getPersonType(
						pronoun.getPartOfSpeech(), pronoun.getString()
				) == Person.SECOND ) {
					// penalize and remove from list
					errorcount_2a = errorcount_2a + 1;
					allPronouns.remove( j );
				}
				else {
					// remove from list
					allPronouns.remove( j );
				}
			}

			// queue of entities and their generation - 1,2,3....
			// remove from queue if generation > 2
			// if antecedent, remove entity from queue, re-add to queue with
			// generation current
			// if pronoun without antecedent, penalise
			// ambiguous antecedent, less errornous

			for ( EntityGen eg : entities ) {
				eg.setGeneration( eg.getGeneration() + 1 );

				if ( eg.getGeneration() > 2 ) {
					entities.remove( eg );
				}
			}

			for ( Parse pn : allNouns ) {
				for ( EntityGen eg : entities ) {
					if ( eg.getEntity().equals( pn.toString() ) ) {
						// System.out.println("removing - " + eg.getEntity());
						entities.remove( eg );
					}
				}

				entities.add( new EntityGen( pn.toString(), 0 ) );
			}

			for ( Parse allPronoun : allPronouns ) {

				Stack<EntityGen> antecedents;
				antecedents = hasAntecedent(
						allPronoun, entities,
						conjunction
				);

				if ( antecedents.isEmpty() ) {
					errorcount_2a = errorcount_2a + 1;
				}
				else if ( antecedents.size() > 1 ) {
					errorcount_2a = errorcount_2a
							+ (1 / (float) antecedents.size());
				}

				EntityGen candidateAntecedent;

				if ( antecedents.size() > 0 ) {
					candidateAntecedent = antecedents.firstElement();

					entities.remove( candidateAntecedent );

					EntityGen neweg = new EntityGen(
							candidateAntecedent.getEntity(), 0
					); // current
					entities.add( neweg );
				}
			}
		}
		essay.getEssayScore().setCoherenceScore(
				essay.getEssayScore().computeScore2a(
						errorcount_2a,
						size + bonus_2a
				)
		);

	}

	private Stack<EntityGen> hasAntecedent(
			Parse parse,
			ConcurrentLinkedQueue<EntityGen> entities, boolean conjuction) {

		Stack<EntityGen> en = new Stack<>();

		for ( EntityGen eg : entities ) {

			if ( getGenderType( parse.toString() ) ==
					getGenderType( eg.getEntity() )
					&& getNumberType( getPOS( parse.getType() ), "" ) ==
					getNumberType( null, eg.getEntity() ) ) {

				en.add( eg );
			}

			if ( getNumberType( getPOS( parse.getType() ), "" ) !=
					getNumberType( null, eg.getEntity() ) && conjuction ) {
				if ( !en.contains( eg ) ) {
					en.add( eg );
				}
			}

		}

		return en;
	}
}
