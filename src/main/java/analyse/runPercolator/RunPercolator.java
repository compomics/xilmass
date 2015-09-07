/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.runPercolator;

import config.ConfigHolder;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

/**
 * To run Percolator for each given percolator-input files
 *
 * @author Sule
 */
public class RunPercolator {

    private static final Logger LOGGER = Logger.getLogger(ConfigHolder.class);

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     * @throws java.lang.InterruptedException
     * @throws org.apache.commons.configuration.ConfigurationException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ConfigurationException {
        File input = new File(ConfigHolder.getPercolatorRunInstance().getString("input")),
                resultFolder = new File(ConfigHolder.getPercolatorRunInstance().getString("resultFolder"));
        for (File f : input.listFiles()) {
            LOGGER.info("Running.." + f.getPath());
            Runtime rt = Runtime.getRuntime();
            String res = resultFolder + File.separator + f.getName().substring(0, f.getName().length() - 4) + "_output.txt";
            LOGGER.info(res);
            String callAndArgs = "cmd /c percolator " + f.getPath() + " >" + res;
            LOGGER.info("before");
            Process p = rt.exec(callAndArgs);
            BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            while ((s = stdOut.readLine()) != null) {
                //nothing or print
                p.waitFor();
                p.wait();
                LOGGER.info(s);
                LOGGER.info(p.getInputStream().toString());
                LOGGER.info(p.getOutputStream().toString());
            }
            stdOut.close();
            System.out.println("after");
        }

    }

}
