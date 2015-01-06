/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.AminoAcid;
import com.compomics.util.experiment.biology.Atom;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author Sule
 */
public class LinkedPeptideIon {

    private Peptide linkedPeptide;
    private int index_linkedPeptide;
    private ArrayList<ArrayList<Character>> aaLinkedPeptideStrs;
    private ArrayList<Double> cTerminiMasses = new ArrayList<Double>(),
            nTerminiMasses = new ArrayList<Double>();

    public LinkedPeptideIon(Peptide linkedPeptide, int index_linkedPeptide) {
        this.linkedPeptide = linkedPeptide;
        this.index_linkedPeptide = index_linkedPeptide;
        getAALinkedPeptide();
    }

    public ArrayList<ArrayList<Character>> getAaLinkedPeptideStrs() {
        return aaLinkedPeptideStrs;
    }

    public void setAaLinkedPeptideStrs(ArrayList<ArrayList<Character>> aaLinkedPeptideStrs) {
        this.aaLinkedPeptideStrs = aaLinkedPeptideStrs;
    }

    public ArrayList<Double> getcTerminiMasses() {
        return cTerminiMasses;
    }

    public void setcTerminiMasses(ArrayList<Double> cTerminiMasses) {
        this.cTerminiMasses = cTerminiMasses;
    }

    public ArrayList<Double> getnTerminiMasses() {
        return nTerminiMasses;
    }

    public void setnTerminiMasses(ArrayList<Double> nTerminiMasses) {
        this.nTerminiMasses = nTerminiMasses;
    }

    public ArrayList<ArrayList<Character>> getAALinkedPeptide() {
        ArrayList<ArrayList<Character>> characters = new ArrayList<ArrayList<Character>>();
        int startIndex = index_linkedPeptide,
                endIndex = linkedPeptide.getSequence().length();
        ArrayList<Character> startCharacters = new ArrayList<Character>();
        for (int i = startIndex; i < endIndex; i++) {
            char start = linkedPeptide.getSequence().charAt(i);
            startCharacters.add(start);
            characters.add(new ArrayList(startCharacters));
            for (int start_i = startIndex - 1; start_i >= 0; start_i--) {
                char tmp_char_start_i = linkedPeptide.getSequence().charAt(start_i);
                ArrayList<Character> cpy = new ArrayList<Character>(characters.get(characters.size() - 1));
                cpy.add(0, tmp_char_start_i);
                characters.add(cpy);
            }
        }
        aaLinkedPeptideStrs = characters;
        return characters;
    }

    public void calculateNTerminiMasses(int type) {
        int startIndex = index_linkedPeptide,
                endIndex = linkedPeptide.getSequence().length();
        double startMass = 0;
        double nTerminiMassAddition = getMassDiff(type);
        for (int i = startIndex; i < endIndex; i++) {
            char start = linkedPeptide.getSequence().charAt(i);
            // here is the first one with N termini 
            if (i == 0) {
                startMass += AminoAcid.getAminoAcid(start).monoisotopicMass;
                startMass += nTerminiMassAddition;
            } else {
                startMass += AminoAcid.getAminoAcid(start).monoisotopicMass;
            }
            nTerminiMasses.add(startMass);
            double tmp_mass = startMass;
            for (int start_i = startIndex - 1; start_i >= 0; start_i--) {
                char tmp_char_start_i = linkedPeptide.getSequence().charAt(start_i);
                tmp_mass += AminoAcid.getAminoAcid(tmp_char_start_i).monoisotopicMass;
                if (start_i == 0) {
                    tmp_mass += nTerminiMassAddition;
                }
                nTerminiMasses.add(tmp_mass);
            }
        }
    }

    public void calculateCTerminiMasses(int type) {
        int startIndex = index_linkedPeptide,
                endIndex = linkedPeptide.getSequence().length();
        double startMass = 0;
        double cTerminiMassAddition = getMassDiff(type);
        for (int i = startIndex; i < endIndex; i++) {
            char start = linkedPeptide.getSequence().charAt(i);
            // here is the first one with N termini 
            if (i == endIndex - 1) {
                startMass += AminoAcid.getAminoAcid(start).monoisotopicMass;
                startMass += cTerminiMassAddition;
            } else {
                startMass += AminoAcid.getAminoAcid(start).monoisotopicMass;
            }
            cTerminiMasses.add(startMass);
            double tmp_mass = startMass;
            for (int start_i = startIndex - 1; start_i >= 0; start_i--) {
                char tmp_char_start_i = linkedPeptide.getSequence().charAt(start_i);;
                tmp_mass += AminoAcid.getAminoAcid(tmp_char_start_i).monoisotopicMass;
                cTerminiMasses.add(tmp_mass);
            }
        }
    }

    private double getMassDiff(int type) {
        double mass = Atom.H.getMonoisotopicMass();
        switch (type) {
            case PeptideFragmentIon.A_ION:
                mass = -(Atom.C.getMonoisotopicMass() + Atom.O.getMonoisotopicMass());
                break;
            case PeptideFragmentIon.C_ION:
                mass = Atom.N.getMonoisotopicMass() + (3 * Atom.H.getMonoisotopicMass());
                break;
            case PeptideFragmentIon.X_ION:
                mass = Atom.C.getMonoisotopicMass() + (2 * Atom.O.getMonoisotopicMass());
                break;
            case PeptideFragmentIon.Y_ION:
                mass = 2 * Atom.H.getMonoisotopicMass() + (Atom.O.getMonoisotopicMass());
                break;
            case PeptideFragmentIon.Z_ION:
                mass = Atom.O.getMonoisotopicMass() - Atom.N.getMonoisotopicMass();
                break;
        }
        return mass;
    }
}
