package myBatchProcessor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Example of using Java's built-in DOM XML parsing to parse one of the XML
 * batch files. This is functionality that should be placed in the class
 * BatchParser
 */
public class BatchParser {
	public static Batch batch;
	private static ArrayList<String> validAttributeList = new ArrayList<>();

	protected void parse(String filename) throws Exception {
		try {
			addValidAttributes(validAttributeList);
			batch = new Batch();
			FileInputStream fis = new FileInputStream(filename);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fis);

			Element pnode = doc.getDocumentElement();
			NodeList nodes = pnode.getChildNodes();
			for (int idx = 0; idx < nodes.getLength(); idx++) {
				Node node = nodes.item(idx);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element elem = (Element) node;
					parseCommand(elem);
				}
			}

		} catch (ProcessException | ParserConfigurationException | SAXException | IOException e) {
			throw e;
		}
	}

	public static void parseCommand(Element elem) throws ProcessException {
		String cmdName = elem.getNodeName();
		ArrayList<String> attributeList = (ArrayList<String>) getAttributeList(elem);
		if (cmdName == null) {
			throw new ProcessException("unable to parse command from " + elem.getTextContent());
		} else if ("filename".equalsIgnoreCase(cmdName) || ("file".equalsIgnoreCase(cmdName))) {
			System.out.println("Parsing filename");
			for (String attribute : attributeList) {
				if (!(validAttributeList.contains(attribute)))
					throw new Error(
							"Unable to process xml element. Unknown attribute " + attribute + " from " + cmdName);
			}
			Command cmd = new FileNameCommand();
			cmd.parse(elem);
			batch.addCommand(cmd);
		} else if ("exec".equalsIgnoreCase(cmdName) || "cmd".equalsIgnoreCase(cmdName)) {
			System.out.println("Parsing exec");
			for (String attribute : attributeList) {
				if (!(validAttributeList.contains(attribute)))
					throw new Error(
							"Unable to process xml element. Unknown attribute " + attribute + " from " + cmdName);
			}

			Command cmd = new ExecCommand();
			cmd.parse(elem);
			batch.addCommand(cmd);
		} else if ("pipe".equalsIgnoreCase(cmdName) || "pipecmd".equalsIgnoreCase(cmdName)) {
			System.out.println("Parsing pipe");
			for (String attribute : attributeList) {
				if (!(validAttributeList.contains(attribute)))
					throw new Error(
							"Unable to process xml element. Unknown attribute " + attribute + " from " + cmdName);
			}

			Command cmd = new PipeCmdCommand();
			cmd.parse(elem);
			batch.addCommand(cmd);
		} else {
			throw new ProcessException("Unknown command " + cmdName + " from: " + elem.getBaseURI());
		}
	}

	private static List<String> getAttributeList(Element element) {
		Node attribute;
		String attributeName;
		ArrayList<String> attributeList = new ArrayList<>();
		NamedNodeMap attributes = element.getAttributes();
		int numberOfAttributes = attributes.getLength();
		for (int i = 0; i < numberOfAttributes; i++) {
			attribute = attributes.item(i);
			attributeName = attribute.getNodeName();
			attributeList.add(attributeName);
		}
		return attributeList;
	}

	private void addValidAttributes(ArrayList<String> validAttributesList) {
		validAttributesList.add("path");
		validAttributesList.add("in");
		validAttributesList.add("out");
		validAttributesList.add("args");
		validAttributesList.add("id");
	}

	public Batch getBatch() {
		return batch;
	}

}
