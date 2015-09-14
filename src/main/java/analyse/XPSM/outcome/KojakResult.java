/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.XPSM.outcome;

import com.compomics.dbtoolkit.io.DBLoaderLoader;
import com.compomics.dbtoolkit.io.interfaces.DBLoader;
import com.compomics.util.protein.Protein;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * This class holds all written information on Kojak result file
 *
 * @author Sule
 */
public class KojakResult extends Outcome {

    private int charge;
    private double obsMass,
            psms_mass,
            ppmErr,
            score,
            dScore,
            linkerMass,
            pepDiff;
    private String modPeptide1,
            modPeptide2;
    private File fasta;
    private ArrayList<Protein> proteins = new ArrayList<Protein>();

    public KojakResult(String spectrumFileName, String scanNumber, double obsMass, int charge, double psm_mass, double ppmErr, double score, double dScore, double pepDiff,
            String peptide1, int link1, String protein1, String peptide2, int link2, String protein2, double linkerMass, String[] protein_names,
            File databaseFile, String target_decoy) throws IOException {
        super.spectrumFileName = spectrumFileName;
        super.scanNumber = scanNumber;
        super.spectrumTitle = spectrumFileName + "_" + scanNumber;
        this.obsMass = obsMass;
        this.charge = charge;
        this.psms_mass = psm_mass;
        this.ppmErr = ppmErr;
        this.score = score;
        this.dScore = dScore;
        this.pepDiff = pepDiff;
        this.modPeptide1 = getMod(peptide1);
        super.crossLinkedSitePro1 = link1;
        this.accProteinA = protein1;
        this.modPeptide2 = getMod(peptide2);
        super.crossLinkedSitePro2 = link2;
        super.accProteinB = protein2;
        this.linkerMass = linkerMass;
        this.target_proteins = protein_names;
        fasta = databaseFile;
        label = "light";
        if (Math.abs(linkerMass - 150.143404) < 0.01 || Math.abs(linkerMass - 168.15393) < 0.01) {
            label = "heavy";
        }
        DBLoader loader = DBLoaderLoader.loadDB(fasta);
        Protein protein = null;
        // get a crossLinkerName object        
        while ((protein = loader.nextProtein()) != null) {
            proteins.add(protein);
        }
        // make sure about proteinAccessions...
        super.peptideA = getSeqNoMod(peptide1);
        super.peptideB = getSeqNoMod(peptide2);

        //>sp|Q15149|175-400PlectinABDisoform1a(FromJakeSong)(33); - add 33 to the link..
        for (Protein p : proteins) {
            if (p.getHeader().getAccession().equals(accProteinA)) {
                crossLinkedSitePro1 += p.getSequence().getSequence().indexOf(this.peptideA);
            }
            if (p.getHeader().getAccession().equals(accProteinB)) {
                crossLinkedSitePro2 += p.getSequence().getSequence().indexOf(this.peptideB);
            }
        }
        super.target_decoy = target_decoy;
    }

    public String getModPeptide1() {
        return modPeptide1;
    }

    public void setModPeptide1(String modPeptide1) {
        this.modPeptide1 = modPeptide1;
    }

    public String getModPeptide2() {
        return modPeptide2;
    }

    public void setModPeptide2(String modPeptide2) {
        this.modPeptide2 = modPeptide2;
    }

    /**
     * Returns a peptide sequence without modification on a string itself
     *
     * @param pep
     * @return
     */
    public String getSeqNoMod(String pep) {
        String tmpPep = "";
        boolean control = false;
        for (int i = 0; i < pep.length(); i++) {
            char tmpCh = pep.charAt(i);
            if (tmpCh == '[') {
                control = true;
            }
            if (!control) {
                tmpPep += tmpCh;
            } else if (tmpCh == ']') {
                control = false;
            }
        }
        return tmpPep;
    }

    /**
     * Returns a peptide sequence without modification on a string itself
     *
     * @param pep
     * @return
     */
    public String getMod(String pep) {
        String tmpPep = "";
        boolean control = false;
        for (int i = 0; i < pep.length(); i++) {
            char tmpCh = pep.charAt(i);
            if (tmpCh == '[') {
                control = true;
            }
            if (control) {
                if (tmpCh == ']') {
                    control = false;
                } else {
                    tmpPep += tmpCh;
                }
            }
        }
        return tmpPep;
    }

    public int getCharge() {
        return charge;
    }

    public void setCharge(int charge) {
        this.charge = charge;
    }

    public double getObsMass() {
        return obsMass;
    }

    public void setObsMass(double obsMass) {
        this.obsMass = obsMass;
    }

    public double getPsms_mass() {
        return psms_mass;
    }

    public void setPsms_mass(double psms_mass) {
        this.psms_mass = psms_mass;
    }

    public double getPpmErr() {
        return ppmErr;
    }

    public void setPpmErr(double ppmErr) {
        this.ppmErr = ppmErr;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public double getdScore() {
        return dScore;
    }

    public void setdScore(double dScore) {
        this.dScore = dScore;
    }

    public double getLinkerMass() {
        return linkerMass;
    }

    public void setLinkerMass(double linkerMass) {
        this.linkerMass = linkerMass;
    }

    public double getPepDiff() {
        return pepDiff;
    }

    public void setPepDiff(double pepDiff) {
        this.pepDiff = pepDiff;
    }

    public String getScanNumber() {
        return scanNumber;
    }

    public void setScanNumber(String scanNumber) {
        this.scanNumber = scanNumber;
    }

    public String getSpectrumFileName() {
        return spectrumFileName;
    }

    public void setSpectrumFileName(String spectrumFileName) {
        this.spectrumFileName = spectrumFileName;
    }

    public String getPeptide1() {
        return peptideA;
    }

    public void setPeptide1(String peptide1) {
        this.peptideA = peptide1;
    }

    public String getPeptide2() {
        return peptideB;
    }

    public void setPeptide2(String peptide2) {
        this.peptideB = peptide2;
    }

    public File getFasta() {
        return fasta;
    }

    public void setFasta(File fasta) {
        this.fasta = fasta;
    }

    public ArrayList<Protein> getProteins() {
        return proteins;
    }

    public void setProteins(ArrayList<Protein> proteins) {
        this.proteins = proteins;
    }

    public String toPrint() {
//        String title = "SpectrumFile" + "\t" + "ScanNr" + "\t"
//                + "ObservedMass(Da)" + "\t" + "PrecursorCharge" + "\t"
//                + "PSM_Mass" + "\t"
//                + "Score" + "\t" + "dScore" + "\t" + "PepDiff" + "\t"
//                + "PeptideA" + "\t" + "ProteinA" + "\t" + "ModA" + "\t"
//                + "PeptideB" + "\t" + "ProteinB" + "\t" + "ModB" + "\t"
//                + "LinkPeptideA" + "\t" + "LinkPeptideB" + "\t" + "LinkProteinA" + "\t" + "LinkProteinB" + "\t"
//                + "TargetDecoy" + "\t"
//                + "LinkerLabeling" + "\t"
//                + "Predicted" + "\t" + "EuclideanDistance(Carbon-betas-A)" + "\t" + "EuclideanDistance (Carbon alphas-A)";
        String res = spectrumFileName + "\t" + scanNumber + "\t"
                + obsMass + "\t" + charge + "\t"
                + psms_mass + "\t"
                + score + "\t" + dScore + "\t" + pepDiff + "\t"
                + peptideA + "\t" + accProteinA + "\t" + modPeptide1 + "\t"
                + peptideB + "\t" + accProteinB + "\t" + modPeptide2 + "\t"
                + "-" + "\t" + "-" + "\t"
                + super.crossLinkedSitePro1 + "\t" + super.crossLinkedSitePro2 + "\t"
                + target_decoy + "\t"
                + label + "\t"
                + super.trueCrossLinking;
        return res;
    }

    public static final Comparator<KojakResult> ScoreDSC
            = new Comparator<KojakResult>() {
                @Override
                public int compare(KojakResult o1, KojakResult o2) {
                    double res = o1.getScore() - o2.getScore();
                    if (o1.getScore() > o2.getScore()) {
                        return -1;
                    } else if (o1.getScore() < o2.getScore()) {
                        return 1;
                    } else {
                        if (o1.getTarget_decoy().equals("TT")) {
                            if (o2.getTarget_decoy().equals("TT")) {
                                return 0;
                            } else {
                                return -1;
                            }
                        } else if (o1.getTarget_decoy().equals("DD")) {
                            if (o2.getTarget_decoy().equals("DD")) {
                                return 0;
                            } else {
                                return 1;
                            }
                        } else if (o2.getTarget_decoy().equals("TD")) {
                            if (o2.getTarget_decoy().equals("TT")) {
                                return 1;
                            } else {
                                return -1;
                            }
                        }
                    }
                    return 0;
                }
            };

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.charge;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.obsMass) ^ (Double.doubleToLongBits(this.obsMass) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.psms_mass) ^ (Double.doubleToLongBits(this.psms_mass) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.ppmErr) ^ (Double.doubleToLongBits(this.ppmErr) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.score) ^ (Double.doubleToLongBits(this.score) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.dScore) ^ (Double.doubleToLongBits(this.dScore) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.linkerMass) ^ (Double.doubleToLongBits(this.linkerMass) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.pepDiff) ^ (Double.doubleToLongBits(this.pepDiff) >>> 32));
        hash = 47 * hash + (this.scanNumber != null ? this.scanNumber.hashCode() : 0);
        hash = 47 * hash + (this.spectrumFileName != null ? this.spectrumFileName.hashCode() : 0);
        hash = 47 * hash + (this.peptideA != null ? this.peptideA.hashCode() : 0);
        hash = 47 * hash + (this.peptideB != null ? this.peptideB.hashCode() : 0);
        hash = 47 * hash + (this.modPeptide1 != null ? this.modPeptide1.hashCode() : 0);
        hash = 47 * hash + (this.modPeptide2 != null ? this.modPeptide2.hashCode() : 0);
        hash = 47 * hash + (this.fasta != null ? this.fasta.hashCode() : 0);
        hash = 47 * hash + (this.proteins != null ? this.proteins.hashCode() : 0);
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
        final KojakResult other = (KojakResult) obj;
        if (this.charge != other.charge) {
            return false;
        }
        if (Double.doubleToLongBits(this.obsMass) != Double.doubleToLongBits(other.obsMass)) {
            return false;
        }
        if (Double.doubleToLongBits(this.psms_mass) != Double.doubleToLongBits(other.psms_mass)) {
            return false;
        }
        if (Double.doubleToLongBits(this.ppmErr) != Double.doubleToLongBits(other.ppmErr)) {
            return false;
        }
        if (Double.doubleToLongBits(this.score) != Double.doubleToLongBits(other.score)) {
            return false;
        }
        if (Double.doubleToLongBits(this.dScore) != Double.doubleToLongBits(other.dScore)) {
            return false;
        }
        if (Double.doubleToLongBits(this.linkerMass) != Double.doubleToLongBits(other.linkerMass)) {
            return false;
        }
        if (Double.doubleToLongBits(this.pepDiff) != Double.doubleToLongBits(other.pepDiff)) {
            return false;
        }
        if ((this.scanNumber == null) ? (other.scanNumber != null) : !this.scanNumber.equals(other.scanNumber)) {
            return false;
        }
        if ((this.spectrumFileName == null) ? (other.spectrumFileName != null) : !this.spectrumFileName.equals(other.spectrumFileName)) {
            return false;
        }
        if ((this.peptideA == null) ? (other.peptideA != null) : !this.peptideA.equals(other.peptideA)) {
            return false;
        }
        if ((this.peptideB == null) ? (other.peptideB != null) : !this.peptideB.equals(other.peptideB)) {
            return false;
        }
        if ((this.modPeptide1 == null) ? (other.modPeptide1 != null) : !this.modPeptide1.equals(other.modPeptide1)) {
            return false;
        }
        if ((this.modPeptide2 == null) ? (other.modPeptide2 != null) : !this.modPeptide2.equals(other.modPeptide2)) {
            return false;
        }
        if (this.fasta != other.fasta && (this.fasta == null || !this.fasta.equals(other.fasta))) {
            return false;
        }
        if (this.proteins != other.proteins && (this.proteins == null || !this.proteins.equals(other.proteins))) {
            return false;
        }
        return true;
    }

}

enum PersonComparator implements Comparator<KojakResult> {

    SCORE_SORT {
                @Override
                public int compare(KojakResult o1, KojakResult o2) {
                    return o1.getScore() > o2.getScore() ? -1 : o1.getScore() == o2.getScore() ? 0 : 1;
                }
            },
    TARGET_SORT {
                @Override
                public int compare(KojakResult o1, KojakResult o2) {
                    return o1.getTarget_decoy().compareTo(o2.getTarget_decoy());
                }
            };

    public static Comparator<KojakResult> decending(final Comparator<KojakResult> other) {
        return new Comparator<KojakResult>() {
            @Override
            public int compare(KojakResult o1, KojakResult o2) {
                return -1 * other.compare(o1, o2);
            }
        };
    }

    public static Comparator<KojakResult> getComparator(final PersonComparator... multipleOptions) {
        return new Comparator<KojakResult>() {
            @Override
            public int compare(KojakResult o1, KojakResult o2) {
                for (PersonComparator option : multipleOptions) {
                    int result = option.compare(o1, o2);
                    if (result != 0) {
                        return result;
                    }
                }
                return 0;
            }
        };
    }
}
