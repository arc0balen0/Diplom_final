import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        File pdfsDir = new File("pdfs");
        BooleanSearchEngine engine = new BooleanSearchEngine(pdfsDir);

        System.out.println(engine.search("бизнес"));
        System.out.println(engine.search("DevOps"));

        SearchServer searchServer = new SearchServer(8989);
        searchServer.start();
    }
}