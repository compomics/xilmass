/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.outcome.KojakResult;
import analyse.CXPSM.outcome.Outcome;
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
import org.apache.log4j.Logger;

/**
 * This method combines all given files from each run on Kojak. For each
 * spectrum, it keeps the highest score one. If there is one spectrum with more
 * than one cross linking site with the highest score, it keeps both of these
 *
 * @author Sule
 */
public class AnalyzeKojak extends AnalyzeOutcomes {

    private File output,
            kojakResultFolder,
            database;
    private static final Logger LOGGER = Logger.getLogger(ConfigHolder.class);
    private double fdr;
    private HashSet<KojakResult> validatedPSMs = new HashSet<KojakResult>();
    private boolean isValidatedPSMs = false;
    //true: (full_decoy+half_decoy)/target and false:(half_decoy-full_decoy)/target
    private HashMap<String, HashSet<String>> contaminant_MSMSMap;

    public AnalyzeKojak(File output, File kojakResultFolder, File prediction_file, File psms_contaminant, File database, String[] target_names, double fdr, boolean isConventionalFDR) throws IOException {
        super.target_names = target_names;
        super.psms_contaminant = psms_contaminant;
        super.prediction_file = prediction_file;
        this.output = output;
        this.kojakResultFolder = kojakResultFolder;
        super.psms_contaminant = psms_contaminant;
        this.database = database;
        contaminant_MSMSMap = super.getContaminant_MSMSMap();
        super.isPIT = isConventionalFDR;
        this.fdr = fdr;
    }

    @Override
    public void run() throws FileNotFoundException, IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        bw.write(writeTitle() + "\n");
        HashSet<KojakResult> psmsList = new HashSet<KojakResult>();
        for (File f : kojakResultFolder.listFiles()) {
            LOGGER.info(f.getName());
            // read each file and fill hashset with all PSMs
            HashSet<KojakResult> tmp_list = read_and_fill(f);
            // merge all together
            psmsList.addAll(tmp_list);
        }
        ArrayList<KojakResult> res = new ArrayList<KojakResult>(psmsList);
        // sort filled list        
        Collections.sort(res, KojakResult.ScoreDSC);
        ArrayList<Outcome> res2 = new ArrayList<Outcome>();
        for (int i = 0; i < res.size(); i++) {
            res2.add(res.get(i));
        }
        // select PSMs with a given FDR value
        ArrayList<Outcome> validatedOutcome = getValidatedPSMs(res2, fdr);
        for (Outcome o : validatedOutcome) {
            if (o instanceof KojakResult) {
                validatedPSMs.add((KojakResult) o);
            }
        }
        // write validated PSMs
        ArrayList<KojakResult> validatedPSMSAL = new ArrayList<KojakResult>(validatedPSMs);
        Collections.sort(validatedPSMSAL, KojakResult.ScoreDSC);
        writeOutput(validatedPSMSAL, bw);
        bw.close();
        isValidatedPSMs = true;
    }

    public File getOutput() {
        return output;
    }

    public void setOutput(File output) {
        this.output = output;
    }

    public File getKojakResultFolder() {
        return kojakResultFolder;
    }

    public void setKojakResultFolder(File kojakResultFolder) {
        this.kojakResultFolder = kojakResultFolder;
    }

    public File getDatabase() {
        return database;
    }

    public void setDatabase(File database) {
        this.database = database;
    }

    public double getFdr() {
        return fdr;
    }

    public void setFdr(double fdr) {
        this.fdr = fdr;
    }

    public HashSet<KojakResult> getValidatedPSMs() throws IOException {
        if (!isValidatedPSMs) {
            run();
        }
        return validatedPSMs;
    }

    public void setValidatedPSMs(HashSet<KojakResult> validatedPSMs) {
        this.validatedPSMs = validatedPSMs;
    }

    private HashSet<KojakResult> read_and_fill(File f) throws FileNotFoundException, IOException {
        HashSet<KojakResult> tmpKojakResults = new HashSet<KojakResult>();
        System.out.println("My name is " + "\t" + f.getName());
        String mgfFileName = f.getName().substring(0, f.getName().indexOf("_kojak")) + ".mgf";
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Kojak") && !line.startsWith("Scan")) {
                String[] sp = line.split("\t");
                String scanNumber = sp[0],
                        peptide1 = sp[8],
                        protein1 = sp[10],
                        peptide2 = sp[11],
                        protein2 = sp[13];
                double obsMass = Double.parseDouble(sp[1]),
                        psmMass = Double.parseDouble(sp[3]),
                        ppmErr = Double.parseDouble(sp[4]),
                        score = Double.parseDouble(sp[5]),
                        dScore = Double.parseDouble(sp[6]),
                        pepDiff = Double.parseDouble(sp[7]),
                        linkerMass = Double.parseDouble(sp[14]);
                // So making sure that it is indeed a cross linked one..
                if (!peptide1.equals("-") && !peptide2.equals("-")) {
                    int charge = Integer.parseInt(sp[2]),
                            link1 = Integer.parseInt(sp[9]),
                            link2 = Integer.parseInt(sp[12]);
                    String td = getTargetDecoy(protein1, protein2);
                    // just keep cross linked pairs                    
                    if (link1 != -1 && link2 != -1) {
                        protein1 = protein1.split("\\|")[1];
                        protein2 = protein2.split("\\|")[1];
                        boolean isContaminantDerived = false;
                        KojakResult kr = new KojakResult(mgfFileName, scanNumber, obsMass, charge, psmMass, ppmErr, score, dScore,
                                pepDiff, peptide1, link1, protein1, peptide2, link2, protein2, linkerMass, target_names, database, td);
                        if (contaminant_MSMSMap.containsKey(mgfFileName)) {
                            for (String tmpsScans : contaminant_MSMSMap.get(mgfFileName)) {
                                if (tmpsScans.equals(scanNumber)) {
                                    isContaminantDerived = true;
                                }
                            }
                        }
                        if (!isContaminantDerived) {
                            // check if true-cross-linking is ready..
                            if (kr.getTrueCrossLinking().isEmpty()) {
                                // set true cross linking info 
                                String trueCrossLinking = assetTrueLinking(kr.getAccProteinA(), kr.getAccProteinB(), kr.getCrossLinkedSitePro1(), kr.getCrossLinkedSitePro2());
                                // set a true cross-linking info
                                kr.setTrueCrossLinking(trueCrossLinking);
                            }
                            tmpKojakResults.add(kr);
                        }
                    }
                }
            }
        }
        return tmpKojakResults;
    }

    private String writeTitle() {
        String title = "SpectrumFile" + "\t" + "ScanNr" + "\t"
                + "ObservedMass(Da)" + "\t" + "PrecursorCharge" + "\t"
                + "PSM_Mass" + "\t"
                + "Score" + "\t" + "dScore" + "\t" + "PepDiff" + "\t"
                + "PeptideA" + "\t" + "ProteinA" + "\t" + "ModA" + "\t"
                + "PeptideB" + "\t" + "ProteinB" + "\t" + "ModB" + "\t"
                + "LinkPeptideA" + "\t" + "LinkPeptideB" + "\t" + "LinkProteinA" + "\t" + "LinkProteinB" + "\t"
                + "TargetDecoy" + "\t"
                + "LinkerLabeling" + "\t"
                + "Predicted" + "\t" + "EuclideanDistance(Carbon-betas-A)" + "\t" + "EuclideanDistance (Carbon alphas-A)";

        return title;
    }

    private void writeOutput(ArrayList<KojakResult> validatedPSMs, BufferedWriter bw) throws IOException {
        for (KojakResult kr : validatedPSMs) {
            bw.write(kr.toPrint() + "\n");
        }
    }
}
