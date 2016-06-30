/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.outcome.Outcome;
import analyse.CXPSM.outcome.XilmassResult;
import com.google.common.io.Files;
import config.ConfigHolder;
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
import java.util.Random;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 * This class merges xilmass outputs together to return validated list of
 * CX-PSMs
 *
 * @author Sule
 */
public class AnalyzeXilmass extends AnalyzeOutcomes {

    private File xilmassFolder,
            output,
            allXPSMs;
    private int proteinAaccession = 10,
            protinBaccession = 13,
            spectrum_title_index = 1;
    private double fdr,
            fdr_inter,
            fdr_intra;
    private HashSet<String> contaminant_MSMS = new HashSet<String>();
    private HashSet<XilmassResult> validatedPSMs = new HashSet<XilmassResult>();
    private boolean doesContainsCPeptidePattern, // to store CPeptideFragment info on the output
            doesContainsIonWeight, // to store ion weights..
            isMS1PPM, // is MS1Err calculated with PPM... true:PPM false:Da - to write unit on the table
            doessplit = false, // doessplit=T means that given Xilmass input splits into two sub-groups
            hasPredictions = false; // T: if Xwalk predictions are avaliable from a customized input; F: no Xwalk prediction
    private static final Logger LOGGER = Logger.getLogger(AnalyzeXilmass.class);

    public AnalyzeXilmass(File xilmassFolder, File output, File prediction_file, File psms_contaminant,
            double fdr, boolean isConventionalFDR, boolean isMS1PPM, boolean doesContainsCPeptidePattern, boolean doesContainsIonWeight,
            File allXPSMs, String scoringFunctionName) throws IOException {
        super.target_names = target_names;
        super.psms_contaminant = psms_contaminant;
        super.prediction_file = prediction_file;
        super.isPIT = isConventionalFDR;
        this.xilmassFolder = xilmassFolder;
        this.fdr = fdr;
        this.output = output;
        contaminant_MSMS = getContaminant_MSMS();
        this.isMS1PPM = isMS1PPM;
        this.doesContainsCPeptidePattern = doesContainsCPeptidePattern;
        this.doesContainsIonWeight = doesContainsIonWeight;
        this.allXPSMs = allXPSMs;
        super.scoringFunctionName = scoringFunctionName;
    }

    public AnalyzeXilmass(File xilmassFolder, File output, File prediction_file, File psms_contaminant,
            double fdr, boolean isConventionalFDR, boolean isMS1PPM, boolean doesContainsCPeptidePattern, boolean doesContainsIonWeight,
            double fdr_inter, double fdr_intra, File allXPMs, String scoringFunctionName) throws IOException {
        super.target_names = target_names;
        super.psms_contaminant = psms_contaminant;
        super.prediction_file = prediction_file;
        super.isPIT = isConventionalFDR;
        this.xilmassFolder = xilmassFolder;
        this.fdr = fdr;
        this.output = output;
        contaminant_MSMS = getContaminant_MSMS();
        this.isMS1PPM = isMS1PPM;
        this.doesContainsCPeptidePattern = doesContainsCPeptidePattern;
        this.doesContainsIonWeight = doesContainsIonWeight;
        this.doessplit = true;
        this.fdr_inter = fdr_inter;
        this.fdr_intra = fdr_intra;
        this.allXPSMs = allXPMs;
        super.scoringFunctionName = scoringFunctionName;
    }

    public AnalyzeXilmass(File xilmassFolder, File output, File psms_contaminant,
            double fdr, boolean isConventionalFDR, boolean doessplit, boolean isMS1PPM,
            double fdr_inter, double fdr_intra, File allXPSMs, String scoringFunctionName) throws IOException {
        super.target_names = target_names;
        super.psms_contaminant = psms_contaminant;
        super.prediction_file = prediction_file;
        super.isPIT = isConventionalFDR;
        this.doessplit = doessplit;
        this.xilmassFolder = xilmassFolder;
        this.fdr = fdr;
        this.output = output;
//        contaminant_MSMS = getContaminant_MSMS();
        this.isMS1PPM = isMS1PPM;
        this.doesContainsCPeptidePattern = false;
        this.doesContainsIonWeight = false;
        this.doessplit = true;
        this.fdr_inter = fdr_inter;
        this.fdr_intra = fdr_intra;
        this.allXPSMs = allXPSMs;
        super.scoringFunctionName = scoringFunctionName;
        hasPredictions = false;
    }

    /**
     * This method read each Xilmass file on the given folder. Then, it collects
     * every PSM on these Xilmass files Followed by finding a list of PSMs with
     * a given FDR This list of validated PSMs is written on the output file
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    @Override
    public void run() throws FileNotFoundException, IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write(writeTitle() + "\n");
        HashSet<XilmassResult> psmsList = new HashSet<XilmassResult>();
        for (File f : xilmassFolder.listFiles()) {
            if (f.getName().endsWith("_xilmass.txt")) {
                LOGGER.info(f.getName());
                // read each file and fill hashset with all PSMs
                HashSet<XilmassResult> tmp_list = read_and_fill(f);
                // merge all together
                psmsList.addAll(tmp_list);
            }
        }
        if (!doessplit) {
            ArrayList<XilmassResult> res = new ArrayList<XilmassResult>(psmsList);
            // write them all
            writeAllXPSMs(new HashSet<Outcome>(psmsList), hasPredictions);

            // sort filled list        
            Collections.sort(res, XilmassResult.ScoreDSC);
            ArrayList<Outcome> res2 = new ArrayList<Outcome>();
            for (int i = 0; i < res.size(); i++) {
                res2.add(res.get(i));
            }
            // select PSMs with a given FDR value for XPSMs
            ArrayList<Outcome> validatedOutcome = getValidatedPSMs(res2, fdr, true);
            // to fill validated PSMs list with XilmassResult objects
            for (Outcome o : validatedOutcome) {
                validatedPSMs.add((XilmassResult) o);
            }
        } else {
            ArrayList<Outcome> res_interPro = getSubsetRes(psmsList, true),
                    res_intraPro = getSubsetRes(psmsList, false);
            // select PSMs with a given FDR value
            ArrayList<Outcome> validatedOutcome = getValidatedPSMs(res_interPro, fdr_inter, true),
                    validatedOutcome_intraPro = getValidatedPSMs(res_intraPro, fdr_intra, true);
            validatedOutcome.addAll(validatedOutcome_intraPro);
            // to fill validated PSMs list with XilmassResult objects
            for (Outcome o : validatedOutcome) {
                validatedPSMs.add((XilmassResult) o);
            }
            res_interPro.addAll(res_intraPro);
            // writing down all inputs now...
            writeAllXPSMs(new HashSet<Outcome>(res_interPro), hasPredictions);
        }
        // sort filled list 
        ArrayList<XilmassResult> validatedPSMSAL = new ArrayList<XilmassResult>(validatedPSMs);
        Collections.sort(validatedPSMSAL, XilmassResult.ScoreDSC);
        writeOutput(validatedPSMSAL, bw, hasPredictions);
        bw.close();

        // move validated and allxpsms to a given folder
        Files.copy(output, new File(xilmassFolder.getAbsolutePath() + File.separator + output.getName()));
        output.delete();
        Files.copy(allXPSMs, new File(xilmassFolder.getAbsolutePath() + File.separator + allXPSMs.getName()));
        allXPSMs.delete();
    }

    public void writeAllXPSMs(HashSet<Outcome> currentXPSMs, boolean hasPredictions) throws IOException {
        BufferedWriter bw2 = new BufferedWriter(new FileWriter(allXPSMs));
        String title = "SpectrumFile" + "\t" + "SpectrumTitle" + "\t" + "ObservedMass(Da)" + "\t" + "PrecursorCharge" + "\t" + "RetentionTime(Seconds)" + "\t" + "Scan" + "\t"
                + "MS1Err(PPM)" + "\t" + "AbsMS1Err(PPM)" + "\t" + "CalculatedMass(Da)" + "\t"
                + "peptideA" + "\t" + "proteinA" + "\t" + "ModA" + "\t" + "peptideB" + "\t" + "proteinB" + "\t" + "ModB" + "\t"
                + "XLType" + "\t" + "pepLinkA" + "\t" + "pepLinkB" + "\t" + "linkA" + "\t" + "linkB" + "\t"
                + "ScoringFunctionName" + "\t" + "Score" + "\t" + "DeltaScore" + "\t"
                + "lnNumSp" + "\t" + "lnNumXSp" + "\t"
                + "MatchedPeakList" + "\t" + "TheoMatchedPeakList" + "\t"
                + "TargetDecoy" + "\t" + "LinkerLabeling";
        if (hasPredictions) {
            title += "\t" + "Predicted" + "\t" + "SASDist" + "\t" + "EuclideanDistance(Carbon-betas-A)" + "\t" + "EuclideanDistance (Carbon alphas-A)" + "\t" + "CPeptidePattern";
        }
        bw2.write(title);
        bw2.newLine();
        for (Outcome o : currentXPSMs) {
            XilmassResult x = (XilmassResult) o;
            bw2.write(x.toPrint(hasPredictions));
            bw2.newLine();
        }
        bw2.close();
    }

    public ArrayList<Outcome> retrieveValidatedPSMs(ArrayList<XilmassResult> res, double fdr) {
        Collections.sort(res, XilmassResult.ScoreDSC);
        ArrayList<Outcome> res2 = new ArrayList<Outcome>(),
                validatedOutcome = new ArrayList<Outcome>();
        for (int i = 0; i < res.size(); i++) {
            res2.add(res.get(i));
        }
        try {
            // select PSMs with a given FDR value
            validatedOutcome = getValidatedPSMs(res2, fdr, true);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(AnalyzeXilmass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return validatedOutcome;
    }

    /**
     * This method writes every validated PSM on a given BufferedWriter
     *
     * @param validatedPSMs
     * @param bw
     * @throws NumberFormatException
     * @throws IOException
     */
    private void writeOutput(ArrayList<XilmassResult> validatedPSMs, BufferedWriter bw, boolean hasPredictions) throws NumberFormatException, IOException {
        // now write up..
        int size = 0;
        for (XilmassResult validatedPSM : validatedPSMs) {
            bw.write(validatedPSM.toPrint(hasPredictions));
            size++;
            if (size != validatedPSMs.size()) {
                bw.newLine();
            }
        }
    }

    /**
     * This method reads a given file and select every PSM if they are matched
     * to contaminants
     *
     * @param f xilmass results files
     * @return a list of XilmassResults objects from a given file
     * @throws FileNotFoundException
     * @throws IOException
     */
    private HashSet<XilmassResult> read_and_fill(File f) throws FileNotFoundException, IOException {
        HashSet<XilmassResult> res = new HashSet<XilmassResult>();
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("File") && !line.startsWith("SpectrumFile") && !line.startsWith("Xilmass")) {
                String[] split = line.split("\t");
                String specTitle = split[spectrum_title_index],
                        proteinA = split[proteinAaccession];
                if (!contaminant_MSMS.contains(specTitle) && !proteinA.contains("contaminant")) {
                    XilmassResult r = new XilmassResult(line, doesContainsCPeptidePattern, doesContainsIonWeight, scoringFunctionName);
                    // first set if a given ID is target or decoy or half decoy
                    if (r.getTarget_decoy().isEmpty()) {
                        String td = getTargetDecoy(r.getAccProteinA(), r.getAccProteinB());
                        r.setTarget_decoy(td);
                    }
                    res.add(r);
                } else if (contaminant_MSMS.contains(specTitle)) {
//                    System.out.println(line);
                }
            }
        }
        // remove redundant PSMs.
        res = removeRedundant(res);
        return res;
    }

    /**
     * To write a title on the output with the validated PSMs
     *
     * @return the title
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String writeTitle() throws FileNotFoundException, IOException {
        String ms1Err = "MS1Err(PPM)",
                absMS1Err = "AbsMS1Err(PPM)";
        if (!isMS1PPM) {
            ms1Err = "MS1Err(Da)";
            absMS1Err = "AbsMS1Err(Da)";
        }
        String title = "SpectrumFile" + "\t" + "SpectrumTitle" + "\t"
                + "ObservedMass(Da)" + "\t" + "PrecursorCharge" + "\t" + "RetentionTime(Seconds)" + "\t" + "Scan" + "\t"
                + ms1Err + "\t" + absMS1Err + "\t"
                + "CalculatedMass(Da)" + "\t"
                + "peptideA" + "\t" + "proteinA" + "\t" + "ModA" + "\t"
                + "peptideB" + "\t" + "proteinB" + "\t" + "ModB" + "\t"
                + "XLType" + "\t"
                + "pepLinkA" + "\t" + "pepLinkB" + "\t" + "linkA" + "\t" + "linkB" + "\t"
                + "ScoringFunctionName" + "\t" + "Score" + "\t" + "DeltaScore" + "\t"
                + "lnNumSp" + "\t" + "lnNumXSp" + "\t"
                + "MatchedPeakList" + "\t" + "TheoMatchedPeakList" + "\t"
                + "TargetDecoy" + "\t"
                + "LinkerLabeling";
        if (hasPredictions) {
            title += "\t" + "Predicted" + "\t" + "SASDist" + "\t" + "EuclideanDistance(Carbon-betas-A)" + "\t" + "EuclideanDistance (Carbon alphas-A)";
        }
        if (doesContainsCPeptidePattern) {
            title += "\t" + "CPeptidePattern";
        }
        return title;
    }

    public HashSet<XilmassResult> getValidatedPSMs() {
        return validatedPSMs;
    }

    public void setValidatedPSMs(HashSet<XilmassResult> validatedPSMs) {
        this.validatedPSMs = validatedPSMs;
    }

    public static HashSet<XilmassResult> removeRedundant(HashSet<XilmassResult> res) {
        HashMap<String, ArrayList<XilmassResult>> scan_and_res = new HashMap<String, ArrayList<XilmassResult>>();
        HashSet<XilmassResult> nonredundantPSMs = new HashSet<XilmassResult>();
        for (XilmassResult r : res) {
            if (scan_and_res.containsKey(r.getScanNr())) {
                scan_and_res.get(r.getScanNr()).add(r);
            } else {
                ArrayList<XilmassResult> rs = new ArrayList<XilmassResult>();
                rs.add(r);
                scan_and_res.put(r.getScanNr(), rs);
            }
        }
        // now check
        for (String scn : scan_and_res.keySet()) {
            if (scan_and_res.get(scn).size() > 1) {
                ArrayList<XilmassResult> targets = new ArrayList<XilmassResult>(),
                        decoys = new ArrayList<XilmassResult>(),
                        half_decoys = new ArrayList<XilmassResult>();
                // organize the list..
                for (XilmassResult r : scan_and_res.get(scn)) {
                    if (r.getTarget_decoy().equals("TT")) {
                        targets.add(r);
                    } else if (r.getTarget_decoy().equals("TD") || r.getTarget_decoy().equals("DT")) {
                        decoys.add(r);
                    } else if (r.getTarget_decoy().equals("DD")) {
                        half_decoys.add(r);
                    }
                }
                // select target..
                int rndIndex = 0;
                if (targets.size() >= 1) {
                    rndIndex = new Random().nextInt(targets.size());
                    nonredundantPSMs.add(targets.get(rndIndex));
                } else if (half_decoys.size() > 1) {
                    rndIndex = new Random().nextInt(half_decoys.size());
                    nonredundantPSMs.add(half_decoys.get(rndIndex));
                } else if (decoys.size() > 1) {
                    rndIndex = new Random().nextInt(decoys.size());
                    nonredundantPSMs.add(decoys.get(rndIndex));
                }
            } else {
                nonredundantPSMs.add(scan_and_res.get(scn).get(0));
            }
        }
        return nonredundantPSMs;
    }

    private ArrayList<Outcome> getSubsetRes(HashSet<XilmassResult> psmsList, boolean doesInterProtein) {
        ArrayList<XilmassResult> subsetPSMs = new ArrayList<XilmassResult>();
        for (XilmassResult r : psmsList) {
            if (r.getType().equals("interProtein") && doesInterProtein) {
                subsetPSMs.add(r);
            } else if (!r.getType().equals("interProtein") && !doesInterProtein) {
                subsetPSMs.add(r);
            }
        }
        // sort filled list     
        Collections.sort(subsetPSMs, XilmassResult.ScoreDSC);
        ArrayList<Outcome> res2 = new ArrayList<Outcome>();
        for (int i = 0; i < subsetPSMs.size(); i++) {
            res2.add(subsetPSMs.get(i));
        }
        return res2;
    }

}
