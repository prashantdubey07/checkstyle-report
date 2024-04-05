
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.TransformerException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConvertXmlToCsv {

    public static void main(String[] args) {
        try {
            convertXml2CsvStax("/Users/prashant/checkstyle-result.xml", "/Users/prashant/Documents/report-checkstyle.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

    static void convertXml2CsvStax(String xmlFilePath, String csvFilePath) throws IOException, TransformerException {
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try (InputStream in = Files.newInputStream(Paths.get(xmlFilePath)); BufferedWriter writer = new BufferedWriter(new FileWriter(csvFilePath))) {
            writer.write("checkstyle_version,file_name,error_line,error_column,error_severity,error_message,error_source\n");

            XMLStreamReader reader = inputFactory.createXMLStreamReader(in);

            String currentElement;
            StringBuilder csvRow = new StringBuilder();
            StringBuilder fileRow = new StringBuilder();
            StringBuilder bookstoreInfo = new StringBuilder();

            while (reader.hasNext()) {
                int eventType = reader.next();

                switch (eventType) {
                    case XMLStreamConstants.START_ELEMENT:
                        currentElement = reader.getLocalName();
                        if ("xml".equals(currentElement)){
                            continue;
                        }
                        if ("checkstyle".equals(currentElement)) {
                            bookstoreInfo.setLength(0);
                            bookstoreInfo.append(reader.getAttributeValue(null, "version"))
                                    .append(",");
                        }
                        if ("file".equals(currentElement)) {
                            fileRow.append(bookstoreInfo)
                                    .append(reader.getAttributeValue(null, "name"))
                                    .append(",");
                        }
                        if ("error".equals(currentElement)) {
                            csvRow.append(fileRow)
                                    .append(reader.getAttributeValue(null, "line"))
                                    .append(",")
                                    .append(reader.getAttributeValue(null, "column"))
                                    .append(",")
                                    .append(reader.getAttributeValue(null, "severity"))
                                    .append(",")
                                    .append(reader.getAttributeValue(null, "message"))
                                    .append(",")
                                    .append(reader.getAttributeValue(null, "source"))
                                    .append(",");
                        }
                        break;

                    case XMLStreamConstants.CHARACTERS:
                        if (!reader.isWhiteSpace()) {
                            csvRow.append(reader.getText()
                                            .trim())
                                    .append(",");
                        }
                        break;

                    case XMLStreamConstants.END_ELEMENT:
                        if ("error".equals(reader.getLocalName())) {
                            csvRow.setLength(csvRow.length() - 1);
                            csvRow.append("\n");
                            writer.write(csvRow.toString());
                            csvRow.setLength(0);
                        }
                        if ("file".equals(reader.getLocalName())) {
                            fileRow.setLength(fileRow.length() - 1);
                            fileRow.append("\n");
                            //writer.write(fileRow.toString());
                            fileRow.setLength(0);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
