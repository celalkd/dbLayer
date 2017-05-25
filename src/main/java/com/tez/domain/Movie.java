package com.tez.domain;


import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Connection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Movie {
	
	public static float success;
	public static float verifySuccess;
	public static float noAnyLangSource;		
	
	private int id;
	private ArrayList<String> genre = new ArrayList<>();
	private double rating;
	private String vikiURL_TR;
	private String wikiURL_EN;
	private int year;
	private String context_TR;
	private String context_ENG;
        private String plot_ENG;
        private String plot_TR;

	
	private InfoBox infoBox;
	private boolean verified;
	
	private ArrayList<Word> wordListTr = new ArrayList<Word>();
	private ArrayList<Word> wordListEng = new ArrayList<Word>();
	
	//CONSTRUCTORS
	public Movie(){
		setVikiURL_TR("Kaynak Bulunamad�");//t�rk�e kaynak bulunamazsa bu de�er b�yle kalacakt�r.
	}
	public Movie(int id, String wikiURL_EN, String vikiURL_TR, int year){
		setId(id);
		setWikiURL_EN(wikiURL_EN);
		setVikiURL_TR(vikiURL_TR);
		setYear(year);
	}	
	
	//FUNCTIONS
        public void setPlot_ENG(){
            String plot="";
            try {	
			Connection.Response res = Jsoup.connect(this.wikiURL_EN).execute();
			String html = res.body();
			Document doc = Jsoup.parseBodyFragment(html);
                        
                        Element plot_span_element = doc.getElementById("Plot");
                        Element plot_h2_element = plot_span_element.parent();
                        Element el = plot_h2_element.nextElementSibling();
                        while(!el.tagName().equals("h2")){
                            if(el.tagName().equals("p")){
                                plot = plot+el.text()+"\n";
                            }
                            el = el.nextElementSibling();
                        }
                        this.plot_ENG = plot;
                        
            }catch (IOException ex) {
                Logger.getLogger(Movie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        public void setPlot_TR(){
            String plot="";
            try {	
			Connection.Response res = Jsoup.connect(this.vikiURL_TR).execute();
			String html = res.body();
			Document doc = Jsoup.parseBodyFragment(html);
                        
                        Element plot_span_element=null;
                        
                        if(doc.getElementById("Özet")!=null)
                            plot_span_element = doc.getElementById("Özet");
                        else if(doc.getElementById("Konusu")!=null)
                            plot_span_element = doc.getElementById("Konusu");
                        else if(doc.getElementById("Konu")!=null)
                            plot_span_element = doc.getElementById("Konu");
                        
                        if(plot_span_element!=null){
                            Element plot_h2_element = plot_span_element.parent();
                            Element el = plot_h2_element.nextElementSibling();
                            while(!el.tagName().equals("h2")){
                                if(el.tagName().equals("p")){
                                    plot = plot+el.text()+"\n";
                                }
                                el = el.nextElementSibling();
                            }
                            this.plot_TR = plot;
                        }
                        
            }catch (IOException ex) {
                Logger.getLogger(Movie.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        public String getPlot(){
            return this.plot_ENG;
        }
        public String getPlotTR(){
            return this.plot_TR;
        }
	public void setActiveWikiLink(){
		/*
		 * _(YIL_film), _(film) ve uzant�s�z linkleri 404 hatas� almayana kadar dener, 
		 * eri�ilebilen linki uzant�syla birlikte movie'nin wikiURL_EN field�na set eder.
		 */
		
		FileIO fileIO = FileIO.getFileIO();
		String activeLink = null;

		if(fileIO.check404(this.wikiURL_EN+"_("+this.year+"_film)")){
			activeLink = this.wikiURL_EN+"_("+this.year+"_film)";
		}
		else if(fileIO.check404(this.wikiURL_EN+"_(film)")){
			activeLink = this.wikiURL_EN+"_(film)";
		}
		else if(fileIO.check404(this.wikiURL_EN)){
			activeLink = this.wikiURL_EN;
		}
		else {
			activeLink = "No Url Source";
			setNoAnyLangSource(getNoAnyLangSource() + 1);//ingilizce kaynak yoksa t�rk�e kaynak da ��kmayaca��n� kabul ediyoruz
		}		
		this.setWikiURL_EN(activeLink);
	}
	public void setActiveVikiURL(){
		/*
		 * setActiveWikiLink() methodu �al��t�ktan sonra elimizde movienin eri�ilebilir ve do�ru inglizce linki var
		 * 404 hatas� yani exception gelme ihtimali yok. Bu link jsoup ile parse edilip t�rk�e link i�in kontrol edilir
		 * yarat�l��ta movie nesneleri new Movie() constructor�nda vikiURL_TR olarak "T�RK�E KAYNAK BULUNAMADI" de�erini al�r
		 * method i�inde bu de�erin de�i�mesini bekleriz, e�er de�i�mi�se success 1 atar.
		 */
		Document doc = null;
		try {
			
			if(!this.getWikiURL_EN().equals("No Url Source")){
				
				doc = Jsoup.connect(this.getWikiURL_EN()).get();
				Elements links = doc.select("a[href*=https://tr.");//https://tr. i�eren a-href'leri bul
		    	for(Element element : links){
                                String url = element.attr("href");
                                url = URLDecoder.decode(url, "UTF-8");
		    		this.setVikiURL_TR(url);//zaten 1 tane gelicek vikiURL'ye kaydet
		    	}	
		    	if(!this.getVikiURL_TR().equals("Kaynak Bulunamad�"))
		    		setSuccess(getSuccess() + 1);
	    	}	    	
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	public void setWordLists() throws IOException{
		String textBody = setAndReturnContext(this.getVikiURL_TR(),"TR");				
		this.splitContextToWords(textBody, this.getWordListTr(), "TR");
		
		textBody = setAndReturnContext(this.getWikiURL_EN(),"ENG");				
		this.splitContextToWords(textBody, this.getWordListEng(), "ENG");
	}
	public String setAndReturnContext(String url, String language){
		
		Document doc = null;
                try {
                    doc = Jsoup.connect(url).get();
                }catch (IOException ex) {
                 Logger.getLogger(Movie.class.getName()).log(Level.SEVERE, null, ex);
                }
		String textBody = doc.select("div#mw-content-text").text();
		if(language.equals("TR")){
			this.setContext_TR(textBody);
		}
		else if(language.equals("ENG")){
			this.setContext_ENG(textBody);
		}
		return textBody;
	}
	public void splitContextToWords(String textBody, ArrayList<Word> wordList, String language){
		String[] words = textBody.split("[\\p{Punct}\\s]+");
		for(String word_str : words){
			if(Character.isLetter(word_str.charAt(0)) ){
				searchWordAndIncFreq(word_str,wordList,language);				
			}
		}
		Collections.sort(wordList, new CustomComparator());
	}	
	public void searchWordAndIncFreq(String str, ArrayList<Word> wordList, String language){
		
		str = str.toLowerCase();
		ArrayList<String> selectedLangStopWordList = null;
		
		if(language.equals("ENG"))
			selectedLangStopWordList = FileIO.getFileIO().stopWordListENG;
		else if (language.equals("TR"))
			selectedLangStopWordList = FileIO.getFileIO().stopWordListTR;		
		
		if(selectedLangStopWordList.contains(str)==false)//e�er stop-word de�ilse kelime de�erlenirilmeli
		{			
			boolean find=false;
	        for(Word a_word: wordList){//methoda g�nderilen string yine Methoda g�nderilen wordList'te aran�r
	            if(a_word.getWord().equals(str)){//bu kelime varsa freq artt�r
	            	a_word.incFreq();
	                find=true;
	            }
	        }
	        if(!find){
	            Word a_word = new Word(str);
	            wordList.add(a_word);
	        }
		}
	}
	
	public String toString(){		
		String str = "\n"+this.id+")"+this.infoBox.getTitle()+"("+this.year+")"+this.infoBox.toString()+
						("\nWiki EN: "+this.wikiURL_EN+"\nViki TR: "+this.vikiURL_TR+"\nRating: "+this.getRating())+"\nGenre: ";
		for(String genre : this.getGenre()){
			str = str + genre+", ";
		}
		return str;
	}
	
	//GETTER-STTER METHODLARI

	public ArrayList<String> getGenre() {
		return genre;
	}
	public double getRating() {
		return rating;
	}
	public void setGenre(ArrayList<String> genre) {
		this.genre = genre;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	
	public ArrayList<Word> getWordListTr() {
		return wordListTr;
	}
	public void setWordListTr(ArrayList<Word> wordListTr) {
		this.wordListTr = wordListTr;
	}
	public ArrayList<Word> getWordListEng() {
		return wordListEng;
	}
	public void setWordListEng(ArrayList<Word> wordListEng) {
		this.wordListEng = wordListEng;
	}
	public InfoBox getInfoBox() {
		return infoBox;
	}
	public void setInfoBox(InfoBox infoBox) {
		this.infoBox = infoBox;
	}	
		
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getVikiURL_TR() {
		return vikiURL_TR;
	}
	public void setVikiURL_TR(String vikiURL_TR) {
		this.vikiURL_TR = vikiURL_TR;
	}
	public String getWikiURL_EN() {
		return wikiURL_EN;
	}
	public void setWikiURL_EN(String wikiURL_EN) {
		this.wikiURL_EN = wikiURL_EN;
	}
	public float getSuccess() {
		return success;
	}
	public void setSuccess(float success) {
		Movie.success = success;
	}
	public float getNoAnyLangSource() {
		return noAnyLangSource;
	}
	public void setNoAnyLangSource(float noAnyLangSource) {
		Movie.noAnyLangSource = noAnyLangSource;
	}
	public void setVerified(boolean verified){
		this.verified = verified;
	}
	public boolean getVerified(){
		return this.verified;
	}
	public float getVerifySuccess() {
		return verifySuccess;
	}
	public void setVerifySuccess(float verifySuccess) {
		Movie.verifySuccess = verifySuccess;
	}
	public String getContext_TR() {
		return context_TR;
	}
	public void setContext_TR(String context) {
		this.context_TR = context;
	}
	public String getContext_ENG() {
		return context_ENG;
	}
	public void setContext_ENG(String context) {
		this.context_ENG = context;
	}
	public String getContext(String language) {
		if(language.equals("TR"))
			return context_TR;
		else if(language.equals("ENG"))
			return context_ENG;
		return null;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
        
        
        public class CustomComparator implements Comparator<Word> {
        @Override
        public int compare(Word o1, Word o2) {
            Integer f1 = o1.getFreq();
            Integer f2 = o2.getFreq();

            return f1.compareTo(f2);
        }
}
	
}

