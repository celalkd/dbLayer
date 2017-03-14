
import dbClasses.DatabaseBuilder;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws IOException {	
        String movieListFileLocation = "C:\\Users\\celalkd\\Documents\\NetBeansProjects\\DatabaseLayer\\resources\\top250_liste_15film.txt";
        String imdbListFileLocation = "C:\\Users\\celalkd\\Documents\\NetBeansProjects\\DatabaseLayer\\resources\\top250_imdb_15film.txt";
        
        String stopWordListENGLocation = "C:\\Users\\celalkd\\Documents\\NetBeansProjects\\DatabaseLayer\\resources\\stopWordListENG.txt";
        String stopWordListTRLocation = "C:\\Users\\celalkd\\Documents\\NetBeansProjects\\DatabaseLayer\\resources\\stopWordListTR.txt";
        
        
        DatabaseBuilder.getDatabaseBuilder().build(movieListFileLocation, imdbListFileLocation, stopWordListENGLocation, stopWordListTRLocation);
        DatabaseBuilder.getDatabaseBuilder().report();
    }	
	
   
}
