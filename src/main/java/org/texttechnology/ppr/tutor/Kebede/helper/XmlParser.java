package org.texttechnology.ppr.tutor.Kebede.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.texttechnology.ppr.tutor.Kebede.database.Neo4jConnection;
import org.texttechnology.ppr.tutor.Kebede.implementation.SalaryImpl;
import org.texttechnology.ppr.tutor.Kebede.implementation.StaffImpl;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import java.io.*;


/**
 * Parse xml file from the working directory and save to Neo4j
 */
public class XmlParser {

    private static final Logger log = LoggerFactory.getLogger(XmlParser.class);

    private Neo4jConnection neo4jConnection;

    /**
     * Parse XML file using Neo4jConnection
     */
    public void parseFileWithConnection(String xmlPath, Neo4jConnection connection) throws IOException {
        this.neo4jConnection = connection;

        InputStream is = loadXml(xmlPath);
        if (is == null) {
            throw new IOException("XML file not found: " + xmlPath);
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = dbf.newDocumentBuilder();

            Document document = builder.parse(is);
            document.getDocumentElement().normalize();

            NodeList staffNodes = document.getElementsByTagName("staff");

            for (int i = 0; i < staffNodes.getLength(); i++) {
                Element staffElem = (Element) staffNodes.item(i);

                String id = staffElem.getAttribute("id");
                String firstname = getTagText(staffElem, "firstname");
                String lastname = getTagText(staffElem, "lastname");
                String nickname = getTagText(staffElem, "nickname");

                Element salaryElem = (Element) staffElem.getElementsByTagName("salary").item(0);
                if (salaryElem != null) {
                    String salaryValue = salaryElem.getTextContent().trim();
                    String currency = salaryElem.getAttribute("currency");

                    SalaryImpl salary = new SalaryImpl(salaryValue, currency);
                    StaffImpl staff = new StaffImpl(id, firstname, lastname, nickname, salary);

                    saveToNeo4j(staff);
                }
            }

        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException("XML parsing error", e);
        }
    }

    /**
     * Load XML either from classpath OR filesystem.
     */
    private InputStream loadXml(String path) throws FileNotFoundException {
        // 1) Try classpath
        InputStream is = XmlParser.class.getClassLoader().getResourceAsStream(path);
        if (is != null) {
            System.out.println("Loaded XML from classpath: " + path);
            return is;
        }

        // 2) Try filesystem
        File file = new File(path);
        if (file.exists()) {
            System.out.println("Loaded XML from filesystem: " + file.getAbsolutePath());
            return new FileInputStream(file);
        }

        // Nothing found
        return null;
    }

    /**
     * Helper to read child text content.
     */
    private static String getTagText(Element parent, String tag) {
        Node n = parent.getElementsByTagName(tag).item(0);
        return n != null ? n.getTextContent().trim() : null;
    }

    /**
     * Save staff to Neo4j.
     */
    private void saveToNeo4j(StaffImpl staff) {
        try {
            neo4jConnection.createStaffNode(staff);
            System.out.println("Saved staff: " + staff.getId());
        } catch (Exception e) {
            System.out.println("Failed to save staff: " + staff.getId());
            log.warn("Failed to save staff: {}", staff.getId(), e);
        }
    }

}