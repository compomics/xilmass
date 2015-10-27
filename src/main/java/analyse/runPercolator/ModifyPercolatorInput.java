/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.runPercolator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * This class modify Percolator inputs of Xilmass to check the results without a
 * parameter on ln(NumSp)
 *
 * @author Sule
 */
public class ModifyPercolatorInput {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        File folder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\xilmass\\runs\\mc4_TMSAm_HCD_MODS_minPeaks2_maxPeaks15_noCleaning\\elitea2_b2/percolator_input/"),
                newPercolatorInputFolder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\xilmass\\runs\\mc4_TMSAm_HCD_MODS_minPeaks2_maxPeaks15_noCleaning\\elitea2_b2/percolator_input_updated/");

        for (File f : folder.listFiles()) {
            removeLnNumSp(f, new File(newPercolatorInputFolder.getAbsolutePath() + "/" + f.getName()));
        }
    }

    private static void removeLnNumSp(File percolatorInput, File newPercolatorInput) throws FileNotFoundException, IOException {
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(percolatorInput));
        BufferedWriter bw = new BufferedWriter(new FileWriter(newPercolatorInput));
        while ((line = br.readLine()) != null) {
            String[] sp = line.split("\t");
            String info = "";
            for (int i = 0; i < 11; i++) {
                info += sp[i] + "\t";
            }
            info += sp[12] + "\t" + sp[13] + "\n";
            bw.write(info);
        }
        bw.close();
    }

}
