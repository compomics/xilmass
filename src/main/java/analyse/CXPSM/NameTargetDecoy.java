/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM;

import analyse.CXPSM.prepareOutcome.*;
import com.compomics.util.experiment.identification.protein_sequences.SequenceFactory;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class selects the best scored XPSM for each spectrum for Xilmass.
 *
 * @author Sule
 */
public class NameTargetDecoy {

    public static void main(String[] args) {

        // The method of FDR calculation:
        // true: (full_decoy+half_decoy)/target and false:(half_decoy-full_decoy)/target (pLink-like)        
        boolean isPITFDR = false;
        boolean doesContainCPeptidePattern = false,
                doesContainIonWeight = false;

        int analysis = Integer.parseInt(args[0]);
        File xilmassResFolder = new File(args[1]),
                prediction = null,
                // The validated PSM list from contaminants
                psms_contamination = null;
        String scoringFunctionName = args[2];
        File td = new File(args[3]),
                allXPSMs = new File(args[4]);
        double fdr_interPro = Double.parseDouble(args[5]),
                fdr_intraPro = Double.parseDouble(args[6]),
                fdr_cutoff = Double.parseDouble(args[7]);
        boolean doesSplitFDR = Boolean.parseBoolean(args[8]),
                isMS1ErrPPM = Boolean.parseBoolean(args[9]);

        try {
            td.createNewFile();
        } catch (IOException ex) {
            System.out.println("An output file that contains validated XPSMs (by setting output) cannot be created! Check \"output\" properties");
            Logger.getLogger(NameTargetDecoy.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            allXPSMs.createNewFile();
        } catch (IOException ex) {
            System.out.println("A file that contains all XPSMs (by setting allXPSMOutputName) cannot be created! Check \"allXPSMOutputName\" properties");
            Logger.getLogger(NameTargetDecoy.class.getName()).log(Level.SEVERE, null, ex);
        }

        // NOW RUN!!!
        AnalyzeOutcomes o = null;
        try {
            switch (analysis) {
                case 0:
                    o = new AnalyzeXilmass(xilmassResFolder, td, prediction, psms_contamination, fdr_cutoff, isPITFDR, isMS1ErrPPM, doesContainCPeptidePattern, doesContainIonWeight,
                            allXPSMs, scoringFunctionName);
                    break;
                case 10:
                    o = new AnalyzeXilmass(xilmassResFolder, td, prediction, psms_contamination, fdr_cutoff, isPITFDR, isMS1ErrPPM, doesContainCPeptidePattern, doesContainIonWeight,
                            fdr_interPro, fdr_intraPro, allXPSMs, scoringFunctionName);
                    break;
                case 11:
                    o = new AnalyzeXilmass(xilmassResFolder, td, psms_contamination, fdr_cutoff, doesSplitFDR, isPITFDR, isMS1ErrPPM,
                            fdr_interPro, fdr_intraPro, allXPSMs, scoringFunctionName);
                    break;
            }
            o.run();

            // now check post-processing
        } catch (IOException ex) {
            Logger.getLogger(NameTargetDecoy.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * This method decides if there is a2/b2 pair on given theoretical peak from
     * either proteinA (isProteinA=true) or proteinB (isProteinA=false)
     *
     * @param theoreticalPeaks
     * @param isProteinA
     * @return
     */
    private static boolean search_pairs(String theoreticalPeaks, boolean isProteinA) {
        boolean hasPairs = false;
        int a2 = 0,
                b2 = 0;
        String[] split = theoreticalPeaks.split(" ");
        for (String st : split) {
            String[] st_split = st.split("_");
            for (int i = 0; i < st_split.length - 1; i++) {
                if (st_split[i].equals("pepA") && st_split[i + 1].equals("a2") && isProteinA) {
                    a2++;
                } else if (st_split[i].equals("pepA") && st_split[i + 1].equals("b2") && isProteinA) {
                    b2++;
                } else if (st_split[i].equals("pepB") && st_split[i + 1].equals("a2") && !isProteinA) {
                    a2++;
                } else if (st_split[i].equals("pepB") && st_split[i + 1].equals("b2") && !isProteinA) {
                    b2++;
                }
            }
        }
        if (a2 > 0 && b2 > 0) {
            hasPairs = true;
        }
        return hasPairs;
    }

    public static HashMap<String, String> getAccs(File fasta) throws IOException, FileNotFoundException, ClassNotFoundException, IOException, IllegalArgumentException, InterruptedException {
        HashMap<String, String> acc_seq = new HashMap<String, String>();
        SequenceFactory fct = SequenceFactory.getInstance();
        fct.loadFastaFile(fasta);
        Set<String> accession_original_db = fct.getAccessions();
        for (String acc : accession_original_db) {
            String proSeq = fct.getProtein(acc).getSequence();
            acc_seq.put(acc, proSeq);
        }
        return acc_seq;
    }

    private static void post_processing(File output, File measuredDistOuputFile) throws FileNotFoundException, IOException {
        File outputWdists = new File(output.getAbsolutePath().substring(0, output.getAbsolutePath().indexOf(".txt")) + "_dist.txt");
        BufferedReader br = new BufferedReader(new FileReader(output));
        BufferedWriter bw = new BufferedWriter(new FileWriter(outputWdists));

        HashMap<String, Double> distName_and_computedDist = new HashMap<String, Double>();

        String line = "",
                title = "";
        int xlinkingsiteind = 0,
                sasdistind = 0;
        boolean isFirstLine = false;
        double computedDist = 0;
        while ((line = br.readLine()) != null) {
            if (!isFirstLine) {
                isFirstLine = true;
                title = line + "\t" + "ComputedEuclideanAlpha" + "\t" + "PostProcessingName" + "\t" + "PredictedByXWalk";
                String[] sp = line.split("\t");
                for (int i = 0; i < sp.length; i++) {
                    if (sp[i].equals("crossLinkingSite")) {
                        xlinkingsiteind = i;
                    } else if (sp[i].equals("SASDist")) {
                        sasdistind = i;
                    }

                }
                bw.write(title);
                bw.newLine();
            } else {
                // first name based on distance
                String crossLinkingSites = line.split("\t")[xlinkingsiteind];
                String postprocessingname = "within";
                if (distName_and_computedDist.containsKey(crossLinkingSites)) {
                    computedDist = distName_and_computedDist.get(crossLinkingSites);
                    if (computedDist >= 30.00) {
                        postprocessingname = "exceeding";
                    } else if (computedDist == -1) {
                        postprocessingname = "missing";
                        String[] sp = crossLinkingSites.split("_");
                        int first = Integer.parseInt(sp[1]),
                                second = Integer.parseInt(sp[3]);
                        if (first == second && sp[0].equals(sp[2])) {
                            postprocessingname = "missing_intra_peptide";
                        }
                    } else if (computedDist == 0) {
                        postprocessingname = "intra_peptide";
                    }
                } else {
                    postprocessingname = "noInfo";
                    computedDist = -2;
                }
                // now check  xwalk predicted ones
                String sasPredict = "Pred";
                if (line.split("\t")[sasdistind].equals("-")) {
                    sasPredict = "UnPred";
                }
                bw.write(line + "\t" + computedDist + "\t" + postprocessingname + "\t" + sasPredict);
                bw.newLine();
            }
        }
        bw.close();
    }

}
