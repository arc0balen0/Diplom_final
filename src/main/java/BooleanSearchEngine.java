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
                    String wordToLowerCase = word.toLowerCase();
                    freqs.put(wordToLowerCase, freqs.getOrDefault(wordToLowerCase, 0) + 1);
                }

                for (var entry : freqs.entrySet()) {
                    String word = entry.getKey();
                    int count = entry.getValue();
                    words.computeIfAbsent(word, k -> new ArrayList<>()).add(new PageEntry(pdf.getName(), i + 1, count));
                }
                freqs.clear();
            }
        }

        // Сортировка по числу вхождений на этапе индексации
        for (var entry : words.entrySet()) {
            entry.getValue().sort(Comparator.comparingInt(PageEntry::getCount).reversed());
        }
    }

    @Override
    public List<PageEntry> search(String word) {
        String wordToLowerCase = word.toLowerCase();
        if (words.containsKey(wordToLowerCase)) {
            List<PageEntry> sortedEntries = new ArrayList<>(words.get(wordToLowerCase));
            sortedEntries.sort(Comparator.comparingInt(PageEntry::getCount).reversed());
            return sortedEntries;
        }
        return Collections.emptyList();
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
