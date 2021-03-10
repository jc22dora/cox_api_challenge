import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;



public class Async {
    CompletableFuture[] response;

    public void testAsync(List<URI> uris) throws InterruptedException, ExecutionException {
        HttpClient httpClient = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(10))
                        .followRedirects(HttpClient.Redirect.ALWAYS)
                        .build();
        CompletableFuture[] futures = uris.stream()
                            .map(uri -> verifyUri(httpClient, uri))
                            .toArray(CompletableFuture[]::new);     
        CompletableFuture.allOf(futures).join();   
        this.response =  futures;
        
 
        
    }
    private CompletableFuture<String> verifyUri(HttpClient httpClient, URI uri) {
        HttpRequest request = HttpRequest.newBuilder()
                                    .timeout(Duration.ofSeconds(5))
                                    .uri(uri)
                                    .build();
 
        CompletableFuture<String> body = httpClient.sendAsync(request,HttpResponse.BodyHandlers.ofString())
                        .thenApply(HttpResponse::body);

        return body;
                        
}
    
}
