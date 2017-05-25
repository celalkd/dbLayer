package com.tez.database;

import com.tez.domain.Movie;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Response;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.neo4j.driver.v1.*;

import static org.neo4j.driver.v1.Values.parameters;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class Neo4j {
	public Driver driver ;
	public Session session ;
	String bookmark;
	
	public Neo4j(){
		driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "neo4jj" ) );
		session = driver.session(AccessMode.WRITE);
		
	}
	
	public boolean readGraphData(String url){
		
		StatementResult result;
		
		try ( Session session = driver.session( AccessMode.READ ) )
	    {
	        try ( Transaction tx = session.beginTransaction(bookmark) )
	        {
	        	result = tx.run( "MATCH (a:Link) " +
	        			"WHERE a.url = {url} " +
	    		        "RETURN a.url AS url",
	    		        parameters( "url", url ) );
	        	tx.success();
	            tx.close();
	        }
	        finally
	        {
	            bookmark = session.lastBookmark();
	        }
	    }
		if(result.hasNext()){
			return true;
		}
		else{
			try ( Session session = driver.session( AccessMode.READ ) )
		    {
		        try ( Transaction tx = session.beginTransaction(bookmark) )
		        {
		        	result = tx.run( "MATCH (m:Movie) " +
		        			"WHERE m.url = {url} " +
		    		        "RETURN m.url AS url",
		    		        parameters( "url", url ) );
		        	tx.success();
		            tx.close();
		        }
		        finally
		        {
		            bookmark = session.lastBookmark();
		        }
		    }
			if(result.hasNext()){
				return true;
			}
		}		
		return false;
	}
	public boolean readGraphRealitonship(String url1, String url2){
		StatementResult result;
		try ( Session session = driver.session( AccessMode.READ ) )
		{
			try ( Transaction tx = session.beginTransaction(bookmark) )
		    {
		        result = tx.run( "MATCH p =(:Link {url:{url1}})-[r:level2]-(:Link{url:{url2}}) RETURN p",
		    		       parameters( "url1", url1,"url2", url2 ) );
		        tx.success();
		        tx.close();
		     }
		     finally
		     {
		       bookmark = session.lastBookmark();
		     }
		}
		if(result.hasNext()){
			//System.out.println(result.next());
			return true;
		}
		return false;
	}
	public void insertMovie(Movie movie){
		
		try ( Session session = driver.session( AccessMode.WRITE ) )
		{
		        try ( Transaction tx = session.beginTransaction() )
		        {
		        	String url = movie.getWikiURL_EN();
		    		String movieId = new Integer(movie.getId()).toString();
                                String title = movie.getInfoBox().getTitle();
		    		
		    		tx.run( "CREATE (a:Movie {title:{title}, movieId:{movieId}, url:{url}})", parameters("title", title,"movieId", movieId, "url", url));
                                tx.success();
                                tx.close();
		        }
		        finally
		        {
		            bookmark = session.lastBookmark();
		        }
		}		
		
	}
        public void insertDirector(Movie movie){ 
		try ( Session session = driver.session( AccessMode.WRITE ) )
		{
		        try ( Transaction tx = session.beginTransaction() )
		        {
		        	String name = movie.getInfoBox().getDirector();
                                StatementResult result = tx.run( "MATCH (a:Director) WHERE a.name = {name} RETURN a",
		    		        parameters( "name", name ) );
                                
                                if(!result.hasNext()){
                                    tx.run( "CREATE (a:Director {name:{name}})", parameters("name", name));
                                    
                                }
                                tx.run("MATCH (m:Movie),(d:Director) "
						+ "WHERE m.title = {title} AND d.name ={name} "
						+ "CREATE (d)-[r:directed]->(m)"
                                                , parameters( "title", movie.getInfoBox().getTitle(), "name", name) );
                                
                                
                                tx.success();
                                tx.close();
		        }
		        finally
		        {
		            bookmark = session.lastBookmark();
		        }
		}		
	}
        public void insertStarring(Movie movie){ 
		try ( Session session = driver.session( AccessMode.WRITE ) )
		{
		        try ( Transaction tx = session.beginTransaction() )
		        {
		        	List<String> starList = movie.getInfoBox().getStarring();
                                
                                for(String name : starList){
                                    StatementResult result = tx.run( "MATCH (a:Star) WHERE a.name = {name} RETURN a",
		    		        parameters( "name", name ) );                                
                                    if(!result.hasNext()){
                                        tx.run( "CREATE (a:Star {name:{name}})", parameters("name", name));
                                        
                                        
                                    }
                                    tx.run("MATCH (m:Movie),(s:Star) "
						+ "WHERE m.title = {title} AND s.name ={name} "
						+ "CREATE (s)-[r:acted]->(m)"
                                                , parameters( "title", movie.getInfoBox().getTitle(), "name", name) );
                                }
                                
                                tx.success();
                                tx.close();
		        }
		        finally
		        {
		            bookmark = session.lastBookmark();
		        }
		}		
	}
        public void insertGenre(Movie movie){ 
		try ( Session session = driver.session( AccessMode.WRITE ) )
		{
		        try ( Transaction tx = session.beginTransaction() )
		        {
		        	List<String> genreList = movie.getGenre();
                                
                                for(String name : genreList){
                                    StatementResult result = tx.run( "MATCH (a:Genre) WHERE a.name = {name} RETURN a",
		    		        parameters( "name", name ) );                                
                                    if(!result.hasNext()){
                                        tx.run( "CREATE (a:Genre {name:{name}})", parameters("name", name));
                                        
                                    }
                                    tx.run("MATCH (m:Movie),(g:Genre) "
						+ "WHERE m.title = {title} AND g.name ={name} "
						+ "CREATE (m)<-[r:in_genre]-(g)"
                                                , parameters( "title", movie.getInfoBox().getTitle(), "name", name) );
                                }
                                
                                tx.success();
                                tx.close();
		        }
		        finally
		        {
		            bookmark = session.lastBookmark();
		        }
		}		
	}
	public void writeLink(String url){
		if(!readGraphData(url)){
			try ( Session session = driver.session( AccessMode.WRITE ) )
		    {
		        try ( Transaction tx = session.beginTransaction(bookmark) )
		        {		        	
		    		tx.run( "CREATE (a:Link {url: {url}})", parameters("url", url));
		            tx.success();
		            tx.close();
		        }
		        finally
		        {
		            bookmark = session.lastBookmark();
		        }
		    }
		}
	}
	
	public void connectMovie_Link(String movieUrl, String link){
		
		try ( Session session = driver.session( AccessMode.WRITE )  )
	    {
	        try ( Transaction tx = session.beginTransaction(bookmark) )
	        {		        	
	    		tx.run("MATCH (m:Movie),(l:Link) "
						+ "WHERE m.url = {movieUrl} AND l.url ={link} "
						+ "CREATE (m)-[r:level1]->(l)", parameters( "movieUrl", movieUrl, "link", link) );
	            tx.success();
	            tx.close();
	        }
	        finally
	        {
	            bookmark = session.lastBookmark();
	        }
	    }
	}
	public void connectLink_Link(String link1, String link2){
		if(!readGraphRealitonship(link1, link2)){
			try ( Session session = driver.session( AccessMode.WRITE )  )
			{
			    try ( Transaction tx = session.beginTransaction(bookmark) )
			    {		        	
					tx.run("MATCH (u:Link),(l:Link) "
							+ "WHERE u.url = {link1} AND l.url ={link2} "
							+ "CREATE (u)-[r:level2]->(l)", parameters( "link1", link1, "link2", link2) );
			        tx.success();
			        tx.close();
			    }
			    finally
			    {
			        bookmark = session.lastBookmark();
			    }
			}
		}
	}
	public void createGraph(ArrayList<Movie> movieList){
				
		ArrayList<String> links_depth_1 = new ArrayList<>();
		ArrayList<String> links_depth_2 = new ArrayList<>();
		
		this.cleanDatabase();
				
		for(Movie movie : movieList){

			//System.out.println(movie.getInfoBox().getTitle()+" Neo4j");
			
			if(!movie.getWikiURL_EN().equals("No Url Source")){
				insertMovie(movie);
				links_depth_1 = collectLinks(movie.getWikiURL_EN());
				for(String link1 : links_depth_1){
									
					writeLink(link1);
					connectMovie_Link(movie.getWikiURL_EN(), link1);
					links_depth_2 = collectLinks(link1);
					for(String link2 : links_depth_2){
						writeLink(link2);
						connectLink_Link(link1, link2);
					}
				}
			}
			else {
				//System.out.println(movie.getWikiURL_EN());
			}
		}				
	}
	public void cleanDatabase(){
            try ( Session session = driver.session( AccessMode.READ ) )
		{
                    try ( Transaction tx = session.beginTransaction(bookmark) )
		    {
		        tx.run("MATCH (n)" +
						"OPTIONAL MATCH (n)-[r]-() "+
						"DELETE n,r");
		        tx.success();
		        tx.close();
		     }
		     finally
		     {
		       bookmark = session.lastBookmark();
		     }
		}
	}	
	public ArrayList<String> collectLinks(String wikiUrl){
		
		
		ArrayList<String> urlList = new ArrayList<>();
		
		Response res;
		try {	
			res = Jsoup.connect(wikiUrl).execute();
			String html = res.body();
			Document doc = Jsoup.parseBodyFragment(html);
			Element paragraph = doc.select("#content").first().select("p").first();
			Elements links = paragraph.select("a");
			for(Element e : links){	
				String href = e.attr("href");
				if(!href.startsWith("#") && !href.startsWith("https://")){
                                    
                                    href = URLDecoder.decode(href, "UTF-8");
                                    urlList.add("https://en.wikipedia.org"+href);
				}
				if(href.startsWith("https://")){
                                    href = URLDecoder.decode(href, "UTF-8");
                                    urlList.add(href);
				}
				
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//System.out.println(wikiUrl);
			e.printStackTrace();
		}		
		return urlList;
	}
	public void insertInfoBoxGraph(ArrayList<Movie> movies){
            for(Movie m : movies){
                insertMovie(m);
                insertDirector(m);
                insertStarring(m);
                insertGenre(m);
            }
        }
	
	public String substring(String str){		
		return str.substring(1, str.length()-1);		
	}
}

