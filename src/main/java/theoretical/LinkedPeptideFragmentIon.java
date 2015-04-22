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

/**
 * This class holds information about a linked peptide. B-peptide is a linked
 * peptide to a A-peptide. So, all linkedPeptideIon masses derived from
 * B-peptide are calculated here. Also, all possible fragments derived from
 * B-peptides according to a linked position are generated.
 *
 *
 * @author Sule
 */
public final class LinkedPeptideFragmentIon {

    private Peptide linkedPeptide; // 
    private int linker_position_on_linkedPeptide; // An index on where a linker is bond to that linkedPeptide
    private ArrayList<Double> cTerminiMasses = new ArrayList<Double>(),
            nTerminiMasses = new ArrayList<Double>();

    public LinkedPeptideFragmentIon(Peptide linkedPeptide, int linker_position_on_linkedPeptide) {
        this.linkedPeptide = linkedPeptide;
        this.linker_position_on_linkedPeptide = linker_position_on_linkedPeptide;
        getAALinkedPeptide();
    }

    public ArrayList<Double> getCTerminiMasses(int fragmentIonType) {
        if (cTerminiMasses.isEmpty()) {
            calculateCTerminiMasses(fragmentIonType);
        }
        return cTerminiMasses;
    }

    public ArrayList<Double> getNTerminiMasses(int fragmentIonType) {
        if (nTerminiMasses.isEmpty()) {
            calculateNTerminiMasses(fragmentIonType);
        }
        return nTerminiMasses;
    }

    /**
     * Here all possible fragment ion parts from linked peptide is written as an
     * arraylist of character of a single letter amino acid code
     *
     * @return
     */
    public ArrayList<ArrayList<Character>> getAALinkedPeptide() {
        ArrayList<ArrayList<Character>> characters = new ArrayList<ArrayList<Character>>();
        int startIndex = linker_position_on_linkedPeptide,
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
        return characters;
    }

    /**
     * This method calculates monoisotopic masses of all linkedIons on Ntermini
     *
     * AI*KN would yield ions of K, IK, (Ntermini+)AIK, KN, IKN, (Ntermini+)AIKN
     * and their n-termini masses are calculated.
     *
     * @param fragmentIonType : PeptideFragmentIon
     * type-PeptideFragmentIon.A_ION, PeptideFragmentIon.B_ION,
     * PeptideFragmentIon.C_ION
     */
    private void calculateNTerminiMasses(int fragmentIonType) {
        int startIndex = linker_position_on_linkedPeptide,
                pepLength = linkedPeptide.getSequence().length();
        double startMass = 0;
        boolean isLeftResiduesCalculated = false;
        double nTerminiMassAddition = getMassDiff(fragmentIonType);   // b yields 0, the other needs to be calculated 
        ArrayList<Double> leftResidues = new ArrayList<Double>(3);
        for (int i = startIndex; i < pepLength; i++) {
            char start = linkedPeptide.getSequence().charAt(i);
            // here is the first residue on the left containing N-termini 
            if (i == 0) {
                startMass += AminoAcid.getAminoAcid(start).monoisotopicMass;
                startMass += nTerminiMassAddition;
            } else {
                startMass += AminoAcid.getAminoAcid(start).monoisotopicMass;
            }
            nTerminiMasses.add(startMass);
            double tmp_mass = startMass;
            if (isLeftResiduesCalculated) {
                // Add all left residues to calculated ones!
                for (Double leftRes : leftResidues) {
                    tmp_mass += leftRes;
                    nTerminiMasses.add(tmp_mass);
                }
            }
            // here prepare residues derived from the left of the linked one!
            if (!isLeftResiduesCalculated) {
                isLeftResiduesCalculated = true;
                for (int start_i = startIndex - 1; start_i >= 0; start_i--) {
                    char tmp_char_start_i = linkedPeptide.getSequence().charAt(start_i);
                    double leftRes = AminoAcid.getAminoAcid(tmp_char_start_i).monoisotopicMass;
                    leftResidues.add(leftRes);
                    tmp_mass += leftRes;
                    if (start_i == 0) {
                        leftRes += nTerminiMassAddition;
                    }
                    nTerminiMasses.add(tmp_mass);
                }
            }
        }
    }

    /**
     * This method calculates monoisotopic masses of all linkedIons on Ctermini
     *
     * AI*KN would yield ions of K, IK, AIK, KN(+Ctermini), IKN(+Ctermini) ,
     * AIKN(+Ctermini) and their c-termini masses are calculated.
     *
     * @param fragmentIonType : PeptideFragmentIon
     * type-PeptideFragmentIon.X_ION, PeptideFragmentIon.Y_ION,
     * PeptideFragmentIon.Z_ION
     */
    private void calculateCTerminiMasses(int fragmentIonType) {
        int startIndex = linker_position_on_linkedPeptide,
                pepLength = linkedPeptide.getSequence().length();
        double startMass = 0;
        boolean isLeftResiduesCalculated = false;
        double cTerminiMassAddition = getMassDiff(fragmentIonType);   // b yields 0, the other needs to be calculated 
        ArrayList<Double> leftResidues = new ArrayList<Double>(3);
        for (int i = startIndex; i < pepLength; i++) {
            char start = linkedPeptide.getSequence().charAt(i);
            // here is the first residue on the left containing C-termini 
            if (i == pepLength - 1) {
                startMass += AminoAcid.getAminoAcid(start).monoisotopicMass;
                startMass += cTerminiMassAddition;
            } else {
                startMass += AminoAcid.getAminoAcid(start).monoisotopicMass;
            }
            cTerminiMasses.add(startMass);
            double tmp_mass = startMass;
            if (isLeftResiduesCalculated) {
                // Add all left residues to calculated ones!
                for (Double leftRes : leftResidues) {
                    tmp_mass += leftRes;
                    cTerminiMasses.add(tmp_mass);
                }
            }
            // here prepare residues derived from the left of the linked one!
            if (!isLeftResiduesCalculated) {
                isLeftResiduesCalculated = true;
                for (int start_i = startIndex - 1; start_i >= 0; start_i--) {
                    char tmp_char_start_i = linkedPeptide.getSequence().charAt(start_i);
                    double leftRes = AminoAcid.getAminoAcid(tmp_char_start_i).monoisotopicMass;
                    leftResidues.add(leftRes);
                    tmp_mass += leftRes;
                    if (start_i == pepLength - 1) {
                        leftRes += cTerminiMassAddition;
                    }
                    cTerminiMasses.add(tmp_mass);
                }
            }
        }
    }

    /**
     * This method calculate mass shift on the first ion based on either
     * containing N-termini or C-termini
     *
     * @param fragmentIonType PeptideFragmentIon int class (use
     * PeptideFragmentIon.xxx_ION!
     * @return
     */
    private double getMassDiff(int fragmentIonType) {
        double mass = 0;
        switch (fragmentIonType) {
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
