/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package analyse.XPSM.outcome;
import java.io.IOException;


/**
 * This class holds cross linking site information from validated pLink results
 * 
 * @author Sule
 */
public class PLinkValidatedResult extends Outcome {

    private double pLinkScore,
            calc_m,
            delta_m,
            ppm;
    private String spectrumTitle,
            spectrumFileName,
            peptide_pairs,
            peptideA,
            peptideB,
            modifications,
            xlinkedProteins;

    public PLinkValidatedResult(double pLinkScore, double calc_m, double delta_m, double ppm, String spectrumTitle, String spectrumFileName, String peptide_pairs, String modifications, String xlinkedProteins, String []targets) throws IOException {
        this.pLinkScore = pLinkScore;
        this.calc_m = calc_m;
        this.delta_m = delta_m;
        this.ppm = ppm;
        this.spectrumTitle = spectrumTitle;
        this.spectrumFileName = spectrumFileName;
        this.peptide_pairs = peptide_pairs;
        this.modifications = modifications;
        this.xlinkedProteins = xlinkedProteins;
        super.target_proteins = targets;
        // prepare accessions
        String[] splitProteins = xlinkedProteins.split("\\|");
        accessProteinA = splitProteins[1].replace(" ", "");
        accessProteinB = splitProteins[3].replace(" ", "");
        System.out.println(accessProteinA);
        System.out.println(accessProteinB);
        crossLinkedSitePro1 = Integer.parseInt(splitProteins[2].substring(splitProteins[2].lastIndexOf("(")+1, splitProteins[2].lastIndexOf(")")));
        crossLinkedSitePro2 = Integer.parseInt(splitProteins[4].substring(splitProteins[4].lastIndexOf("(")+1, splitProteins[4].lastIndexOf(")")));

        System.out.println(peptide_pairs.split("-")[0]);
        String[] split = peptide_pairs.split("-");
        peptideA = split[0].substring(0, split[0].indexOf("("));
        peptideB = split[1].substring(0, split[1].indexOf("("));
        label = "light";
        int labelOption = Integer.parseInt(peptide_pairs.split(":")[1]);
        if (labelOption == 1) {
            label = "heavy";
        }
    }

    public double getpLinkScore() {
        return pLinkScore;
    }

    public void setpLinkScore(double pLinkScore) {
        this.pLinkScore = pLinkScore;
    }

    public double getCalc_m() {
        return calc_m;
    }

    public void setCalc_m(double calc_m) {
        this.calc_m = calc_m;
    }

    public double getDelta_m() {
        return delta_m;
    }

    public void setDelta_m(double delta_m) {
        this.delta_m = delta_m;
    }

    public double getPpm() {
        return ppm;
    }

    public void setPpm(double ppm) {
        this.ppm = ppm;
    }

    public String getSpectrumTitle() {
        return spectrumTitle;
    }

    public void setSpectrumTitle(String spectrumTitle) {
        this.spectrumTitle = spectrumTitle;
    }

    public String getSpectrumFileName() {
        return spectrumFileName;
    }

    public void setSpectrumFileName(String spectrumFileName) {
        this.spectrumFileName = spectrumFileName;
    }

    public String getPeptide_pairs() {
        return peptide_pairs;
    }

    public void setPeptide_pairs(String peptide_pairs) {
        this.peptide_pairs = peptide_pairs;
    }

    public String getPeptideA() {
        return peptideA;
    }

    public void setPeptideA(String peptideA) {
        this.peptideA = peptideA;
    }

    public String getPeptideB() {
        return peptideB;
    }

    public void setPeptideB(String peptideB) {
        this.peptideB = peptideB;
    }

    public String getModifications() {
        return modifications;
    }

    public void setModifications(String modifications) {
        this.modifications = modifications;
    }

    public String getXlinkedProteins() {
        return xlinkedProteins;
    }

    public void setXlinkedProteins(String xlinkedProteins) {
        this.xlinkedProteins = xlinkedProteins;
    }

}

  
