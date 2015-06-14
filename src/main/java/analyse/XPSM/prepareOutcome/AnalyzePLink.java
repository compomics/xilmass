/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.prepareOutcome;

import analyse.XPSM.outcome.PLinkResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import start.CalculateMS1Err;
import start.CalculatePrecursorMass;

/**
 * This class combines all given files on raw scoring
 * (XNAME_inter_qry.proteins.txt) for every possible found (best-ranked per a
 * spectrum) cross linked pairs. It reads each file and then constructs
 * pLinkResult objects to write down down for evaluation.
 *
 * pLink selects the best xlinked pair based on calculated score, not evalue.
 * Higher score is good.. When a score lower, the prediction gets worse
 *
 *
 * @author Sule
 */
public class AnalyzePLink extends AnalyzeOutcomes {

    private File folder, // a folder which contains all "XXX_inter_qry.proteins.txt" files to be merged
            output; // all PSM resuts (best ranked score for xlinked pair)
    private HashMap<String, String> resultFileNameForSpectrumFile; // to get mgf file name for that pLink run

    public AnalyzePLink(File folder, File output, HashMap<String, String> resultFileNameForSpectrumFile, File prediction_file, File psms_contaminant, String[] target_names) {
        this.folder = folder;
        this.output = output;
        this.resultFileNameForSpectrumFile = resultFileNameForSpectrumFile;
        super.prediction_file = prediction_file;
        super.psms_contaminant = psms_contaminant;
        super.target_names = target_names;
    }

    @Override
    public void run() throws FileNotFoundException, IOException {
        HashMap<String, HashSet<String>> contaminant_MSMSMap = super.getContaminant_MSMSMap();
        ArrayList<PLinkResult> results = new ArrayList<PLinkResult>();
        // There need to be a folder which all files are stored.
        for (File f : folder.listFiles()) {
            if (f.getName().endsWith("qry.proteins.txt")) {
                System.out.println("My name is " + f.getName());
                String spectrumFile = resultFileNameForSpectrumFile.get(f.getName());
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = "";
                String spectrumTitle = "",
                        label = "",
                        alphaSeq = "",
                        alphaProtein = "",
                        modLocationAlpha = "",
                        modNameAlpha = "",
                        betaSeq = "",
                        betaProtein = "",
                        modLocationBeta = "",
                        modNameBeta = "";
                int charge = 0,
                        xlinkPos1 = 0,
                        xlinkPos2 = 0;
                double intensity = 0,
                        mH = 0,
                        experimentalMZ = 0,
                        pLinkScore = 0,
                        eValue = 0,
                        alphaEValue = 0,
                        betaEValue = 0,
                        matchedIntensity = 0,
                        unMatchedIntensity = 0,
                        xlinkedMass = 0;
                boolean isContaminant = false,
                        isAlphaDecoy = false,
                        isBetaDecoy = false;

                while ((line = br.readLine()) != null) {
                    if (line.startsWith("[Spectrum")) {
                        // reset booleans to control 
                        isAlphaDecoy = false;
                        isContaminant = false;
                        isBetaDecoy = false;
                    }
                    if (line.startsWith("Input")) {
                        spectrumTitle = line.split("=")[1];
                    }
                    if (line.startsWith("Charge")) {
                        charge = Integer.parseInt(line.split("=")[1]);
                    }
                    if (line.startsWith("Intensity")) {
                        intensity = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("MH")) {
                        mH = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("MZ")) {
                        experimentalMZ = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_Score=")) {
                        pLinkScore = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_EValue=")) {
                        eValue = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_AlphaEValue=")) {
                        alphaEValue = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_BetaEValue=")) {
                        betaEValue = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_MatchedIntensity=")) {
                        matchedIntensity = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_UnMatchedIntensity=")) {
                        unMatchedIntensity = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_Mass=")) {
                        xlinkedMass = Double.parseDouble(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_Linker_Id=")) {
                        label = "light";
                        int labelInfo = Integer.parseInt(line.split("=")[1]);
                        if (labelInfo == 1) {
                            label = "heavy";
                        }
                    }
                    if (line.startsWith("NO1_XLink_Pos1=")) {
                        xlinkPos1 = Integer.parseInt(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_XLink_Pos2=")) {
                        xlinkPos2 = Integer.parseInt(line.split("=")[1]);
                    }
                    if (line.startsWith("NO1_Alpha_SQ=")) {
                        alphaSeq = line.split("=")[1];
                    }
                    if (line.startsWith("NO1_Alpha_ProteinSites=")) {
                        int toAdd = Integer.parseInt(line.split(",")[1]);
                        xlinkPos1 += toAdd;
                    }
                    if (line.startsWith("NO1_Alpha_Proteins=")) {
                        alphaProtein = line.split("\\|")[1];
                        if (line.contains("REVERSE")) {
                            isAlphaDecoy = true;
                        }
                    }
                    if (line.startsWith("NO1_Alpha_Modify_Pos")) {
                        modLocationAlpha = line.split("=")[1];
                    }
                    if (line.startsWith("NO1_Alpha_Modify_Name")) {
                        modNameAlpha = line.split("=")[1];
                    }
                    if (line.startsWith("NO1_Beta_SQ=")) {
                        betaSeq = line.split("=")[1];
                    }
                    if (line.startsWith("NO1_Beta_ProteinSites=")) {
                        int toAdd = Integer.parseInt(line.split(",")[1]);
                        xlinkPos2 += toAdd;
                    }
                    if (line.startsWith("NO1_Beta_Proteins=")) {
                        betaProtein = line.split("\\|")[1];
                        if (line.contains("REVERSE")) {
                            isBetaDecoy = true;
                        }
                    }
                    if (line.startsWith("NO1_Beta_Modify_Pos")) {
                        modLocationBeta = line.split("=")[1];
                    }
                    if (line.startsWith("NO1_Beta_Modify_Name")) {
                        modNameBeta = line.split("=")[1];
                        // here is parsing end. check if it derives from contaminants and construct an object
                        // now generate a object to keep all...
                        if (contaminant_MSMSMap.containsKey(spectrumFile)) {
                            for (String contaminant : contaminant_MSMSMap.get(spectrumFile)) {
                                if (contaminant.equals(spectrumTitle)) {
                                    isContaminant = true;
                                }
                            }
                        }
                        if (!isContaminant && !isAlphaDecoy && !isBetaDecoy) {
                            PLinkResult r = new PLinkResult(spectrumTitle, spectrumFile, label,
                                    alphaSeq, alphaProtein, modLocationAlpha, modNameAlpha,
                                    betaSeq, betaProtein, modLocationBeta, modNameBeta,
                                    charge,
                                    xlinkPos1, xlinkPos2,
                                    intensity, mH, experimentalMZ,
                                    pLinkScore, eValue, alphaEValue, betaEValue,
                                    matchedIntensity, unMatchedIntensity,
                                    xlinkedMass,
                                    target_names);
                            results.add(r);
                            isContaminant = false;
                        }
                    }
                }
            }
        }
        // now write result together
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        String title = "SpectrumFileName" + "\t" + "SpectrumTitle" + "\t"
                + "Charge" + "\t" + "Intensity" + "\t" + "ExperimentalMZ" + "\t" + "MH" + "\t"
                + "PLinkScore" + "\t" + "E-value" + "\t" + "AlphaE-value" + "\t" + "BetaE-value" + "\t"
                + "MatchedIntensity" + "\t" + "UnmatchedIntensity" + "\t" + "XLinkedMass" + "\t"
                + "Labeled"+"\t"
                + "ProteinAlpha_Accession" + "\t" + "ModNameAlpha" + "\t" + "ModLocsAlpha" + "\t" + "PeptideAlpha" + "\t" + "LinkedSiteAlpha" + "\t"
                + "ProteinBeta_Accession" + "\t" + "ModNameBeta" + "\t" + "ModLocsBeta" + "\t" + "PeptideBeta" + "\t" + "LinkedSiteBeta" + "\t"
                + "Target_Decoy" + "\t"
                + "Predicted" + "\t" + "Euclidean_distance_Alpha(A)" + "\t" + "Euclidean_distance_Beta(A)";

        bw.write(title + "\n");
        for (PLinkResult r : results) {
            bw.write(r.getSpectrumFileName() + "\t" + r.getSpectrumTitle() + "\t"
                    + r.getCharge() + "\t" + r.getIntensity() + "\t" + r.getExperimentalMZ() + "\t" + r.getMH() + "\t"
                    + r.getpLinkScore() + "\t" + r.geteValue() + "\t" + r.getAlphaEValue() + "\t" + r.getBetaEValue() + "\t"
                    + r.getMatchedIntensity() + "\t" + r.getUnMatchedIntensity() + "\t" + r.getXlinkedMass() + "\t"
                    + r.getLabel() + "\t"
                    + r.getAccessProteinA() + "\t" + r.getModNameAlpha() + "\t" + r.getModLocationAlpha() + "\t" + r.getPeptideA() + "\t" + r.getCrossLinkedSitePro1() + "\t"
                    + r.getAccessProteinB() + "\t" + r.getModNameBeta() + "\t" + r.getModLocationBeta() + "\t" + r.getPeptideB() + "\t" + r.getCrossLinkedSitePro2() + "\t"
                    + r.getTargetDecoy() + "\t"
                    + assetTrueLinking(r.getAccessProteinA(), r.getAccessProteinB(), r.getCrossLinkedSitePro1(), r.getCrossLinkedSitePro2()) + "\n");
        }
        bw.close();

    }

}
