package demo.xmlassembly.service.utils;

import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.Collections;
import java.util.Iterator;

@Component
public class XmlTestUtils {

    /**
     * Parses the given XML string into a DOM Document.
     *
     * @param xml the XML string to parse.
     * @return the parsed Document.
     */
    private Document parseXml(String xml) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true); // important for handling namespaces
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new RuntimeException("Error parsing XML", e);
        }
    }

    /**
     * Creates an XPath instance with a NamespaceContext that maps the prefix "ns" to
     * "http://www.example.com/demo/xmlassembly/xml".
     *
     * @return an XPath instance.
     */
    private XPath createXPath() {
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if ("ns".equals(prefix)) {
                    return "http://www.example.com/demo/xmlassembly/xml";
                }
                return XMLConstants.NULL_NS_URI;
            }
            @Override
            public String getPrefix(String namespaceURI) {
                if ("http://www.example.com/demo/xmlassembly/xml".equals(namespaceURI)) {
                    return "ns";
                }
                return null;
            }
            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return Collections.singleton("ns").iterator();
            }
        });
        return xpath;
    }

    /**
     * Checks if at least one node exists in the given XML for the provided XPath expression.
     *
     * @param xml       the XML string.
     * @param xpathExpr the XPath expression (should use the prefix "ns" for namespaced elements).
     * @return true if one or more nodes match; false otherwise.
     */
    public boolean elementExists(String xml, String xpathExpr) {
        Document doc = parseXml(xml);
        XPath xpath = createXPath();
        try {
            NodeList nodes = (NodeList) xpath.evaluate(xpathExpr, doc, XPathConstants.NODESET);
            return nodes.getLength() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating XPath: " + xpathExpr, e);
        }
    }

    /**
     * Checks if the string value of the node selected by the given XPath is not empty.
     *
     * @param xml       the XML string.
     * @param xpathExpr the XPath expression (should use the prefix "ns" for namespaced elements).
     * @return true if the node exists and its trimmed text is not empty.
     */
    public boolean elementIsNotEmpty(String xml, String xpathExpr) {
        Document doc = parseXml(xml);
        XPath xpath = createXPath();
        try {
            String text = xpath.evaluate(xpathExpr, doc);
            return text != null && !text.trim().isEmpty();
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating XPath: " + xpathExpr, e);
        }
    }

    /**
     * Counts the number of nodes matching the given XPath expression.
     *
     * @param xml       the XML string.
     * @param xpathExpr the XPath expression (should use the prefix "ns" for namespaced elements).
     * @return the count of matching nodes.
     */
    public int countElement(String xml, String xpathExpr) {
        Document doc = parseXml(xml);
        XPath xpath = createXPath();
        try {
            Double count = (Double) xpath.evaluate("count(" + xpathExpr + ")", doc, XPathConstants.NUMBER);
            return count.intValue();
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating XPath: " + xpathExpr, e);
        }
    }

    /**
     * Retrieves the inner text of the first node that matches the given XPath expression.
     *
     * @param xml       the XML string.
     * @param xpathExpr the XPath expression (should use the prefix "ns" for namespaced elements).
     * @return the trimmed inner text, or an empty string if not found.
     */
    public String getElementInnerText(String xml, String xpathExpr) {
        Document doc = parseXml(xml);
        XPath xpath = createXPath();
        try {
            return xpath.evaluate(xpathExpr, doc).trim();
        } catch (Exception e) {
            throw new RuntimeException("Error evaluating XPath: " + xpathExpr, e);
        }
    }
}
