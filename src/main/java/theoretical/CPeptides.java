/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.NeutralLoss;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import crossLinker.CrossLinker;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import static theoretical.LinkedPeptideFragmentIon.getAbbrIonType;

/**
 * This class generates a spectrum containing theoretical ions derived from a
 * crosslinked peptide.
 *
 * @author Sule
 */
public class CPeptides {

    private Peptide peptideA,// first occured peptide on a database
            peptideB;// second occured peptide on a database
    private String proteinA,
            proteinB;
    private CrossLinker linker;
    private int linker_position_on_peptideA,
            linker_position_on_peptideB;
    private FragmentationMode fragmentation_mode;
    private HashSet<CPeptideIon> theoretical_ions = new HashSet<CPeptideIon>();
    private IonFactory fragmentFactory = IonFactory.getInstance();
    private HashMap<Integer, ArrayList<Ion>> product_ions_peptideA,
            product_ions_peptideB;
    private boolean is_monoisotopic_mass = true,
            is_Branching, // true/false to create linkedPeptideFragmentIons with Branching/Attaching
            isMassCalculated = false;
    private double intensity = 100,
            theoretical_xlinked_mass = 0;

    /* Constructor */
    public CPeptides(String proteinA, String proteinB,
            Peptide peptideA, Peptide peptideB,
            CrossLinker linker, int linker_position_on_peptideA, int linker_position_on_peptideB,
            FragmentationMode fragmentation_mode, boolean is_Branching_Approach) {
        this.proteinA = proteinA;
        this.proteinB = proteinB;
        this.peptideA = peptideA;
        this.peptideB = peptideB;
        this.linker = linker;
        this.linker_position_on_peptideA = linker_position_on_peptideA;
        this.linker_position_on_peptideB = linker_position_on_peptideB;
        this.fragmentation_mode = fragmentation_mode;
        product_ions_peptideA = fragmentFactory.getFragmentIons(peptideA).get(0); // only peptide fragment ions
        product_ions_peptideB = fragmentFactory.getFragmentIons(peptideB).get(0);
        this.is_Branching = is_Branching_Approach;
    }

    /* getters and setters */
    public String getProteinA() {
        return proteinA;
    }

    public void setProteinA(String proteinA) {
        isMassCalculated = false;
        this.proteinA = proteinA;
    }

    public String getProteinB() {
        return proteinB;
    }

    public void setProteinB(String proteinB) {
        isMassCalculated = false;
        this.proteinB = proteinB;
    }

    public Peptide getPeptideA() {
        return peptideA;
    }

    public void setPeptideA(Peptide peptideA) {
        isMassCalculated = false;
        this.peptideA = peptideA;
    }

    public Peptide getPeptideB() {
        return peptideB;
    }

    public void setPeptideB(Peptide peptideB) {
        isMassCalculated = false;
        this.peptideB = peptideB;
    }

    public CrossLinker getLinker() {
        return linker;
    }

    public void setLinker(CrossLinker linker) {
        isMassCalculated = false;
        this.linker = linker;
    }

    public int getLinker_position_on_peptideA() {
        return linker_position_on_peptideA;
    }

    public void setLinker_position_on_peptideA(int linker_position_on_peptideA) {
        this.linker_position_on_peptideA = linker_position_on_peptideA;
    }

    public int getLinker_position_on_peptideB() {
        return linker_position_on_peptideB;
    }

    public void setLinker_position_on_peptideB(int linker_position_on_peptideB) {
        this.linker_position_on_peptideB = linker_position_on_peptideB;
    }

    public FragmentationMode getFragmentation_mode() {
        return fragmentation_mode;
    }

    public void setFragmentation_mode(FragmentationMode fragmentation_mode) {
        this.fragmentation_mode = fragmentation_mode;
    }

    public boolean isIs_Branching() {
        return is_Branching;
    }

    public void setIs_Branching(boolean is_Branching) {
        this.is_Branching = is_Branching;
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
    public HashSet<CPeptideIon> getTheoterical_ions() {
        if (theoretical_ions.isEmpty()) {
            product_ions_peptideA = fragmentFactory.getFragmentIons(peptideA).get(0); // only peptide fragment ions
            product_ions_peptideB = fragmentFactory.getFragmentIons(peptideB).get(0);
            prepare_theoretical_spectrum();
        }
        return theoretical_ions;
    }

    public void setTheoterical_ions(HashSet<CPeptideIon> theoterical_ions) {

        this.theoretical_ions = theoterical_ions;
    }

    public IonFactory getFragmentFactory() {
        return fragmentFactory;
    }

    public void setFragmentFactory(IonFactory fragmentFactory) {
        this.fragmentFactory = fragmentFactory;
    }

    public HashMap<Integer, ArrayList<Ion>> getProduct_ions_peptideA() {
        return product_ions_peptideA;
    }

    public void setProduct_ions_peptideA(HashMap<Integer, ArrayList<Ion>> product_ions_peptideA) {
        this.product_ions_peptideA = product_ions_peptideA;
    }

    public HashMap<Integer, ArrayList<Ion>> getProduct_ions_peptideB() {
        return product_ions_peptideB;
    }

    public void setProduct_ions_peptideB(HashMap<Integer, ArrayList<Ion>> product_ions_peptideB) {
        this.product_ions_peptideB = product_ions_peptideB;
    }

    public boolean isIs_monoisotopic_mass() {
        return is_monoisotopic_mass;
    }

    public void setIs_monoisotopic_mass(boolean is_monoisotopic_mass) {
        this.is_monoisotopic_mass = is_monoisotopic_mass;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    public double getTheoreticalXLinkedMass() {
        if (!isMassCalculated) {
            double tmp_mass_peptideA = peptideA.getMass(),
                    tmp_mass_peptideB = peptideB.getMass(),
                    tmp_mass_linker = linker.getMassShift_Type2();
            theoretical_xlinked_mass = tmp_mass_peptideA + tmp_mass_peptideB + tmp_mass_linker;
            isMassCalculated = true;
        }
        return theoretical_xlinked_mass;
    }

    public void setTheoreticalMass(double theoretical_xlinked_mass) {
        this.theoretical_xlinked_mass = theoretical_xlinked_mass;
    }

    /**
     * This method generates a theoretical spectrum.
     *
     * It, first of all, generates product ions from alpha and beta peptides.
     * Then, it constructs linked peptide ions and attaches them to product ions
     * from a peptide
     *
     */
    public void prepare_theoretical_spectrum() {
        boolean isLinkedPeptideA = true,
                isPeptideA = true;
        theoretical_ions.addAll(getBackbone(product_ions_peptideA, isPeptideA));
        theoretical_ions.addAll(getBackbone(product_ions_peptideB, !isPeptideA));
        prepare_linkedBackbone(!isLinkedPeptideA);
        prepare_linkedBackbone(isLinkedPeptideA);
        // sort them all theoretical_ions - NOT SURE!
        ArrayList<CPeptideIon> theoretical_ions_al = new ArrayList<CPeptideIon>(theoretical_ions);
        Collections.sort(theoretical_ions_al, CPeptideIon.Ion_ASC_mass_order);
        theoretical_ions = new HashSet<CPeptideIon>(theoretical_ions_al);
    }

    public HashSet<CPeptideIon> getBackbone(HashMap<Integer, ArrayList<Ion>> product_ions, boolean isPeptideA) {
        HashSet<CPeptideIon> backbones = new HashSet<CPeptideIon>();
        // prepare for naming
        String pepName = "pepA";
        int pep_length = getPeptideA().getSequence().length();
        int linked_index = linker_position_on_peptideA;
        CPeptideIonType cPepIonType = CPeptideIonType.Backbone_PepA;
        double mass_shift = getPeptideB().getMass();
        if (!isPeptideA) {
            cPepIonType = CPeptideIonType.Backbone_PepB;
            pepName = "pepB";
            linked_index = linker_position_on_peptideB;
            mass_shift = getPeptideA().getMass();
            pep_length = getPeptideB().getSequence().length();
        }
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
            backbones.addAll(prepareBackbone(product_ions, tmp_ion_type, index, mass_shift, pepName, cPepIonType));
        }
        return backbones;
    }

    /**
     * This method retrieves product ions in a selected mode for only a peptide
     * TODO: MAKE SURE THAT WORKS WITH C-TERMI
     *
     * @param ion_type
     * @param linked_index
     * @param product_ions
     * @param mass_shift
     * @param lepName
     * @param cPepIonType
     *
     * @return NI IONS!
     */
    public HashSet<CPeptideIon> prepareBackbone(HashMap<Integer, ArrayList<Ion>> product_ions,
            int ion_type, int linked_index, double mass_shift, String lepName, CPeptideIonType cPepIonType) {
        HashSet<CPeptideIon> backbones = new HashSet<CPeptideIon>();
        String abbrIonType = LinkedPeptideFragmentIon.getAbbrIonType(ion_type);
        String rootName = lepName + "_" + abbrIonType;
        ArrayList<Ion> tmp_ions = product_ions.get(ion_type);
        for (int index = 0; index < tmp_ions.size(); index++) {
            Ion ion = tmp_ions.get(index);
            double ion_mass = ion.getTheoreticMass();
            if (index > linked_index) { // from a linker index on a peptide, shift remaining ions with a mass of a linkedPeptide                
                ion_mass += mass_shift + linker.getMassShift_Type2();
            }
            int index_to_show = index + 1;
            String ionName = rootName + index_to_show;
            boolean isFound = false;
            // check if there is an ion with the same mass already...Because there are two N-terminis and C-terminis!
            for (CPeptideIon cPepTheo : theoretical_ions) {
                double tmp_cpeptheo = cPepTheo.getMass();
                if (Math.abs(tmp_cpeptheo - ion_mass) < 0.0000001) {
                    ionName = cPepTheo.getName() + "_" + ionName;
                    cPepTheo.setName(ionName);
                    isFound = true;
                }
            }
            if (!isFound) {
                CPeptideIon cIon = new CPeptideIon(intensity, ion_mass, cPepIonType, ion_type, ionName);
                backbones.add(cIon);
                theoretical_ions.add(cIon);
            }
        }
        return backbones;
    }

    /**
     * This method constructs linked fragment ions for a peptide.
     *
     * A peptide is linked by a crosslinker to another peptide called a
     * linked_peptide. A linker_position at a peptide is connected to a
     * linker_position_at_linked_peptide at a linked_peptide.
     *
     * Any amino acid at and after a linker_position at a peptide has linked
     * fragment ions from a linked_peptide according to a
     * linker_position_at_linked_peptide at a linked_peptide. In addition, a
     * peptide also attaches to only a cross linker, without any fragments from
     * a linked_peptide.
     *
     * The root of linked fragment ions is an amino acid at a
     * linker_position_at_linked_peptide at a linked_peptide. Then, linked
     * fragment ions are generated towards N-termini and C-termini till an
     * entire linked_peptide is constructed. (To achieve this, another method is
     * called xxx).
     *
     * Each of these linked fragment ions are attached to each ion with index
     * equal to or bigger to a linker_position on a peptide.
     *
     * Redundant fragment ions are removed.
     *
     * @param isLinkedPeptideA - boolean attribute showing if a linked peptide
     * is peptideA
     */
    public void prepare_linkedBackbone(boolean isLinkedPeptideA) {
        if (fragmentation_mode.equals(FragmentationMode.CID)) {
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.B_ION, isLinkedPeptideA)); // b ions            
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.Y_ION, isLinkedPeptideA)); // y ions
        } else if (fragmentation_mode.equals(FragmentationMode.ETD)) {
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.C_ION, isLinkedPeptideA)); // c ions
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.Z_ION, isLinkedPeptideA)); // z ions
        } else if (fragmentation_mode.equals(FragmentationMode.HCD)) {
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.B_ION, isLinkedPeptideA)); // b ions
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.A_ION, isLinkedPeptideA)); // a ions
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.Y_ION, isLinkedPeptideA)); // y ions
        } else if (fragmentation_mode.equals(FragmentationMode.HCD_all)) {
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.A_ION, isLinkedPeptideA)); // a ions
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.B_ION, isLinkedPeptideA)); // b ions
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.X_ION, isLinkedPeptideA)); // x ions
            theoretical_ions.addAll(prepare_linked_peptides(PeptideFragmentIon.Y_ION, isLinkedPeptideA)); // y ions
        }
    }

    /**
     * This method generates
     *
     * @param fragmentIonType
     * @param isLinkedPeptideA true/false - a linked peptide is peptideA or
     * peptideB
     * @return
     */
    public ArrayList<CPeptideIon> prepare_linked_peptides(int fragmentIonType, boolean isLinkedPeptideA) {
        String lepName = "lepA",
                pepName = "pepB";
        HashMap<Integer, ArrayList<Ion>> backbone_ions = product_ions_peptideB;
        int linker_location_of_product_ions = linker_position_on_peptideB,
                linker_position_of_linkedPeptide = linker_position_on_peptideA,
                attachedToPepLength = peptideB.getSequence().length(),
                linkedPepLength = peptideA.getSequence().length();
        Peptide linkedPeptide = peptideA;
        CPeptideIonType cPeptideIonType = CPeptideIonType.LinkedPeptideBackbone_PepA;
        if (!isLinkedPeptideA) {
            backbone_ions = product_ions_peptideA;
            linker_location_of_product_ions = linker_position_on_peptideA;
            linker_position_of_linkedPeptide = linker_position_on_peptideB;
            linkedPeptide = peptideB;
            cPeptideIonType = CPeptideIonType.LinkedPeptideBackbone_PepB;
            lepName = "lepB";
            pepName = "pepA";
            attachedToPepLength = peptideA.getSequence().length();
            linkedPepLength = peptideB.getSequence().length();
        }
        String abbrIonType = getAbbrIonType(fragmentIonType);

        ArrayList<CPeptideIon> linked_ions = new ArrayList<CPeptideIon>();
        // select n termini ions of peptide backbone
        ArrayList<Ion> peptide_backbone_ions = get_product_ions(backbone_ions, fragmentIonType);
        boolean isCtermini = false;
        // get a linkedIon.. But first correct ion index for C-termini ions...
        if (fragmentIonType == PeptideFragmentIon.X_ION || fragmentIonType == PeptideFragmentIon.Y_ION || fragmentIonType == PeptideFragmentIon.Z_ION) {
            isCtermini = true;
            linker_location_of_product_ions = attachedToPepLength - linker_location_of_product_ions - 1;
            linker_position_of_linkedPeptide = linkedPepLength - linker_position_of_linkedPeptide - 1;
        }
        Ion linkedIon = null;
        // a control is needed, to check if C-termini ion is on the first aminoacid (because none of C-termini ions is generated for the first aa
        if (!isCtermini || (isCtermini && linker_location_of_product_ions < attachedToPepLength - 1)) {
            linkedIon = peptide_backbone_ions.get(linker_location_of_product_ions);
            // first add monolink one
            double mono_linked_ion_mass = linkedIon.getTheoreticMass() + linker.getMassShift_Type0();
            int index_to_show_for_linked = linker_position_of_linkedPeptide + 1,
                    index_to_show_for_backbone = linker_location_of_product_ions + 1;
            String name = pepName + "_" + abbrIonType + index_to_show_for_backbone + "_" + lepName + "_monolink_" + abbrIonType + index_to_show_for_linked;
            CPeptideIon cPepIonMonoLink = new CPeptideIon(intensity, mono_linked_ion_mass, cPeptideIonType, fragmentIonType, name);
            linked_ions.add(cPepIonMonoLink);
            // It is possible that a peptide is linked to another one by the first amino acid but in C-termini ions this linked peptide is not calculated...
            // By CXLinked DB construction, it is impossible to linked a peptide on the last aminoacid..
        } else if (isCtermini && linker_location_of_product_ions == attachedToPepLength - 1) {
            linkedIon = peptide_backbone_ions.get(linker_location_of_product_ions - 1);
            double mass_fragment_ion = LinkedPeptideFragmentIon.getMassDiff(fragmentIonType) + linkedIon.getTheoreticMass();
            linkedIon = new PeptideFragmentIon(fragmentIonType, 0, mass_fragment_ion, new ArrayList<NeutralLoss>());
            double mono_linked_ion_mass = linker.getMassShift_Type0() + mass_fragment_ion;
            int index_to_show = linker_position_of_linkedPeptide + 1;
            String name = lepName + "_" + "monoLink_" + abbrIonType + index_to_show;
            CPeptideIon cPepIonMonoLink = new CPeptideIon(intensity, mono_linked_ion_mass, cPeptideIonType, fragmentIonType, name);
            linked_ions.add(cPepIonMonoLink);
        }
        // now add all linked fragment ions
        LinkedPeptideFragmentIon obj = new LinkedPeptideFragmentIon(linkedPeptide, linker_position_of_linkedPeptide, isLinkedPeptideA, intensity, is_Branching);
        ArrayList<CPeptideIon> selectedTerminiIons;
        boolean isNtermini = false;
        if (fragmentIonType == PeptideFragmentIon.A_ION || fragmentIonType == PeptideFragmentIon.B_ION || fragmentIonType == PeptideFragmentIon.C_ION) {
            // N-termini ones
            selectedTerminiIons = obj.getNTerminiMasses(fragmentIonType);
            isNtermini = true;
        } else {
            selectedTerminiIons = obj.getCTerminiMasses(fragmentIonType);
        }
        // now generate all cPeptideIon objects..
        int index_to_show_for_backbone = linker_location_of_product_ions + 1;
        String rootName = pepName + "_" + abbrIonType + index_to_show_for_backbone;
        //lepName + "_" + abbrIonType;
        for (int i = 0; i < selectedTerminiIons.size(); i++) {
            CPeptideIon tmp_ion = selectedTerminiIons.get(i);
            int reader_index = i + 1;
            String name = rootName + "_" + lepName + "_" + abbrIonType + reader_index;
            tmp_ion.setName(name);
            double tmp_mass = linkedIon.getTheoreticMass() + tmp_ion.getMass() + linker.getMassShift_Type2();
            tmp_ion.setMass(tmp_mass);
            // select to be removed one..
            if ((isLinkedPeptideA && i == linker_position_on_peptideA && isNtermini)
                    || (isLinkedPeptideA && i == (peptideA.getSequence().length() - linker_position_on_peptideA - 1) && !isNtermini)) {
                // do nothing......
            } else if (((!isLinkedPeptideA) && i == linker_position_on_peptideB && isNtermini)
                    || ((!isLinkedPeptideA) && i == (peptideB.getSequence().length() - linker_position_on_peptideB - 1) && !isNtermini)) {
                // update a name...
                String addName = "_pepB_" + abbrIonType + reader_index + "_lepA_" + abbrIonType + index_to_show_for_backbone;
                name = name + addName;
                tmp_ion.setName(name);
                linked_ions.add(tmp_ion);
            } else {
                linked_ions.add(tmp_ion);
            }
        }
        return linked_ions;
    }

    /**
     * This method select product ions based on fragmentation mode for including
     * either n-termini or c-termini. CID: b and y ions ETD: c and z ions HCD: a
     * and x ions
     *
     * @param fragment_ions All possible product ions already generated from a
     * Peptide
     * @param is_n_termini either includes n-termini (a/b/c ions) or c-termini
     * (x/y/z ions)
     *
     * @param fragmentIonType 0-a, 1-b, 2-c, 3-x, 4-y, 5-z
     * @param fragment_ions
     * @return an arraylist of ions for selected product ions
     */
    private ArrayList<Ion> get_product_ions(HashMap<Integer, ArrayList<Ion>> fragment_ions, int fragmentIonType) {
        ArrayList<Ion> ions = new ArrayList<Ion>();
        ArrayList<Ion> tmp_ions = fragment_ions.get(fragmentIonType);
        ions.addAll(tmp_ions);
        return ions;
    }

    /**
     *
     *
     * @param fragment_ion_type
     * @return
     */
    public ArrayList<String> get_corresponredundant_linked_ions(int fragment_ion_type) {
        ArrayList<String> redundant_ions = new ArrayList<String>();
        String abbrIonType = getAbbrIonType(fragment_ion_type);
        if ((fragment_ion_type == PeptideFragmentIon.A_ION || fragment_ion_type == PeptideFragmentIon.B_ION || fragment_ion_type == PeptideFragmentIon.C_ION) // Make sure that it is N-termini ones
                && linker_position_on_peptideA == linker_position_on_peptideB) {
            // There are the same ion with the same mass on N-termini 
            String tmpName = "pepB" + "_" + abbrIonType + linker_position_on_peptideA + "_" + "lepA" + "_" + abbrIonType + linker_position_on_peptideB;
            redundant_ions.add(tmpName);
        }
        int linker_position_on_peptideA_ctermini = peptideA.getSequence().length() - linker_position_on_peptideA - 1,
                linker_position_on_peptideB_ctermini = peptideB.getSequence().length() - linker_position_on_peptideB - 1;
        if ((fragment_ion_type == PeptideFragmentIon.X_ION || fragment_ion_type == PeptideFragmentIon.Y_ION || fragment_ion_type == PeptideFragmentIon.Z_ION) // Make sure that it is N-termini ones
                && linker_position_on_peptideA_ctermini == linker_position_on_peptideB_ctermini) {
            // There are the same ion with the same mass on C-termini 
            String tmpName = "pepB" + "_" + abbrIonType + linker_position_on_peptideA + "_" + "lepA" + "_" + abbrIonType + linker_position_on_peptideB;
            redundant_ions.add(tmpName);
        }
        return redundant_ions;
    }
    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + (this.peptideA != null ? this.peptideA.hashCode() : 0);
        hash = 73 * hash + (this.peptideB != null ? this.peptideB.hashCode() : 0);
        hash = 73 * hash + (this.proteinA != null ? this.proteinA.hashCode() : 0);
        hash = 73 * hash + (this.proteinB != null ? this.proteinB.hashCode() : 0);
        hash = 73 * hash + (this.linker != null ? this.linker.hashCode() : 0);
        hash = 73 * hash + this.linker_position_on_peptideA;
        hash = 73 * hash + this.linker_position_on_peptideB;
        hash = 73 * hash + (this.fragmentation_mode != null ? this.fragmentation_mode.hashCode() : 0);
        hash = 73 * hash + (this.theoretical_ions != null ? this.theoretical_ions.hashCode() : 0);
        hash = 73 * hash + (this.fragmentFactory != null ? this.fragmentFactory.hashCode() : 0);
        hash = 73 * hash + (this.is_Branching ? 1 : 0);
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
        final CPeptides other = (CPeptides) obj;
        if (this.peptideA != other.peptideA && (this.peptideA == null || !this.peptideA.equals(other.peptideA))) {
            return false;
        }
        if (this.peptideB != other.peptideB && (this.peptideB == null || !this.peptideB.equals(other.peptideB))) {
            return false;
        }
        if ((this.proteinA == null) ? (other.proteinA != null) : !this.proteinA.equals(other.proteinA)) {
            return false;
        }
        if ((this.proteinB == null) ? (other.proteinB != null) : !this.proteinB.equals(other.proteinB)) {
            return false;
        }
        if (this.linker != other.linker && (this.linker == null || !this.linker.equals(other.linker))) {
            return false;
        }
        if (this.linker_position_on_peptideA != other.linker_position_on_peptideA) {
            return false;
        }
        if (this.linker_position_on_peptideB != other.linker_position_on_peptideB) {
            return false;
        }
        if (this.fragmentation_mode != other.fragmentation_mode) {
            return false;
        }
        if (this.theoretical_ions != other.theoretical_ions && (this.theoretical_ions == null || !this.theoretical_ions.equals(other.theoretical_ions))) {
            return false;
        }
        if (this.fragmentFactory != other.fragmentFactory && (this.fragmentFactory == null || !this.fragmentFactory.equals(other.fragmentFactory))) {
            return false;
        }
        if (this.is_Branching != other.is_Branching) {
            return false;
        }
        if (this.theoretical_xlinked_mass != other.theoretical_xlinked_mass && this.theoretical_xlinked_mass == 0) {
            return false;
        }
        return true;
    }

}
