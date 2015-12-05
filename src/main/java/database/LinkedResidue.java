/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.compomics.util.protein.Protein;

/**
 * This class contains all information for linked residue.
 *
 * @author Sule
 */
public class LinkedResidue {

    private Protein protein; // protein (indeed this is part of a protein that putative peptide is obtained)
    private LinkedResidueType resType; // linked residue information (for example N-termini or M resiude)
    private String sequence; // sequence of given protein
    private int position; // position number of a given sequence that a linked residue is attached
    private boolean doesContainProteinNTerminus = false, // true: this sequence still has a protein N-terminus; false: this sequence does not have a protein N-terminus 
            doesContainProteinCTerminus = false, // true: this sequence still has a protein C-terminus; false: this sequence does not have a protein C-terminus 
            isMethionineFirstResidue = false; // true: Methionine is the first residue, false: a sequence starts with any other residue than methionine

    public LinkedResidue(Protein protein, int position, LinkedResidueType resType, boolean doesContainProteinNtermini, boolean doesContainProteinCTermini) {
        this.protein = protein;
        this.resType = resType;
        this.sequence = protein.getSequence().getSequence();
        this.position = position;
        this.doesContainProteinNTerminus = doesContainProteinNtermini;
        this.doesContainProteinCTerminus = doesContainProteinCTermini;
        if (sequence.startsWith("M")) {
            isMethionineFirstResidue = true;  
        }
        if(resType.equals(LinkedResidueType.NTerminiIncludesM) && position==1){
            this.position=position-1;
        }
    }

    public Protein getProtein() {
        return protein;
    }

    public void setProtein(Protein protein) {
        this.protein = protein;
    }

    public LinkedResidueType getResType() {
        return resType;
    }

    public void setResType(LinkedResidueType resType) {
        this.resType = resType;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isDoesContainProteinNtermini() {
        return doesContainProteinNTerminus;
    }

    public void setDoesContainProteinNtermini(boolean doesContainProteinNtermini) {
        this.doesContainProteinNTerminus = doesContainProteinNtermini;
    }

    public boolean isDoesContainProteinCTermini() {
        return doesContainProteinCTerminus;
    }

    public void setDoesContainProteinCTermini(boolean doesContainProteinCTermini) {
        this.doesContainProteinCTerminus = doesContainProteinCTermini;
    }

    public boolean isIsMethionineFirstResidue() {
        return isMethionineFirstResidue;
    }

    public void setIsMethionineFirstResidue(boolean isMethionineFirstResidue) {
        this.isMethionineFirstResidue = isMethionineFirstResidue;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.protein != null ? this.protein.hashCode() : 0);
        hash = 97 * hash + (this.resType != null ? this.resType.hashCode() : 0);
        hash = 97 * hash + (this.sequence != null ? this.sequence.hashCode() : 0);
        hash = 97 * hash + this.position;
        hash = 97 * hash + (this.doesContainProteinNTerminus ? 1 : 0);
        hash = 97 * hash + (this.doesContainProteinCTerminus ? 1 : 0);
        hash = 97 * hash + (this.isMethionineFirstResidue ? 1 : 0);
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
        final LinkedResidue other = (LinkedResidue) obj;
        if (this.protein != other.protein && (this.protein == null || !this.protein.equals(other.protein))) {
            return false;
        }
        if (this.resType != other.resType) {
            return false;
        }
        if ((this.sequence == null) ? (other.sequence != null) : !this.sequence.equals(other.sequence)) {
            return false;
        }
        if (this.position != other.position) {
            return false;
        }
        if (this.doesContainProteinNTerminus != other.doesContainProteinNTerminus) {
            return false;
        }
        if (this.doesContainProteinCTerminus != other.doesContainProteinCTerminus) {
            return false;
        }
        if (this.isMethionineFirstResidue != other.isMethionineFirstResidue) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LinkedResidue{" + "protein=" + protein + ", resType=" + resType + ", sequence=" + sequence + ", position=" + position
                + ", doesContainProteinCtermini=" + doesContainProteinCTerminus + ", doesContainProteinNtermini=" + doesContainProteinNTerminus
                + ", isMethionineFirstResidue=" + isMethionineFirstResidue + '}';
    }

}
