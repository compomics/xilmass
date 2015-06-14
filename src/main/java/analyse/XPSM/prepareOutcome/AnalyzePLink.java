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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * This class combines all given files from different pLink validated PSM
 * results on a given folder. It merges these all and then also assess their
 * target/decoy and true linking situations Note that on a given pLink validated
 * PSM result, there is only one cross linked peptides matched! They keep the
 * best scored on on their files!
 *
 * @author Sule
 */
public class AnalyzePLink extends AnalyzeOutcomes {

    private File folder, // a folder which contains all ".xls" result files to be merged
            output; // a merged validated PSM resuts
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
            if (f.getName().endsWith(".xls")) {
                System.out.println("My name is " + f.getName());
                String spectrumFile = resultFileNameForSpectrumFile.get(f.getName());
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line = "";
                String spectrumTitle = "";
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
                        if (!isContaminant) {
                            PLinkResult r = new PLinkResult(score, calcM, deltaM, ppm, spectrumTitle, spectrumFile, peptide_pair, mod, proteins, target_names);
                            results.add(r);
                            isSpecFileInfoChecked = false;
                        }
                    } else if (!line.startsWith("#") && !isSpecFileInfoChecked && !line.startsWith("*")) {
                        String[] split = line.split("\t");
                        spectrumTitle = split[1];
                        score = Double.parseDouble(split[5]);
                        isSpecFileInfoChecked = true;
                    }
                }
            }
        }
        // now write result together
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        String title = "SpectrumFileName" + "\t" + "SpectrumTitle" + "\t" + "PLinkScore(E-value)" + "\t" + "Calc_M" + "\t" + "Delta_M" + "\t" + "PPM" + "\t"
                + "Labeled" + "\t" + "PeptidePairs" + "\t" + "Modification" + "\t"
                + "Protein1" + "\t" + "Peptide1" + "\t" + "LinkSite1" + "\t"
                + "Protein2" + "\t" + "Peptide2" + "\t" + "LinkSite2" + "\t"
                + "Target_Decoy" + "\t"
                + "Predicted" + "\t" + "Euclidean_distance_Alpha(A)" + "\t" + "Euclidean_distance_Beta(A)";
        bw.write(title + "\n");
        for (PLinkResult r : results) {
            bw.write(r.getSpectrumFileName() + "\t" + r.getSpectrumTitle() + "\t"
                    + r.getpLinkScore() + "\t" + r.getCalc_m() + "\t" + r.getDelta_m() + "\t" + r.getPpm() + "\t"
                    + r.getLabel() + "\t" + r.getPeptide_pairs() + "\t" + r.getModifications() + "\t"
                    + r.getAccessProteinA() + "\t" + r.getPeptideA() + "\t" + r.getCrossLinkedSitePro1() + "\t"
                    + r.getAccessProteinB() + "\t" + r.getPeptideB() + "\t" + r.getCrossLinkedSitePro2() + "\t"
                    + r.getTargetDecoy() + "\t" + assetTrueLinking(r.getAccessProteinA(), r.getAccessProteinB(), r.getCrossLinkedSitePro1(), r.getCrossLinkedSitePro2()) + "\n");
        }
        bw.close();

    }

}
