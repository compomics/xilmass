/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.outcome.PercolatorResult;
import analyse.xwalk_uniprot.LinkingProbability;
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
import java.util.Random;
import org.apache.log4j.Logger;

/**
 * To read Percolator outputs from either Kojak or Xilmass
 *
 * @author Sule
 */
public class AnalyzePercolator extends AnalyzeOutcomes {

    private File output, // result-integrated output from list of files on kojakFolder
            folder;// percolator result files
    private HashMap<String, String> accs;
    private boolean isXilmass = false;
    private double qvalue = 0.0500;
    private boolean checkLysine,
            isBasedOnManualValidation;
    private static final Logger LOGGER = Logger.getLogger(AnalyzePercolator.class);

    public AnalyzePercolator(File output, File folder, File prediction, File psms, String[] protein_names, HashMap<String, String> accs, double qvalue, boolean checkLysine, boolean isBasedOnManualValidation) throws IOException, FileNotFoundException, ClassNotFoundException, IOException, IllegalArgumentException, InterruptedException {
        super.prediction_file = prediction;
        super.psms_contaminant = psms;
        super.target_names = target_names;
        this.output = output;
        this.folder = folder;
        this.accs = accs;
        this.qvalue = qvalue;
        this.checkLysine = checkLysine;
        this.isBasedOnManualValidation = isBasedOnManualValidation;
    }

    public AnalyzePercolator(File output, File folder, File prediction, File psms, String[] protein_names, HashMap<String, String> accs, boolean isXilmass, double qvalue, boolean checkLysine, boolean isBasedOnManualValidation) throws IOException, FileNotFoundException, ClassNotFoundException, IOException, IllegalArgumentException, InterruptedException {
        super.prediction_file = prediction;
        super.psms_contaminant = psms;
        super.target_names = target_names;
        this.output = output;
        this.folder = folder;
        this.accs = accs;
        this.isXilmass = isXilmass;
        this.qvalue = qvalue;
        this.checkLysine = checkLysine;
        this.isBasedOnManualValidation = isBasedOnManualValidation;
    }

    @Override
    public void run() throws FileNotFoundException, IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
        HashMap<String, HashSet<Integer>> contaminant_MSMSMap = super.getContaminant_specFile_and_scans();
        HashMap<String, ArrayList<PercolatorResult>> id_and_percolatorResults = new HashMap<String, ArrayList<PercolatorResult>>();
        System.out.println(folder.getName());
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith(".txt")) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line = "",
                        mgfName = "",
                        type = "intraProtein";
                if (file.getName().toLowerCase().contains("inter")) {
                    type = "interProtein";
                }
                if (!isXilmass) {
                    mgfName = file.getName().substring(0, file.getName().indexOf("_percolator")) + ".mgf";
                } else {
                    mgfName = file.getName().substring(0, file.getName().indexOf("_xilmass")) + ".mgf";
                }
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("PSMId")) {
                        boolean isContaminant = false;
                        String[] split = line.split("\t");
                        String psmID = split[0],
                                peptides = split[4],
                                proteins = split[5];
                        // make sure that the paired protein can be found
                        if (split.length == 7) {
                            proteins += ("\t") + split[6];
                        }
                        Double score = Double.parseDouble(split[1]),
                                qValue = Double.parseDouble(split[2]),
                                posterior_error = Double.parseDouble(split[3]);
                        PercolatorResult o = new PercolatorResult(mgfName, psmID, peptides, proteins, type, score, qValue, posterior_error, accs, isXilmass, checkLysine);
                        // check if a spectrum is assigned to any contaminants...
                        if (contaminant_MSMSMap.containsKey(mgfName)) {
                            HashSet<Integer> contaminants = contaminant_MSMSMap.get(mgfName);
                            for (Integer tmpScan : contaminants) {
                                if (tmpScan == (o.getScan())) {
                                    isContaminant = true;
                                    System.out.println("Contaminant derived spectrum: scannum=" + tmpScan + "\t mgfName=" + mgfName + "\t" + "qvalue=" + qValue);
                                    LOGGER.info("spectra=" + mgfName + "\t" + "contaminant scan=" + tmpScan + "\t" + "qvalue=" + qValue);
                                }
                            }
                        }
                        if (!isContaminant && qValue <= this.qvalue) {
                            String id = o.getMgfName() + "_" + o.getScan();
                            if (id_and_percolatorResults.containsKey(id)) {
                                id_and_percolatorResults.get(id).add(o);
                            } else {
                                ArrayList<PercolatorResult> res = new ArrayList<PercolatorResult>();
                                res.add(o);
                                id_and_percolatorResults.put(id, res);
                            }
                        }
                    }
                }
            }
        }
        //here filter the result..
        ArrayList<PercolatorResult> filtered = filter(id_and_percolatorResults);
        // write down findings
        String title = "SpectrumFile" + "\t" + "Scan" + "\t" + "Score" + "\t" + "q-value" + "\t" + "posterior_error_prob" + "\t"
                + "proteinA" + "\t" + "proteinB" + "\t" + "peptideA" + "\t" + "peptideB" + "\t" + "XLtype" + "\t"
                + "linkA" + "\t" + "linkB" + "\t" + "pepLinkA" + "\t" + "pepLinkB" + "\t"
                + "Predicted" + "\t" + "SASDist" + "\t" + "Euclidean_distance beta(A)" + "\t" + "Euclidean_distance alpha(A)";
        bw.write(title + "\n");
        for (PercolatorResult o : filtered) {
            String trueCrossLinking = assetTrueLinking(o.getProteinA(), o.getProteinB(), o.getLinkA(), o.getLinkB());
            int pepLinkA = o.getLinkA() - accs.get(o.getProteinA()).indexOf(o.getPeptideA()),
                    pepLinkB = o.getLinkB() - accs.get(o.getProteinB()).indexOf(o.getPeptideB());
            // now write out..
            bw.write(o.getMgfName() + "\t" + o.getScan() + "\t" + o.getScore() + "\t" + o.getQvalue() + "\t" + o.getPosterior_error() + "\t"
                    + o.getProteinA() + "\t" + o.getProteinB() + "\t" + o.getPeptideA() + "\t" + o.getPeptideB() + "\t" + o.getType() + "\t"
                    + o.getLinkA() + "\t" + o.getLinkB() + "\t" + pepLinkA + "\t" + pepLinkB + "\t"
                    + trueCrossLinking + "\n");
        }
        bw.close();
    }

    /**
     * This method checks if a given results contains redundant spectra.(the
     * same spectrum with more than one identification results). This might
     * happen due to the different cross-linking locations, the same
     * modification with different locations Such spectrum will be assigned to
     * only one cross-linking site results based on the probability of this
     * location. Firstly, "possible cross-linking sites" will be preferred, then
     * likely-possible, then likely impossible, and impossible and
     * not-predicted. If there are more than one possible cross-linking site,
     * this cross-linking site will be randomly selected between these.
     *
     * @param id_and_percolatorResults
     * @return
     */
    public ArrayList<PercolatorResult> filter(HashMap<String, ArrayList<PercolatorResult>> id_and_percolatorResults) throws IOException {
        ArrayList<PercolatorResult> filteredRes = new ArrayList<PercolatorResult>();
        // for each spectrum identification, check all percolator outputs..
        for (String id : id_and_percolatorResults.keySet()) {
            ArrayList<PercolatorResult> res = id_and_percolatorResults.get(id);
            ArrayList<PercolatorResult> all = new ArrayList<PercolatorResult>();
            if (isBasedOnManualValidation) {
                ArrayList<PercolatorResult> poss = new ArrayList<PercolatorResult>(),
                        likelyposs = new ArrayList<PercolatorResult>(),
                        notpreds = new ArrayList<PercolatorResult>(),
                        impossible = new ArrayList<PercolatorResult>(),
                        likelyimpossible = new ArrayList<PercolatorResult>();
                for (PercolatorResult r : res) {
                    all.add(r);
                    String trueCrossLinking = assetTrueLinking(r.getProteinA(), r.getProteinB(), r.getPeptideA(), r.getPeptideB(), r.getLinkA(), r.getLinkB()).split("\t")[0];
                    if (trueCrossLinking.equals(LinkingProbability.POSSIBLE.toString())) {
                        poss.add(r);
                    } else if (trueCrossLinking.equals("LIKELYPOSSIBLE")) {
                        likelyposs.add(r);
                    } else if (trueCrossLinking.equals("Not-predicted")) {
                        notpreds.add(r);
                    } else if (trueCrossLinking.equals("IMPOSSIBLE")) {
                        impossible.add(r);
                    } else if (trueCrossLinking.equals("Not-predicted")) {
                        likelyimpossible.add(r);
                    }
                }
                // if there is only one Percolator result, it is possible to select the first one only..
                PercolatorResult toAdd = all.get(0);
                if (all.size() > 1) {
                    // now selects first possible one..
                    if (!poss.isEmpty()) {
                        int index = AnalyzePercolator.returnRandomIndex(poss.size());
                        toAdd = poss.get(index);
                    } else if (!likelyposs.isEmpty()) {
                        int index = AnalyzePercolator.returnRandomIndex(likelyposs.size());
                        toAdd = likelyposs.get(index);
                    } else if (!likelyimpossible.isEmpty()) {
                        int index = AnalyzePercolator.returnRandomIndex(likelyimpossible.size());
                        toAdd = impossible.get(index);
                    } else if (!impossible.isEmpty()) {
                        int index = AnalyzePercolator.returnRandomIndex(impossible.size());
                        toAdd = impossible.get(index);
                    } else if (!notpreds.isEmpty()) {
                        int index = AnalyzePercolator.returnRandomIndex(notpreds.size());
                        toAdd = notpreds.get(index);
                    }
                }
                // put the filtered output into the list
                filteredRes.add(toAdd);
            } else {
                for (PercolatorResult r : res) {
                    all.add(r);
                }// if there is only one Percolator result, it is possible to select the first one only..
                PercolatorResult toAdd = all.get(0);
                if (all.size() > 1) {
                    int index = AnalyzePercolator.returnRandomIndex(res.size());
                    toAdd = res.get(index);
                }
                // put the filtered output into the list
                filteredRes.add(toAdd);
            }
        }
        return filteredRes;
    }

    public static int returnRandomIndex(int list_size) {
        int last_index = list_size - 1;
        int index = last_index;
        if (list_size > 1) {
            Random random = new Random();
            index = random.nextInt(last_index);
        }
        return index;
    }

}
