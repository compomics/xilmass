/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.outcome.Outcome;
//import analyse.xwalk_uniprot.TrueLinking;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This abstract class enables preparing the same outcome obtained from the
 * different outcomes by different algorithms.
 *
 * @author Sule
 */
public abstract class AnalyzeOutcomes {

    protected File prediction_file, // a file which predicted linking locations are stores
            psms_contaminant; // a validated PSM list from PeptideShaker - default one, with spectrum index as 8
    protected String[] target_names; // target protein accession names
    protected boolean areReversedDecoys = true, // true:target/decoy (reversed or shuffled) approach and false: Pfu is decoy ## TODO: for the future...
            isPIT = false, //true: all_decoy/all_target false: (half_decoy-full_decoy)/all_target
            areContaminantMSMSReady = false; // in order to fill out a list of validated contaminant peptide derived PSMs
    protected String scoringFunctionName;

    /**
     * This method analyze a given PSM list for a software
     *
     * @throws java.io.FileNotFoundException
     */
    public abstract void run() throws FileNotFoundException, IOException;

    /**
     * This method returns a list of validated outputs with a given fd FDR is
     * calculated based on either PIT (Kall, 2008) or pLink-derived (Yang, 2012)
     *
     * @param res a list of all outputs
     * @param fdr FDR cutoff
     * @param isCrossLinkedXPSMs true: validated PSMs are cross-linked;
     * otherwise only mono-linked PSMs
     * @return
     * @throws IOException
     */
    protected ArrayList<Outcome> getValidatedPSMs(ArrayList<Outcome> res, double fdr, boolean isCrossLinkedXPSMs) throws IOException {
        ArrayList<Outcome> tmpValidatedPSMlist = new ArrayList<Outcome>();
        double tmp_fdr = 0.00;
        int targets = 0,
                full_decoys = 0,
                half_decoys = 0;
        boolean isBiggerFDR = false;
        for (int i = 0; i < res.size(); i++) {
            Outcome o = res.get(i);
            if ((isCrossLinkedXPSMs && !o.getAccProteinB().equals("-"))
                    || (!isCrossLinkedXPSMs && o.getAccProteinB().equals("-"))) {

                boolean isTarget = false,
                        isHalfDecoy = false,
                        isDecoy = false;
                if (o.getTarget_decoy().equals("TT")) {
                    isTarget = true;
                } else if (o.getTarget_decoy().equals("TD")) {
                    isHalfDecoy = true;
                } else if (o.getTarget_decoy().equals("DD")) {
                    isDecoy = true;
                } else if (o.getTarget_decoy().equals("T")) {
                    isTarget = true;
                } else if (o.getTarget_decoy().equals("D")) {
                    isDecoy = true;
                }

                if (isTarget) {
                    targets++;
                } else if (isHalfDecoy) {
                    half_decoys++;
                } else if (isDecoy) {
                    full_decoys++;
                }
                // means any decoy divided by all target..
                if (isPIT && targets > 0) {
                    tmp_fdr = (double) (full_decoys + half_decoys) / (double) targets;
                    // means pLink based calculation..    
                } else if (!isPIT && (targets > 0)) {
                    if (half_decoys - full_decoys > 0) {
                        tmp_fdr = (double) (half_decoys - full_decoys) / (double) targets;
                    } else {
                        tmp_fdr = (double) full_decoys / (double) targets;
                    }
                }
                if (fdr >= tmp_fdr && !isBiggerFDR) {
                    if (o.getTarget_decoy().equals("TT")) {
                        tmpValidatedPSMlist.add(o);
                    } else {
//                        System.out.println(o.toString());
                    }
                } else if (isBiggerFDR && fdr <= tmp_fdr) {
                    isBiggerFDR = true;
                }
            }
        }
        return tmpValidatedPSMlist;
    }

    /**
     * This method returns a list of validated MS2 spectra for contaminant
     * proteins (default PS file, with spectrum name as index of 8)
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected HashSet<String> getContaminant_MSMS() throws FileNotFoundException, IOException {
        HashSet<String> contaminants = new HashSet<String>();
        if (!areContaminantMSMSReady) {
            BufferedReader br = new BufferedReader(new FileReader(psms_contaminant));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("Spect")) {
                    String[] split = line.split("\t");
                    String spectrumTitle = split[8];
                    contaminants.add(spectrumTitle);
                }
            }
            areContaminantMSMSReady = true;
        }
        return contaminants;
    }

    /**
     * This method returns a list of validated MS2 spectra derived from
     * contaminants (default PS file, with spectrum title as index of 8 and
     * spectrum file name as index of 7) A given file is validated PSM at FDR
     * PSM label This selects all scan numbers assigned to one spectrum file A
     * returned map contains spectrum file name and assigned scan numbers
     *
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected HashMap<String, HashSet<Integer>> getContaminant_specFile_and_scans() throws FileNotFoundException, IOException {
        HashMap<String, HashSet<Integer>> contaminants = new HashMap<String, HashSet<Integer>>();
        if (!areContaminantMSMSReady) {
            BufferedReader br = new BufferedReader(new FileReader(psms_contaminant));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("Protein")) {
                    String[] split = line.split("\t");
                    String spectrumFile = split[7], //Probe2_v_x1_top15HCD-precolumn-1.mgf
                            specTitle = split[8], //QEplus009907.7333.7333.2 File:"QEplus009907.raw", NativeID:"controllerType=0 controllerNumber=1 scan=7333"
                            scan = specTitle.substring(specTitle.indexOf("scan=") + 5).replace("\"", "");
                    if (contaminants.containsKey(spectrumFile)) {
                        contaminants.get(spectrumFile).add(Integer.parseInt(scan));
                    } else {
                        HashSet<Integer> scns = new HashSet<Integer>();
                        scns.add(Integer.parseInt(scan));
                        contaminants.put(spectrumFile, scns);
                    }
                }
            }
            areContaminantMSMSReady = true;
        }
        return contaminants;
    }

    /**
     * It decides based on the given two proteins either target or full_decoy or
     * half_decoy
     *
     * @param proteinA the accession number of proteinA
     * @param proteinB the accession number of proteinB
     * @return
     */
    protected static String getTargetDecoy(String proteinA, String proteinB) {
        boolean isProteinAdecoy = false;
        int proteinBtype = 1; //-1: nothing, 0:decoy, 1:target
        String targetName = "";
        // First decide on a name
        if (proteinA.contains("REVERSED") || proteinA.contains("SHUFFLED") || proteinA.contains("DECOY")) {
            isProteinAdecoy = true;
        }
        if (proteinB.contains("REVERSED") || proteinB.contains("SHUFFLED") || proteinB.contains("DECOY")) {
            proteinBtype = 0;
        } else if (proteinB.equals("-") || proteinB.isEmpty()) {
            proteinBtype = -1;
        }

        switch (proteinBtype) {
            case -1:// so it is monolinked..
                if (isProteinAdecoy) {
                    targetName = "D";
                } else {
                    targetName = "T";
                }
                break;
            case 0: // crosslinked but the proteinB is a decoy...
                if (isProteinAdecoy) {
                    targetName = "DD";
                } else {
                    targetName = "TD";
                }
                break;
            case 1:// crosslinked but the proteinB is a TARGET...
                if (isProteinAdecoy) {
                    targetName = "TD";
                } else {
                    targetName = "TT";
                }
                break;
        }
        return targetName;
    }

}
