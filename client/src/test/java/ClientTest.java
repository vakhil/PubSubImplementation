import com.example.pubsub.client.Client;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;


public class ClientTest {

    @Test
    public void givenGreetingClient_whenServerRespondsWhenStarted_thenCorrect() throws IOException {
        Client client = new Client();
        client.startConnection("127.0.0.1", 8888);
       // String response = client.sendMessage("hello client");
    }

}
