package tourism;

import java.util.Scanner;

public class Menu {
    
    public void menu(){
        Scanner sc = new Scanner(System.in);
        int choice;
        do{
            System.out.println("1. Demo search (using stored files)");
            System.out.println("2. Search");
            System.out.println("0. Exit");
            choice = Integer.parseInt(sc.nextLine());
            switch (choice){
                case 1:
                    demo();
                    break;
                case 2:
                    search();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Wrong choice!");
            }
        }while(choice != 0);
    }
    
    public void demo(){
        String place = "naxos";
        String checkin = "2020-08-07";
        String checkout = "2020-08-11";
        System.out.println("Demo search: 'naxos' from '2020-08-07' to '2020-08-11'");
        
        
        DB db = new DB();
        db.connect();
        
        Search[] searchers = new Search[4];
        
        searchers[0] = new BookingSearch(place, checkin, checkout, db, true);
            
        searchers[1] = new ExpediaSearch(place, checkin, checkout, db, true);
        
        searchers[2] = new HotelsSearch(place, checkin, checkout, db, true);
        
        searchers[3] = new AirbnbSearch(place, checkin, checkout, db, true);

        for(int i=0;i<4;i++){
            searchers[i].start();
        }
        
        //wait to finish
        //in this thread we could use user input isntead of wating to  interrupt threads and stop searching
        for(int i=0;i<4;i++){
            try {
                searchers[i].join();
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        //print total results
        db.get_stats(place, checkin, checkout);
        
        db.close();
    }
    
    
    public void search(){
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Give place name: ");
        String place = sc.nextLine();
        System.out.println("Give checkin date (YYYY-MM-DD, e.g. 2020-08-31): ");
        String checkin = sc.nextLine();
        System.out.println("Give checkout date (YYYY-MM-DD, e.g. 2020-08-31): ");
        String checkout = sc.nextLine();
            
        DB db = new DB();
        db.connect();
        
        Search[] searchers = new Search[4];
        
        searchers[0] = new BookingSearch(place, checkin, checkout, db, false);
            
        searchers[1] = new ExpediaSearch(place, checkin, checkout, db, false);
        
        searchers[2] = new HotelsSearch(place, checkin, checkout, db, false);
        
        searchers[3] = new AirbnbSearch(place, checkin, checkout,  db, false);
        
        for(int i=0;i<4;i++){
            searchers[i].start();
        }
        
        //wait to finish
        //in this thread we could use user input isntead of wating to  interrupt threads and stop searching
        for(int i=0;i<4;i++){
            try {
                searchers[i].join();
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
        
        //print total results
        db.get_stats(place, checkin, checkout);
        
        db.close();
    }
}
