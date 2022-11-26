package com;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Tool {
    private DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    private static final Logger logger = LogManager.getLogger("tool");

    public void writeXML(String fullFilePath, String galTitle, String currTimestamp)
            throws ParserConfigurationException, TransformerException {
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("GalRecord");
        doc.appendChild(rootElement);

        Element title = doc.createElement("title");
        title.appendChild(doc.createTextNode(galTitle));

        Element time = doc.createElement("time");
        time.appendChild(doc.createTextNode(currTimestamp));

        rootElement.appendChild(title);
        rootElement.appendChild(time);

        try (FileOutputStream output = new FileOutputStream(fullFilePath)) {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);
            transformer.transform(source, result);
        } catch (IOException e) {
            logger.error("error in writing XML file... ", e);
        }
    }

    public HashMap<String, String> readXML(String fullFilePath)
            throws ParserConfigurationException, TransformerException {
        DocumentBuilder dBuilder = docFactory.newDocumentBuilder();
        try {
            docFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Document doc = dBuilder.parse(new File(fullFilePath));
            doc.getDocumentElement().normalize();
            NodeList list = doc.getElementsByTagName("staff");
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error("error in reading XML file... ", e);
        }
        return null;
    }
}
