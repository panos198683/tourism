package tourism;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class AirbnbSearch extends Search{
    
    public AirbnbSearch(String place, String checkin, String checkout, DB db, boolean fromFile){
        super(place, checkin, checkout, db, fromFile);
        this.website = "airbnb";
        this.fname = website + "_" + place + ".html";         
    }
    
    public void run(){
        double mean_price = 0, mean_rating = 0, mean_reviews = 0;
        int k = 0, hotels = 0;
        
        try {
                        
            String booking_link = "https://www.airbnb.gr/s/" + place + "/homes?tab_id=home_tab&refinement_paths%5B%5D=%2Fhomes&query=" + place + 
                    "&checkin=" + checkin.toString() + "&checkout=" + checkout.toString() + 
                    "&adults=2"; 
                            
            Document doc;
            if(fromFile){
                System.out.println("Reading from file: " + fname);
                File input = new File(fname);
                doc = Jsoup.parse(input, "UTF-8", "http://www.airbnb.gr/");
                hotels = 20;
            }else{
                System.out.println("Searching airbnb.gr");
                
                doc = Jsoup.connect(booking_link).get();
                
                //get number of hotels
                Elements header = doc.select("._1snxcqc");
                String[] header_text = header.text().split(" ");
                boolean flag;
                for(int i=0;i<header_text.length;i++){
                    try{
                        flag = true;
                        hotels = Integer.parseInt(header_text[i]);
                    }catch(Exception ex){
                        flag = false;

                    }
                    if(flag){
                        break;
                    }
                }
                System.out.println(website + " - searching: " + hotels + " hotels");
                
                boolean save = false;
                //save first page for demo
                if(save){
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fname));
                    writer.write(doc.html());
                    writer.close();
                }          
            }

      
            
            k = 0;
            double rating_value, price_value;
            int reviews_value;
            while(k < hotels){
                //hotel list
                Elements hotels_list = doc.select("div[itemprop$=itemListElement]");
                for(Element hotel : hotels_list){

                    price_value = 0;
                    rating_value = 0;
                    reviews_value = 0;

                    //get title
                    String title = hotel.select("a._gjfol0").attr("aria-label");
                    //get rating e.g. "4.5"
                    Elements rating = hotel.select("span._10fy1f8");
                    if(!("".equals(rating.text()))){
                        rating_value = 2*Double.parseDouble(rating.text()); //make it out of 10
                    }
                    //get reviews, e.g. "50 reviews"
                    Elements reviews = hotel.select("span._a7a5sx");
                   if(!("".equals(reviews.text()))){
                        String tmp = reviews.text().replace("(","").replace(")","");
                        reviews_value = Integer.parseInt(tmp);
                    }
                    //get price, eg 100€
                    Elements price = hotel.select("button._ebe4pze");
                    if(!("".equals(price.text()))){
                        String tmp = price.text().split(" ")[0].replace("€","").replace(",","");
                        price_value = Double.parseDouble(tmp);                  
                    }
                    
                    /*
                    //print hotel details (debugging)
                    System.out.print(k + " " + website + " ");
                    System.out.print(title + " ");
                    System.out.print(" " + rating_value);                        
                    System.out.print(" " + reviews_value);
                    System.out.print(" " + price_value);
                    System.out.print("\n");
                    */
                    
                    k = k + 1;
                    mean_price += price_value;
                    mean_rating += rating_value;
                    mean_reviews += reviews_value;
                    
                    
                }
            
                //get next page of results (using offset)
                if(k < hotels){
                    doc = Jsoup.connect(booking_link + "&section_offset=2&items_offset=" + k +"&search_type=pagination").get();
                }            
            
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        mean_price = mean_price / k;
        mean_rating = mean_rating / k;
        mean_reviews = mean_reviews / k;
        hotels = k;
        System.out.println(website + " - Number of hotels: " + hotels + ", Mean price: " + mean_price + ", Mean Rating: " + mean_rating + ", Mean number of reviews: " + mean_reviews);
        
        System.out.println();
        
        db.insert_stats(website, place, checkin.toString(), checkout.toString(), hotels, mean_price, mean_rating, mean_reviews);

    }
}
