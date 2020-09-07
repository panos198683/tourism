package tourism;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class BookingSearch extends Search{
    
    public BookingSearch(String place, String checkin, String checkout, DB db, boolean fromFile){
        super(place, checkin, checkout, db, fromFile);
        this.website = "booking";
        this.fname = website + "_" + place + ".html"; 
    }
    
    public void run(){
        double mean_price = 0, mean_rating = 0, mean_reviews = 0;
        int k = 0, hotels = 0;
        
        try {
                        
            String booking_link = "https://www.booking.com/searchresults.el.html?ss=" + place + 
                "%2C+Greece&checkin_year=" +  checkin.getYear() + "&checkin_month=" +  checkin.getMonthValue() + "&checkin_monthday=" +  checkin.getDayOfMonth() + 
                "&checkout_year=" +  checkout.getYear() + "&checkout_month=" +  checkout.getMonthValue() + "&checkout_monthday=" +  checkout.getDayOfMonth() + 
                "&group_adults=2&group_children=0&no_rooms=1&dest_type=region&rows=100";

            
            Document doc;
            if(fromFile){
                System.out.println("Reading from file: " + fname);
                File input = new File(fname);
                doc = Jsoup.parse(input, "UTF-8", "http://www.booking.com/");
                hotels = 25;
            }else{
                System.out.println("Searching booking.com");

                doc = Jsoup.connect(booking_link).get();

                Elements header = doc.select(".sr_header ").select("h1");
                //get number of hotels
                String[] header_text = header.text().split(" ");
                hotels = Integer.parseInt(header_text[2].replace(".",""));
                if(hotels > 500){
                    hotels = 500; //too slow else..
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
            while( k < hotels){
                //hotel list
                Elements hotels_list = doc.select("#hotellist_inner div");
                for(Element hotel : hotels_list){
                    if(hotel.hasClass("sr_item")){ //only for hotels
                        price_value = 0;
                        rating_value = 0;
                        reviews_value = 0;
                        
                        //get title
                        Elements title = hotel.select(".sr-hotel__name"); //getElementByClass
                        //get rating eg 8,5
                        Elements rating = hotel.select(".bui-review-score__badge");
                        if(!("".equals(rating.text()))){
                            rating_value = Double.parseDouble(rating.text().replace(",","."));
                        }
                        //get number reviews eg 100 σχόλια
                        Elements reviews = hotel.select(".bui-review-score__text");
                        if(!("".equals(reviews.text()))){
                            reviews_value = Integer.parseInt(reviews.text().replace(" σχόλια","").replace(".",""));
                        }
                        //get price eg € 1.500,50
                        Elements price = hotel.select(".bui-price-display__value");
                        if(!("".equals(price.text()))){
                            price_value = Double.parseDouble(price.text().replace("€ ", "").replace(".","").replace(",","."));
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
                }

                //get next page of results (using offset)
                if(k < hotels){
                    doc = Jsoup.connect(booking_link + "&offset=" + k).get();
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        mean_price = mean_price / k;
        mean_rating = mean_rating / k;
        mean_reviews = mean_reviews / k;
        System.out.println(website + " - Number of hotels: " + hotels + ", Mean price: " + mean_price + ", Mean Rating: " + mean_rating + ", Mean number of reviews: " + mean_reviews);
        
        db.insert_stats(website, place, checkin.toString(), checkout.toString(), hotels, mean_price, mean_rating, mean_reviews);

    }
}
