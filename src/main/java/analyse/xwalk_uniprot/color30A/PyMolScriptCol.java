/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.xwalk_uniprot.color30A;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Sule
 */
public class PyMolScriptCol {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        // algorithm=0: Xilmass, algorithm=1: Kojak, algorithm=2: pLink
        int algorithm = 0;
        String folderName = "C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol\\optQExactivePlus\\plink\\dist/pLink",
                structure = "4Q57";

        if (algorithm == 0) {
            folderName = "C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol\\optQExactivePlus\\xilmass\\inclMono/dist/xilmass";
        } else if (algorithm == 1) {
            folderName = "C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol\\optQExactivePlus\\kojak\\dist/kojak";
        }

        File computedDist = new File(folderName + "_qexactiveplus_FDR005_" + structure + "_computedDistance.txt"), // get name and calculatedDist
                scriptToCompute = new File(folderName + "_qexactiveplus_FDR005_" + structure + "_DistToCompute.pml"),
                scriptDiffColour = new File(folderName + "_qexactiveplus_FDR005_" + structure + "_DiffColor.pml");

        // an hashmap that holds a name of cross-linking and computed euclidean alpha distance
        HashMap<String, Double> distName_and_computedDist = get_distName_and_computedDist(computedDist, false);

        // an hashmap that holds a name of cross-linking and command to make a line
        HashMap<String, String> distName_and_command = get_distName_and_command(scriptToCompute);

        // write out color-updated pymol script.
        BufferedWriter scriptDiffColourBW = new BufferedWriter(new FileWriter(scriptDiffColour));
        // now write identified distances with colouring (red shows exceeding, blue shows within)
        int times = 0;
        for (String distName : distName_and_computedDist.keySet()) {
            times++;

            Double distance = distName_and_computedDist.get(distName);
            String command = distName_and_command.get(distName);
            String color = "blue";

            // now rename measures to enable plotting with two different colors
            String shorterDistName = distName.replaceAll(distName, "t" + times),
                    shorterCommand = command.replaceAll(distName, "t" + times);

            if (distance > 30.00) {
                color = "red";
            }
            scriptDiffColourBW.write(shorterCommand);
            scriptDiffColourBW.newLine();
            scriptDiffColourBW.write("cmd.set(\"dash_color\", '" + color + "','" + shorterDistName + "')");
            scriptDiffColourBW.newLine();
        }
        scriptDiffColourBW.close();
    }

    /**
     * This method reads a given pymol script to parse distance/command
     * information
     *
     * @param scriptToCompute a script to measure distances
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static HashMap<String, String> get_distName_and_command(File scriptToCompute) throws FileNotFoundException, IOException {
        HashMap<String, String> name_and_distCommand = new HashMap<String, String>();

        BufferedReader scriptToComputeBR = new BufferedReader(new FileReader(scriptToCompute));
        String line = "";
        while ((line = scriptToComputeBR.readLine()) != null) {
            if (line.startsWith("dst")) {
                String[] split = line.split("'");
                String distName = split[1];
                name_and_distCommand.put(distName, line);
            }
        }
        return name_and_distCommand;

    }

    /**
     * This method parse a given computedDist text file and returns an hashmap
     * with distName as a key and computed measure distance as a value
     *
     * @param computedDist
     * @param isOnlyXLinkingSiteInfo true means that xlinkingsite only contains
     * ACC1_IND1_ACC2_IND2 without
     * @return
     */
    public static HashMap<String, Double> get_distName_and_computedDist(File computedDist, boolean isOnlyXLinkingSiteInfo) throws FileNotFoundException, IOException {
        HashMap<String, Double> name_and_computedDist = new HashMap<String, Double>();

        BufferedReader computedDistBR = new BufferedReader(new FileReader(computedDist));
        // read every computed distance
        String line = "";
        while ((line = computedDistBR.readLine()) != null) {
            if (!line.startsWith("Name")) {
                String reg = ",";
                if (line.contains("\t")) {
                    reg = "\t";
                }
                String tmpDistName = line.split("'")[1];
                if (isOnlyXLinkingSiteInfo) {
                    String[] tmp = tmpDistName.split("_");
                    tmpDistName = tmp[tmp.length - 4] + "_" + tmp[tmp.length - 3] + "_" + tmp[tmp.length - 2] + "_" + tmp[tmp.length - 1];
                    System.out.println(tmpDistName);
                }
                if (reg.equals("\t")) {
                    Double tmpComputedDist = Double.parseDouble(line.split("\t")[3]);
                    name_and_computedDist.put(tmpDistName, tmpComputedDist);
                } else {
                    Double tmpComputedDist = Double.parseDouble(line.split("\\)")[1]);
                    name_and_computedDist.put(tmpDistName, tmpComputedDist);
                }
            }
        }

        return name_and_computedDist;
    }

}
