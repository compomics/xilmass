/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM;

import analyse.XPSM.prepareOutcome.AnalyzeKojak;
import analyse.XPSM.prepareOutcome.AnalyzeOutcomes;
import analyse.XPSM.prepareOutcome.AnalyzePLink;
import analyse.XPSM.prepareOutcome.AnalyzeXilmass;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * This class select best scored XPSM for one spectrum. If analysis_option is
 * set to 1, first it merges two input files and then select the tops ones It
 * also checks true cross linking option based on predicted cross linkings
 *
 *
 *
 * @author Sule
 */
public class NameTargetDecoy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        // heavy and light labeled Xilmass outputs
        File  lightLabeledXilmass = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/competetives/xilmass/rawFiles/dss_hcdElite_NoLabel_Both_WT_MinPeak0.txt"),
              heavyLabeledXilmass = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/competetives/xilmass/rawFiles/dss_hcdElite_Label_Both_WT_MinPeak0.txt"),
                
                //                output = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_run\\combined/pLink_elite_allBestsMerged.txt"),
                // contaminant files for each data set
                psms_contaminant_elite = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\1_Cleaning\\SGRunsHCD_identification\\velos_orbitra_elite/hcd_orbi_elite_crap_10psmFDR_psms_validated.txt"),
                psms_contaminant_qexactive = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\1_Cleaning\\SGRunsHCD_identification\\qExactive/hcd_orbi_qexactive_crap_10psmFDR_psms_validated.txt"),
                // Xwalk predicted and manullay curated cross linking sites
                prediction = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction_uniprot2.txt"),
                // competetive results...
                kojakFolderElite = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\kojak_runs\\elite"),
                pLinkFolderElite = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_run\\combined\\elite");

        File psms = psms_contaminant_elite,
                pLinkFolder = pLinkFolderElite,
                kojakFolder = kojakFolderElite;

        boolean isElite = false;
        File output = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_run\\combined/pLink_qexactive_all.txt");
                
//                "C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\kojak_runs\\combined/kojak135_qexactive_co.txt");
//                "C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/competetives/xilmass/xilmass_elite_WT_MinPeak0.txt");
        if (!isElite) {
            psms = psms_contaminant_qexactive;
            pLinkFolder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_run\\combined\\qexactive");
            kojakFolder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\kojak_runs\\qexactive");           
            lightLabeledXilmass = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/competetives/xilmass/rawFiles/dss_hcdQExactive_NoLabel_Both_WT_MinPeak0.txt");
            heavyLabeledXilmass = new File("C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/competetives/xilmass/rawFiles/dss_hcdQExactive_Label_Both_WT_MinPeak0.txt");
        }

        String[] protein_names = {"Q15149", "P62158"};
        HashMap<String, String> resultFileNameForSpectrumFile = prepare(isElite);

        int analysis = 2;
        AnalyzeOutcomes o = null;
        switch (analysis) {
            case 0:
                o = new AnalyzeXilmass(lightLabeledXilmass, heavyLabeledXilmass, output, prediction, psms_contaminant_qexactive, protein_names);
                break;
            case 1:
                o = new AnalyzeKojak(output, kojakFolder, prediction, psms, protein_names);
                break;
            case 2:
                o = new AnalyzePLink(pLinkFolder, output, resultFileNameForSpectrumFile, prediction, psms, protein_names);
                break;
        }
        o.run();
    }

//    /**
//     * This method finds a2/b2 pairs for each cross linked peptide
//     *
//     * @param input
//     * @param output
//     * @param first_protein_index
//     * @param second_protein_index
//     * @param first_protein_name
//     * @param second_protein_name
//     * @param spectrum_name_index
//     * @param score_index
//     * @param hasTraditionalDecoy
//     * @throws FileNotFoundException
//     * @throws IOException
//     */
//    private void get_paired_info(File input, File output,
//            int first_protein_index, int second_protein_index,
//            String[] protein_names,
//            int spectrum_name_index, int score_index, boolean hasTraditionalDecoy,
//            int theoretical_peaks_index) throws FileNotFoundException, IOException {
//
//        BufferedReader br = new BufferedReader(new FileReader(input));
//        String line = "";
//        BufferedWriter bw = new BufferedWriter(new FileWriter(output));
//
//        while ((line = br.readLine()) != null) {
//            if (!line.startsWith("Spect")) {
//                String[] split = line.split("\t");
//
//                String proteinAInfo = split[first_protein_index],
//                        proteinBInfo = split[second_protein_index],
//                        proteinAName = proteinAInfo.substring(0, proteinAInfo.indexOf("(")),
//                        proteinBName = proteinBInfo.substring(0, proteinBInfo.indexOf("(")),
//                        indexProteinA = proteinAInfo.substring(proteinAInfo.indexOf("(") + 1, proteinAInfo.indexOf(")")),
//                        indexProteinB = proteinBInfo.substring(proteinBInfo.indexOf("(") + 1, proteinBInfo.indexOf(")")),
//                        targetType = getTargetType(proteinAName, proteinBName, protein_names, hasTraditionalDecoy),
//                        theoreticalPeaks = split[theoretical_peaks_index];
//                boolean has_pair_proteinA = search_pairs(theoreticalPeaks, true),
//                        has_pair_proteinB = search_pairs(theoreticalPeaks, false);
//
//                bw.write(line + "\t" + indexProteinA + "\t" + indexProteinB + "\t" + targetType + "\t" + has_pair_proteinA + "\t" + has_pair_proteinB + "\n");
//
//            } else {
//                bw.write(line + "\t" + "IndexProteinA" + "\t" + "IndexProteinB" + "\t" + "Target_Decoy" + "\t" + "hasPairProteinA" + "\t" + "hasPairProteinB" + "\n");
//            }
//        }
//        bw.close();
//    }
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

    /**
     * This method matches pLink config file and corresponding mgf file
     *
     * @param isElite
     * @return
     */
    private static HashMap<String, String> prepare(boolean isElite) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (isElite) {
            map.put("hcd_elite1_inter_qry.proteins.txt", "Probe2_v_mc1_top15HCD-1.mgf");
            map.put("hcd_elite2_inter_qry.proteins.txt", "Probe2_v_mp1_top15HCD-1.mgf");
            map.put("hcd_elite3_inter_qry.proteins.txt", "Probe2_v_x1_top15HCD-1.mgf");
            map.put("hcd_elite4_inter_qry.proteins.txt", "Probe2_v_x1_top15HCD-precolumn-1.mgf");
        } else {
            map.put("hcd_qexactive1_inter_qry.proteins.txt", "Probe2_h_X1_run1.mgf");
            map.put("hcd_qexactive2_inter_qry.proteins.txt", "Probe2_h_X1_run2.mgf");
            map.put("hcd_qexactive3_inter_qry.proteins.txt", "Probe2_h_X2_excl3_500ms_run1.mgf");
            map.put("hcd_qexactive4_inter_qry.proteins.txt", "Probe2_h_X2_excl3_500ms_run2.mgf");
        }
        return map;
    }

}
