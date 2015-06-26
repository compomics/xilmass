/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM;

import analyse.XPSM.prepareOutcome.AnalyzeKojak;
import analyse.XPSM.prepareOutcome.AnalyzeOutcomes;
import analyse.XPSM.prepareOutcome.AnalyzePLink;
import analyse.XPSM.prepareOutcome.AnalyzePLinkValidatedResult;
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
        File xilmassResFile = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\xilmass/runs/dss_hcdElite_Both_Scr4_MinPeak0_rDecoy_allPeaks.txt"),
                // contaminant files for each data set
                psms_contaminant_elite = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\1_Cleaning\\SGRunsHCD_identification\\velos_orbitra_elite/hcd_orbi_elite_crap_10psmFDR_psms_validated.txt"),
                psms_contaminant_qexactive = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\1_Cleaning\\SGRunsHCD_identification\\qExactive/hcd_orbi_qexactive_crap_10psmFDR_psms_validated.txt"),
                // Xwalk predicted and manullay curated cross linking sites
                prediction = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction_uniprot2.txt"),
                // competetive results...
                kojakFolderElite = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\kojak_runs\\elite"),
                pLinkFolderElite = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_run\\pLinkValidationBased\\pLink_DSS_HCD_elite_ONLYTARGET\\all_query"),
                //pLinkCombValidatedFile = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\targetsAlso2Pfus/hcd_elite_inter_combine.spectra_targetsPfus.txt");
                pLinkCombValidatedFile = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_run\\pLinkValidationBased\\pLink_DSS_HCD_elite_ONLYTARGET\\1.sample\\search\\hcd_elite_inter_combine/hcd_elite_inter_combine.spectra.xls");
        
        File psms = psms_contaminant_elite,
                pLinkFolder = pLinkFolderElite,
                kojakFolder = kojakFolderElite;
        boolean isElite = true,
                isValidatedResult = false;

        if (!isElite) {
            psms = psms_contaminant_qexactive;
            pLinkFolder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_run\\combined\\qexactive");
            kojakFolder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\kojak_runs\\qexactive");
            pLinkCombValidatedFile = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\onlyTargets/hcd_qexactive_inter_combine.spectra_only_Target_QExactive.txt");
        }

        File output = new File(
                //                "C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\kojak\\kojak_runs\\combined/kojak135_qexactive_co.txt");                
                //                "C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\pLink\\pLink_Validated_Elite_target2Pfus.txt");
                //                "C:/Users/Sule/Documents/PhD/XLinked/XLinkData_Freiburg/competetives/xilmass/td_dss_hcdElite_Both_Scr4_MinPeak0_rDecoy_allPeaks.txt");
                "C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\xilmass\\td_dss_hcdElite_Both_Scr4_MinPeak0_rDecoy_allPeaks.txt");
        String[] protein_names = {"Q15149", "P62158"};

        // NOW RUN!!!
        int analysis = 4;
        boolean hasTraditionalFDR = true;
        if (analysis == 3) {
            isValidatedResult = true;
        }
        HashMap<String, String> resultFileNameForSpectrumFile = prepare(isElite, isValidatedResult);
        AnalyzeOutcomes o = null;
        switch (analysis) {
            case 0:
//                o = new AnalyzeXilmass(lightLabeledXilmass, heavyLabeledXilmass, output, prediction, psms, protein_names, hasTraditionalFDR);
                break;
            case 1:
                o = new AnalyzeKojak(output, kojakFolder, prediction, psms, protein_names);
                break;
            case 2:
                o = new AnalyzePLink(pLinkFolder, output, resultFileNameForSpectrumFile, prediction, psms, protein_names);
                break;
            case 3:
                o = new AnalyzePLinkValidatedResult(pLinkCombValidatedFile, output, resultFileNameForSpectrumFile, prediction, psms, protein_names);
                break;
            case 4:
                o = new AnalyzeXilmass(xilmassResFile, output, prediction, psms, protein_names, hasTraditionalFDR);
                break;
        }
        o.run();
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

    /**
     * This method matches pLink config file and corresponding mgf file
     *
     * @param isElite
     * @return
     */
    private static HashMap<String, String> prepare(boolean isElite, boolean isValidatedResult) {
        HashMap<String, String> map = new HashMap<String, String>();
        if (!isValidatedResult) {
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
        } else {
            if (isElite) {
                map.put("File2235", "Probe2_v_mc1_top15HCD-1.mgf");
                map.put("File2239", "Probe2_v_mp1_top15HCD-1.mgf");
                map.put("File2241", "Probe2_v_x1_top15HCD-1.mgf");
                map.put("File2244", "Probe2_v_x1_top15HCD-precolumn-1.mgf");
            } else {
                map.put("File202", "Probe2_h_X1_run1.mgf");
                map.put("File203", "Probe2_h_X1_run2.mgf");
                map.put("File204", "Probe2_h_X2_excl3_500ms_run1.mgf");
                map.put("File205", "Probe2_h_X2_excl3_500ms_run2.mgf");
            }
        }
        return map;
    }

}
