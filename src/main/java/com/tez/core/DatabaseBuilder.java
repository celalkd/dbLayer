package com.tez.core;

import com.tez.database.MongoDB;
import com.tez.database.Neo4j;
import com.tez.database.Redis;
import com.tez.domain.Movie;
import com.tez.domain.Archive;
import com.tez.domain.FileIO;
import java.io.IOException;
import java.util.ArrayList;


public class DatabaseBuilder {	
	
        private static DatabaseBuilder  databaseBuilder   = new DatabaseBuilder();
        
        public static DatabaseBuilder getDatabaseBuilder() {
		return databaseBuilder;
	}
        
	MongoDB mongoDB = MongoDB.getMongoDB();
	Neo4j neo4j = new Neo4j();
	Redis redis = new Redis();
	
	Archive archieve = Archive.getArchive();
	FileIO fileIO = FileIO.getFileIO();
        
	
	public void build(String movieListFileLocation, String imdbListFileLocation, String stopWordListENGLocation, String stopWordListTRLocation  ){
		try {
                        fileIO.createStopWordList(stopWordListENGLocation, stopWordListTRLocation);
			archieve.getMovies(fileIO.fileToString(movieListFileLocation));//wikiLink, id ve yap�m y�l�
			archieve.checkMovies(fileIO.fileToString(imdbListFileLocation));//infobox(title, director, starring), genre, rating
			archieve.writeWordsToFile("TR");//wordList
			archieve.writeWordsToFile("ENG");                       
                        
						
 			mongoDB.createAndInsertMovieDocs(archieve.getMovieArchive());//mongodb		
			redis.createRedis(archieve.getMovieArchive());//redis
				
                        neo4j.cleanDatabase();
                        neo4j.insertInfoBoxGraph(archieve.getMovieArchive());
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
        
        public  void report(){
	Archive archieve = Archive.getArchive();
	ArrayList<Movie> movieArchive = archieve.getMovieArchive();
	String string = 
			"\nIncelenen Film Sayısı= "+movieArchive.size()
			+ "\nIngilizce Kaynak Bulunan Film Say?s?= "+(int)(movieArchive.size()-Movie.noAnyLangSource)
			+ "\nIngilizce ve Türkçe Kaynak Bulunan Film sayısı= "+(int)Movie.success
			+ "\nTürkçe Link Başarı Oranı= %"+(Movie.success*100)/movieArchive.size()
			+ "\nTürkçe Link Onaylanma Oranı= %"+(Movie.verifySuccess*100)/new Movie().getSuccess();
	System.out.println(string);
    }	
}
