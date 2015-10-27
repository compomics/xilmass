/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import crossLinker.CrossLinker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

/**
 * This class holds information derived from monolinked peptides (when their
 * masses were recorded)
 *
 * @author Sule
 */
public class MonoLinkedPeptides extends CrossLinking {

    private Peptide peptide;
    private String protein;
    private int linker_position;
    private HashMap<Integer, ArrayList<Ion>> product_ions = new HashMap<Integer, ArrayList<Ion>>();

    public MonoLinkedPeptides(Peptide peptide, String protein,
            int linkerPosition,
            CrossLinker linker,
            FragmentationMode fragmentation_mode) {
        this.protein = protein;
        this.peptide = peptide;
        super.linker = linker;
        this.linker_position = linkerPosition;
        this.fragmentation_mode = fragmentation_mode;
        product_ions = fragmentFactory.getFragmentIons(peptide).get(0); // only peptide fragment ions
        super.linkingType = CrossLinkingType.MONOLINK;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public String getProtein() {
        return protein;
    }

    public int getLinker_position() {
        return linker_position;
    }

    public void setPeptide(Peptide peptide) {
        this.peptide = peptide;
        product_ions = fragmentFactory.getFragmentIons(peptide).get(0);
        isMassCalculated = false;
    }

    public void setProtein(String protein) {
        isMassCalculated = false;
        this.protein = protein;
    }

    public void setLinker_position(int linker_position) {
        isMassCalculated = false;
        this.linker_position = linker_position;
    }

    @Override
    public double getTheoretical_xlinked_mass() {
        if (!isMassCalculated) {
            double tmp_mass_peptideA = peptide.getMass(),
                    tmp_mass_linker = linker.getMassShift_Type0();
            theoretical_xlinked_mass = tmp_mass_peptideA + tmp_mass_linker;
            isMassCalculated = true;
        }
        return theoretical_xlinked_mass;
    }

    /**
     * This method returns theoretical spectrum.
     *
     * If a theoretical spectrum is empty, then prepare_theoretical_spectrum
     * method generates it.
     *
     *
     * @return contains all fragment ions in ASC mass order.
     */
    @Override
    public HashSet<CPeptideIon> getTheoretical_ions() {
        if (theoretical_ions.isEmpty()) {
            product_ions = fragmentFactory.getFragmentIons(peptide).get(0); // only peptide fragment ions
            prepare_theoretical_spectrum();
        }
        return theoretical_ions;
    }

    private void prepare_theoretical_spectrum() {
        theoretical_ions.addAll(getBackbone(product_ions));
        ArrayList<CPeptideIon> theoretical_ions_al = new ArrayList<CPeptideIon>(theoretical_ions);
        Collections.sort(theoretical_ions_al, CPeptideIon.Ion_ASC_mass_order);
        theoretical_ions = new HashSet<CPeptideIon>(theoretical_ions_al);
    }

    private HashSet<CPeptideIon> getBackbone(HashMap<Integer, ArrayList<Ion>> product_ions) {
        HashSet<CPeptideIon> backbones = new HashSet<CPeptideIon>();
        // prepare for naming
        String pepName = "pep";
        int pep_length = peptide.getSequence().length();
        int linked_index = linker_position;
        CPeptideIonType cPepIonType = CPeptideIonType.Backbone;
        double mass_shift = linker.getMassShift_Type0();
        ArrayList<Integer> ion_types = new ArrayList<Integer>();
        // get only peptide fragment ions
        // 0= a, 1=b, 2=c, 3=x, 4=y, 5=z
        if (fragmentation_mode.equals(FragmentationMode.CID)) {
            // mostly b and y ions
            ion_types.add(PeptideFragmentIon.B_ION);
            ion_types.add(PeptideFragmentIon.Y_ION);
        } else if (fragmentation_mode.equals(FragmentationMode.ETD)) {
            // mostly c and z ions
            ion_types.add(PeptideFragmentIon.C_ION);
            ion_types.add(PeptideFragmentIon.Z_ION);
        } else if (fragmentation_mode.equals(FragmentationMode.HCD)) {
            // mostly y ions and then b and a ions
            ion_types.add(PeptideFragmentIon.A_ION);
            ion_types.add(PeptideFragmentIon.B_ION);
            ion_types.add(PeptideFragmentIon.Y_ION);
        } else if (fragmentation_mode.equals(FragmentationMode.HCD_all)) {
            // mostly y ions and then b and a ions
            ion_types.add(PeptideFragmentIon.A_ION);
            ion_types.add(PeptideFragmentIon.B_ION);
            ion_types.add(PeptideFragmentIon.X_ION);
            ion_types.add(PeptideFragmentIon.Y_ION);
        }
        for (Integer tmp_ion_type : ion_types) {
            int index = linked_index;
            if (tmp_ion_type == PeptideFragmentIon.X_ION || tmp_ion_type == PeptideFragmentIon.Y_ION || tmp_ion_type == PeptideFragmentIon.Z_ION) {
                index = pep_length - linked_index - 1;
            }
            backbones.addAll(prepareBackbone(product_ions, tmp_ion_type, index, mass_shift, pepName, cPepIonType, true));
        }
        return backbones;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.peptide != null ? this.peptide.hashCode() : 0);
        hash = 73 * hash + (this.protein != null ? this.protein.hashCode() : 0);
        hash = 73 * hash + (this.linker != null ? this.linker.hashCode() : 0);
        hash = 73 * hash + this.linker_position;
        hash = 73 * hash + (this.fragmentation_mode != null ? this.fragmentation_mode.hashCode() : 0);
        hash = 73 * hash + (this.theoretical_ions != null ? this.theoretical_ions.hashCode() : 0);
        hash = 73 * hash + (this.fragmentFactory != null ? this.fragmentFactory.hashCode() : 0);
        hash = 73 * hash + (int) (Double.doubleToLongBits(this.theoretical_xlinked_mass) ^ (Double.doubleToLongBits(this.theoretical_xlinked_mass) >>> 32));
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
        final MonoLinkedPeptides other = (MonoLinkedPeptides) obj;
        if (this.peptide != other.peptide && (this.peptide == null || !this.peptide.equals(other.peptide))) {
            return false;
        }
        if ((this.protein == null) ? (other.protein != null) : !this.protein.equals(other.protein)) {
            return false;
        }
        if (this.linker_position != other.linker_position) {
            return false;
        }
        if (this.product_ions != other.product_ions && (this.product_ions == null || !this.product_ions.equals(other.product_ions))) {
            return false;
        }
        return true;
    }

    @Override
    public String toPrint() {
        return peptide.getSequenceWithLowerCasePtms() + "\t" + protein + "\t" + getModificationInfo(peptide) + "\t"
                + "-" + "\t" + "-" + "\t" + "-" + "\t"
                + linker_position + "\t" + "-" + "\t"
                + "MonoLinked";
    }

}
