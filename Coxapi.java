import java.net.URL;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.JSONException;
import org.json.simple.JSONArray;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import com.google.gson.Gson;

public class Coxapi {
    int responseCode;
    Object datasetId = new Object();
    JSONArray vehicles = new JSONArray();
    JSONArray dealers = new JSONArray();
    Answer answer = new Answer();
    HashMap<Integer, List<Vehicle>> dToV = new HashMap<>();
    public void makeConnection() {
        try {
            URL url = new URL("http://api.coxauto-interview.com/swagger/v1/swagger.json");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            responseCode = conn.getResponseCode();
            setResponseCode(responseCode); 
        }catch(IOException e) {
            System.out.println("error");
        }
    }

    public void setResponseCode(int code) {
        this.responseCode = code;
    }

    public void testResponseCode() {
        if (this.responseCode != 200) {
            throw new RuntimeException("HttpResponseCode: " + this.responseCode);
        } else {
            ;
        }
    }
    public void setDatasetId() throws JSONException {
        try {
            URL url = new URL("http://api.coxauto-interview.com/api/datasetid");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            String inline = "";
            Scanner scanner = new Scanner(url.openStream());
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }

            scanner.close();
            JSONParser parse = new JSONParser();
            JSONObject data_obj = (JSONObject) parse.parse(inline);
            this.datasetId = data_obj.get("datasetId");
        
        } catch(Exception e){
                System.out.println("datasetId error");
            }
        }

    public Object getDatasetId() {
        return this.datasetId;
    }
    public void setVehicles() throws JSONException{
        String link = "http://api.coxauto-interview.com/api/"+this.datasetId.toString()+"/vehicles";
        Calls setVCall = new Calls();
        JSONObject data_obj = setVCall.getDataObject(link);
        JSONArray arr = (JSONArray) data_obj.get("vehicleIds");
        this.vehicles = arr;
    }
    public Vehicle findDealerIdAsync(JSONObject data_obj) {

        int year = (int) (long) data_obj.get("year");
        int dId = (int) (long) data_obj.get("dealerId");
        String model = (String) data_obj.get("model");
        int vId = (int) (long) data_obj.get("vehicleId");
        String make  = (String) data_obj.get("make");
        Vehicle v = new Vehicle(vId, year, make, model);
        this.dToV.computeIfAbsent(dId, k -> new ArrayList<Vehicle>()).add(v);
        return v;
    }

    public List<URI> getURIList() {
        List<URI> uriList = new ArrayList<URI>();
        String link = "http://api.coxauto-interview.com/api/"+this.datasetId.toString()+"/vehicles/";
        for(int i =0;i<this.vehicles.size();i++) {
            String id =  (String) this.vehicles.get(i).toString();
            try {
                URI uri = new URI(link+id);
                uriList.add(uri);
            }catch(Exception e) {
            ;
            }
        }
        return uriList;
    }
    public List<URI> getDealerURIList() {
        Iterator iterator = this.dToV.entrySet().iterator();
        List<URI> uriList = new ArrayList<URI>();
        String link = "http://api.coxauto-interview.com/api/"+this.datasetId.toString()+"/dealers/";
        while (iterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator.next();
            int dId =(int) me2.getKey();
            String id = Integer.toString(dId);
            try {
                URI uri = new URI(link+id);
                uriList.add(uri);
            }catch(Exception e) {
            ;
            }
        }

        return uriList;

    }

    public List<String> getDealerNameList(List<URI> uris) throws InterruptedException, ExecutionException, ParseException {
        List<String> names = new ArrayList<String>();
        Async async = new Async();
        async.testAsync(uris);
        for(int i =0;i<async.response.length;i++) {
            String inline = (String) async.response[i].get();
            JSONObject data_obj = new JSONObject();
            JSONParser parse = new JSONParser();
            data_obj = (JSONObject) parse.parse(inline);
            names.add(data_obj.get("name").toString());
        }
        return names;
    }


    public String getDealerName(int dId) {
        String name = "";
        String link = "http://api.coxauto-interview.com/api/"+this.datasetId.toString()+"/dealers/"+String.valueOf(dId);
        Calls getDealerNameCall = new Calls();
        JSONObject data_obj = getDealerNameCall.getDataObject(link);
        name = (String) data_obj.get("name");
        return name;

    }
    public void createAnswer() throws InterruptedException, ExecutionException, ParseException {
        Answer ans = new Answer();
        Iterator iterator = this.dToV.entrySet().iterator();
        List<URI> uriList = getDealerURIList();
        List<String> names = getDealerNameList(uriList);
        int i =0;///
        while (iterator.hasNext()) {
            Map.Entry me2 = (Map.Entry) iterator.next();
            int dId =(int) me2.getKey();
            List vList = (List) me2.getValue();
            String name = names.get(i);
            Dealer d = new Dealer(dId, name, vList);
            ans.add(d);
            i++;
        }
        Gson gson = new Gson();
        String json = gson.toJson(ans);
        System.out.printf( "JSON: %s", String.valueOf(json) );
        postAnswer(String.valueOf(json));
    }
    public void postAnswer(String json) {
        String link = "http://api.coxauto-interview.com/api/"+this.datasetId.toString()+"/answer";
        Calls c = new Calls();
        c.postAnswer(link, json);
    }
}


    
