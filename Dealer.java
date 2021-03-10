import java.util.List;
import java.util.ArrayList;

public class Dealer {
    int dealerId;
    String name;
    List<Vehicle> vehicles = new ArrayList<>(); //list of vehicle objects 
    
    public Dealer(int dealerId, String name, List<Vehicle> vehicles) {
        this.dealerId = dealerId;
        this.name = name;
        this.vehicles = vehicles;
    }
    
}
