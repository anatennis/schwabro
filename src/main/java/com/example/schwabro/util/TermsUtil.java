package com.example.schwabro.util;

import com.example.schwabro.terminology.TermEntity;
import com.intellij.openapi.application.PathManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import java.util.Optional;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TermsUtil {
    static String FILE_PATH_MAIN = "/data/glossary.xml";
    static String MY_FILE_NAME = "my_glossary.xml";

    public static Map<String, TermEntity> readAllTerms() {

        URL resource = TermsUtil.class.getResource(FILE_PATH_MAIN);
        String filePath = FILE_PATH_MAIN;

        if (resource != null) {
            filePath = resource.toString();
        }

        Map<String, TermEntity> termsMap = new TreeMap<>(readTermFile(filePath, false));

        filePath = getMyGlossaryPath().toAbsolutePath().toString();
        termsMap.putAll(readTermFile(filePath, true));
        return termsMap;
    }

    @NotNull
    private static Path getMyGlossaryPath() {
        String configPath = PathManager.getConfigPath();
        Path pluginConfigPath = Paths.get(configPath, "schwaBroPlugin");
        File configDir = pluginConfigPath.toFile();
        if (!configDir.exists()) {
            configDir.mkdirs();
        }

        return pluginConfigPath.resolve(MY_FILE_NAME);
    }

    private static Map<String, TermEntity> readTermFile(String filePath, boolean isMyTerms) {
        Map<String, TermEntity> termsMap = new TreeMap<>();
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse(filePath);
            Node root = document.getDocumentElement();

            NodeList terms = root.getChildNodes();

            for (int i = 0; i < terms.getLength(); i++) {
                Node term = terms.item(i);
                if (term.getNodeType() != Node.TEXT_NODE) {
                    String key = Optional.ofNullable(term.getAttributes().item(0))
                            .map(Node::getTextContent).orElse("");
                    if (key.isEmpty()) {
                        continue;
                    }

                    TermEntity value = new TermEntity(key);
                    NodeList termFields = term.getChildNodes();
                    for (int j = 0; j < termFields.getLength(); j++) {
                        Node termField = termFields.item(j);
                        if (termField.getNodeType() != Node.TEXT_NODE) {
                            switch (termField.getNodeName()) {
                                case "word":
                                    value.setTerm(termField.getTextContent());
                                case "definition":
                                    value.setDefinition(termField.getTextContent());
                                case "hyperlink":
                                    value.setHyperlink(termField.getTextContent());
                            }
                        }

                        if (isMyTerms) {
                            value.setMyTermTrue();
                        }
                    }

                    termsMap.put(key, value);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException | NullPointerException ex) {
            ex.printStackTrace();
        }

        return termsMap;
    }

    public static void writeNewTerm(TermEntity termEntity) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            File file = getMyGlossaryPath().toFile();
            Document doc;
            if (file.exists()) {
                doc = factory.newDocumentBuilder().parse(file);
                doc.getDocumentElement().normalize();
            } else {
                doc = factory.newDocumentBuilder().newDocument();
                Element root = doc.createElement("Glossary");
                root.setAttribute("xmlns", "http://www.javacore.ru/schemas/");
                doc.appendChild(root);
            }

            Element root = doc.getDocumentElement();
            Element term = doc.createElement("term");
            term.setAttribute("id", termEntity.getTerm());
            root.appendChild(term);

            Element word = doc.createElement("word");
            Element definition = doc.createElement("definition");
            Element hyperlink = doc.createElement("hyperlink");

            term.appendChild(word);
            term.appendChild(definition);
            term.appendChild(hyperlink);

            word.setTextContent(termEntity.getTerm());
            definition.setTextContent(termEntity.getDefinition());
            hyperlink.setTextContent(termEntity.getHyperlink());

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(new DOMSource(doc), new StreamResult(String.valueOf(file)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void removeMyTermsFile() {
        getMyGlossaryPath().toFile().delete();
    }
}

