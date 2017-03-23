package com.tez.database;



import com.tez.domain.Movie;
import com.tez.domain.Word;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;



import redis.clients.jedis.*;

public class Redis {
	public Jedis jedis;
	public Connection connection;
	public ArrayList<Integer> keyList = new ArrayList<>();
	
	public Redis(){
		jedis = new Jedis("localhost");
	}
	
	public void createWordFreqStore(Movie movie, ArrayList<Word> wordList){
		
		String key = new Integer(movie.getId()).toString();
		
		for(Word word : wordList){
			if(word.getFreq()>=10){
				String freqStr = new Integer(word.getFreq()).toString();			
				jedis.lpush(key,(word.getWord()+" "+freqStr) );
			}			
		}		
	}
	public void createRedis(ArrayList<Movie> movieList) throws IOException{
		
		jedis.flushAll();
		
		for(Movie movie : movieList){			
			
			System.out.println(movie.getInfoBox().getTitle()+" Redis");
			movie.setWordLists();//movie'lerin word'lerini kelime-frekans olarak kaydeder
			
			jedis.select(0);
			createWordFreqStore(movie, movie.getWordListTr());	
			
			jedis.select(1);
			createWordFreqStore(movie, movie.getWordListEng());
		}
	}	
	
	
	
	
	
	
	
	
	
}

