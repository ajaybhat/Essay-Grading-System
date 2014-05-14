package com.nlpgrading.grader;


import java.util.ArrayList;

import opennlp.tools.parser.Parse;

import com.nlpgrading.evaluation.Score;

public class Essay {

	private ArrayList<String> sentences;
	private ArrayList<Parse> parsedSentences;
	private ArrayList<String> detectedSentences;
	private Score essayScore;
	private ArrayList<ArrayList<PosTag>> posTags;
	private Integer length;
	
	public ArrayList<ArrayList<PosTag>> getPosTags(){
		return this.posTags;
	}
	
	public void setPosTags(ArrayList<ArrayList<PosTag>> _posTags){
		this.posTags = _posTags;
	}
	
	public ArrayList<String> getSentences(){
		return this.sentences;
	}
	
	public void setSentences(ArrayList<String> _sentences){
		this.sentences = _sentences;
	}
	
	public ArrayList<String> getDetectedSentences(){
		return this.detectedSentences;
	}
	
	public void setDetectedSentences(ArrayList<String> _sentences){
		this.detectedSentences = _sentences;
	}
	
	public ArrayList<Parse> getParsedSentences(){
		return this.parsedSentences;
	}
	
	public void setParsedSentences(ArrayList<Parse> _parsedSentences){
		this.parsedSentences = _parsedSentences;
	}
	
	public Score getEssayScore(){
		return this.essayScore;
	}
	
	public void setEssayScore(Score _score){
		this.essayScore = _score;
	}
	
	public Integer getLength(){
		return this.length;
	}
	
	public void setLength(Integer _len){
		this.length = _len;
	}
	
	public Essay(){
		this.sentences = new ArrayList<String>();
		this.parsedSentences = new ArrayList<Parse>();
		this.essayScore = new Score();
		this.posTags = new ArrayList<ArrayList<PosTag>>();
		this.length = 0;
		this.detectedSentences = new ArrayList<String>();
		//this.rawText = "";
	}
	
	/**
	 * Add a sentence of the essay to the list
	 * @param sentence from the essay
	 * @author pavan
	 */
	public void addSentence(String sentence){
		
		if(this.sentences == null) return;		
		this.sentences.add(sentence);
	}	
	
	public void addParsedSentence(Parse parsedSentence){
		
		if(this.parsedSentences == null) return;		
		this.parsedSentences.add(parsedSentence);
	}	
	
	public void addPosTag(ArrayList<PosTag> postags){
		
		if(this.posTags == null) return;		
		this.posTags.add(postags);
	}	
	
	@Override
	public boolean equals(Object newEssay){
		
	    if (newEssay == null) return false;
	    if (newEssay == this) return true;	    
	    if (!(newEssay instanceof Essay))return false;
	    
	    Essay instance = (Essay)newEssay;
	    
	    return instance.essayScore.equals(this.essayScore) &&
	    		instance.sentences.equals(this.sentences) &&
	    		instance.parsedSentences.equals(this.parsedSentences) &&
	    		instance.posTags.equals(this.posTags);	    		
	}
	
}
