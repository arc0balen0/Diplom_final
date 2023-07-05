import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BooleanSearchEngine implements SearchEngine {

    private Map<String, List<PageEntry>> words;

    public BooleanSearchEngine(File pdfsDir) throws IOException {

        List<File> listOfPDFFiles = List.of(Objects.requireNonNull(pdfsDir.listFiles()));

        words = new HashMap<>();

        for (File pdf : listOfPDFFiles) {

            var doc = new PdfDocument(new PdfReader(pdf));

            for (int i = 0; i < doc.getNumberOfPages(); i++) {

                var textOfOnePage = PdfTextExtractor.getTextFromPage(doc.getPage(i + 1));

                var allWordsOnPage = textOfOnePage.split("\\P{IsAlphabetic}+");

                Map<String, Integer> freqs = new HashMap<>();

                for (var word : allWordsOnPage) {
                    if (word.isEmpty()) {
                        continue;
                    }
                    freqs.put(word.toLowerCase(), freqs.getOrDefault(word.toLowerCase(), 0) + 1);
                }
                int count = 0;
                for (var word : freqs.keySet()) {
                    String wordToLowerCase = word.toLowerCase();
                    if (freqs.get(wordToLowerCase) != null) {
                        count = freqs.get(wordToLowerCase);
                        words.computeIfAbsent(wordToLowerCase, k -> new ArrayList<>()).add(new PageEntry(pdf.getName(), i + 1, count));
                    }
                }
                freqs.clear();
            }
        }
    }

    @Override
    public List<PageEntry> search(String word) {

        List<PageEntry> result = new ArrayList<>();
        String wordToLowerCase = word.toLowerCase();
        if (words.get(wordToLowerCase) != null) {
            for (PageEntry pageEntry : words.get(wordToLowerCase)) {
                result.add(pageEntry);
            }
        }
        Collections.sort(result);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BooleanSearchEngine)) return false;
        BooleanSearchEngine that = (BooleanSearchEngine) o;
        return Objects.equals(words, that.words);
    }

    @Override
    public int hashCode() {
        return Objects.hash(words);
    }

    @Override
    public String toString() {
        return "BooleanSearchEngine{" +
                "words=" + words +
                '}';
    }
}
