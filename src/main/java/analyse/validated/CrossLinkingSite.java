/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.validated;

/**
 * This class contains linking site information! 
 * 
 * @author Sule
 */
public class CrossLinkingSite {

    private String proteinA,
            proteinB,
            linkA,
            linkB,
            tD, // target decoy.
            euclideanAlpha,
            euclideanBeta,
            prediction;

    public CrossLinkingSite(String proteinA, String proteinB, String linkA, String linkB, String tD, String euclideanAlpha, String euclideanBeta, String prediction) {
        this.proteinA = proteinA;
        this.proteinB = proteinB;
        this.linkA = linkA;
        this.linkB = linkB;
        this.tD = tD;
        this.euclideanAlpha = euclideanAlpha;
        this.euclideanBeta = euclideanBeta;
        this.prediction = prediction;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public String gettD() {
        return tD;
    }

    public void settD(String tD) {
        this.tD = tD;
    }

    public String getEuclideanAlpha() {
        return euclideanAlpha;
    }

    public void setEuclideanAlpha(String euclideanAlpha) {
        this.euclideanAlpha = euclideanAlpha;
    }

    public String getEuclideanBeta() {
        return euclideanBeta;
    }

    public void setEuclideanBeta(String euclideanBeta) {
        this.euclideanBeta = euclideanBeta;
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

    public String getLinkA() {
        return linkA;
    }

    public void setLinkA(String linkA) {
        this.linkA = linkA;
    }

    public String getLinkB() {
        return linkB;
    }

    public void setLinkB(String linkB) {
        this.linkB = linkB;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + (this.proteinA != null ? this.proteinA.hashCode() : 0);
        hash = 11 * hash + (this.proteinB != null ? this.proteinB.hashCode() : 0);
        hash = 11 * hash + (this.linkA != null ? this.linkA.hashCode() : 0);
        hash = 11 * hash + (this.linkB != null ? this.linkB.hashCode() : 0);
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
        final CrossLinkingSite other = (CrossLinkingSite) obj;
        if ((this.proteinA == null) ? (other.proteinA != null) : !this.proteinA.equals(other.proteinA)) {
            return false;
        }
        if ((this.proteinB == null) ? (other.proteinB != null) : !this.proteinB.equals(other.proteinB)) {
            return false;
        }
        if ((this.linkA == null) ? (other.linkA != null) : !this.linkA.equals(other.linkA)) {
            return false;
        }
        if ((this.linkB == null) ? (other.linkB != null) : !this.linkB.equals(other.linkB)) {
            return false;
        }
        return true;
    }

   

    @Override
    public String toString() {
        return "CrossLinkingSite{" + "proteinA=" + proteinA + ", proteinB=" + proteinB + ", linkA=" + linkA + ", linkB=" + linkB + ", tD=" + tD + '}';
    }

}
