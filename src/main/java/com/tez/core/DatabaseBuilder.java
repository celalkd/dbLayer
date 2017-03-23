package com.tez.core;

import com.tez.database.MongoDB;
import com.tez.database.Neo4j;
import com.tez.database.Redis;
import com.tez.domain.Movie;
import com.tez.domain.Archive;
import com.tez.domain.FileIO;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;


public class DatabaseBuilder {	
	
        private static DatabaseBuilder  databaseBuilder   = new DatabaseBuilder();
        
        public static DatabaseBuilder getDatabaseBuilder() {
		return databaseBuilder;
	}
        
	//MongoDB mongoDB = MongoDB.getMongoDB();
	Neo4j neo4j = new Neo4j();
	//Redis redis = new Redis();
	
	Archive archieve = Archive.getArchive();
	FileIO fileIO = FileIO.getFileIO();
        
	
	public void build(String movieListFileLocation, String imdbListFileLocation, String stopWordListENGLocation, String stopWordListTRLocation  ){
		try {
                        fileIO.createStopWordList(stopWordListENGLocation, stopWordListTRLocation);
			archieve.getMovies(fileIO.fileToString(movieListFileLocation));//wikiLink, id ve yap�m y�l�
			archieve.checkMovies(fileIO.fileToString(imdbListFileLocation));//infobox(title, director, starring), genre, rating
			archieve.writeWordsToFile("TR");//wordList
			archieve.writeWordsToFile("ENG");                       
                        
						
			//mongoDB.createAndInsertMovieDocs(archieve.getMovieArchive());//mongodb		
			//redis.createRedis(archieve.getMovieArchive());//redis
			//neo4j.createGraph(archieve.getMovieArchive());//neo4j	
                        neo4j.cleanDatabase();
                        for(Movie m : archieve.getMovieArchive()){
                            neo4j.insertMovie(m);
                            neo4j.insertDirector(m);
                            neo4j.insertStarring(m);
                            neo4j.insertGenre(m);
                        }
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
        
        public  void report(){
	Archive archieve = Archive.getArchive();
	ArrayList<Movie> movieArchive = archieve.getMovieArchive();
	String string = 
			"\nIncelenen Film Say?s?= "+movieArchive.size()
			+ "\nIngilizce Kaynak Bulunan Film Say?s?= "+(int)(movieArchive.size()-Movie.noAnyLangSource)
			+ "\nIngilizce ve T�rk�e Kaynak Bulunan Film say?s?= "+(int)Movie.success
			+ "\nTR Link Ba?ar? Oran?= %"+(Movie.success*100)/movieArchive.size()
			+ "\nTR Link Onaylanma Oran?= %"+(Movie.verifySuccess*100)/new Movie().getSuccess();
	System.out.println(string);
    }	
}
