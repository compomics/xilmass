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

    private File input,
            prediction,
            findingToPyMOLScript;
    private boolean isCarbonAlpha = true,
            is4Q57Asked = false,
            hasDeltaScores = false;
    private HashMap<String, PyMOLEntry> commands = new HashMap<String, PyMOLEntry>();
    private HashMap<String, String> names = new HashMap<String, String>();
    private HashMap<PyMOLEntry, String> all = new HashMap<PyMOLEntry, String>();
    private int xlinkingProsAndSitesIndex,
            typeIndex,
            pepASeqIndex = 9,
            pepBSeqIndex = 12,
            proteinAAccIndex = 10,
            proteinBAccIndex = 13,
            proteinALinkIndex = 18,
            proteinBLinkIndex = 19,
            peptideALinkIndex = 16,
            peptideBLinkIndex = 17;

    public toPyMOL(File input, File prediction, File findingToPyMOLScript, boolean isCarbonAlpha, boolean is4Q57Asked) {
        this.input = input;
        this.prediction = prediction;
        this.findingToPyMOLScript = findingToPyMOLScript;
        this.isCarbonAlpha = isCarbonAlpha;
        this.is4Q57Asked = is4Q57Asked;
        xlinkingProsAndSitesIndex = 32;
        typeIndex = 15;
    }

    public toPyMOL(File input, File prediction, File findingToPyMOLScript, boolean isCarbonAlpha, boolean is4Q57Asked, boolean hasDeltaScores) {
        this.input = input;
        this.prediction = prediction;
        this.findingToPyMOLScript = findingToPyMOLScript;
        this.isCarbonAlpha = isCarbonAlpha;
        this.is4Q57Asked = is4Q57Asked;
        xlinkingProsAndSitesIndex = 35;
        typeIndex = 15;
        this.hasDeltaScores = hasDeltaScores;
    }

    public toPyMOL(File xilmass, File prediction, File findingToPyMOLScript, boolean isCarbonAlpha, boolean is4Q57Asked,
            int xlinkingProsAndSitesIndex, int typeIndex,
            int pepASeqIndex, int pepBSeqIndex,
            int proAccIndex, int proBAccIndex,
            int proteinALinkIndex, int proteinBLinkIndex,
            int peptideALinkIndex, int peptideBLinkIndex) {
        this.input = xilmass;
        this.prediction = prediction;
        this.findingToPyMOLScript = findingToPyMOLScript;
        this.isCarbonAlpha = isCarbonAlpha;
        this.is4Q57Asked = is4Q57Asked;
        this.xlinkingProsAndSitesIndex = xlinkingProsAndSitesIndex;
        this.typeIndex = typeIndex;
        this.pepASeqIndex = pepASeqIndex;
        this.pepBSeqIndex = pepBSeqIndex;
        this.proteinAAccIndex = proAccIndex;
        this.proteinBAccIndex = proBAccIndex;
        this.proteinALinkIndex = proteinALinkIndex;
        this.proteinBLinkIndex = proteinBLinkIndex;
        this.peptideALinkIndex = peptideALinkIndex;
        this.peptideBLinkIndex = peptideBLinkIndex;
    }

    private void plotIDentifiedXLinkingSitesAlt() throws IOException {
        // read each line on prediction file
        getPredictions(prediction, isCarbonAlpha);
        String line = "";
        BufferedReader br = new BufferedReader(new FileReader(input));
        BufferedWriter bw = new BufferedWriter(new FileWriter(findingToPyMOLScript));
        int times = 1;
        HashSet<PyMOLEntry> uniqueCrossLinkingSites = new HashSet<PyMOLEntry>();
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("SpectrumFile")) {
                String[] sp = line.split("\t");
                String crossLinkingSite = sp[xlinkingProsAndSitesIndex].replace(" ", ""),
                        type = sp[typeIndex].replace(" ", ""),
                        pepAseq = sp[pepASeqIndex].replace(" ", ""),
                        pepBseq = sp[pepBSeqIndex].replace(" ", ""),
                        firstAcc = sp[proteinAAccIndex].replace(" ", ""),
                        secondAcc = sp[proteinBAccIndex].replace(" ", "");
                int firstIndexPeptide = Integer.parseInt(sp[proteinALinkIndex].replace(" ", "")),
                        secondIndexPeptide = Integer.parseInt(sp[proteinBLinkIndex].replace(" ", "")),
                        linkAprotein = Integer.parseInt(sp[peptideALinkIndex].replace(" ", "")),
                        linkBprotein = Integer.parseInt(sp[peptideBLinkIndex].replace(" ", ""));
                char residueA = pepAseq.charAt(linkAprotein - 1),
                        residueB = pepBseq.charAt(linkBprotein - 1);
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
                    String pdbName = "4Q57.pdb";
                    PyMOLEntry entry = null;
                    if (crossLinkingSite.equals("4Q57:B_80_P62158_31")) {
//                        System.out.println("here...");
                    }
                    if (firstAcc.equals("P62158") && secondAcc.equals("P62158") && !is4Q57Asked) {
                        pdbName = "2F3Y.pdb";
                        int tmpFirstIndex = firstIndexPeptide - 1,
                                tmpSecondIndex = secondIndexPeptide - 1;
                        if (tmpFirstIndex <= 3 || tmpSecondIndex <= 3 || tmpFirstIndex >= 148 || tmpSecondIndex == 148) {
                            String missing = type + "_missing";
                            entry = preparePyMolCommand(residueA, residueB, pdbName, tmpFirstIndex, tmpSecondIndex, missing, "A", "A", times, bw, crossLinkingSite);
                        } else {
                            entry = preparePyMolCommand(residueA, residueB, pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "A", times, bw, crossLinkingSite);
                        }
                    } else if (!firstAcc.equals("P62158") && secondAcc.equals("P62158") && is4Q57Asked) {
                        int tmpFirstIndex = firstIndexPeptide + 19,
                                tmpSecondIndex = secondIndexPeptide - 1;
                        if (firstIndexPeptide < 3) {
                            tmpFirstIndex = firstIndexPeptide - 4;
                        }
                        // only partial info...
                        if (tmpSecondIndex <= 73 && tmpSecondIndex >= 9) {
                            entry = preparePyMolCommand(residueA, residueB, pdbName, tmpFirstIndex, tmpSecondIndex, type, "B", "A", times, bw, crossLinkingSite);
                        } else {
                            String missing = type + "_missing";
                            entry = preparePyMolCommand(residueA, residueB, pdbName, tmpFirstIndex, tmpSecondIndex, missing, "B", "A", times, bw, crossLinkingSite);
                        }
                    } else if (firstAcc.equals("P62158") && !secondAcc.equals("P62158") && is4Q57Asked) {
                        int tmpFirstIndex = firstIndexPeptide - 1,
                                tmpSecondIndex = secondIndexPeptide + 19;
                        if (secondIndexPeptide < 3) {
                            tmpSecondIndex = secondIndexPeptide - 4;
                        }
                        if (tmpFirstIndex <= 73 && tmpFirstIndex >= 9) {
                            entry = preparePyMolCommand(residueA, residueB, pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "B", times, bw, crossLinkingSite);
                        } else {
                            String missing = type + "_missing";
                            entry = preparePyMolCommand(residueA, residueB, pdbName, tmpFirstIndex, tmpSecondIndex, missing, "A", "B", times, bw, crossLinkingSite);
                        }
                    } else if (!firstAcc.equals("P62158") && !secondAcc.equals("P62158") && is4Q57Asked) {
                        int tmpFirstIndex = firstIndexPeptide + 19,
                                tmpSecondIndex = secondIndexPeptide + 19;
                        if (firstIndexPeptide < 3) {
                            tmpFirstIndex = firstIndexPeptide - 4;
                        }
                        if (secondIndexPeptide < 3) {
                            tmpSecondIndex = secondIndexPeptide - 4;
                        }
                        entry = preparePyMolCommand(residueA, residueB, pdbName, tmpFirstIndex, tmpSecondIndex, type, "B", "B", times, bw, crossLinkingSite);
                    } else if (firstAcc.equals("P62158") && secondAcc.equals("P62158") && is4Q57Asked) {
                        // indices -1
                        int tmpFirstIndex = firstIndexPeptide - 1,
                                tmpSecondIndex = secondIndexPeptide - 1;
                        if (tmpFirstIndex <= 73 && tmpFirstIndex >= 9 && tmpSecondIndex <= 73 && tmpSecondIndex >= 9) {
//                            entry = preparePyMolCommand(residueA, residueB, pdbName, tmpFirstIndex, tmpSecondIndex, type, "A", "A", times, bw, crossLinkingSite);
                        } else if ((tmpFirstIndex <= 3 || tmpFirstIndex >= 148) || (tmpSecondIndex <= 3 || tmpSecondIndex >= 148)) {
                            String missing = type + "_missing";
                            // This will be retrieve from only 2F3Y structure.
//                            entry = preparePyMolCommand(residueA, residueB, pdbName, tmpFirstIndex, tmpSecondIndex, missing, "A", "A", times, bw, crossLinkingSite);
                        }
                    }
                    if (entry != null) {
                        uniqueCrossLinkingSites.add(entry);
                    }
                }
            }
        }
        // now write all identified cross-linking sites to map onto structure.
        int occ = 0;
        for (PyMOLEntry e : uniqueCrossLinkingSites) {
            times++;
            String name = names.get(e.getCrossLinkingSite()) + "_" + occ;
            write(bw, e, e.getType(), name);
        }
        bw.close();
    }

    private PyMOLEntry preparePyMolCommand(char resA, char resB, String pdbName, int tmpFirstIndex, int tmpSecondIndex, String type,
            String infoA, String infoB, int times, BufferedWriter bw, String crossLinkingSite) throws IOException {
        String atomA = decideLetter(resA, tmpFirstIndex, infoA),
                atomB = decideLetter(resB, tmpSecondIndex, infoB),
                name = type + times;
        PyMOLEntry entry = new PyMOLEntry(pdbName, name, atomA, atomB, "", "", "", "", "", "", "", "", "", isCarbonAlpha, false, crossLinkingSite);
        write(bw, entry, type, name);
        commands.put(crossLinkingSite, entry);
        names.put(crossLinkingSite, name);
        return entry;
    }

    private String decideLetter(char resA, int tmpFirstIndex, String infoA) {
        String atom = "LYS_" + tmpFirstIndex + "_" + infoA + "_CB";
        if (resA == 'G') {
            atom = "GLY_" + tmpFirstIndex + "_" + infoA + "_CB";
        } else if (resA == 'A') {
            atom = "ALA_" + tmpFirstIndex + "_" + infoA + "_CB";
        } else if (resA == 'P') {
            atom = "PRO_" + tmpFirstIndex + "_" + infoA + "_CB";
        } else if (resA == 'M') {
            atom = "MET_" + tmpFirstIndex + "_" + infoA + "_CB";
        }
        return atom;
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
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("PDBS")) {
                String[] sp = line.split("\t");
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
                String name = type;
                PyMOLEntry o = new PyMOLEntry(pdb, name, atomA, atomB, idDis, type, uniprotAccA, uniprotIndexA, uniprotAccB, uniprotIndexB, sas, betaDist, alphaDist, isCarbonAlpha, true, crossLinkingSite);
                commands.put(crossLinkingSite, o);
                names.put(crossLinkingSite, name);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        boolean isCarbonAlpha = true,
                toWrite = false,
                is4Q57Asked = true;
        // 0-10-20 for Xilmass, 1-11-21 for Kojak and 2-12-22 for pLink
        int algorithm = 22;
        boolean hasDeltaScores = true;

        String algorithm_name = "xilmass",
                n = "xilmass";
        if (algorithm == 1) {
            algorithm_name = "kojak";
            n = "kojak";
        } else if (algorithm == 2) {
            algorithm_name = "pLink";
            n = "pLink";
        } else if (algorithm == 10) {
            algorithm_name = "xilmassCommon";
            n = "xilmassCommon";
        } else if (algorithm == 11) {
            algorithm_name = "kojakCommon";
            n = "kojakCommon";
        } else if (algorithm == 12) {
            algorithm_name = "pLinkCommon";
            n = "pLinkCommon";
        } else if (algorithm == 20) {
            algorithm_name = "xilmassOnly";
            n = "xilmassOnly";
        } else if (algorithm == 21) {
            algorithm_name = "kojakOnly";
            n = "kojakOnly";
        } else if (algorithm == 22) {
            algorithm_name = "pLinkOnly";
            n = "pLinkOnly";
        }
        String structureInfo = "2F3Y";
        if (is4Q57Asked) {
            structureInfo = "4Q57";
        }

        String dataset = "qexactiveplus";
        File //input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\optqexactive/validated_list\\cross_linking_sites/" + n + "_crosslinkingsites.txt"),
                //input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\xilmass\\runs\\incl_monolinkeds\\allFDR/td_incl_monolinked_bestsetting_xilmass_allFDR_crosslinkingsites.txt"),
                //input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\optqexactive/intersectXilmassXPSMs.txt"),
                //input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\optqexactive/only_xilmass_SplitFDR005.txt"), // option=Xilmass-Specific
                //input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\optqexactive/only_kojak_SplitFDR005.txt"), // option=Kojak-Specific
                input = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\optqexactive/only_pLink_SplitFDR005.txt"),
                prediction = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_pymol_manul_autoAAcal_4Q57B.txt"),
                f = new File(""),
                pymolScript = new File(""),
                calculatedDistance = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol/optQExactivePlus/xilmass/inclMono/" + algorithm_name + "_" + dataset + "_SplitFDR005_" + structureInfo + "_DistToCompute.pml"),
                findingToPyMOLScript = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol/optQExactivePlus/xilmass/inclMono/" + algorithm_name + "_" + dataset + "_SplitFDR005_" + structureInfo + ".pml"),
                finalText = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol/optQExactivePlus/xilmass/inclMono/" + algorithm_name + "_" + dataset + "_SplitFDR005_" + structureInfo + "_to_plot.pml");
        // xilmass = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\results\\figure2_3softwareComparison/xilmass_qexactive_percolatorFDR005.txt");
        String distanceOutput = "C:\\Users\\Sule\\Documents\\PhD\\XLinked\\PyMol/optQExactivePlus/xilmass/inclMono/" + algorithm_name + "_" + dataset + "_ALLFDR005_" + structureInfo + "_computedDistance.txt";
        int xlinkingProsAndSitesIndex = 19,
                typeIndex = 9,
                pepASeqIndex = 7,
                pepBSeqIndex = 8,                
                proAAccIndex = 5,
                proBAccIndex = 6,                
                proteinALinkIndex = 10,
                proteinBLinkIndex = 11,
                peptideALinkIndex = 12,
                peptideBLinkIndex = 13;
        int option = 2;
        toPyMOL obj = null;
        if (algorithm == 0 || algorithm == 10 || algorithm == 20) {
            obj = new toPyMOL(input, prediction, findingToPyMOLScript, isCarbonAlpha, is4Q57Asked, hasDeltaScores);
            // Kojak identifications...
        } else if (algorithm == 1 || algorithm == 11 || algorithm == 21) {
            obj = new toPyMOL(input, prediction, findingToPyMOLScript, isCarbonAlpha, is4Q57Asked,
                    xlinkingProsAndSitesIndex, typeIndex,
                    pepASeqIndex, pepBSeqIndex, proAAccIndex, proBAccIndex,
                    proteinALinkIndex, proteinBLinkIndex, peptideALinkIndex, peptideBLinkIndex);
            // pLink identifications
        } else if (algorithm == 2 || algorithm == 12 || algorithm == 22) {
            xlinkingProsAndSitesIndex = 25;
            typeIndex = 19;
            pepASeqIndex = 11;
            pepBSeqIndex = 15;
            proAAccIndex = 10;
            proBAccIndex = 14;
            proteinALinkIndex = 12;
            proteinBLinkIndex = 16;
            peptideALinkIndex = 13;
            peptideBLinkIndex = 17;

            obj = new toPyMOL(input, prediction, findingToPyMOLScript, isCarbonAlpha, is4Q57Asked,
                    xlinkingProsAndSitesIndex, typeIndex,
                    pepASeqIndex, pepBSeqIndex, proAAccIndex, proBAccIndex,
                    proteinALinkIndex, proteinBLinkIndex, peptideALinkIndex, peptideBLinkIndex);
        }

        // print cross-linking parts..
        if (option == 0) {
            obj.plotIDentifiedXLinkingSitesAlt();
        } else if (option == 2) {
            obj.plotIDentifiedXLinkingSitesAlt();
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
