/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.outcome.PLinkValidatedResult;
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

/**
 * This class combines all given files from different pLink validated PSM
 * results on a given folder. It merges these all together and then also assess their
 * target/decoy and true linking situations. Note that on a given pLink validated
 * PSM result, there is only one cross linked peptides matched! They keep the
 * best scored on on their files!
 *
 * @author Sule
 */
public class AnalyzePLinkValidatedResult extends AnalyzeOutcomes {

    private File file, // a folder which contains all ".xls" result files to be merged
            output; // a merged validated PSM resuts

    public AnalyzePLinkValidatedResult(File file, File output, File prediction_file, File psms_contaminant, String[] target_names) {
        this.file = file;
        this.output = output;
        super.prediction_file = prediction_file;
        super.psms_contaminant = psms_contaminant;
        super.target_names = target_names;
    }

    @Override
    public void run() throws FileNotFoundException, IOException {
        HashMap<String, HashSet<String>> contaminant_MSMSMap = super.getContaminant_MSMSMap();
        ArrayList<PLinkValidatedResult> results = new ArrayList<PLinkValidatedResult>();
        // There need to be a folder which all files are stored.
        System.out.println("My name is " + file.getName());
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = "",
                spectrumTitle = "",
                spectrumFile = "",
                scanNum = "";
        double score = 0;
        boolean isSpecFileInfoChecked = false,
                isContaminant = false;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("#") && line.startsWith("*") && isSpecFileInfoChecked) {
                String[] split = line.split("\t");
                String peptide_pair = split[2],
                        mod = split[4],
                        proteins = split[9];
                double calcM = Double.parseDouble(split[6]),
                        deltaM = Double.parseDouble(split[7]),
                        ppm = Double.parseDouble(split[8]);

                // before check if it is assigned to contaminants
                if (contaminant_MSMSMap.containsKey(spectrumFile)) {
                    for (String contaminant : contaminant_MSMSMap.get(spectrumFile)) {
                        if (contaminant.equals(spectrumTitle)) {
                            isContaminant = true;
                        }
                    }
                }
                //String spectrumFile, String spectrumTitle, String scanNumber, 
                if (!isContaminant) {
                    PLinkValidatedResult r = new PLinkValidatedResult(spectrumFile, spectrumTitle, scanNum,
                            score, calcM, deltaM, ppm, peptide_pair, mod, proteins, target_names);
                    results.add(r);
                    isSpecFileInfoChecked = false;
                }
                spectrumFile = "";
                scanNum = "";
            } else if (!line.startsWith("#") && !isSpecFileInfoChecked && !line.startsWith("*")) {
                String[] split = line.split("\t");
                spectrumTitle = split[1];
                score = Double.parseDouble(split[5]);
                isSpecFileInfoChecked = true;
                // TODO: This part may vary from one to another pLink runs
//                spectrumFile = spectrumTitle.substring(spectrumTitle.indexOf("File:") + 5);
//                spectrumFile = spectrumFile.substring(1, spectrumFile.indexOf(".raw")) + ".mgf"; // start from 1 to remove "
                scanNum = spectrumTitle.substring(spectrumTitle.indexOf("scan=") + 5);
                scanNum = scanNum.replace("\"", "");
            }
        }
        // now write result together
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        String title = "SpectrumFile" + "\t" + "SpectrumTitle" + "\t" + "Scan" + "\t" + "Score(E-value)" + "\t" + "Calc_M" + "\t" + "Delta_M" + "\t" + "PPM" + "\t"
                + "Labeled" + "\t" + "PeptidePairs" + "\t" + "Modification" + "\t"
                + "proteinA" + "\t" + "peptideA" + "\t" + "linkA" + "\t"
                + "proteinB" + "\t" + "peptideB" + "\t" + "linkB" + "\t"
                + "Target_Decoy" + "\t"
                + "Predicted" + "\t" + "Euclidean_distance_Alpha(A)" + "\t" + "Euclidean_distance_Beta(A)";
        bw.write(title + "\n");
        for (PLinkValidatedResult r : results) {
            String toWrite =r.getSpectrumFileName() + "\t" + r.getSpectrumTitle() + "\t" + r.getScanNumber() + "\t"
                    + r.getpLinkScore() + "\t" + r.getCalc_m() + "\t" + r.getDelta_m() + "\t" + r.getPpm() + "\t"
                    + r.getLabel() + "\t" + r.getPeptide_pairs() + "\t" + r.getModifications() + "\t"
                    + r.getAccProteinA()+ "\t" + r.getPeptideA() + "\t" + r.getCrossLinkedSitePro1() + "\t"
                    + r.getAccProteinB()+ "\t" + r.getPeptideB() + "\t" + r.getCrossLinkedSitePro2() + "\t"
                    + getTargetDecoy(r.getAccProteinA(), r.getAccProteinB()) + "\t" 
                    + assetTrueLinking(r.getAccProteinA(), r.getAccProteinB(), r.getCrossLinkedSitePro1(), r.getCrossLinkedSitePro2());
            System.out.println(toWrite);
            bw.write(toWrite + "\n");
        }
        bw.close();

    }

}
