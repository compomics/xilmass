/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.runPercolator;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * To run Percolator for each given percolator-input files
 *
 * @author Sule
 */
public class RunPercolator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        //File input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\percolator_inputs\\elite\\test"),
        //        resultFolder= new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\percolator_inputs\\elite\\r");
        File input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\xilmass\\runs\\mc4_TMSAm_HCD_contaminants\\t"),
                resultFolder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\xilmass\\runs\\mc4_TMSAm_HCD_contaminants\\r");
        for (File f : input.listFiles()) {
            System.out.println("Running.." + f.getPath());
            Runtime rt = Runtime.getRuntime();
            String res = resultFolder + File.separator + f.getName().substring(0, f.getName().length() - 4) + "_output.txt";
            System.out.println(res);
            String callAndArgs = "cmd /c percolator " + f.getPath() + " >" + res;
            System.out.println("before");
            Process p = rt.exec(callAndArgs);
            BufferedReader stdOut = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String s;
            while ((s = stdOut.readLine()) != null) {
                //nothing or print
                p.waitFor();
                p.wait();
                System.out.println(s);
                System.out.println(p.getInputStream().toString());
                System.out.println(p.getOutputStream().toString());
            }
            stdOut.close();
            System.out.println("after");
        }

    }

}
