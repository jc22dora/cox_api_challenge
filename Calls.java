import org.json.simple.JSONObject;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.Scanner;
import org.json.simple.parser.JSONParser;


public class Calls {
    public JSONObject getDataObject(String link) {
        JSONObject data_obj = new JSONObject();
        try {
            
            URL url = new URL(link);
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
            data_obj = (JSONObject) parse.parse(inline);
        } catch(Exception e) 
        {
            ;
        }
        return data_obj;
    }
    public void postAnswer(String link, String answer) {
        try {
            
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = answer.getBytes("utf-8");
                os.write(input, 0, input.length);			
            }
            try(BufferedReader br = new BufferedReader( new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            System.out.println(response.toString());
}
            
            conn.connect();
        } catch(Exception e) 
        {
            ;
        }
    }
}
