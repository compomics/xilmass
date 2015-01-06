/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Peptide;
import crossLinker.CrossLinker;
import java.util.ArrayList;
import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import java.util.HashMap;

/**
 *
 * @author Sule
 */
public class CrossLinkedPeptides {

    private Peptide peptide_alpha,// longer peptide
            peptide_beta;// shorter peptide
    private CrossLinker linker;
    private int linker_position_on_alpha,
            linker_position_on_beta;
    private FragmentationMode fragmentation_mode;
    private int fragment_ion_charge,
            c_termini_type = PeptideFragmentIon.Y_ION,
            n_termini_type = PeptideFragmentIon.B_ION;
    private ArrayList<CrossLinkedPeptideIon> theoterical_ions = new ArrayList<CrossLinkedPeptideIon>();
    private IonFactory fragmentFactory = IonFactory.getInstance();
    private HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> ions_alpha_peptide,
            ions_beta_peptide;
    private HashMap<Integer, ArrayList<Ion>> fragment_ions_alpha,
            fragment_ions_beta;
    private boolean is_monoisotopic_mass = true;
    private double intensity = 100;

    public CrossLinkedPeptides(Peptide peptide_alpha, Peptide peptide_beta, CrossLinker linker, int linker_position_on_alpha, int linker_position_on_beta, FragmentationMode fragmentation_mode, int fragment_ion_charge) {
        this.peptide_alpha = peptide_alpha;
        this.peptide_beta = peptide_beta;
        this.linker = linker;
        this.linker_position_on_alpha = linker_position_on_alpha;
        this.linker_position_on_beta = linker_position_on_beta;
        this.fragmentation_mode = fragmentation_mode;
        this.fragment_ion_charge = fragment_ion_charge;
        ions_alpha_peptide = fragmentFactory.getFragmentIons(peptide_alpha);
        fragment_ions_alpha = ions_alpha_peptide.get(0); // only peptide fragment ions
        ions_beta_peptide = fragmentFactory.getFragmentIons(peptide_beta);
        fragment_ions_beta = ions_beta_peptide.get(0); // only peptide fragment ions
    }

    public CrossLinkedPeptides(Peptide peptide_alpha, Peptide peptide_beta, CrossLinker linker, int linker_position_on_alpha, int linker_position_on_beta, FragmentationMode fragmentation_mode, int fragment_ion_charge, boolean is_monoisotopic_mass) {
        this.peptide_alpha = peptide_alpha;
        this.peptide_beta = peptide_beta;
        this.linker = linker;
        this.linker_position_on_alpha = linker_position_on_alpha;
        this.linker_position_on_beta = linker_position_on_beta;
        this.fragmentation_mode = fragmentation_mode;
        this.fragment_ion_charge = fragment_ion_charge;
        this.is_monoisotopic_mass = is_monoisotopic_mass;
        ions_alpha_peptide = fragmentFactory.getFragmentIons(peptide_alpha);
        fragment_ions_alpha = ions_alpha_peptide.get(0); // only peptide fragment ions
        ions_beta_peptide = fragmentFactory.getFragmentIons(peptide_beta);
        fragment_ions_beta = ions_beta_peptide.get(0); // only peptide fragment ions
    }

    public CrossLinkedPeptides(Peptide peptide_alpha, Peptide peptide_beta, CrossLinker linker, int linker_position_on_alpha, int linker_position_on_beta, FragmentationMode fragmentation_mode,
            int fragment_ion_charge, int n_termini_ion_type, int c_termini_ion_type) {
        this.peptide_alpha = peptide_alpha;
        this.peptide_beta = peptide_beta;
        this.linker = linker;
        this.linker_position_on_alpha = linker_position_on_alpha;
        this.linker_position_on_beta = linker_position_on_beta;
        this.fragmentation_mode = fragmentation_mode;
        this.fragment_ion_charge = fragment_ion_charge;
        ions_alpha_peptide = fragmentFactory.getFragmentIons(peptide_alpha);
        fragment_ions_alpha = ions_alpha_peptide.get(0); // only peptide fragment ions
        ions_beta_peptide = fragmentFactory.getFragmentIons(peptide_beta);
        fragment_ions_beta = ions_beta_peptide.get(0); // only peptide fragment ions
        this.n_termini_type = n_termini_ion_type;
        this.c_termini_type = c_termini_ion_type;
    }

    public CrossLinkedPeptides(Peptide peptide_alpha, Peptide peptide_beta, CrossLinker linker, int linker_position_on_alpha, int linker_position_on_beta, FragmentationMode fragmentation_mode, 
            int fragment_ion_charge, boolean is_monoisotopic_mass, int n_termini_ion_type, int c_termini_ion_type) {
        this.peptide_alpha = peptide_alpha;
        this.peptide_beta = peptide_beta;
        this.linker = linker;
        this.linker_position_on_alpha = linker_position_on_alpha;
        this.linker_position_on_beta = linker_position_on_beta;
        this.fragmentation_mode = fragmentation_mode;
        this.fragment_ion_charge = fragment_ion_charge;
        this.is_monoisotopic_mass = is_monoisotopic_mass;
        ions_alpha_peptide = fragmentFactory.getFragmentIons(peptide_alpha);
        fragment_ions_alpha = ions_alpha_peptide.get(0); // only peptide fragment ions
        ions_beta_peptide = fragmentFactory.getFragmentIons(peptide_beta);
        fragment_ions_beta = ions_beta_peptide.get(0); // only peptide fragment ions
        this.n_termini_type = n_termini_ion_type;
        this.c_termini_type = c_termini_ion_type;
    }

    public Peptide getPeptide_alpha() {
        return peptide_alpha;
    }

    public void setPeptide_alpha(Peptide peptide_alpha) {
        this.peptide_alpha = peptide_alpha;
    }

    public Peptide getPeptide_beta() {
        return peptide_beta;
    }

    public void setPeptide_beta(Peptide peptide_beta) {
        this.peptide_beta = peptide_beta;
    }

    public CrossLinker getLinker() {
        return linker;
    }

    public void setLinker(CrossLinker linker) {
        this.linker = linker;
    }

    public int getLinker_position_on_alpha() {
        return linker_position_on_alpha;
    }

    public void setLinker_position_on_alpha(int linker_position_on_alpha) {
        this.linker_position_on_alpha = linker_position_on_alpha;
    }

    public int getLinker_position_on_beta() {
        return linker_position_on_beta;
    }

    public void setLinker_position_on_beta(int linker_position_on_beta) {
        this.linker_position_on_beta = linker_position_on_beta;
    }

    public FragmentationMode getFragmentation_mode() {
        return fragmentation_mode;
    }

    public void setFragmentation_mode(FragmentationMode fragmentation_mode) {
        this.fragmentation_mode = fragmentation_mode;
    }

    public int getFragment_ion_charge() {
        return fragment_ion_charge;
    }

    public void setFragment_ion_charge(int fragment_ion_charge) {
        this.fragment_ion_charge = fragment_ion_charge;
    }

    public ArrayList<CrossLinkedPeptideIon> getTheoterical_ions() {
        if (theoterical_ions.isEmpty()) {
            prepare_theoretical_spectrumAlt();
            // TODO: Make sure sorting!
        }
        return theoterical_ions;
    }

    public void setTheoterical_ions(ArrayList<CrossLinkedPeptideIon> theoterical_ions) {
        this.theoterical_ions = theoterical_ions;
    }

    public IonFactory getFragmentFactory() {
        return fragmentFactory;
    }

    public void setFragmentFactory(IonFactory fragmentFactory) {
        this.fragmentFactory = fragmentFactory;
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> getIons_alpha_peptide() {
        return ions_alpha_peptide;
    }

    public void setIons_alpha_peptide(HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> ions_alpha_peptide) {
        this.ions_alpha_peptide = ions_alpha_peptide;
    }

    public HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> getIons_beta_peptide() {
        return ions_beta_peptide;
    }

    public void setIons_beta_peptide(HashMap<Integer, HashMap<Integer, ArrayList<Ion>>> ions_beta_peptide) {
        this.ions_beta_peptide = ions_beta_peptide;
    }

    public HashMap<Integer, ArrayList<Ion>> getFragment_ions_alpha() {
        return fragment_ions_alpha;
    }

    public void setFragment_ions_alpha(HashMap<Integer, ArrayList<Ion>> fragment_ions_alpha) {
        this.fragment_ions_alpha = fragment_ions_alpha;
    }

    public HashMap<Integer, ArrayList<Ion>> getFragment_ions_beta() {
        return fragment_ions_beta;
    }

    public void setFragment_ions_beta(HashMap<Integer, ArrayList<Ion>> fragment_ions_beta) {
        this.fragment_ions_beta = fragment_ions_beta;
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

//    public void prepare_theoretical_spectrum() {
//        prepare_only_peptide(fragment_ions_alpha);
//        prepare_only_peptide(fragment_ions_beta);
//        ArrayList<CrossLinkedPeptideIon> all_linked_peptide_ions_alpha = prepare_linked_peptide(fragment_ions_alpha, fragment_ions_beta, linker_position_on_alpha);
//        ArrayList<CrossLinkedPeptideIon> all_linked_peptide_ions_beta = prepare_linked_peptide(fragment_ions_beta, fragment_ions_alpha, linker_position_on_beta);
//        theoterical_ions.addAll(all_linked_peptide_ions_alpha);
//        theoterical_ions.addAll(all_linked_peptide_ions_beta);
//        // sort them all 
//    }

    public void prepare_theoretical_spectrumAlt() {
        prepare_only_peptide(fragment_ions_alpha);
        prepare_only_peptide(fragment_ions_beta);
        ArrayList<CrossLinkedPeptideIon> all_linked_peptide_ions_alpha = prepare_linked_peptideAlt(fragment_ions_alpha, peptide_alpha, linker_position_on_alpha);
        ArrayList<CrossLinkedPeptideIon> all_linked_peptide_ions_beta = prepare_linked_peptideAlt(fragment_ions_beta, peptide_beta, linker_position_on_beta);
        theoterical_ions.addAll(all_linked_peptide_ions_alpha);
        theoterical_ions.addAll(all_linked_peptide_ions_beta);
        // sort them all 
    }

    private void prepare_only_peptide(HashMap<Integer, ArrayList<Ion>> fragment_ions) {
        // get only peptide fragment ions
        for (Integer ion_type : fragment_ions.keySet()) {
            // 0= a, 1=b, 2=c, 3=x, 4=y, 5=z
            if (fragmentation_mode.equals(FragmentationMode.CID)) {
                // mostly b and y ions
                if (ion_type == 1 || ion_type == 4) {
                    // ? b1 is not observed much, so removed it or keep it?
                    ArrayList<Ion> tmp_ions = fragment_ions.get(ion_type);
                    for (Ion ion : tmp_ions) {

                        CrossLinkedPeptideIon cIon = new CrossLinkedPeptideIon(intensity, ion.getTheoreticMass(), fragment_ion_charge);
                        theoterical_ions.add(cIon);
                    }
                }
            } else if (fragmentation_mode.equals(FragmentationMode.ETD)) {
                // mostly c and z ions
                if (ion_type == 2 || ion_type == 5) {
                    ArrayList<Ion> tmp_ions = fragment_ions.get(ion_type);
                    for (Ion ion : tmp_ions) {
                        CrossLinkedPeptideIon cIon = new CrossLinkedPeptideIon(intensity, ion.getTheoreticMass(), fragment_ion_charge);
                        theoterical_ions.add(cIon);
                    }
                }
            } else if (fragmentation_mode.equals(FragmentationMode.HCD)) {
                // mostly a and x ions
                if (ion_type == 0 || ion_type == 3) {
                    ArrayList<Ion> tmp_ions = fragment_ions.get(ion_type);
                    for (Ion ion : tmp_ions) {
                        CrossLinkedPeptideIon cIon = new CrossLinkedPeptideIon(intensity, ion.getTheoreticMass(), fragment_ion_charge);
                        theoterical_ions.add(cIon);
                    }
                }
            }
        }
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
     *
     * @param peptide is
     */
//    private ArrayList<CrossLinkedPeptideIon> prepare_linked_peptide(HashMap<Integer, ArrayList<Ion>> fragment_ions,
//            HashMap<Integer, ArrayList<Ion>> fragment_ions_linked,
//            int linker_position) {
//        ArrayList<CrossLinkedPeptideIon> all_linked_peptide_ions = new ArrayList<CrossLinkedPeptideIon>();
//        // get a start ion
//        ArrayList<Ion> n_termini_ions = get_ions(fragment_ions, true),
//                c_termini_ions = get_ions(fragment_ions, false),
//                n_termini_fragment_ions_linked = get_ions(fragment_ions_linked, true),
//                c_termini_fragment_ions_linked = get_ions(fragment_ions_linked, false);
//        double massShiftType0 = linker.getMassShift_Type0(),
//                massShiftType2 = linker.getMassShift_Type2();
//
//        // Now only b ions (or a or c ions)/N-termini
//        for (int i = linker_position; i < n_termini_ions.size(); i++) {
//            Ion ion = n_termini_ions.get(i);
//            // now add all fragment ions to 
//            // first add a mass to this one            
//            double ion_with_only_linker_mass = ion.getTheoreticMass() + massShiftType0;
//            // now add all linked fragment ions to each ions from now on.
//            CrossLinkedPeptideIon crossLinkedPeptideOnlywithLinker = new CrossLinkedPeptideIon(intensity, ion_with_only_linker_mass, fragment_ion_charge, CrossLinkedPeptideIonType.CrossLinker);
//            all_linked_peptide_ions.add(crossLinkedPeptideOnlywithLinker);
//            ArrayList<CrossLinkedPeptideIon> linked_peptide_ions = get_linked_peptide_ions(ion, massShiftType2, n_termini_fragment_ions_linked);
//            all_linked_peptide_ions.addAll(linked_peptide_ions);
//        }
//        // For c-termini
//        for (int i = linker_position; i > 0; i--) {
//            Ion ion = c_termini_ions.get(i);
//            // now add all fragment ions to 
//            // first add a mass to this one
//            double ion_with_only_linker_mass = ion.getTheoreticMass() + massShiftType0;
//            // now add all linked fragment ions to each ions from now on.
//            CrossLinkedPeptideIon crossLinkedPeptideOnlywithLinker = new CrossLinkedPeptideIon(intensity, ion_with_only_linker_mass, fragment_ion_charge, CrossLinkedPeptideIonType.CrossLinker);
//            all_linked_peptide_ions.add(crossLinkedPeptideOnlywithLinker);
//            ArrayList<CrossLinkedPeptideIon> linked_peptide_ions = get_linked_peptide_ions(ion, massShiftType2, c_termini_fragment_ions_linked);
//            all_linked_peptide_ions.addAll(linked_peptide_ions);
//        }
//        return all_linked_peptide_ions;
//    }
//       private ArrayList<CrossLinkedPeptideIon> get_linked_peptide_ions(Ion ion, double mass, ArrayList<Ion> linked_ions) {
//        ArrayList<CrossLinkedPeptideIon> crossLinkedPeptideIons = new ArrayList<CrossLinkedPeptideIon>();
//        for (Ion linked_ion : linked_ions) {
//            double shifted_mass = ion.getTheoreticMass() + mass + linked_ion.getTheoreticMass();
//            CrossLinkedPeptideIon crossLinkedPeptideIon = new CrossLinkedPeptideIon(intensity, shifted_mass, fragment_ion_charge, CrossLinkedPeptideIonType.CrossLinkedPeptideFragmentIon);
//            crossLinkedPeptideIons.add(crossLinkedPeptideIon);
//        }
//        return crossLinkedPeptideIons;
//    }

    private ArrayList<CrossLinkedPeptideIon> prepare_linked_peptideAlt(HashMap<Integer, ArrayList<Ion>> fragment_ions,
            Peptide linkedPeptide,
            int linker_position) {
        ArrayList<CrossLinkedPeptideIon> all_linked_peptide_ions = new ArrayList<CrossLinkedPeptideIon>();
        // get a start ion
        ArrayList<Ion> n_termini_ions = get_ions(fragment_ions, true),
                c_termini_ions = get_ions(fragment_ions, false);

        LinkedPeptideIon obj = new LinkedPeptideIon(linkedPeptide, linker_position);
        // here temini information necessary 
        ArrayList<Double> cTerminiMasses = obj.getcTerminiMasses(c_termini_type),
                nTerminiMasses = obj.getnTerminiMasses(n_termini_type);

        double massShiftType0 = linker.getMassShift_Type0(),
                massShiftType2 = linker.getMassShift_Type2();
        // Now only b ions (or a or c ions)/N-termini
        for (int i = linker_position; i < n_termini_ions.size(); i++) {
            Ion ion = n_termini_ions.get(i);
            // now add all fragment ions to 
            // first add a mass to this one            
            double ion_with_only_linker_mass = ion.getTheoreticMass() + massShiftType0;
            // now add all linked fragment ions to each ions from now on.
            CrossLinkedPeptideIon crossLinkedPeptideOnlywithLinker = new CrossLinkedPeptideIon(intensity, ion_with_only_linker_mass, fragment_ion_charge, CrossLinkedPeptideIonType.CrossLinker);
            all_linked_peptide_ions.add(crossLinkedPeptideOnlywithLinker);

            ArrayList<CrossLinkedPeptideIon> linked_peptide_ions = get_linked_peptide_ionsAlt(ion, massShiftType2, nTerminiMasses);
            all_linked_peptide_ions.addAll(linked_peptide_ions);
        }
        // For c-termini
        for (int i = linker_position; i > 0; i--) {
            Ion ion = c_termini_ions.get(i);
            // now add all fragment ions to 
            // first add a mass to this one
            double ion_with_only_linker_mass = ion.getTheoreticMass() + massShiftType0;
            // now add all linked fragment ions to each ions from now on.
            CrossLinkedPeptideIon crossLinkedPeptideOnlywithLinker = new CrossLinkedPeptideIon(intensity, ion_with_only_linker_mass, fragment_ion_charge, CrossLinkedPeptideIonType.CrossLinker);
            all_linked_peptide_ions.add(crossLinkedPeptideOnlywithLinker);

            ArrayList<CrossLinkedPeptideIon> linked_peptide_ions = get_linked_peptide_ionsAlt(ion, massShiftType2, cTerminiMasses);
            all_linked_peptide_ions.addAll(linked_peptide_ions);
        }
        return all_linked_peptide_ions;
    }

    private ArrayList<CrossLinkedPeptideIon> get_linked_peptide_ionsAlt(Ion ion, double mass, ArrayList<Double> linked_ions_masses) {
        ArrayList<CrossLinkedPeptideIon> crossLinkedPeptideIons = new ArrayList<CrossLinkedPeptideIon>();
        for (Double linked_mass : linked_ions_masses) {
            double shifted_mass = ion.getTheoreticMass() + mass + linked_mass;
            CrossLinkedPeptideIon crossLinkedPeptideIon = new CrossLinkedPeptideIon(intensity, shifted_mass, fragment_ion_charge, CrossLinkedPeptideIonType.CrossLinkedPeptideFragmentIon);
            crossLinkedPeptideIons.add(crossLinkedPeptideIon);
        }
        return crossLinkedPeptideIons;
    }

    private ArrayList<Ion> get_ions(HashMap<Integer, ArrayList<Ion>> fragment_ions, boolean is_n_termini) {
        ArrayList<Ion> ions = new ArrayList<Ion>();
        for (Integer ion_type : fragment_ions.keySet()) {
            // 0= a, 1=b, 2=c, 3=x, 4=y, 5=z
            if (fragmentation_mode.equals(FragmentationMode.CID)) {
                // mostly b and y ions
                if (ion_type == 1 && is_n_termini) { // b ions
                    // ? b1 is not observed much, so removed it or keep it?
                    ArrayList<Ion> tmp_ions = fragment_ions.get(ion_type);
                    ions.addAll(tmp_ions);
                } else if (ion_type == 4 && !is_n_termini) { // y ions
                    // ? b1 is not observed much, so removed it or keep it?
                    ArrayList<Ion> tmp_ions = fragment_ions.get(ion_type);
                    ions.addAll(tmp_ions);
                }
            } else if (fragmentation_mode.equals(FragmentationMode.ETD)) {
                // mostly c and z ions
                if (ion_type == 2 && is_n_termini) { // c ions
                    // ? b1 is not observed much, so removed it or keep it?
                    ArrayList<Ion> tmp_ions = fragment_ions.get(ion_type);
                    ions.addAll(tmp_ions);
                } else if (ion_type == 5 && !is_n_termini) { // z ions
                    // ? b1 is not observed much, so removed it or keep it?
                    ArrayList<Ion> tmp_ions = fragment_ions.get(ion_type);
                    ions.addAll(tmp_ions);
                }
            } else if (fragmentation_mode.equals(FragmentationMode.HCD)) {
                // mostly a and x ions
                if (ion_type == 0 && is_n_termini) { // & ions
                    // ? b1 is not observed much, so removed it or keep it?
                    ArrayList<Ion> tmp_ions = fragment_ions.get(ion_type);
                    ions.addAll(tmp_ions);
                } else if (ion_type == 3 && !is_n_termini) { // y ions
                    // ? b1 is not observed much, so removed it or keep it?
                    ArrayList<Ion> tmp_ions = fragment_ions.get(ion_type);
                    ions.addAll(tmp_ions);
                }
            }
        }
        return ions;
    }

}
