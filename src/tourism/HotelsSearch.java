package tourism;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class HotelsSearch extends Search{
    
    public HotelsSearch(String place, String checkin, String checkout, DB db, boolean fromFile){
        super(place, checkin, checkout, db, fromFile);
        this.website = "hotels";
        this.fname = website + "_" + place + ".html";         
    }
    
    public void run(){
        double mean_price = 0, mean_rating = 0, mean_reviews = 0;
        int k = 0, hotels = 0;
        
        try {
                        
            String booking_link = "https://www.hotels.com/search.do?&q-destination=" + place +
                    "&q-check-in=" + checkin.toString() + "&q-check-out=" + checkout.toString() + 
                    "&q-rooms=1&q-room-0-adults=2&q-room-0-children=0"; 
                
            
            Document doc;
            if(fromFile){
                System.out.println("Reading from file: " + fname);
                File input = new File(fname);
                doc = Jsoup.parse(input, "UTF-8", "http://www.hotels.com/");
            }else{
                System.out.println("Searching hotels.com");

                doc = Jsoup.connect(booking_link).get();

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
            //hotel list
            Elements hotels_list = doc.select("li.hotel");
            for(Element hotel : hotels_list){
                
                price_value = 0;
                rating_value = 0;
                reviews_value = 0;

                //get title
                String title = hotel.attr("data-title");
                //get rating e.g. "Exceptional 9,5"
                Elements rating = hotel.select(".guest-reviews-badge");
                if(!("".equals(rating.text()))){
                    String[] tmp = rating.text().split(" ");
                    String tmp2 = tmp[tmp.length-1].replace(",",".");
                    rating_value = Double.parseDouble(tmp2);
                }
                //get reviews, e.g. "50 hotels.com geuest reviews"
                Elements reviews = hotel.select(".guest-reviews-link span.small-view");
                if(!("".equals(reviews.text()))){
                    String tmp = reviews.text().split(" ")[0];
                    reviews_value = Integer.parseInt(tmp);
                }
                //get price, eg 100€
                Elements price = hotel.select(".price-link");
                if(!("".equals(price.text()))){
                    String tmp = price.text().split("€")[0]; //if some comments eg discount
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
