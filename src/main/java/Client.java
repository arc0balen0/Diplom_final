import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {

    public static void main(String[] args) throws IOException {
        try (
                Socket socket = new Socket("localhost", 8989);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            out.println("{\"word\": \"Бизнес\"}");//
            System.out.println(jsonToList(in.readLine()));//
            out.println("{\"word\": \"DevOps\"}");
            System.out.println(jsonToList(in.readLine()));
        }
    }

    public static List<PageEntry> jsonToList(String json) {

        List<PageEntry> list = new ArrayList<>();
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(json);
            JSONArray jsonArray = (JSONArray) obj;

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();

            for (Object oneObject : jsonArray) {

                JSONObject jsonObject = (JSONObject) oneObject;
                PageEntry pageEntry = gson.fromJson(String.valueOf(jsonObject), PageEntry.class);
                list.add(pageEntry);
            }

        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        return list;
    }
}
