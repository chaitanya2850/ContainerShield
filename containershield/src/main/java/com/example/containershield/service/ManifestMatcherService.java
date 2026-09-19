package com.example.containershield.service;

import com.example.containershield.dto.FixSuggestion;
import com.example.containershield.dto.TrivyVulnerability;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManifestMatcherService {

    public List<FixSuggestion> matchAgainstPomXml(
            String pomXmlContent,
            List<TrivyVulnerability> findings) {

        Map<String, String> declaredDependencies = parsePomDependencies(pomXmlContent);
        List<FixSuggestion> suggestions = new ArrayList<>();

        for (TrivyVulnerability finding : findings) {

            String manifestVersion = declaredDependencies.get(finding.getPackageName());

            suggestions.add(new FixSuggestion(
                    finding.getPackageName(),
                    manifestVersion, // null if not directly declared
                    finding.getInstalledVersion(),
                    finding.getFixedVersion(),
                    finding.getSeverity(),
                    manifestVersion != null
            ));
        }

        return suggestions;
    }

    private Map<String, String> parsePomDependencies(String pomXmlContent) {

        Map<String, String> dependencies = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(pomXmlContent)));

            NodeList dependencyNodes = doc.getElementsByTagName("dependency");

            for (int i = 0; i < dependencyNodes.getLength(); i++) {
                Element dependency = (Element) dependencyNodes.item(i);

                String groupId = getTagValue(dependency, "groupId");
                String artifactId = getTagValue(dependency, "artifactId");
                String version = getTagValue(dependency, "version");

                if (groupId != null && artifactId != null) {
                    String key = groupId + ":" + artifactId;
                    dependencies.put(key, version);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse pom.xml", e);
        }

        return dependencies;
    }
    private String getTagValue(Element parent, String tagName) {
        NodeList nodes = parent.getElementsByTagName(tagName);
        if (nodes.getLength() == 0) {
            return null;
        }
        return nodes.item(0).getTextContent();
    }
}