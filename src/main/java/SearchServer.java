import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;

public class SearchServer {

    private final int port;
    private final BooleanSearchEngine engine;

    public SearchServer(int port) throws IOException {
        this.port = port;
        engine = new BooleanSearchEngine(new File("pdfs"));
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(this.port)) {
            System.out.println("\nЗапуск сервера по адресу " + this.port + "... \nСервер запущен...\n");
            while (true) {
                try (
                        Socket clientSocket = serverSocket.accept();
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
                ) {
                    System.out.println("Новое подключение принято!");
                    System.out.println("Адрес клиента: " + clientSocket.getInetAddress() + " , порт: " + clientSocket.getPort());

                    // Считываем текстовый запрос напрямую из входного потока
                    String query = in.readLine();

                    if (query != null && !query.isEmpty()) {
                        List<PageEntry> result = this.engine.search(query);
                        System.out.println(listToJson(result));
                        out.println(listToJson(result));
                    }

                    System.out.println("Сообщение клиента: " + query);
                    out.println("Привет!");
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер!");
            e.printStackTrace();
        }
    }

    public static <T> Object listToJson(List<T> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<T>>() {}.getType();
        return gson.toJson(list, listType);
    }
}
