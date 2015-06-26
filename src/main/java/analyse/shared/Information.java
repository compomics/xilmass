/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package analyse.shared;

import java.util.Comparator;

/**
 * This class holds XPSMs regarding informations from program output! 
 * 
 * @author Sule
 */
public class Information {
    private String fileName,
            scanNumber,
            proteinA,
            peptideA,
            linkA,
            proteinB,
            peptideB,
            linkB,
            label,
            td,
            predicted,
            euclidean_alpha,
            euclidean_beta,
            foundBy;
    private double score;

    public Information(String foundBy, String fileName, String scanNumber, 
            String proteinA, String peptideA, String modA, String linkA, 
            String proteinB, String peptideB, String modB, String linkB, 
            String label, String td, String predicted, 
            String euclidean_alpha, String euclidean_beta, 
            double score) {
        this.foundBy = foundBy;
        this.fileName = fileName;
        this.scanNumber = scanNumber;
        this.proteinA = proteinA;
        this.peptideA = peptideA;
        this.linkA = linkA;
        this.proteinB = proteinB;
        this.peptideB = peptideB;
        this.linkB = linkB;
        this.label = label;
        this.td = td;
        this.predicted = predicted;
        this.euclidean_alpha = euclidean_alpha;
        this.euclidean_beta = euclidean_beta;
        this.score = score;
    }

    public String getFoundBy() {
        return foundBy;
    }

    public void setFoundBy(String foundBy) {
        this.foundBy = foundBy;
    }
    
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getScanNumber() {
        return scanNumber;
    }

    public void setScanNumber(String scanNumber) {
        this.scanNumber = scanNumber;
    }

    public String getProteinA() {
        return proteinA;
    }

    public void setProteinA(String proteinA) {
        this.proteinA = proteinA;
    }

    public String getPeptideA() {
        return peptideA;
    }

    public void setPeptideA(String peptideA) {
        this.peptideA = peptideA;
    }

    public String getLinkA() {
        return linkA;
    }

    public void setLinkA(String linkA) {
        this.linkA = linkA;
    }

    public String getProteinB() {
        return proteinB;
    }

    public void setProteinB(String proteinB) {
        this.proteinB = proteinB;
    }

    public String getPeptideB() {
        return peptideB;
    }

    public void setPeptideB(String peptideB) {
        this.peptideB = peptideB;
    }

    public String getLinkB() {
        return linkB;
    }

    public void setLinkB(String linkB) {
        this.linkB = linkB;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTd() {
        return td;
    }

    public void setTd(String td) {
        this.td = td;
    }

    public String getPredicted() {
        return predicted;
    }

    public void setPredicted(String predicted) {
        this.predicted = predicted;
    }

    public String getEuclidean_alpha() {
        return euclidean_alpha;
    }

    public void setEuclidean_alpha(String euclidean_alpha) {
        this.euclidean_alpha = euclidean_alpha;
    }

    public String getEuclidean_beta() {
        return euclidean_beta;
    }

    public void setEuclidean_beta(String euclidean_beta) {
        this.euclidean_beta = euclidean_beta;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
    
    
     public static final Comparator<Information> ScoreASC
            = new Comparator<Information>() {
                @Override
                public int compare(Information o1, Information o2) {
                    return o1.getScore() < o2.getScore() ? -1 : o1.getScore() == o2.getScore()? 0 : 1;
                }
            };
     
     public static final Comparator<Information> ScoreDESC
            = new Comparator<Information>() {
                @Override
                public int compare(Information o1, Information o2) {
                    return o2.getScore() < o1.getScore() ? -1 : o2.getScore() == o1.getScore()? 0 : 1;
                }
            };

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + (this.fileName != null ? this.fileName.hashCode() : 0);
        hash = 53 * hash + (this.scanNumber != null ? this.scanNumber.hashCode() : 0);
        hash = 53 * hash + (this.proteinA != null ? this.proteinA.hashCode() : 0);
        hash = 53 * hash + (this.peptideA != null ? this.peptideA.hashCode() : 0);
        hash = 53 * hash + (this.linkA != null ? this.linkA.hashCode() : 0);
        hash = 53 * hash + (this.proteinB != null ? this.proteinB.hashCode() : 0);
        hash = 53 * hash + (this.peptideB != null ? this.peptideB.hashCode() : 0);
        hash = 53 * hash + (this.linkB != null ? this.linkB.hashCode() : 0);
        hash = 53 * hash + (this.label != null ? this.label.hashCode() : 0);
        hash = 53 * hash + (this.td != null ? this.td.hashCode() : 0);
        hash = 53 * hash + (this.predicted != null ? this.predicted.hashCode() : 0);
        hash = 53 * hash + (this.euclidean_alpha != null ? this.euclidean_alpha.hashCode() : 0);
        hash = 53 * hash + (this.euclidean_beta != null ? this.euclidean_beta.hashCode() : 0);
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.score) ^ (Double.doubleToLongBits(this.score) >>> 32));
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
        final Information other = (Information) obj;
        if ((this.fileName == null) ? (other.fileName != null) : !this.fileName.equals(other.fileName)) {
            return false;
        }
        if ((this.scanNumber == null) ? (other.scanNumber != null) : !this.scanNumber.equals(other.scanNumber)) {
            return false;
        }
        if ((this.proteinA == null) ? (other.proteinA != null) : !this.proteinA.equals(other.proteinA)) {
            return false;
        }
//        if ((this.peptideA == null) ? (other.peptideA != null) : !this.peptideA.equals(other.peptideA)) {
//            return false;
//        }
        if ((this.linkA == null) ? (other.linkA != null) : !this.linkA.equals(other.linkA)) {
            return false;
        }
        if ((this.proteinB == null) ? (other.proteinB != null) : !this.proteinB.equals(other.proteinB)) {
            return false;
        }
//        if ((this.peptideB == null) ? (other.peptideB != null) : !this.peptideB.equals(other.peptideB)) {
//            return false;
//        }
        if ((this.linkB == null) ? (other.linkB != null) : !this.linkB.equals(other.linkB)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return  fileName + "\t" + scanNumber + "\t" + proteinA + "\t" + peptideA + "\t" + linkA + "\t" + proteinB + "\t" + peptideB + "\t" + linkB + "\t" 
                + label + "\t" + td + "\t" + predicted + "\t" + euclidean_alpha + "\t" + euclidean_beta + "\t" + foundBy + "\t" + score ;
    }
    
    
    
    
}
