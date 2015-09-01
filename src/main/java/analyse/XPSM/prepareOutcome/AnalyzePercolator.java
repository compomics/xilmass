/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.prepareOutcome;

import analyse.XPSM.outcome.PercolatorResult;
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
 * To read Percolator results from either Kojak or Xilmass
 *
 * @author Sule
 */
public class AnalyzePercolator extends AnalyzeOutcomes {

    private File output, // result-integrated output from list of files on kojakFolder
            folder;// percolator result files
    private HashMap<String, String> accs;
    private boolean isXilmass = false;
    private double qvalue = 0.0500;

    public AnalyzePercolator(File output, File folder, File prediction, File psms, String[] protein_names, HashMap<String, String> accs, double qvalue) throws IOException, FileNotFoundException, ClassNotFoundException, IOException, IllegalArgumentException, InterruptedException {
        super.prediction_file = prediction;
        super.psms_contaminant = psms;
        super.target_names = target_names;
        this.output = output;
        this.folder = folder;
        this.accs = accs;
        this.qvalue = qvalue;
    }

    public AnalyzePercolator(File output, File folder, File prediction, File psms, String[] protein_names, HashMap<String, String> accs, boolean isXilmass, double qvalue) throws IOException, FileNotFoundException, ClassNotFoundException, IOException, IllegalArgumentException, InterruptedException {
        super.prediction_file = prediction;
        super.psms_contaminant = psms;
        super.target_names = target_names;
        this.output = output;
        this.folder = folder;
        this.accs = accs;
        this.isXilmass = isXilmass;
        this.qvalue = qvalue;
    }

    @Override
    public void run() throws FileNotFoundException, IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        HashMap<String, HashSet<String>> contaminant_MSMSMap = super.getContaminant_MSMSMap();
        ArrayList<PercolatorResult> results = new ArrayList<PercolatorResult>();
        for (File file : folder.listFiles()) {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = "",
                    mgfName = "",
                    type = "intra_protein";
            if (file.getName().contains("inter") || file.getName().contains("Inter")) {
                type = "inter_protein";
            }
            if (!isXilmass) {
                mgfName = file.getName().substring(0, file.getName().indexOf("-percolator")) + ".mgf";
            } else {
                mgfName = file.getName().substring(0, file.getName().indexOf("_xilmass")) + ".mgf";
            }
//            System.out.println("MGFName=" + mgfName);
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("PSMId")) {
                    boolean isContaminant = false;
                    String[] split = line.split("\t");
                    String psmID = split[0],
                            peptides = split[4],
                            proteins = split[5];
                    Double score = Double.parseDouble(split[1]),
                            qValue = Double.parseDouble(split[2]),
                            posterior_error = Double.parseDouble(split[3]);
                    PercolatorResult o = new PercolatorResult(mgfName, psmID, peptides, proteins, type, score, qValue, posterior_error, accs, isXilmass);
                    // check if a spectrum is assigned to any contaminants...
                    if (contaminant_MSMSMap.containsKey(mgfName)) {
                        for (String tmpTitle : contaminant_MSMSMap.get(mgfName)) {
                            Integer scan = Integer.parseInt(tmpTitle.substring(tmpTitle.indexOf("scan") + 5, tmpTitle.length() - 1));
                            if (scan == (o.getScan())) {
                                isContaminant = true;
                            }
                        }
                    }
                    if (!isContaminant && qValue <= this.qvalue) {
                        results.add(o);
                    }
                }
            }
        }
        // write down findings
        String title = "SpectrumFile" + "\t" + "Scan" + "\t" + "Score" + "\t" + "q-value" + "\t" + "posterior_error_prob" + "\t"
                + "proteinA" + "\t" + "proteinB" + "\t" + "peptideA" + "\t" + "peptideB" + "\t" + "XLtype" + "\t"
                + "linkA" + "\t" + "linkB" + "\t"
                + "Predicted" + "\t" + "Euclidean_distance beta(A)" + "\t" + "Euclidean_distance alpha(A)";
        bw.write(title + "\n");
        for (PercolatorResult o : results) {
            String trueCrossLinking = assetTrueLinking(o.getProteinA(), o.getProteinB(), o.getLinkA(), o.getLinkB());
            bw.write(o.getMgfName() + "\t" + o.getScan() + "\t" + o.getScore() + "\t" + o.getQvalue() + "\t" + o.getPosterior_error() + "\t"
                    + o.getProteinA() + "\t" + o.getProteinB() + "\t" + o.getPeptideA() + "\t" + o.getPeptideB() + "\t" + o.getType() + "\t"
                    + o.getLinkA() + "\t" + o.getLinkB() + "\t"
                    + trueCrossLinking + "\n");
        }
        bw.close();
    }

}
