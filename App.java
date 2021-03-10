import java.net.URI;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class App {
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException, ParseException{
        Coxapi coxapi = new Coxapi();
        coxapi.makeConnection();
        coxapi.testResponseCode();
        coxapi.setDatasetId();
        coxapi.getDatasetId();
        coxapi.setVehicles();
        List<URI> uris = coxapi.getURIList();
        Async async = new Async();
        async.testAsync(uris);
        for(int i =0;i<async.response.length;i++){
            String inline = (String) async.response[i].get();
            JSONObject data_obj = new JSONObject();
            JSONParser parse = new JSONParser();
            data_obj = (JSONObject) parse.parse(inline);
            coxapi.findDealerIdAsync(data_obj);
        }
        coxapi.createAnswer();
        }
    }
