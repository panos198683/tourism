package tourism;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class ExpediaSearch extends Search{
    
    public ExpediaSearch(String place, String checkin, String checkout, DB db, boolean fromFile){
        super(place, checkin, checkout, db, fromFile);
        this.website = "expedia";
        this.fname = website + "_" + place + ".html"; 
    }
    
    public void run(){
        double mean_price = 0, mean_rating = 0, mean_reviews = 0;
        int k = 0, hotels = 0;
        
        try {
                        
            String booking_link = "https://www.expedia.com/Hotel-Search?destination=" + place + 
                "&startDate=" + checkin.getMonthValue() + "%2F" +  +  checkin.getDayOfMonth() + "%2F" + checkin.getYear() + 
                "&endDate=" +  checkout.getMonthValue() + "%2F" +  checkout.getDayOfMonth() + "%2F" +  checkout.getYear() + 
                "&rooms=1&adults=2";

            
            Document doc;
            if(fromFile){
                System.out.println("Reading from file: " + fname);
                File input = new File(fname);
                doc = Jsoup.parse(input, "UTF-8", "http://www.expedia.com/");
            }else{
                System.out.println("Searching expedia.com");
                
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
            Elements hotels_list = doc.select(".results-list li");
            for(Element hotel : hotels_list){
                //skip if not hotel
                if(!hotel.attr("data-stid").equals("")){
                    continue;
                }
                price_value = 0;
                rating_value = 0;
                reviews_value = 0;

                //get title
                Elements title = hotel.select("h3.uitk-type-heading-500");
                //get rating e.g. 4.5/5
                Elements rating = hotel.select("span[data-stid$=content-hotel-reviews-rating]");
                if(!("".equals(rating.text()))){
                    String tmp = rating.text().split("/")[0];
                    rating_value = 2*Double.parseDouble(tmp); //make it out of 10
                }
                //get reviews, e.g. (50 reviews)
                Elements reviews = hotel.select("span[data-stid$=content-hotel-reviews-total]");
                if(!("".equals(reviews.text()))){
                    String tmp = reviews.text().replace("(","").replace(" reviews)","");
                    reviews_value = Integer.parseInt(tmp);
                }
                //get price, eg $100
                Elements price = hotel.select("span[data-stid$=content-hotel-lead-price]");
                if(!("".equals(price.text()))){
                    price_value = Double.parseDouble(price.text().replace("$", ""));
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
