package tourism;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class Tourism {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Menu menu = new Menu();
        menu.menu();
        
    }
    

}
