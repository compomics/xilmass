/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.prepareOutcome;

import analyse.XPSM.outcome.XilmassResult;
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
import java.util.HashSet;
import org.apache.log4j.Logger;

/**
 * This class merges xilmass outputs (labeled and not labeled ones)
 *
 * TODO: Maybe spliting interproteins and interproteins PSMs list? Not sure...
 *
 * @author Sule
 */
public class AnalyzeXilmass extends AnalyzeOutcomes {

    private File xilmassFolder,
            output;
    private int proteinAaccession = 9,
            spectrum_title_index = 1;
    private double fdr;
    private HashSet<String> contaminant_MSMS = new HashSet<String>();
    private HashSet<XilmassResult> validatedPSMs = new HashSet<XilmassResult>();
    private boolean doesContainsCPeptidePattern, // to store CPeptideFragment info on the output
            doesContainsIonWeight, // to store ion weights..
            isMS1PPM; // is MS1Err calculated with PPM... true:PPM false:Da - to write unit on the table
    private static final Logger LOGGER = Logger.getLogger(ConfigHolder.class);

    public AnalyzeXilmass(File xilmassFolder, File output, File prediction_file, File psms_contaminant,
            String[] target_names, double fdr, boolean isConventionalFDR, boolean isMS1PPM, boolean doesContainsCPeptidePattern, boolean doesContainsIonWeight) throws IOException {
        super.target_names = target_names;
        super.psms_contaminant = psms_contaminant;
        super.prediction_file = prediction_file;
        super.isConventionalFDR = isConventionalFDR;
        this.xilmassFolder = xilmassFolder;
        this.fdr = fdr;
        this.output = output;
        contaminant_MSMS = getContaminant_MSMS();
        this.isMS1PPM = isMS1PPM;
        this.doesContainsCPeptidePattern = doesContainsCPeptidePattern;
        this.doesContainsIonWeight = doesContainsIonWeight;
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
            LOGGER.info(f.getName());
            // read each file and fill hashset with all PSMs
            HashSet<XilmassResult> tmp_list = read_and_fill(f);
            // merge all together
            psmsList.addAll(tmp_list);
        }
        ArrayList<XilmassResult> res = new ArrayList<XilmassResult>(psmsList);
        // sort filled list        
        Collections.sort(res, XilmassResult.ScoreDSC);
        // select PSMs with a given FDR value
        validatedPSMs = getValidatedPSMs(res, fdr);
        // write validated PSMs
        writeOutput(validatedPSMs, bw);
        bw.close();
    }

    /**
     * This method writes every validated PSM on a given BufferedWriter
     *
     * @param validatedPSMs
     * @param bw
     * @throws NumberFormatException
     * @throws IOException
     */
    private void writeOutput(HashSet<XilmassResult> validatedPSMs, BufferedWriter bw) throws NumberFormatException, IOException {
        // now write up..
        for (XilmassResult validatedPSM : validatedPSMs) {
            bw.write(validatedPSM.toPrint());
            bw.newLine();
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
            if (!line.startsWith("File") && !line.startsWith("SpectrumFile")) {
                String[] split = line.split("\t");
                String specTitle = split[spectrum_title_index],
                        proteinA = split[proteinAaccession];
                if (!contaminant_MSMS.contains(specTitle) && !proteinA.contains("contaminant")) {
                    res.add(new XilmassResult(line, doesContainsCPeptidePattern, doesContainsIonWeight));
                }
            }
        }
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
                + "ObservedMass(Da)" + "\t" + "PrecursorCharge" + "\t" + "RetentionTime(Seconds)" + "\t" + "ScanNr" + "\t"
                + ms1Err + "\t" + absMS1Err + "\t"
                + "PeptideA" + "\t" + "ProteinA" + "\t" + "ModA" + "\t"
                + "PeptideB" + "\t" + "ProteinB" + "\t" + "ModB" + "\t"
                + "LinkingType" + "\t"
                + "LinkPeptideA" + "\t" + "LinkPeptideB" + "\t" + "LinkProteinA" + "\t" + "LinkProteinB" + "\t"
                + "ScoringFunctionName" + "\t" + "Score" + "\t"
                + "MatchedPeakList" + "\t" + "TheoMatchedPeakList" + "\t"
                + "lnNumSp" + "\t"
                + "TargetDecoy" + "\t"
                + "LinkerLabeling" + "\t"
                + "Predicted" + "\t" + "EuclideanDistance(Carbon-betas-A)" + "\t" + "EuclideanDistance (Carbon alphas-A)";
        if (doesContainsCPeptidePattern) {
            title += "\t" + "CPeptidePattern";
        }
        return title;
    }

    /**
     * This method selects the list of validated PSMs
     *
     * @param res a list of every PSM result from Xilmass
     * @param fdr is given FDR value to select PSMs
     * @return list of validated XilmassResult objects
     *
     * @throws IOException
     */
    private HashSet<XilmassResult> getValidatedPSMs(ArrayList<XilmassResult> res, double fdr) throws IOException {
        HashSet<XilmassResult> tmpValidatedPSMlist = new HashSet<XilmassResult>();
        double tmp_fdr = 0.00;
        int targets = 0,
                full_decoys = 0,
                half_decoys = 0;
        for (int i = 0; i < res.size(); i++) {
            XilmassResult r = res.get(i);
            // give a name now...
            String proteinA = r.getProteinA(),
                    proteinB = r.getProteinB();
            String td = getTargetDecoy(proteinA, proteinB);
            if (td.equals("TD")) {
                half_decoys++;
            } else if (td.equals("DD")) {
                full_decoys++;
            } else {
                targets++;
            }
            // set target/decoy name on r..
            r.setTarget_decoy(td);
            // means any decoy divided by all target..
            if (isConventionalFDR && (half_decoys > 0 || full_decoys > 0)) {
                tmp_fdr = (double) (full_decoys + half_decoys) / (double) targets;
                // means pLink based calculation..    
            } else if (!isConventionalFDR && (half_decoys > 0 || full_decoys > 0)) {
                tmp_fdr = (double) (half_decoys - full_decoys) / (double) targets;
            }
            // set true cross linking info 
            String proteinAAccession = proteinA.substring(0, proteinA.indexOf("(")),
                    proteinBAccession = proteinB.substring(0, proteinB.indexOf("("));
            String trueCrossLinking = assetTrueLinking(proteinAAccession, proteinBAccession, r.getLinkProteinA(), r.getLinkProteinB());
            // set a true cross-linking info
            r.setTrueCrossLinking(trueCrossLinking);
            // if the current FDR is smaller than given FDR..  select these PSMs into a lsit
            if (fdr >= tmp_fdr && td.equals("TT")) {
                tmpValidatedPSMlist.add(r);
            }
        }
        return tmpValidatedPSMlist;
    }

    public HashSet<XilmassResult> getValidatedPSMs() {
        return validatedPSMs;
    }

    public void setValidatedPSMs(HashSet<XilmassResult> validatedPSMs) {
        this.validatedPSMs = validatedPSMs;
    }

}
