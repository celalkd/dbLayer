package com.tez.core;


import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {	
        String movieListFileLocation = "/Users/celalkd/NetBeansProjects/Database_Layer/resources/top250_liste_25film.txt";
        String imdbListFileLocation = "/Users/celalkd/NetBeansProjects/Database_Layer/resources/top250_imdb_25film.txt";
        
        String stopWordListENGLocation = "/Users/celalkd/NetBeansProjects/Database_Layer/resources/stopWordListENG.txt";
        String stopWordListTRLocation = "/Users/celalkd/NetBeansProjects/Database_Layer/resources/stopWordListTR.txt";
//        String movieListFileLocation = "dataset/top250_liste_25film.txt";
//        String imdbListFileLocation = "dataset/top250_imdb_25film.txt";
//
//        String stopWordListENGLocation = "dataset/stopWordListENG.txt";
//        String stopWordListTRLocation = "dataset/stopWordListTR.txt";
        
        DatabaseBuilder.getDatabaseBuilder().build(movieListFileLocation, imdbListFileLocation, stopWordListENGLocation, stopWordListTRLocation);
        DatabaseBuilder.getDatabaseBuilder().report();
    }	
	
   
}
