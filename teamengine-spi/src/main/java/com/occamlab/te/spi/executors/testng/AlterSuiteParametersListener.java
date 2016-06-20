package com.occamlab.te.spi.executors.testng;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.testng.IAlterSuiteListener;
import org.testng.xml.XmlSuite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * A listener that sets or updates the suite-level parameters in the test suites
 * using the given test run arguments.
 */
public class AlterSuiteParametersListener implements IAlterSuiteListener {

    private final static Logger LOGR = Logger.getLogger(AlterSuiteParametersListener.class.getName());
    private Document testRunArgs;
    private UUID testRunId = UUID.randomUUID();

    /**
     * Sets the test run arguments.
     * 
     * @param testRunArgs
     *            A Document that contains a set of XML properties.
     */
    public void setTestRunArgs(Document testRunArgs) {
        if (null == testRunArgs || testRunArgs.getElementsByTagName("entry").getLength() == 0) {
            throw new IllegalArgumentException(String.format("No test run arguments found."));
        }
        this.testRunArgs = testRunArgs;
    }

    /**
     * Sets the test run identifier.
     * 
     * @param testRunId
     *            A universally unique identifier (128-bit value).
     */
    public void setTestRunId(UUID testRunId) {
        this.testRunId = testRunId;
    }

    @Override
    public void alter(List<XmlSuite> xmlSuites) {
        if (null == this.testRunArgs || this.testRunArgs.getElementsByTagName("entry").getLength() == 0) {
            return;
        }
        for (XmlSuite xmlSuite : xmlSuites) {
            Map<String, String> params = xmlSuite.getParameters();
            NodeList entries = this.testRunArgs.getElementsByTagName("entry");
            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element) entries.item(i);
                String value = entry.getTextContent().trim();
                if (value.isEmpty()) {
                    continue;
                }
                params.put(entry.getAttribute("key"), value);
                LOGR.log(Level.FINE, "Added parameter: {0}={1}", new Object[] { entry.getAttribute("key"), value });
            }
            params.put("uuid", this.testRunId.toString());
        }
    }

}