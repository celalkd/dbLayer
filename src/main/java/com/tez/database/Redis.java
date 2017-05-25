package com.tez.database;



import com.tez.domain.Archive;
import com.tez.domain.Movie;
import com.tez.domain.Word;
import java.io.IOException;
import java.util.ArrayList;



import redis.clients.jedis.*;

public class Redis {
	public Jedis jedis;
	public Connection connection;
	public ArrayList<Integer> keyList = new ArrayList<>();
	
	public Redis(){
		jedis = new Jedis("localhost");
	}
	
	public void createWordFreqStore_ENG(Movie movie, ArrayList<Word> wordList){
		
		String key = new Integer(movie.getId()).toString();
		
		for(Word word : wordList){
			if(word.getFreq()>=5){
				String freqStr = new Integer(word.getFreq()).toString();			
				jedis.lpush(key,(word.getWord()+" "+freqStr) );
			}			
		}		
	}
        public void createWordFreqStore_TR(Movie movie, ArrayList<Word> wordList){
		
                int index = movie.getId()+Archive.getArchive().getMovieArchive().size();
                System.out.println(index);
		String key = new Integer(index).toString();
		
		for(Word word : wordList){
			if(word.getFreq()>=5){
				String freqStr = new Integer(word.getFreq()).toString();			
				jedis.lpush(key,(word.getWord()+" "+freqStr) );
			}			
		}		
	}
	public void createRedis(ArrayList<Movie> movieList) throws IOException{
		
		jedis.flushAll();
                
                
		
		for(Movie movie : movieList){			
			
			//System.out.println(movie.getInfoBox().getTitle()+" Redis ENG");
			movie.setWordLists();//movie'lerin word'lerini kelime-frekans olarak kaydeder
			
			jedis.select(1);
			createWordFreqStore_ENG(movie, movie.getWordListEng());
                        createWordFreqStore_TR(movie, movie.getWordListTr());
		}
	}	
	
	
	
	
	
	
	
	
	
}

