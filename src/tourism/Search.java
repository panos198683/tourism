package tourism;

import java.time.LocalDate;

public abstract class Search extends Thread{
    protected DB db;
    protected String place;
    protected LocalDate checkin;
    protected LocalDate checkout;
    protected boolean fromFile;
    protected String fname;
    protected String website;
    
    public Search(String place, String checkin, String checkout, DB db, boolean fromFile){
        super();
        this.place = place;
        this.checkin = LocalDate.parse(checkin);
        this.checkout = LocalDate.parse(checkout);
        this.fromFile = fromFile;
        fname = place + ".html";
        this.db = db;
        website = "";
    }
    
}
