package com.tez.domain;


import java.io.IOException;
import java.util.ArrayList;

public class Archive {
	
	/*
	 * Singleton Class
	 */
	private static Archive  archive   = new Archive();
	private ArrayList<Movie> movieArchive = new ArrayList<Movie>();
	
	//CONSTRUCTORS
	private Archive(){
		
	}	
	public static Archive getArchive() {
		return archive;
	}
	
	//GETTER_SETTER METHODS
	public void setMovieArchive(ArrayList<Movie> movieArchive) {
		this.movieArchive = movieArchive;
	}
	public ArrayList<Movie> getMovieArchive() {
		return this.movieArchive;
	}
	
	//FUNCTIONS
	
	public void getMovies(String content) throws IOException{
				
		String[] blocks = content.split("\\||\\n");	
		for(int i=0; i<blocks.length; i++){	
			if(i%5 == 1){//her bir yeni title'da(5 adet blok var | ile ayr�lan)
								
				Movie movie = new Movie();//filmin title'�n� tutan blok wikipedia linkinin sonuna eklenecek olan uzant� olacak
				String linkExtension = blocks[i];	
				String year = blocks[i+1];//link extensiondan bir sonraki blok year blo�u		
				
				movie.setYear(Integer.parseInt(year)); //year set edildi
				movie.setId(movieArchive.size());	
				
				
				movie.setWikiURL_EN("https://en.wikipedia.org/wiki/"+linkExtension);//ba�lang�� ENG wiki linki
				movie.setActiveWikiLink();//aktif olan film linki bulunur
				movie.setActiveVikiURL();//aktif linkin t�rk�e sayfas� bulunur
				movie.setPlot_ENG();
                                movie.setPlot_TR();
				InfoBox infoBox = new InfoBox(movie.getWikiURL_EN(), movie.getVikiURL_TR());
				movie.setInfoBox(infoBox);				
				
				movieArchive.add(movie);//t�m filmler obje olarak listeye at�ld�
			}
		}
	}
	public void checkMovies(String content) throws IOException{
		
		Integer id_index=0;
		String[] movieRowsIMDB = content.split("\\n");//sat�rlar enter karakterine g�re ayr�l�yor
		
		for(Movie movie : this.movieArchive){
			String[] dataColumns = movieRowsIMDB[id_index].split("\\|");//sat�rlardaki datalar | karakteri ile ayr�l�yor
			InfoBox comparisonInfoBox = new InfoBox();
			
			for(int i=0; i<dataColumns.length; i++){			
				switch(i%7){ //her sat�rda | ile ayr�lm�� 7 adet data blo�u var
					case 1: 
						String title = null;					
						String[] titleParts = dataColumns[i].split("_");
						for(int j=0; j<titleParts.length; j++){
							if(title==null){
								title=titleParts[j];
							}else title += " "+titleParts[j];
						}
						comparisonInfoBox.setTitle(title);
					case 2:
						String director = dataColumns[i];
						comparisonInfoBox.setDirector(director);
						break;
					case 3:
						String[] actors = dataColumns[i].split(",");//her sat�r�n 3.data blo�undaki oyuncular , ile ayr�l�yor
						ArrayList<String> starring = new ArrayList<String>();
						for(int j=0; j< actors.length; j++){
							if(j!=0){//space'i almamak i�in substring ��kart�yoruz ilk actorden sonra
								actors[j]=actors[j].substring(1, actors[j].length());//(string - 0.char)
								starring.add(actors[j]);								
							}
							else starring.add(actors[j]);
						}
						comparisonInfoBox.setStarring(starring);
						break;
					case 4:
						String[] genres = dataColumns[i].split(",");//her sat�r�n 4.data blo�undaki genre , ile ayr�l�yor
						ArrayList<String> genreList = new ArrayList<String>();
						for(int k=0; k< genres.length; k++){
							if(k!=0){//space'i almamak i�in substring ��kart�yoruz ilk genre'dan sonra
								genres[k]=genres[k].substring(1, genres[k].length());
								genreList.add(genres[k]);								
							}
							else genreList.add(genres[k]);
						}
						movie.setGenre(genreList);
						break;
					case 5:
						double rating = Double.parseDouble(dataColumns[i]);
						movie.setRating(rating);
				}
			}
			
			boolean verified = movie.getInfoBox().isEqual(comparisonInfoBox);//y�netmen, oyucnular, title kar��la�t�rmas�
			movie.setVerified(verified);			
			if(verified)
				movie.setVerifySuccess(movie.getVerifySuccess()+1);	
					
			id_index++;
		}
		
	}
	public void writeWordsToFile(String language) throws IOException{
		//dil se�ene�ine g�re ayn� i�lem farkl� kelime listeleri �zerinde yap�l�r
		for(Movie movie : this.getMovieArchive()){
                    //System.out.println(movie.getInfoBox().getTitle());
			if(language.equals("TR")){
				String textBody = movie.setAndReturnContext(movie.getVikiURL_TR(),language);				
				movie.splitContextToWords(textBody, movie.getWordListTr(), language);				
			}
			else if(language.equals("ENG")){
				String textBody = movie.setAndReturnContext(movie.getWikiURL_EN(),language);
				movie.splitContextToWords(textBody, movie.getWordListEng(),language);
			}
		}
		FileIO.getFileIO().writeWordsAndFreqsToFile(language);//ar�iv �zerinden t�m film nesneleri i�in
	}
	
	
	
}