//1 task:
// 1.1: data.CSV -> Employee
// 1.2: Employee -> GSON -> data.JSON

//2 task:
// 2.1: data.XML -> Employee
// 2.2: 1.2

//3 task:
// data.JSON -> JSONObject -> Employee

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Converter {

    static void convertFromCsvToJson(File input, File output) {
        ColumnPositionMappingStrategy<Employee> cpmStrategy = new ColumnPositionMappingStrategy<>();
        cpmStrategy.setType(Employee.class);
        cpmStrategy.setColumnMapping("id", "firstName", "lastName", "country", "age");
        try (CSVReader csvReader = new CSVReader(new BufferedReader(new FileReader(input)))) {
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(cpmStrategy)
                    .build();
            List<Employee> employees = csv.parse();
            Converter.convertFromClassToJson(employees, output);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void convertFromXmlToJson(File input, File output) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(input);
            Node root = document.getDocumentElement();
            NodeList nodesEmployees = root.getChildNodes();
            List<Employee> employees = new ArrayList<>();
            for (int i = 1; i < nodesEmployees.getLength(); i++) {
                Node node = nodesEmployees.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    employees.add(new Employee(
                            Long.parseLong(element.getElementsByTagName("id").item(0).getTextContent()),
                            element.getElementsByTagName("firstName").item(0).getTextContent(),
                            element.getElementsByTagName("lastName").item(0).getTextContent(),
                            element.getElementsByTagName("country").item(0).getTextContent(),
                            Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent())));
                }
            }
            Converter.convertFromClassToJson(employees, output);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    static List<Employee> convertFromJsonToClass(File input) {
        List<Employee> employees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(input))){
            JSONParser jsonParser = new JSONParser();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(br);
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            for (int i = 0; i < jsonArray.size(); i++) {
                employees.add(gson.fromJson(jsonArray.get(i).toString(), Employee.class));
            }
        } catch (FileNotFoundException | ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return employees;
    }

    static void convertFromClassToJson(List<Employee> employees, File output)
            throws IOException {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write(gson.toJson(employees));
        bw.flush();
    }
}