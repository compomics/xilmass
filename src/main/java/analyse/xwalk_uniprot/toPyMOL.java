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

/**
 *
 * This class is used to prepare a script for PyMol to map a given Xilmass output according to prediction-list with different color
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
                        System.out.println("" + crossLinkingSite + "\t" + entry.getCommand());
                    }
                } else {
                    String[] tmpXLinkingSites = crossLinkingSite.replace(" ", "").split("_");
                    String first = tmpXLinkingSites[0],
                            second = tmpXLinkingSites[2];
                    // do something.. these are not predicted...
                    String pdbName = "4Q57.pdb";
                    PyMOLEntry entry = null;
                    if (first.equals("P62158") && second.equals("P62158") && is4Q57Asked) {
                        pdbName = "2F3Y.pdb";
                        // indices -1
                        int firstIndex = Integer.parseInt(sp[10].replace(" ", "")),
                                secondIndex = Integer.parseInt(sp[11].replace(" ", "")),
                                tmpFirstIndex = firstIndex - 1,
                                tmpSecondIndex = secondIndex - 1;
                        if (tmpFirstIndex < 72 && tmpSecondIndex < 72) {
                            entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "A", times, bw, crossLinkingSite);
                        }
                    } else if (!first.equals("P62158") && second.equals("P62158") && is4Q57Asked) {
                        // indices -1
                        int firstIndex = Integer.parseInt(sp[10].replace(" ", "")),
                                secondIndex = Integer.parseInt(sp[11].replace(" ", "")),
                                tmpFirstIndex = firstIndex + 21,
                                tmpSecondIndex = secondIndex - 1;
                        if (firstIndex < secondIndex) {
                            tmpFirstIndex = firstIndex - 1;
                            tmpSecondIndex = secondIndex + 21;
                        }
                        // only partial info...
                        if (tmpSecondIndex < 72) {
                            entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "B", "A", times, bw, crossLinkingSite);
                        }
                    } else if (first.equals("P62158") && !second.equals("P62158") && is4Q57Asked) {
                        // indices -1
                        int firstIndex = Integer.parseInt(sp[10].replace(" ", "")),
                                secondIndex = Integer.parseInt(sp[11].replace(" ", "")),
                                tmpFirstIndex = firstIndex - 1,
                                tmpSecondIndex = secondIndex + 21;
                        if (firstIndex < secondIndex) {
                            tmpFirstIndex = firstIndex + 21;
                            tmpSecondIndex = secondIndex - 1;
                        }
                        if (tmpFirstIndex < 72) {
                            entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "B", times, bw, crossLinkingSite);
                        }
                    } else if (!first.equals("P62158") && !second.equals("P62158") && is4Q57Asked) {
                        // indices -1
                        int firstIndex = Integer.parseInt(sp[10].replace(" ", "")),
                                secondIndex = Integer.parseInt(sp[11].replace(" ", "")),
                                tmpFirstIndex = firstIndex + 21,
                                tmpSecondIndex = secondIndex + 21;
                        entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "B", "B", times, bw, crossLinkingSite);
                    } else if (first.equals("P62158") && second.equals("P62158") && !is4Q57Asked) {
                        pdbName = "2F3Y.pdb";
                        // indices -1
                        int firstIndex = Integer.parseInt(sp[10].replace(" ", "")),
                                secondIndex = Integer.parseInt(sp[11].replace(" ", "")),
                                tmpFirstIndex = firstIndex - 1,
                                tmpSecondIndex = secondIndex - 1;
                        entry = preparePyMolCommand(pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "A", times, bw, crossLinkingSite);
                    }
                    if (entry != null) {
                        times++;
                        String name = names.get(crossLinkingSite);
                        write(bw, entry, type, name);
                        System.out.println("" + crossLinkingSite + "\t" + entry.getCommand());
                    }
                }
            }
        }
        bw.close();
    }

    private PyMOLEntry preparePyMolCommand(String pdbName, int tmpFirstIndex, int tmpSecondIndex, String type, String infoA, String infoB, int times, BufferedWriter bw, String crossLinkingSite) throws IOException {
        String atomA = "LYS-" + tmpFirstIndex + "-" + infoA + "-CB",
                atomB = "LYS-" + tmpSecondIndex + "-" + infoB + "-CB",
                name = type + "_extra_" + times;
        PyMOLEntry entry = new PyMOLEntry(pdbName, name, atomA, atomB, "", "", "", "", "", "", "", "", "", isCarbonAlpha);
        write(bw, entry, type, name);
        commands.put(crossLinkingSite, entry);
        names.put(crossLinkingSite, name);
        return entry;
    }

    private void write(BufferedWriter bw, PyMOLEntry entry, String type, String name) throws IOException {
        bw.write(entry.getCommand());
        bw.newLine();
        // set color...
        if (type.startsWith("POSSIBLE")) {
            bw.write("cmd.set(\"dash_color\", 'orange', '" + name + "')");
            bw.newLine();
        } else if (type.startsWith("LIKELYPOSSIBLE")) {
            bw.write("cmd.set(\"dash_color\", 'yelloworange', '" + name + "')");
            bw.newLine();
        } else if (type.startsWith("LIKELYIMPOSSIBLE")) {
            bw.write("cmd.set(\"dash_color\", 'magenta', '" + name + "')");
            bw.newLine();
        } else if (type.startsWith("IMPOSSIBLE")) {
            bw.write("cmd.set(\"dash_color\", 'red', '" + name + "')");
            bw.newLine();
        } else if (type.startsWith("Not")) {
            bw.write("cmd.set(\"dash_color\", 'blue', '" + name + "')");
            bw.newLine();
        }
        bw.write("cmd.set(\"dash_gap\", 0.2, '" + name + "')");
        bw.newLine();
        bw.write("cmd.set(\"dash_width\", 5, '" + name + "')");
        bw.newLine();
        bw.write("cmd.set(\"dash_length\", 0.75, '" + name + "')");
        bw.newLine();
    }

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
                if (uniprotIndB > uniprotIndA) {
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
        boolean xilmassResults = true; //false=all to generate script to run all possible computationaly predicted ones, true=work on Xilmass identification
        File xilmass = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\results_diff_no\\figure2_3_softwareComparison/xilmass-eliteFDR005.txt"),
                prediction = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_pymol_manul_autoAAcal.txt"),
                f = new File(""),
                pymolScript = new File(""),
                findingToPyMOLScript = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\pyMol/xilmass_elitefdr005_toPrint_4Q57_2.txt");
        // xilmass = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\results\\figure2_3softwareComparison/xilmass_qexactive_percolatorFDR005.txt");
        String distanceOutput = "";
        boolean isCarbonAlpha = true,
                toWrite = false,
                isTitleReady = false,
                is4Q57Asked = true;
        if (xilmassResults) {
            toPyMOL obj = new toPyMOL(xilmass, prediction, findingToPyMOLScript, isCarbonAlpha, is4Q57Asked);
            obj.plotIDentifiedXLinkingSites();

        } else {
            int times = 1;
            String line = "";
            BufferedReader br = new BufferedReader(new FileReader(f));
            BufferedWriter bw = new BufferedWriter(new FileWriter(pymolScript));
            if (!toWrite) {
                bw.write("f=open('" + distanceOutput + "', 'w')");
                bw.newLine();
            }
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("PDB Structure")) {
                    String[] sp = line.split("\t");
                    String pdbName = sp[0];
                    String structure = pdbName.substring(0, pdbName.indexOf(".pdb"));
                    String firstatom = sp[1],
                            secondatom = sp[4];
                    String[] firsts = firstatom.split("-"),
                            seconds = secondatom.split("-");
                    String firstInfo = "/" + structure + "//" + firsts[2] + "/" + firsts[0] + "`" + firsts[1] + "/" + "CA",
                            secondInfo = "/" + structure + "//" + seconds[2] + "/" + seconds[0] + "`" + seconds[1] + "/" + "CA";
                    if (!isCarbonAlpha) {
                        firstInfo = "/" + structure + "//" + firsts[2] + "/" + firsts[0] + "`" + firsts[1] + "/" + "CB";
                        secondInfo = "/" + structure + "//" + seconds[2] + "/" + seconds[0] + "`" + seconds[1] + "/" + "CB";
                    }
                    String type = sp[7];
                    type = type.replace(" ", "");
                    String name = type + times;
                    String run = "distance " + name + "," + firstInfo + "," + secondInfo;
                    if (!isTitleReady) {
                        String title = "PDBStructure,AtomInfoA,AtomInfoB,IdistanceSequence,Type,UniprotAccProA,UniprotIndexProA"
                                + ",UniprotAccProB,UniprotIndexProB,SASDistance(A),EucDist(Beta_Beta),EucDist(Alpha_Alpha)";
                        bw.write("f.write(" + "\"" + title + "\"" + ")");
                        bw.newLine();
                        bw.write("f.write('\\n')");
                        bw.newLine();
                        isTitleReady = true;
                    }
                    if (toWrite) {
//                        System.out.println(run);
                    } else {
                        String pdb = sp[0];
                        if ((pdb.contains("4Q57") && is4Q57Asked)
                                || (!pdb.contains("4Q57") && !is4Q57Asked)) {
                            String pdbStr = sp[0],
                                    atomIndexA = sp[1],
                                    idDist = sp[2],
                                    sasDist = sp[3],
                                    atomIndexB = sp[4],
                                    eucBeta = sp[5],
                                    uniprotA = sp[8],
                                    uniprotAInd = sp[9],
                                    uniprotB = sp[10],
                                    uniprotBInd = sp[11];
                            run = "dst=cmd.distance('" + name + "','" + firstInfo + "','" + secondInfo + "')";
                            bw.write(run);
                            bw.newLine();
                            //"Number of distances calculated: %s\n % (dst) \n test")
                            // f.write("Number of distances calculated: %s\n" % (counter))
                            String info = pdbStr + "," + atomIndexA + "," + atomIndexB + "," + idDist + "," + type + "," + uniprotA + "," + uniprotAInd + ","
                                    + uniprotB + "," + uniprotBInd + "," + sasDist + "," + eucBeta + ",";
                            String writing = "\"" + info + "%s\"" + "% (dst)";
                            bw.write("value=" + writing);
                            bw.newLine();
                            bw.write("f.write(value)");
                            bw.newLine();
                            bw.write("f.write('\\n')");
                            bw.newLine();
                        }
                    }
                    times++;
                }
            }
            bw.write("f.close()");
            bw.close();
        }
    }

}
