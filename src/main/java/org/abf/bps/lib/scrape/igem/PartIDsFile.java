package org.abf.bps.lib.scrape.igem;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.Closeable;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PartIDsFile implements Closeable {

    private final String FILENAME = "iGemParts.csv";
    private CSVWriter csvWriter;
    private CSVReader csvReader;
    private final Path filePath;

    public PartIDsFile() {
        filePath = Paths.get(FILENAME);
    }

    public void writePartId(String partId, String... otherInfo) throws IOException {
        if (csvWriter == null) {
            if (Files.exists(filePath)) {
                Files.move(filePath, Paths.get(FILENAME + ".old"), StandardCopyOption.REPLACE_EXISTING);
            }

            Files.createFile(filePath);
            FileWriter fileWriter = new FileWriter(filePath.toFile());
            csvWriter = new CSVWriter(fileWriter);
        }

        List<String> data = new ArrayList<>();
        data.add(partId);
        if (otherInfo.length > 0)
            data.addAll(Arrays.asList(otherInfo));

        csvWriter.writeNext(data.toArray(new String[]{}));
    }

    public Iterator<String[]> iterator() throws IOException {
        csvReader = new CSVReader(new FileReader(filePath.toFile()));
        return csvReader.iterator();
    }

    public void close() throws IOException {
        if (csvWriter != null)
            csvWriter.close();

        if (csvReader != null)
            csvReader.close();
    }
}
