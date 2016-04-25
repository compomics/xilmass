/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playground.STYAnalysis;

import analyse.CXPSM.outcome.XilmassResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author Sule
 */
public class MergeXilmassORGXilmassSTYXilmassST {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        File xilmass_org = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\side_reactions\\xilmass_SideReaction_ORG_xlsite.txt"),
                xilmass_sty = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\side_reactions\\xilmass02_sty_1_10_min2/xilmass_SideReaction_STY_xlsite.txt"),
                xilmass_st = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\competetives\\side_reactions\\xilmass02_st_1_10_min2/xilmass_SideReaction_ST_xlsite.txt");

        // read a given file to fill out a hashmap
        HashMap<String, XilmassSideChainResult> xilmass_org_input = read(xilmass_org),
                xilmass_sty_input = read(xilmass_sty),
                xilmass_st_input = read(xilmass_st);

        // spectrum title of all identified ones
        HashSet<String> id_spec_titles = new HashSet<String>();

        // FIND DIFFERENT PEPTIDE-PAIR IDENTIFICATION
        System.out.println("DIFFERENT PEPTIDE PAIR FOR THE SAME ID SPECTRA");
        for (String specID : xilmass_org_input.keySet()) {
            // find different peptide-pairs now
            String tmp_org_peptide_pair = xilmass_org_input.get(specID).getPeptide_pair_alpha_beta();
            // check if this spectrum is identified on STY or ST...
            String tmp_sty_peptide_pair = "",
                    tmp_st_peptide_pair = "";
            if (xilmass_sty_input.containsKey(specID)) {
                tmp_sty_peptide_pair = xilmass_sty_input.get(specID).getPeptide_pair_alpha_beta();
            }
            if (xilmass_st_input.containsKey(specID)) {
                tmp_st_peptide_pair = xilmass_st_input.get(specID).getPeptide_pair_alpha_beta();
            }
            // now check to find different ones            
            if (!tmp_sty_peptide_pair.isEmpty() && !tmp_st_peptide_pair.isEmpty()) {
                if (!tmp_org_peptide_pair.equals(tmp_st_peptide_pair)) {
                    System.out.println(specID + "\t" + tmp_org_peptide_pair + "\t" + xilmass_org_input.get(specID).getScore() + "\t"
                            + tmp_sty_peptide_pair + "\t" + xilmass_sty_input.get(specID).getScore() + "\t"
                            + tmp_st_peptide_pair + "\t" + xilmass_st_input.get(specID).getScore());
                }
            }
        }

        System.out.println("\n");

        // FIND DIFFERENT XL-SITES IDENTIFICATION (XLSMs)
        System.out.println("DIFFERENT XLSMs FOR THE SAME ID SPECTRA");

        int total = 0;
        for (String specID : xilmass_org_input.keySet()) {
            // find different peptide-pairs now
            String tmp_org_xlsm = xilmass_org_input.get(specID).getXlsm();
            // check if this spectrum is identified on STY or ST...
            String tmp_sty_xlsm = "",
                    tmp_st_xlsm = "";
            if (xilmass_sty_input.containsKey(specID)) {
                tmp_sty_xlsm = xilmass_sty_input.get(specID).getXlsm();
            }
            if (xilmass_st_input.containsKey(specID)) {
                tmp_st_xlsm = xilmass_st_input.get(specID).getXlsm();
            }
            // now check to find different ones            
            if (!tmp_sty_xlsm.isEmpty() && !tmp_st_xlsm.isEmpty()) {
                if (!tmp_org_xlsm.equals(tmp_st_xlsm) || !tmp_org_xlsm.equals(tmp_sty_xlsm)) {
                    total++;
                    System.out.println(specID + "\t"
                            + xilmass_org_input.get(specID).getPeptide_pair_alpha_beta() + "\t"
                            + xilmass_sty_input.get(specID).getPeptide_pair_alpha_beta() + "\t"
                            + xilmass_st_input.get(specID).getPeptide_pair_alpha_beta() + "\t"
                            // xl sites
                            + xilmass_org_input.get(specID).getXlsm() + "\t"
                            + xilmass_sty_input.get(specID).getXlsm() + "\t"
                            + xilmass_st_input.get(specID).getXlsm() + "\t"
                            // scores
                            + xilmass_org_input.get(specID).getScore() + "\t"
                            + xilmass_sty_input.get(specID).getScore() + "\t"
                            + xilmass_st_input.get(specID).getScore());
                }
            }
        }
        System.out.println("in total " + total);
    }

    private static HashMap<String, XilmassSideChainResult> read(File input) throws FileNotFoundException, IOException {
        HashMap<String, XilmassSideChainResult> is = new HashMap<String, XilmassSideChainResult>();
        BufferedReader br = new BufferedReader(new FileReader(input));
        String line = "";
        while ((line = br.readLine()) != null) {
            XilmassSideChainResult i = new XilmassSideChainResult(line);
            is.put(i.getSpecID(), i);
        }
        return is;
    }

    // inner class to keep Xilmass-SideChainReaction Results
    public static class XilmassSideChainResult {

        private String line;
        private String spectrum_file,
                spectrum_title,
                score,
                specID,
                xl_site,
                peptide_pair,
                peptide_pair_alpha_beta,
                xlsm,
                xlpms,
                styxlname,
                intra_peptide_info;

        public XilmassSideChainResult(String line) {
            this.line = line;
            String[] sp = line.split("\t");

            spectrum_file = sp[0];
            spectrum_title = sp[1];

            score = sp[21];

            specID = sp[32];
            xl_site = sp[33];
            peptide_pair = sp[34];
            peptide_pair_alpha_beta = sp[35];
            xlsm = sp[36];
            xlpms = sp[37];

            styxlname = sp[38];
            intra_peptide_info = sp[39];
        }

        // getter-setters
        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getSpectrum_file() {
            return spectrum_file;
        }

        public void setSpectrum_file(String spectrum_file) {
            this.spectrum_file = spectrum_file;
        }

        public String getSpectrum_title() {
            return spectrum_title;
        }

        public void setSpectrum_title(String spectrum_title) {
            this.spectrum_title = spectrum_title;
        }

        public String getSpecID() {
            return specID;
        }

        public void setSpecID(String specID) {
            this.specID = specID;
        }

        public String getXl_site() {
            return xl_site;
        }

        public void setXl_site(String xl_site) {
            this.xl_site = xl_site;
        }

        public String getPeptide_pair() {
            return peptide_pair;
        }

        public void setPeptide_pair(String peptide_pair) {
            this.peptide_pair = peptide_pair;
        }

        public String getPeptide_pair_alpha_beta() {
            return peptide_pair_alpha_beta;
        }

        public void setPeptide_pair_alpha_beta(String peptide_pair_alpha_beta) {
            this.peptide_pair_alpha_beta = peptide_pair_alpha_beta;
        }

        public String getXlsm() {
            return xlsm;
        }

        public void setXlsm(String xlsm) {
            this.xlsm = xlsm;
        }

        public String getXlpms() {
            return xlpms;
        }

        public void setXlpms(String xlpms) {
            this.xlpms = xlpms;
        }

        public String getStyxlname() {
            return styxlname;
        }

        public void setStyxlname(String styxlname) {
            this.styxlname = styxlname;
        }

        public String getIntra_peptide_info() {
            return intra_peptide_info;
        }

        public void setIntra_peptide_info(String intra_peptide_info) {
            this.intra_peptide_info = intra_peptide_info;
        }

    }
}
