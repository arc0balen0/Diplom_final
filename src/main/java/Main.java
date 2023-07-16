import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {

        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        System.out.println(engine.search("бизнес"));
        System.out.println(engine.search("DevOps"));

        SearchServer searchServer = new SearchServer(8989);
        searchServer.start();
    }
}