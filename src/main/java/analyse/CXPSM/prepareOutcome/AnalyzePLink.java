/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.outcome.Outcome;
import analyse.CXPSM.outcome.PLinkResult;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

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
            pLinkAlloutput, // all PSM resuts (best ranked score for xlinked pair)
            output; // validated PSMs
    private double fdr;
    private ArrayList<PLinkResult> allResults = new ArrayList<PLinkResult>();

    public AnalyzePLink(File folder, File pLinkAlloutput, File output, File prediction_file, File psms_contaminant, String[] target_names, double fdr, boolean isConventionalFDR) {
        this.folder = folder;
        this.pLinkAlloutput = pLinkAlloutput;
        this.output = output;
        super.prediction_file = prediction_file;
        super.psms_contaminant = psms_contaminant;
        super.target_names = target_names;
        super.isPIT = isConventionalFDR;
        this.fdr = fdr;
    }

    @Override
    public void run() throws FileNotFoundException, IOException {
        HashMap<String, HashSet<Integer>> contaminant_MSMSMap = super.getContaminant_specFile_and_scans();
        // There need to be a folder which all files are stored.
        for (File f : folder.listFiles()) {
            if (f.getName().endsWith("qry.proteins.txt")) {
                prepareAllList(f, contaminant_MSMSMap);
            }
        }
        // now write result together
        System.out.println(allResults.size());
        writeFile(allResults, pLinkAlloutput);
        // write only validated hits - first sort by their e-values
        Collections.sort(allResults, PLinkResult.eValueASC);
        // select PSMs with a given FDR value       
        ArrayList<Outcome> res = new ArrayList<Outcome>(allResults);
        // select PSMs with a given FDR value
        ArrayList<Outcome> validatedOutcome = getValidatedPSMs(res, fdr, true);
        ArrayList<PLinkResult> validatedPSMs = new ArrayList<PLinkResult>();
        for (Outcome o : validatedOutcome) {
            if (o instanceof PLinkResult) {
                validatedPSMs.add((PLinkResult) o);
            }
        }
        // write validated PSMs
        Collections.sort(validatedPSMs, PLinkResult.eValueASC);
        writeFile(validatedPSMs, output);
    }

    private void prepareList(File f, HashMap<String, HashSet<String>> contaminant_MSMSMap) throws NumberFormatException, FileNotFoundException, IOException {
        System.out.println("My name is " + f.getName());
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = "";
        String spectrumTitle = "",
                spectrumFile = "",
                label = "",
                alphaSeq = "",
                alphaProtein = "",
                modLocationAlpha = "",
                modNameAlpha = "",
                betaSeq = "",
                betaProtein = "",
                modLocationBeta = "",
                modNameBeta = "",
                scanNum = "";
        int charge = 0,
                xlinkPos1 = 0,
                xlinkPos2 = 0,
                validCandidate = 0,
                candidateTotal = 0;
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
        boolean isContaminant = false;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("[Spectrum")) {
                // reset booleans to control
                isContaminant = false;
            }
            if (line.startsWith("Input=")) {
                String[] sp = line.split("Input=");
                spectrumTitle = sp[1];
                // possible to get spectrum file here too..
                spectrumFile = line.split("=")[1].substring(spectrumTitle.indexOf("File:") + 5);
                spectrumFile = spectrumFile.substring(1, spectrumFile.indexOf(".raw")) + ".mgf"; // start from 1 to remove "
                scanNum = spectrumTitle.substring(spectrumTitle.indexOf("scan=") + 5);
                scanNum = scanNum.replace("\"", "");
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
                    alphaProtein = "DECOY" + alphaProtein;
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
                    betaProtein = "DECOY" + betaProtein;
                }
            }
            if (line.startsWith("NO1_Beta_Modify_Pos")) {
                modLocationBeta = line.split("=")[1];
            }
            if (line.startsWith("Candidate_Total")) {
                candidateTotal = Integer.parseInt(line.split("=")[1]);
            }
            if (line.startsWith("ValidCandidate")) {
                validCandidate = Integer.parseInt(line.split("=")[1]);
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
                if (!isContaminant) {
                    PLinkResult r = new PLinkResult(spectrumTitle, spectrumFile, scanNum, label,
                            alphaSeq, alphaProtein, modLocationAlpha, modNameAlpha,
                            betaSeq, betaProtein, modLocationBeta, modNameBeta,
                            charge,
                            xlinkPos1, xlinkPos2,
                            intensity, mH, experimentalMZ,
                            pLinkScore, eValue, alphaEValue, betaEValue,
                            matchedIntensity, unMatchedIntensity,
                            xlinkedMass,
                            target_names,
                            validCandidate,
                            candidateTotal);
                    allResults.add(r);
                    isContaminant = false;
                }
            }
        }
    }

    private void prepareAllList(File f, HashMap<String, HashSet<Integer>> contaminant_MSMSMap) throws NumberFormatException, FileNotFoundException, IOException {
        System.out.println("My name is " + f.getName());
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = "";
        String spectrumTitle = "",
                spectrumFile = "",
                label = "",
                alphaSeq = "",
                alphaProtein = "",
                modLocationAlpha = "",
                modNameAlpha = "",
                betaSeq = "",
                betaProtein = "",
                modLocationBeta = "",
                modNameBeta = "",
                scanNum = "";
        int charge = 0,
                xlinkPos1 = 0,
                xlinkPos2 = 0,
                validCandidate = 0,
                candidateTotal = 0;
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
                isInter = false;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("[Spectrum")) {
                // reset booleans to control
                isContaminant = false;
            }
            if (line.startsWith("Input=")) {
                String[] sp = line.split("Input=");
                spectrumTitle = sp[1];
                // possible to get spectrum file here too..
                spectrumFile = line.split("=")[1].substring(spectrumTitle.indexOf("File:") + 5);
                spectrumFile = spectrumFile.substring(1, spectrumFile.indexOf(".raw")) + ".mgf"; // start from 1 to remove "
                scanNum = spectrumTitle.substring(spectrumTitle.indexOf("scan=") + 5);
                scanNum = scanNum.replace("\"", "");
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
            if (line.startsWith("NO1_XLink_Type")) {
                int type = Integer.parseInt(line.split("=")[1]);
                if (type == 2) {
                    isInter = true;
                }
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
                    alphaProtein = "DECOY" + alphaProtein;
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
                    betaProtein = "DECOY" + betaProtein;
                }
            }
            if (line.startsWith("NO1_Beta_Modify_Pos")) {
                modLocationBeta = line.split("=")[1];
            }
            if (line.startsWith("Candidate_Total")) {
                candidateTotal = Integer.parseInt(line.split("=")[1]);
            }
            if (line.startsWith("ValidCandidate")) {
                validCandidate = Integer.parseInt(line.split("=")[1]);
            }

            if (line.startsWith("NO1_Beta_Modify_Name")) {
                modNameBeta = line.split("=")[1];
                // here is parsing end. check if it derives from contaminants and construct an object
                // now generate a object to keep all...
                if (contaminant_MSMSMap.containsKey(spectrumFile)) {
                    for (Integer scan : contaminant_MSMSMap.get(spectrumFile)) {
                        String tmpScanInfo = "scan=" + scan + "\"";
                        if (spectrumTitle.contains(tmpScanInfo)) {
                            isContaminant = true;
                        }
                    }
                }
                if (!isContaminant) {
                    PLinkResult r = new PLinkResult(spectrumTitle, spectrumFile, scanNum, label,
                            alphaSeq, alphaProtein, modLocationAlpha, modNameAlpha,
                            betaSeq, betaProtein, modLocationBeta, modNameBeta,
                            charge,
                            xlinkPos1, xlinkPos2,
                            intensity, mH, experimentalMZ,
                            pLinkScore, eValue, alphaEValue, betaEValue,
                            matchedIntensity, unMatchedIntensity,
                            xlinkedMass,
                            target_names,
                            validCandidate,
                            candidateTotal);
                    allResults.add(r);
                    isContaminant = false;
                }
            }
        }
    }

    public ArrayList<PLinkResult> getAllResults() {
        return allResults;
    }

    public void setAllResults(ArrayList<PLinkResult> allResults) {
        this.allResults = allResults;
    }

    private void writeFile(ArrayList<PLinkResult> results, File pLinkAlloutput) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(pLinkAlloutput));
        bw.write(getTitle() + "\n");
        for (PLinkResult r : results) {
            // first set if a given ID is target or decoy or half decoy
            if (r.getTarget_decoy().isEmpty()) {
                String td = getTargetDecoy(r.getAccProteinA(), r.getAccProteinB());
                r.setTarget_decoy(td);
            }
            if (r.getTrueCrossLinking().isEmpty()) {
                // set true cross linking info 
                String trueCrossLinking = assetTrueLinking(r.getAccProteinA(), r.getAccProteinB(), r.getCrossLinkedSitePro1(), r.getCrossLinkedSitePro2());
                // set a true cross-linking info
                r.setTrueCrossLinking(trueCrossLinking);
            }
            bw.write(r.getSpectrumFileName() + "\t" + r.getSpectrumTitle() + "\t" + r.getScanNumber() + "\t"
                    + r.getCharge() + "\t" + r.getIntensity() + "\t" + r.getExperimentalMZ() + "\t" + r.getMH() + "\t"
                    + r.getpLinkScore() + "\t" + r.geteValue() + "\t" + r.getAlphaEValue() + "\t" + r.getBetaEValue() + "\t"
                    + r.getMatchedIntensity() + "\t" + r.getUnMatchedIntensity() + "\t" + r.getXlinkedMass() + "\t"
                    + r.getLabel() + "\t"
                    + r.getAccProteinA() + "\t" + r.getModNameAlpha() + "\t" + r.getModLocationAlpha() + "\t" + r.getPeptideA() + "\t" + r.getCrossLinkedSitePro1() + "\t"
                    + r.getAccProteinB() + "\t" + r.getModNameBeta() + "\t" + r.getModLocationBeta() + "\t" + r.getPeptideB() + "\t" + r.getCrossLinkedSitePro2() + "\t"
                    + r.getTarget_decoy() + "\t"
                    + r.getTrueCrossLinking() + "\t"
                    + r.getValidCandidate() + "\t"
                    + +r.getCandidateTotal() + "\n");
        }
        bw.close();
    }

    private String getTitle() {
        String title = "SpectrumFile" + "\t" + "SpectrumTitle" + "\t" + "ScanNr" + "\t"
                + "Charge" + "\t" + "Intensity" + "\t" + "ExperimentalMZ" + "\t" + "MH" + "\t"
                + "PLinkScore" + "\t" + "E-value" + "\t" + "AlphaE-value" + "\t" + "BetaE-value" + "\t"
                + "MatchedIntensity" + "\t" + "UnmatchedIntensity" + "\t" + "XLinkedMass" + "\t"
                + "Labeled" + "\t"
                + "ProteinAlpha_Accession" + "\t" + "ModNameAlpha" + "\t" + "ModLocsAlpha" + "\t" + "PeptideAlpha" + "\t" + "LinkedSiteAlpha" + "\t"
                + "ProteinBeta_Accession" + "\t" + "ModNameBeta" + "\t" + "ModLocsBeta" + "\t" + "PeptideBeta" + "\t" + "LinkedSiteBeta" + "\t"
                + "Target_Decoy" + "\t"
                + "Predicted" + "\t" + "SASDist" + "\t" + "Euclidean_distance_Alpha(A)" + "\t" + "Euclidean_distance_Beta(A)" + "\t"
                + "ValidCandidate" + "\t" + "CandidateTotal";
        return title;
    }

}
