package com.tez.core;


import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {	
        String movieListFileLocation = "/Users/celalkd/NetBeansProjects/Database_Layer/resources/top250_liste_15film.txt";
        String imdbListFileLocation = "/Users/celalkd/NetBeansProjects/Database_Layer/resources/top250_imdb_15film.txt";
        
        String stopWordListENGLocation = "/Users/celalkd/NetBeansProjects/Database_Layer/resources/stopWordListENG.txt";
        String stopWordListTRLocation = "/Users/celalkd/NetBeansProjects/Database_Layer/resources/stopWordListTR.txt";
        
        
        DatabaseBuilder.getDatabaseBuilder().build(movieListFileLocation, imdbListFileLocation, stopWordListENGLocation, stopWordListTRLocation);
       // DatabaseBuilder.getDatabaseBuilder().report();
    }	
	
   
}
