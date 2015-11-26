/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.xwalk_uniprot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * This class is used to prepare a script for PyMol to map a given Xilmass
 * output according to prediction-list with different color
 *
 * @author Sule
 */
public class toPyMOL {

    private File xilmass,
            prediction,
            findingToPyMOLScript;
    private boolean isCarbonAlpha = true,
            is4Q57Asked = false;
    private HashMap<String, PyMOLEntry> commands = new HashMap<String, PyMOLEntry>();
    private HashMap<String, String> names = new HashMap<String, String>();
    private HashMap<PyMOLEntry, String> all = new HashMap<PyMOLEntry, String>();
    private int xlinkingIndex,
            typeIndex;

    public toPyMOL(File xilmass, File prediction, File findingToPyMOLScript,
            boolean isCarbonAlpha, boolean is4Q57Asked) {
        this.xilmass = xilmass;
        this.prediction = prediction;
        this.findingToPyMOLScript = findingToPyMOLScript;
        this.isCarbonAlpha = isCarbonAlpha;
        this.is4Q57Asked = is4Q57Asked;
        xlinkingIndex = 16;
        typeIndex = 12;
    }

    private void plotIDentifiedXLinkingSites() throws IOException {
        // read each line on prediction file
        getPredictions(prediction, isCarbonAlpha);
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(xilmass));
        BufferedWriter bw = new BufferedWriter(new FileWriter(findingToPyMOLScript));
        int times = 1;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("SpectrumFile")) {
                String[] sp = line.split("\t");
                String crossLinkingSite = sp[xlinkingIndex].replace(" ", ""),
                        type = sp[typeIndex].replace(" ", "");
                if (commands.containsKey(crossLinkingSite)) {
                    PyMOLEntry entry = commands.get(crossLinkingSite);
                    String pdb = entry.getPdb();
                    if ((pdb.contains("4Q57") && is4Q57Asked)
                            || (!pdb.contains("4Q57") && !is4Q57Asked)) {
                        String name = names.get(crossLinkingSite);
                        write(bw, entry, type, name);
                        System.out.println(crossLinkingSite + "\t" + entry.getCommand());
                    }
                } else {
                    String[] tmpXLinkingSites = crossLinkingSite.replace(" ", "").split("_");
                    String first = tmpXLinkingSites[0],
                            second = tmpXLinkingSites[2];
                    int firstIndex = Integer.parseInt(tmpXLinkingSites[1].replace(" ", "")),
                            secondIndex = Integer.parseInt(tmpXLinkingSites[3].replace(" ", ""));
                    // do something.. these are not predicted...
                    String pdbName = "4Q57.pdb";
                    PyMOLEntry entry = null;
                    if (first.equals("P62158") && second.equals("P62158") && !is4Q57Asked) {
                        pdbName = "2F3Y.pdb";
                        int tmpFirstIndex = firstIndex - 1,
                                tmpSecondIndex = secondIndex - 1;
                        entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "A", times, bw, crossLinkingSite);
                    } else if (!first.equals("P62158") && second.equals("P62158") && is4Q57Asked) {
                        int tmpFirstIndex = firstIndex + 21,
                                tmpSecondIndex = secondIndex - 1;
                        // only partial info...
                        if (tmpSecondIndex < 72) {
                            entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "B", "A", times, bw, crossLinkingSite);
                        } else {
                            String missing = type + "_missing";
                            entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, missing, "B", "A", times, bw, crossLinkingSite);
                        }
                    } else if (first.equals("P62158") && !second.equals("P62158") && is4Q57Asked) {
                        int tmpFirstIndex = firstIndex - 1,
                                tmpSecondIndex = secondIndex + 21;
                        if (tmpFirstIndex < 72) {
                            entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "B", times, bw, crossLinkingSite);
                        } else {
                            String missing = type + "_missing";
                            entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, missing, "A", "B", times, bw, crossLinkingSite);
                        }
                    } else if (!first.equals("P62158") && !second.equals("P62158") && is4Q57Asked) {
                        int tmpFirstIndex = firstIndex + 21,
                                tmpSecondIndex = secondIndex + 21;
                        entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "B", "B", times, bw, crossLinkingSite);
                    } else if (first.equals("P62158") && second.equals("P62158") && !is4Q57Asked) {
                        pdbName = "2F3Y.pdb";
                        // indices -1
                        int tmpFirstIndex = firstIndex - 1,
                                tmpSecondIndex = secondIndex - 1;
                        entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "A", times, bw, crossLinkingSite);
                    } else if (first.equals("P62158") && second.equals("P62158") && is4Q57Asked) {
                        // indices -1
                        int tmpFirstIndex = firstIndex - 1,
                                tmpSecondIndex = secondIndex - 1;
                        if (tmpFirstIndex < 72 && tmpSecondIndex < 72) {
                            entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "A", times, bw, crossLinkingSite);
                        } else {
                            String check23FY = "check2F3Y";
                            entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, check23FY, "A", "A", times, bw, crossLinkingSite);
                        }
                    }
                    if (entry != null) {
                        times++;
                        String name = names.get(crossLinkingSite);
                        write(bw, entry, type, name);
                        System.out.println(crossLinkingSite + "\t" + entry.getCommand());
                    }
                }
            }
        }
        bw.close();
    }

    private PyMOLEntry preparePyMolCommand(String pdbName, int tmpFirstIndex, int tmpSecondIndex, String type, String infoA, String infoB, int times, BufferedWriter bw, String crossLinkingSite) throws IOException {
        String atomA = "LYS-" + tmpFirstIndex + "-" + infoA + "-CB",
                atomB = "LYS-" + tmpSecondIndex + "-" + infoB + "-CB",
                name = type + times;
        PyMOLEntry entry = new PyMOLEntry(pdbName, name, atomA, atomB, "", "", "", "", "", "", "", "", "", isCarbonAlpha);
        write(bw, entry, type, name);
        commands.put(crossLinkingSite, entry);
        names.put(crossLinkingSite, name);
        return entry;
    }

    public HashMap<PyMOLEntry, String> getAll() {
        return all;
    }

    private void write(BufferedWriter bw, PyMOLEntry entry, String type, String name) throws IOException {
        String colorInfo = "";
        bw.write(entry.getCommand());
        bw.newLine();
        // set color...
        if (type.startsWith("POSSIBLE")) {
            colorInfo = ("cmd.set(\"dash_color\", 'orange', '" + name + "')");
        } else if (type.startsWith("LIKELYPOSSIBLE")) {
            colorInfo = ("cmd.set(\"dash_color\", 'yelloworange', '" + name + "')");
        } else if (type.startsWith("LIKELYIMPOSSIBLE")) {
            colorInfo = ("cmd.set(\"dash_color\", 'magenta', '" + name + "')");
        } else if (type.startsWith("IMPOSSIBLE")) {
            colorInfo = ("cmd.set(\"dash_color\", 'red', '" + name + "')");
        } else if (type.startsWith("Not-predicted_missing")) {
            colorInfo = ("cmd.set(\"dash_color\", 'black', '" + name + "')");
        } else if (type.startsWith("Not-predicted")) {
            colorInfo = ("cmd.set(\"dash_color\", 'blue', '" + name + "')");
        } else if (type.startsWith("NoLinkableResidue")) {
            colorInfo = ("cmd.set(\"dash_color\", 'blue', '" + name + "')");
        }
        bw.write(colorInfo);
        bw.newLine();
        all.put(entry, colorInfo);
    }

    /**
     * To get previously predicted possible cross-linking sites.
     *
     * @param prediction
     * @param isCarbonAlpha
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void getPredictions(File prediction, boolean isCarbonAlpha) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(prediction));
        String line = "";
        int times = 1;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("PDBS")) {
                String[] sp = line.split(",");
                String pdb = sp[0],
                        atomA = sp[1],
                        atomB = sp[2],
                        idDis = sp[3],
                        type = sp[4],
                        uniprotAccA = sp[5],
                        uniprotIndexA = sp[6],
                        uniprotAccB = sp[7],
                        uniprotIndexB = sp[8],
                        sas = sp[9],
                        betaDist = sp[10],
                        alphaDist = sp[11];
                int uniprotIndA = Integer.parseInt(uniprotIndexA),
                        uniprotIndB = Integer.parseInt(uniprotIndexB);
                String crossLinkingSite = uniprotAccA + "_" + uniprotIndexA + "_" + uniprotAccB + "_" + uniprotIndexB;
                if ((uniprotIndB > uniprotIndA) && uniprotAccA.equals(uniprotAccB)) {
                    crossLinkingSite = uniprotAccB + "_" + uniprotIndexB + "_" + uniprotAccA + "_" + uniprotIndexA;
                }
                String name = type + "_" + times;
                PyMOLEntry o = new PyMOLEntry(pdb, name, atomA, atomB, idDis, type, uniprotAccA, uniprotIndexA, uniprotAccB, uniprotIndexB, sas, betaDist, alphaDist, isCarbonAlpha);
                commands.put(crossLinkingSite, o);
                names.put(crossLinkingSite, name);
                times++;
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        boolean isCarbonAlpha = true,
                toWrite = false,
                isTitleReady = false,
                is4Q57Asked = false;
        String structureInfo = "2F3Y";
        if (is4Q57Asked) {
            structureInfo = "4Q57";
        }
        //figure2_3_softwareComparison\all_toPymol
        String algorithm = "kojak_v136";
        //String algorithm = "xilmass";
        //String dataset = "elite";
        String dataset = "qexactive";
        File xilmass = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\results_diff_no/figure2_3_softwareComparison/all_toPymol/" + algorithm + "_003Da_" + dataset + "_FDR005.txt"),
                prediction = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_pymol_manul_autoAAcal.txt"),
                f = new File(""),
                pymolScript = new File(""),
                calculatedDistance = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol/final/final_" + algorithm + "_003Da_" + dataset + "_FDR005_" + structureInfo + "_DistToCompute.pml"),
                findingToPyMOLScript = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol/final/final_" + algorithm + "_003Da_" + dataset + "_FDR005_" + structureInfo + ".pml"),
                finalText = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol/final/final_" + algorithm + "_003Da_" + dataset + "_FDR005_" + structureInfo + "_to_plot.pml");
        // xilmass = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\results\\figure2_3softwareComparison/xilmass_qexactive_percolatorFDR005.txt");
        String distanceOutput = "C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol/final/final_" + algorithm + "_003Da_" + dataset + "_FDR005_" + structureInfo + "_computedDistance.txt";

        int option = 2; // print cross-linking parts..
        if (option == 0) {
            toPyMOL obj = new toPyMOL(xilmass, prediction, findingToPyMOLScript, isCarbonAlpha, is4Q57Asked);
            obj.plotIDentifiedXLinkingSites();

        } else if (option == 2) {
            toPyMOL obj = new toPyMOL(xilmass, prediction, findingToPyMOLScript, isCarbonAlpha, is4Q57Asked);
            obj.plotIDentifiedXLinkingSites();
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(findingToPyMOLScript));
            BufferedWriter bw = new BufferedWriter(new FileWriter(calculatedDistance));
            if (!toWrite) {
                bw.write("f=open('" + distanceOutput + "', 'w')");
                bw.newLine();
            }
            HashSet<String> lines = new HashSet<String>();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("dst=")) {
                    lines.add(line);
                }
            }
            for (String tmp : lines) {
                bw.write(tmp);
                bw.newLine();
                String info = tmp.split("\\(")[1];
                //"Number of distances calculated: %s\n % (dst) \n test")
                // f.write("Number of distances calculated: %s\n" % (counter))
                String writing = "\"" + info + "%s\"" + "% (dst)";
                bw.write("value=" + writing);
                bw.newLine();
                bw.write("f.write(value)");
                bw.newLine();
                bw.write("f.write('\\n')");
                bw.newLine();
            }
            bw.write("f.close()");
            bw.close();

            BufferedWriter bw2 = new BufferedWriter(new FileWriter(finalText));
            HashMap<PyMOLEntry, String> commands = obj.getAll();
            for (PyMOLEntry e : commands.keySet()) {
                String colorCommand = commands.get(e);
                if (!colorCommand.contains("Not-predicted_missing")
                        && !colorCommand.contains("check2F3Y")
                        && !colorCommand.contains("NoLinkableResidu")) {
                    bw2.write(e.getCommand());
                    bw2.newLine();
                    bw2.write(colorCommand);
                    bw2.newLine();
                }
            }
            bw2.close();

        }
    }

}
