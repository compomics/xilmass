/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package config;

import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;
import util.ResourceUtils;

public class ConfigHolder extends PropertiesConfiguration {

    private static final Logger LOGGER = Logger.getLogger(ConfigHolder.class);
    private static ConfigHolder ourInstance;

    private ConfigHolder(Resource propertiesResource) throws ConfigurationException, IOException {
        super(propertiesResource.getURL());
    }

    static {
        try {
            Resource propertiesResource = ResourceUtils.getResourceByRelativePath("xLink.properties");
            ourInstance = new ConfigHolder(propertiesResource);
        } catch (IOException | ConfigurationException e) {
            LOGGER.error(e.getMessage());
        }
    }

    /**
     * Gets the PropertiesConfiguration instance to run Xilmass
     *
     * @return the PropertiesConfigurationHolder instance
     */
    public static ConfigHolder getInstance() {
        return ourInstance;
    }

    /**
     * Gets the PropertiesConfiguration instance to run a target-decoy analysis
     *
     * @return the PropertiesConfigurationHolder instance
     * @throws org.apache.commons.configuration.ConfigurationException
     * @throws java.io.IOException
     */
    public static ConfigHolder getTargetDecoyAnalyzeInstance() throws ConfigurationException, IOException {
        Resource propertiesResource = ResourceUtils.getResourceByRelativePath("TargetDecoy.properties");
        ourInstance = new ConfigHolder(propertiesResource);
        return ourInstance;
    }

    /**
     * Gets the PropertiesConfiguration instance to run Percolator on each file
     * on a given folder
     *
     * @return the PropertiesConfigurationHolder instance
     * @throws org.apache.commons.configuration.ConfigurationException
     * @throws java.io.IOException
     */
    public static ConfigHolder getPercolatorRunInstance() throws ConfigurationException, IOException {
        Resource propertiesResource = ResourceUtils.getResourceByRelativePath("PercolatorRun.properties");
        ourInstance = new ConfigHolder(propertiesResource);
        return ourInstance;
    }

}
