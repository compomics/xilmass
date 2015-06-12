/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package analyse.xwalk_uniprot;

/**
 * This class holds information of predicted true cross linkings
 * 
 * @author Sule
 */
public class TrueLinking {
    
    private String proteinA,
            proteinB,
            classification;
    private int indexA,
            indexB;
    private double euclidean_distance_beta,
            euclidean_distance_alpha,
            sas_distance;

    public TrueLinking(String proteinA, String proteinB, String classification, int indexA, int indexB, double euclidean_distance_beta, double euclidean_distance_alpha, double sas_distance) {
        this.proteinA = proteinA;
        this.euclidean_distance_alpha = euclidean_distance_alpha;
        this.proteinB = proteinB;
        this.indexA = indexA;
        this.indexB = indexB;
        this.euclidean_distance_beta = euclidean_distance_beta;
        this.sas_distance = sas_distance;
        this.classification = classification;
    }
    public TrueLinking(String proteinA, String proteinB, String classification, int indexA, int indexB, double euclidean_distance_beta, double euclidean_distance_alpha) {
        this.proteinA = proteinA;
        this.euclidean_distance_alpha = euclidean_distance_alpha;
        this.proteinB = proteinB;
        this.indexA = indexA;
        this.indexB = indexB;
        this.euclidean_distance_beta = euclidean_distance_beta;
        this.classification = classification;
    }

    public double getEuclidean_distance_beta() {
        return euclidean_distance_beta;
    }

    public void setEuclidean_distance_beta(double euclidean_distance_beta) {
        this.euclidean_distance_beta = euclidean_distance_beta;
    }

    public double getEuclidean_distance_alpha() {
        return euclidean_distance_alpha;
    }

    public void setEuclidean_distance_alpha(double euclidean_distance_alpha) {
        this.euclidean_distance_alpha = euclidean_distance_alpha;
    }    

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
    
    public String getProteinA() {
        return proteinA;
    }

    public void setProteinA(String proteinA) {
        this.proteinA = proteinA;
    }

    public String getProteinB() {
        return proteinB;
    }

    public void setProteinB(String proteinB) {
        this.proteinB = proteinB;
    }

    public int getIndexA() {
        return indexA;
    }

    public void setIndexA(int indexA) {
        this.indexA = indexA;
    }

    public int getIndexB() {
        return indexB;
    }

    public void setIndexB(int indexB) {
        this.indexB = indexB;
    }


    public double getSas_distance() {
        return sas_distance;
    }

    public void setSas_distance(double sas_distance) {
        this.sas_distance = sas_distance;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.proteinA != null ? this.proteinA.hashCode() : 0);
        hash = 37 * hash + (this.proteinB != null ? this.proteinB.hashCode() : 0);
        hash = 37 * hash + (this.classification != null ? this.classification.hashCode() : 0);
        hash = 37 * hash + this.indexA;
        hash = 37 * hash + this.indexB;
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.euclidean_distance_beta) ^ (Double.doubleToLongBits(this.euclidean_distance_beta) >>> 32));
        hash = 37 * hash + (int) (Double.doubleToLongBits(this.sas_distance) ^ (Double.doubleToLongBits(this.sas_distance) >>> 32));
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
        final TrueLinking other = (TrueLinking) obj;
        if ((this.proteinA == null) ? (other.proteinA != null) : !this.proteinA.equals(other.proteinA)) {
            return false;
        }
        if ((this.proteinB == null) ? (other.proteinB != null) : !this.proteinB.equals(other.proteinB)) {
            return false;
        }
        if (this.indexA != other.indexA) {
            return false;
        }
        if (this.indexB != other.indexB) {
            return false;
        }
        if (Double.doubleToLongBits(this.euclidean_distance_beta) != Double.doubleToLongBits(other.euclidean_distance_beta)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TrueLinking{" + "proteinA=" + proteinA + ", proteinB=" + proteinB + ", classification=" + classification + ", indexA=" + indexA + ", indexB=" + indexB + ", euclidean_distance=" + euclidean_distance_beta + ", sas_distance=" + sas_distance + '}';
    }
    
    
}
