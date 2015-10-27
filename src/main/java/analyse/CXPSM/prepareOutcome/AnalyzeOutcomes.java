/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.CXPSM.prepareOutcome;

import analyse.CXPSM.outcome.Outcome;
import analyse.xwalk_uniprot.TrueLinking;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This abstract class enables preparing the same outcome obtained from the different outcomes by different algorithms.
 * 
 * @author Sule
 */
public abstract class AnalyzeOutcomes {

    protected File prediction_file, // a file which predicted linking locations are stores
            psms_contaminant; // a validated PSM list from PeptideShaker - default one, with spectrum index as 8
    protected String[] target_names; // target protein accession names
    protected HashSet<TrueLinking> trueLinkings = new HashSet<TrueLinking>(); // a list of predicted cross linking sites
    protected boolean areReversedDecoys = true, // true:target/decoy (reversed or shuffled) approach and false: Pfu is decoy ## TODO: for the future...
            isPIT = false, //true: all_decoy/all_target false: (half_decoy-full_decoy)/all_target
            areContaminantMSMSReady = false; // in order to fill out a list of validated contaminant peptide derived PSMs

    /**
     * This method analyze a given PSM list for a software
     *
     * @throws java.io.FileNotFoundException
     */
    public abstract void run() throws FileNotFoundException, IOException;

    /**
     * This method checks given proteinA and proteinB with their indices to
     * decide if they are possible cross linking sites
     *
     * @param uniprotProAacces Uniprot proteinA accession number
     * @param uniprotProBacces Uniprot proteinB accession number
     * @param uniprotLinkingSiteA linking location on proteinA (based on uniprot
     * sequence)
     * @param uniprotLinkingSiteB linking location on proteinB (based on uniprot
     * sequence)
     * @return
     * @throws IOException
     */
    protected String assetTrueLinking(String uniprotProAacces, String uniprotProBacces, int uniprotLinkingSiteA, int uniprotLinkingSiteB) throws IOException {
        if (trueLinkings.isEmpty()) {
            prepareTrueLinkings();
        }
        String res = "Not-predicted" + "\t" + "-" + "\t" + "-";
        for (TrueLinking tl : trueLinkings) {
            if (tl.getProteinA().equals(uniprotProAacces)
                    && tl.getProteinB().equals(uniprotProBacces)
                    && tl.getIndexA() == uniprotLinkingSiteA
                    && tl.getIndexB() == uniprotLinkingSiteB) {
                res = tl.getClassification() + "\t" + tl.getEuclidean_distance_alpha() + "\t" + tl.getEuclidean_distance_beta();
            }
            if (tl.getProteinA().equals(uniprotProBacces)
                    && tl.getProteinB().equals(uniprotProAacces)
                    && tl.getIndexA() == uniprotLinkingSiteB
                    && tl.getIndexB() == uniprotLinkingSiteA) {
                res = tl.getClassification() + "\t" + tl.getEuclidean_distance_alpha() + "\t" + tl.getEuclidean_distance_beta();
            }
        }
        return res;
    }

    /**
     * This method returns a list of validated outputs with a given fd FDR is
     * calculated based on either PIT (Kall, 2008) or pLink-derived (Yang, 2012)
     *
     * @param res a list of all outputs
     * @param fdr FDR cutoff
     * @return
     * @throws IOException
     */
    protected ArrayList<Outcome> getValidatedPSMs(ArrayList<Outcome> res, double fdr) throws IOException {
        ArrayList<Outcome> tmpValidatedPSMlist = new ArrayList<Outcome>();
        double tmp_fdr = 0.00;
        int targets = 0,
                full_decoys = 0,
                half_decoys = 0;
        boolean isBiggerFDR = false;
        for (int i = 0; i < res.size(); i++) {
            Outcome o = res.get(i);
            boolean isTarget = false,
                    isHalfDecoy = false,
                    isDecoy = false;
            if (o.getTarget_decoy().equals("TT")) {
                isTarget = true;
            } else if (o.getTarget_decoy().equals("TD")) {
                isHalfDecoy = true;
            } else if (o.getTarget_decoy().equals("DD")) {
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
                }
            } else if (isBiggerFDR && fdr <= tmp_fdr) {
                isBiggerFDR = true;
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
    protected HashMap<String, HashSet<String>> getContaminant_MSMSMap() throws FileNotFoundException, IOException {
        HashMap<String, HashSet<String>> contaminants = new HashMap<String, HashSet<String>>();
        if (!areContaminantMSMSReady) {
            BufferedReader br = new BufferedReader(new FileReader(psms_contaminant));
            String line = "";
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("Protein")) {
                    String[] split = line.split("\t");
                    String spectrumFile = split[7], //Probe2_v_x1_top15HCD-precolumn-1.mgf
                            specTitle = split[8], //File2244 Spectrum3004 scans: 4650
                            scan = specTitle.substring(specTitle.indexOf(":") + 1).replace(" ", "");
                    if (contaminants.containsKey(spectrumFile)) {
                        contaminants.get(spectrumFile).add(scan);
                    } else {
                        HashSet<String> scns = new HashSet<String>();
                        scns.add(scan);
                        contaminants.put(spectrumFile, scns);
                    }
                }
            }
            areContaminantMSMSReady = true;
        }
        return contaminants;
    }

    /**
     * This method checks if a given pair of protein is either target or decoy
     * or half-decoy
     *
     * @param proteinAacess - uniprot accession number from ProteinA
     * @param proteinBasses - uniprot accession number from ProteinB
     * @param target_names - list of uniprot accession numbers for proteins
     * @return
     */
//    protected String getTargetType(String proteinAacess, String proteinBasses, String[] target_names) {
//        String first_target_name = target_names[0],
//                second_target_name = target_names[1];
//        String type = "";
//        if (areReversedDecoys) {
//            type = "half-decoy";
//            if ((!proteinAacess.contains("decoy")) && (!proteinBasses.contains("decoy"))) {
//                type = "target";
//            } else if ((proteinAacess.contains("decoy")) && (proteinBasses.contains("decoy"))) {
//                type = "decoy";
//            }
//        } else {
//            type = "half-decoy";
//            if ((proteinAacess.equals(first_target_name) || proteinAacess.equals(second_target_name))
//                    && (proteinBasses.equals(first_target_name) || proteinBasses.equals(second_target_name))) {
//                type = "target";
//            }
//            if ((!proteinAacess.equals(first_target_name) && !proteinAacess.equals(second_target_name))
//                    && (!proteinBasses.equals(first_target_name) && !proteinBasses.equals(second_target_name))) {
//                type = "decoy";
//            }
//        }
//        return type;
//    }
    /**
     * It decided given two proteins are either target or full_decoy or
     * half_decoy
     *
     * @param proteinA the accession number of proteinA
     * @param proteinB the accession number of proteinB
     * @return
     */
    protected String getTargetDecoy(String proteinA, String proteinB) {
        boolean isProteinAdecoy = false,
                isProteinBdecoy = false;
        String targetName = "TD";
        // First decide on a name
        if (proteinA.contains("REVERSED") || proteinA.contains("SHUFFLED") || proteinA.contains("DECOY")) {
            isProteinAdecoy = true;
        }
        if (proteinB.contains("REVERSED") || proteinB.contains("SHUFFLED") || proteinB.contains("DECOY")) {
            isProteinBdecoy = true;
        }
        if (isProteinAdecoy && isProteinBdecoy) {
            targetName = "DD";
        } else if (!isProteinAdecoy && !isProteinBdecoy) {
            targetName = "TT";
        }
        return targetName;
    }

    /**
     * This method reads a given prediction file and fills a hashset of
     * TrueLinking objects
     *
     *
     * @throws IOException
     */
    private void prepareTrueLinkings() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(prediction_file));
        String line = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("PDB")) {
//                PDBStructure	AtomInfoA	AtomInfoB	IdistanceSequence	Type	UniprotAccProA	UniprotIndexProA	UniprotAccProB	UniprotIndexProB	SASDistance(A)	EucDist(Beta_Beta)	EucDist(Alpha_Alpha)
                String[] split = line.split(",");
                String betaMeasuredDistanceStr = split[10],
                        alphaMeasuredDistanceStr = split[11],
                        classification = split[4],
                        uniprotAcc1 = split[5],
                        uniprotAcc1Index = split[6],
                        uniprotAcc2 = split[7],
                        uniprotAcc2Index = split[8];
                int indexA = Integer.parseInt(uniprotAcc1Index),
                        indexB = Integer.parseInt(uniprotAcc2Index);
                double betaMeasuredDistance = Double.parseDouble(betaMeasuredDistanceStr),
                        alphaMeasuredDistance = Double.parseDouble(alphaMeasuredDistanceStr);
                TrueLinking tl = new TrueLinking(uniprotAcc1, uniprotAcc2, classification, indexA, indexB, alphaMeasuredDistance, betaMeasuredDistance);
                trueLinkings.add(tl);
            }
        }
    }

}
