import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        File csvFile = new File("src/main/resources/data.csv");
        File xmlFile = new File("src/main/resources/data.xml");
        File jsonFile = new File("src/main/resources/dataFromCsv.json");
        File jsonFromCsv = new File("src/main/resources/dataFromCsv.json");
        File jsonFromXml = new File("src/main/resources/dataFromXml.json");
        try {
            jsonFromCsv.createNewFile();
            jsonFromXml.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Converter.convertFromCsvToJson(csvFile, jsonFromCsv);
        Converter.convertFromXmlToJson(xmlFile, jsonFromXml);
        System.out.println(Converter.convertFromJsonToClass(jsonFile));
    }
}