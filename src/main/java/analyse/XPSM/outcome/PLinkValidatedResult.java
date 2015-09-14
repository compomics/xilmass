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
    private String peptide_pairs,
            modifications,
            xlinkedProteins;

    public PLinkValidatedResult(String spectrumFile, String spectrumTitle, String scanNumber,
            double pLinkScore, double calc_m, double delta_m, double ppm,
            String peptide_pairs, String modifications, String xlinkedProteins, String[] targets) throws IOException {
        this.pLinkScore = pLinkScore;
        this.calc_m = calc_m;
        this.delta_m = delta_m;
        this.ppm = ppm;
        this.spectrumTitle = spectrumTitle;
        this.spectrumFileName = spectrumFile;
        this.scanNumber = scanNumber;
        this.peptide_pairs = peptide_pairs;
        this.modifications = modifications;
        this.xlinkedProteins = xlinkedProteins;
        super.target_proteins = targets;
        // prepare accessions
        String[] splitProteins = xlinkedProteins.split("\\|");
        super.accProteinA = splitProteins[1].replace(" ", "");
        super.accProteinB = splitProteins[3].replace(" ", "");
        super.crossLinkedSitePro1 = Integer.parseInt(splitProteins[2].substring(splitProteins[2].lastIndexOf("(") + 1, splitProteins[2].lastIndexOf(")")));
        super.crossLinkedSitePro2 = Integer.parseInt(splitProteins[4].substring(splitProteins[4].lastIndexOf("(") + 1, splitProteins[4].lastIndexOf(")")));
        String[] split = peptide_pairs.split("-");
        super.peptideA = split[0].substring(0, split[0].indexOf("("));
        super.peptideB = split[1].substring(0, split[1].indexOf("("));
        super.label = "light";
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

    public String getPeptide_pairs() {
        return peptide_pairs;
    }

    public void setPeptide_pairs(String peptide_pairs) {
        this.peptide_pairs = peptide_pairs;
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

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.pLinkScore) ^ (Double.doubleToLongBits(this.pLinkScore) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.calc_m) ^ (Double.doubleToLongBits(this.calc_m) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.delta_m) ^ (Double.doubleToLongBits(this.delta_m) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.ppm) ^ (Double.doubleToLongBits(this.ppm) >>> 32));
        hash = 37 * hash + (this.spectrumTitle != null ? this.spectrumTitle.hashCode() : 0);
        hash = 37 * hash + (this.scanNumber != null ? this.scanNumber.hashCode() : 0);
        hash = 37 * hash + (this.spectrumFileName != null ? this.spectrumFileName.hashCode() : 0);
        hash = 37 * hash + (this.peptide_pairs != null ? this.peptide_pairs.hashCode() : 0);
        hash = 37 * hash + (this.peptideA != null ? this.peptideA.hashCode() : 0);
        hash = 37 * hash + (this.peptideB != null ? this.peptideB.hashCode() : 0);
        hash = 37 * hash + (this.modifications != null ? this.modifications.hashCode() : 0);
        hash = 37 * hash + (this.xlinkedProteins != null ? this.xlinkedProteins.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PLinkValidatedResult other = (PLinkValidatedResult) obj;
        if (Double.doubleToLongBits(this.pLinkScore) != Double.doubleToLongBits(other.pLinkScore)) {
            return false;
        }
        if (Double.doubleToLongBits(this.calc_m) != Double.doubleToLongBits(other.calc_m)) {
            return false;
        }
        if (Double.doubleToLongBits(this.delta_m) != Double.doubleToLongBits(other.delta_m)) {
            return false;
        }
        if (Double.doubleToLongBits(this.ppm) != Double.doubleToLongBits(other.ppm)) {
            return false;
        }
        if ((this.spectrumTitle == null) ? (other.spectrumTitle != null) : !this.spectrumTitle.equals(other.spectrumTitle)) {
            return false;
        }
        if ((this.scanNumber == null) ? (other.scanNumber != null) : !this.scanNumber.equals(other.scanNumber)) {
            return false;
        }
        if ((this.spectrumFileName == null) ? (other.spectrumFileName != null) : !this.spectrumFileName.equals(other.spectrumFileName)) {
            return false;
        }
        if ((this.peptide_pairs == null) ? (other.peptide_pairs != null) : !this.peptide_pairs.equals(other.peptide_pairs)) {
            return false;
        }
        if ((this.peptideA == null) ? (other.peptideA != null) : !this.peptideA.equals(other.peptideA)) {
            return false;
        }
        if ((this.peptideB == null) ? (other.peptideB != null) : !this.peptideB.equals(other.peptideB)) {
            return false;
        }
        if ((this.modifications == null) ? (other.modifications != null) : !this.modifications.equals(other.modifications)) {
            return false;
        }
        if ((this.xlinkedProteins == null) ? (other.xlinkedProteins != null) : !this.xlinkedProteins.equals(other.xlinkedProteins)) {
            return false;
        }
        return true;
    }

}
