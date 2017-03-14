package dbClasses;

import helperClasses.Movie;
import java.util.ArrayList;
import com.mongodb.*;

public class MongoDB {
	private static MongoDB  mongodb   = new MongoDB();
	private ArrayList<BasicDBObject> docList ;
	private MongoClient mongoClient ;	 
	private DB db ;
	private DBCollection collection;	
	
	//CONSTRUCTORS
	private MongoDB(){
            init("moviesCollection");
	}	
	public static MongoDB getMongoDB() {
		return mongodb;
	}
	
	public void createAndInsertMovieDocs(ArrayList<Movie> movieArchieve){
		this.init("moviesCollection");	
		this.clean();
		for(Movie m : movieArchieve){
			if(m.getVerified()){				
				System.out.println(m.getInfoBox().getTitle()+" Mongodb");				
				BasicDBObject movieDoc = new BasicDBObject();				
				movieDoc.append("_id",m.getId())
				 .append("title", m.getInfoBox().getTitle())
				 .append("director", m.getInfoBox().getDirector())
				 .append("year", m.getYear())
				 .append("starring", m.getInfoBox().getStarring())
				 .append("genre", m.getGenre())
				 .append("rating", m.getRating())
				 .append("wikiURL", m.getWikiURL_EN())
				 .append("vikiURL", m.getVikiURL_TR())
				 /*.append("context_ENG", m.getContext_ENG())
				 .append("context_TR", m.getContext_TR())*/;	
				docList.add(movieDoc);
			}
		}		
		this.collection.insert(docList);//doldurulan doc listesi collectiona insert edilir
	}	
	
	@SuppressWarnings("deprecation")
	public void init(String collectionName){
		this.mongoClient = new MongoClient( "localhost" , 27017 );	 //porta ba�lan�l�r
		this.db = mongoClient.getDB("moviesDatabase");//database al�n�r
		this.collection = db.getCollectionFromString(collectionName);//collection al�n�r
		this.docList = new ArrayList<BasicDBObject>();//bo� doc listesi yarat�l�r
	}
	public void clean(){
		this.collection.remove(new BasicDBObject());
	}
	
	
	
	
	
	
	
	
	/*public void createAndInsertContextDocs(ArrayList<Movie> movieArchieve,String language){
		//ENG ve TR dosyalar� i�indeki context d�k�mananlar�n� mnngodbye ge�irir
		
		//kullan�lacak collection dil se�ene�iyle belirlenip inite g�nderilir	
		if(language.equals("TR"))
			init("contextTR");
		else if(language.equals("ENG"))
			init("contextENG");
		
		for(Movie m : movieArchieve){//her bir movie i�in bo� bir mondogb doc yarat�l�r
			BasicDBObject contextDoc = new BasicDBObject();
			
			contextDoc.append("_id",id)
			.append("title",m.getInfoBox().getTitle())
			.append("context", m.getContext(language));	
			//doc'a movienin context title ve id fieldar� eklenir
			docList.add(contextDoc);//doc bo� listeye eklenir			
			this.id++;
		}		
		this.collection.insert(docList);//dolan liste collectiona insert edilir
		
	}*/
	
}
