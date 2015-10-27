/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import crossLinker.type.DSS;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xmlpull.v1.XmlPullParserException;
import start.GetPTMs;

/**
 *
 * @author Sule
 */
public class CPeptidesTest extends TestCase {

    public CPeptidesTest(String testName) {
        super(testName);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test of getTheoterical_ions method, of class CPeptides.
     */
    public void testGetTheoterical_ions() throws FileNotFoundException, IOException {
        System.out.println("getTheoterical_ions");

        String peptideA_str = "MLSDAK",
                peptideB_str = "AIKNK";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptideA = new Peptide(peptideA_str, parent_proteins_test, modifications_test),
                peptideB = new Peptide(peptideB_str, parent_proteins_test, modifications_test);
        CrossLinker linker = new DSS();
        CPeptides o = new CPeptides("ProteinA(20-25)", "ProteinB(20-25)", peptideA, peptideB, linker, 3, 2, FragmentationMode.CID, false);
        HashSet<CPeptideIon> result = o.getTheoretical_ions();

        File test_theoSpec = new File("Data/Test/theoretical/MLSDAK_AIKNK_by_theo.txt");
        BufferedReader br = new BufferedReader(new FileReader(test_theoSpec));
        String line = "";
        ArrayList<CPeptideIon> list = new ArrayList<CPeptideIon>(result);
        ArrayList<Double> list_from_given_file = new ArrayList<Double>();
        ArrayList<TestIon> list_test_ions = new ArrayList<TestIon>();

        while ((line = br.readLine()) != null) {
            if (!line.startsWith("Ion_Info")) {
                String mz_str = line.split("\t")[1];
                if (!mz_str.equals("-")) {
                    Double tmp_mz = new Double(mz_str);
                    String tmp_name = line.split("\t")[2];
                    TestIon t = new TestIon(tmp_mz, tmp_name);
                    list_test_ions.add(t);
                    list_from_given_file.add(tmp_mz);
                }
            }
        }
        Collections.sort(list, CPeptideIon.Ion_ASC_mass_order);
        Collections.sort(list_test_ions, Ion_ASC_mass_order);
        Collections.sort(list_from_given_file);

        assertEquals(36, list.size());
        // Now check...
        for (int i = 0; i < list_test_ions.size(); i++) {
            Double tmp_mz = list_test_ions.get(i).getMz();
            System.out.println(tmp_mz + "\t" + list_test_ions.get(i).getName() + "\t" + list.get(i).get_theoretical_mz(1) + " found one name = " + list.get(i).getName());
            assertEquals(tmp_mz, list.get(i).get_theoretical_mz(1), 0.02);
            assertEquals(list_test_ions.get(i).getName(), list.get(i).getName());
        }

        // Test a problematic case...
        // PepA=AILVNFKAR	 PepB=KMRPEVR	 at 6	0
        peptideA = new Peptide("AILVNFKAR", parent_proteins_test, modifications_test);
        peptideB = new Peptide("KMRPEVR", parent_proteins_test, modifications_test);
        o = new CPeptides("ProteinA(20-25)", "ProteinB(20-25)", peptideA, peptideB, linker, 6, 0, FragmentationMode.CID, false);
        result = o.getTheoretical_ions();
//        assertEquals(60, result.size());

//        test_theoSpec = new File("Data/Test/theoretical/test_MassTheoSpec_AILVNFKAR_KMRPEVR.txt");
//        br = new BufferedReader(new FileReader(test_theoSpec));
//        line = "";
//        list = new ArrayList<CPeptideIon>(result);
//        list_from_given_file = new ArrayList<Double>();
//
//        while ((line = br.readLine()) != null) {
//            if (!line.startsWith("Ion_Info")) {
//                double tmp_mass = Double.parseDouble(line.split("\t")[1]);
//                list_from_given_file.add(tmp_mass);
//            }
//        }
//        Collections.sort(list, CPeptideIon.Ion_ASC_mass_order);
//        Collections.sort(list_from_given_file);
        // Now check...
    }

    /**
     * Test of getTheoreticalXLinkedMass method, of class CPeptides.
     */
    @Test
    public void testGetTheoretical_mass() {
        System.out.println("getTheoretical_mass");
        ArrayList<String> parent_proteins_test = new ArrayList<String>(),
                parent_proteins_test_2 = new ArrayList<String>();
        parent_proteins_test.add("Pro1(5-9");
        parent_proteins_test_2.add("Pro1(12-15)");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptideA = new Peptide("MLSDA", parent_proteins_test, modifications_test),
                peptideB = new Peptide("AIKN", parent_proteins_test, modifications_test);
        CrossLinker linker = new DSS();
        CPeptides instance = new CPeptides("ProteinA(20-25)", "ProteinB(20-25)", peptideA, peptideB, linker, 3, 2, FragmentationMode.CID, false);

        double expResult = 535.2385 + 444.2769 + 138.0681000;
        double result = instance.getTheoretical_xlinked_mass();
        assertEquals(expResult, result, 0.1);
    }

    /**
     * Test of prepareBackbone method, of class CPeptides.
     */
    @Test
    public void testPrepareBackbone() {
        System.out.println("prepareBackbone");
        String peptideAstr = "MLSDAK",
                peptideBstr = "AIKNK";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1(20-25)");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptideA = new Peptide(peptideAstr, parent_proteins_test, modifications_test),
                peptideB = new Peptide(peptideBstr, parent_proteins_test, modifications_test);
        CrossLinker linker = new DSS();
        CPeptides instance = new CPeptides("ProteinA(20-25)", "ProteinB(20-25)", peptideA, peptideB, linker, 3, 2, FragmentationMode.CID, false);

        // First get N-termini ones!!
        HashMap<Integer, ArrayList<Ion>> product_ions = IonFactory.getInstance().getFragmentIons(peptideA).get(0);
        int ion_type = PeptideFragmentIon.B_ION,
                linked_index = 3;
        double mass_shift = 572.365;
        String lepName = "pepA_b_";
        CPeptideIonType cPepIonType = CPeptideIonType.Backbone_PepA;
        HashSet<CPeptideIon> backbone = instance.prepareBackbone(product_ions, ion_type, linked_index, mass_shift, lepName, cPepIonType, true);
        ArrayList<CPeptideIon> backbone_al = new ArrayList<CPeptideIon>(backbone);
        Collections.sort(backbone_al, CPeptideIon.Ion_ASC_mass_order);
        assertEquals(5, backbone.size()); // 5-N-terminis
        assertEquals(131.04, backbone_al.get(0).getMass(), 0.05);
        assertEquals(244.13, backbone_al.get(1).getMass(), 0.05);
        assertEquals(331.16, backbone_al.get(2).getMass(), 0.05);
        assertEquals(446.19, backbone_al.get(3).getMass(), 0.05);
        assertEquals(1227.66, backbone_al.get(4).getMass(), 0.05);

        product_ions = IonFactory.getInstance().getFragmentIons(peptideB).get(0);
        linked_index = 2;
        lepName = "pepB_b_";
        mass_shift = 663.3262;
        cPepIonType = CPeptideIonType.Backbone_PepB;
        backbone = instance.prepareBackbone(product_ions, ion_type, linked_index, mass_shift, lepName, cPepIonType, true);
        backbone_al = new ArrayList<CPeptideIon>(backbone);
        Collections.sort(backbone_al, CPeptideIon.Ion_ASC_mass_order);
        assertEquals(4, backbone.size()); // 4-N terminis

        assertEquals(71.04, backbone_al.get(0).getMass(), 0.05);
        assertEquals(184.13, backbone_al.get(1).getMass(), 0.05);
        assertEquals(312.22, backbone_al.get(2).getMass(), 0.05);
        assertEquals(1227.65, backbone_al.get(3).getMass(), 0.05);

        // Now C-termini ones!!!
        product_ions = IonFactory.getInstance().getFragmentIons(peptideA).get(0);
        ion_type = PeptideFragmentIon.Y_ION;
        linked_index = 6 - 3 - 1;
        mass_shift = 572.365;
        lepName = "pepA_y_";
        cPepIonType = CPeptideIonType.Backbone_PepA;
        backbone = instance.prepareBackbone(product_ions, ion_type, linked_index, mass_shift, lepName, cPepIonType, true);
        backbone_al = new ArrayList<CPeptideIon>(backbone);
        Collections.sort(backbone_al, CPeptideIon.Ion_ASC_mass_order);
        assertEquals(5, backbone.size()); // 5-N-terminis
        assertEquals(146.11, backbone_al.get(0).getMass(), 0.05);
        assertEquals(217.15, backbone_al.get(1).getMass(), 0.05);
        assertEquals(332.1769, backbone_al.get(2).getMass(), 0.05);
        assertEquals(1129.642, backbone_al.get(3).getMass(), 0.05);
        assertEquals(1242.726, backbone_al.get(4).getMass(), 0.05);

        product_ions = IonFactory.getInstance().getFragmentIons(peptideB).get(0);
        ion_type = PeptideFragmentIon.Y_ION;
        linked_index = 5 - 2 - 1;
        lepName = "pepB_y_";
        mass_shift = 663.3262;
        cPepIonType = CPeptideIonType.Backbone_PepB;
        backbone = instance.prepareBackbone(product_ions, ion_type, linked_index, mass_shift, lepName, cPepIonType, true);
        backbone_al = new ArrayList<CPeptideIon>(backbone);
        Collections.sort(backbone_al, CPeptideIon.Ion_ASC_mass_order);
        assertEquals(3, backbone.size()); // 5-N-termini
//        assertEquals(146.1128, backbone_al.get(0).getMass(), 0.05); - This one is already calculated, so this is only updated!
        assertEquals(260.1557, backbone_al.get(0).getMass(), 0.05);
        assertEquals(388.2507, backbone_al.get(1).getMass(), 0.05);
        assertEquals(1302.729, backbone_al.get(2).getMass(), 0.05);

        cPepIonType = CPeptideIonType.Backbone_PepB;
        ion_type = PeptideFragmentIon.A_ION;
        backbone = instance.prepareBackbone(product_ions, ion_type, linked_index, mass_shift, lepName, cPepIonType, true);
        backbone_al = new ArrayList<CPeptideIon>(backbone);
        Collections.sort(backbone_al, CPeptideIon.Ion_ASC_mass_order);
        assertEquals(1, backbone.size()); // 4-N terminis
        assertEquals(156.13, backbone_al.get(0).getMass(), 0.05);
    }

    /**
     * Test of prepare_linked_peptides method, of class CPeptides.
     */
    @Test
    public void testPrepare_linked_peptides_attaching() {

        System.out.println("prepare_linked_peptides-Attaching_ON");
        String peptideAstr = "MLSDAK",
                peptideBstr = "AIKNK";
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptideA = new Peptide(peptideAstr, modifications_test),
                peptideB = new Peptide(peptideBstr, modifications_test);
        CrossLinker linker = new DSS();
        CPeptides instance = new CPeptides("ProteinA(20-25)", "ProteinB(20-25)", peptideA, peptideB, linker, 3, 2, FragmentationMode.CID, false);

        // First get N-termini one with LinkedPeptideA!!
        int fragmentIonType = PeptideFragmentIon.B_ION;
        boolean isLinkedPeptideA = false;

        ArrayList<CPeptideIon> result = instance.prepare_linked_peptides(fragmentIonType, isLinkedPeptideA, false);
        Collections.sort(result, CPeptideIon.Ion_ASC_mass_order);
        assertEquals(5, result.size());

        assertEquals(602.26 - 18, result.get(0).getMass(), 0.05);
        assertEquals(655.30, result.get(1).getMass(), 0.05);
        assertEquals(768.38, result.get(2).getMass(), 0.05);
        assertEquals(896.482, result.get(3).getMass(), 0.05);
        assertEquals(1010.52, result.get(4).getMass(), 0.05);

        // Then, get N-termini ones with LinkedPeptideB!!
        isLinkedPeptideA = true;

        result = instance.prepare_linked_peptides(fragmentIonType, isLinkedPeptideA, false);
        Collections.sort(result, CPeptideIon.Ion_ASC_mass_order);
        assertEquals(5, result.size());
        assertEquals(468.3 - 18, result.get(0).getMass(), 0.05);
        assertEquals(581.33, result.get(1).getMass(), 0.05);
        assertEquals(694.42, result.get(2).getMass(), 0.05);
        assertEquals(781.45, result.get(3).getMass(), 0.05);
//        assertEquals(896.48, result.get(4).getMass(), 0.05);
        assertEquals(967.5, result.get(4).getMass(), 0.05);

        // Later, get C-termini one with LinkedPeptideA!!
        fragmentIonType = PeptideFragmentIon.Y_ION;
        isLinkedPeptideA = false;
        result = instance.prepare_linked_peptides(fragmentIonType, isLinkedPeptideA, false);
        Collections.sort(result, CPeptideIon.Ion_ASC_mass_order);
        assertEquals(5, result.size());

        assertEquals(470.23, result.get(0).getMass(), 0.05); //linked attached
        assertEquals(616.3, result.get(1).getMass(), 0.05);
        assertEquals(730.4, result.get(2).getMass(), 0.05);
        assertEquals(858.49, result.get(3).getMass(), 0.05);
        assertEquals(971.57, result.get(4).getMass(), 0.05);

        isLinkedPeptideA = true;
        result = instance.prepare_linked_peptides(fragmentIonType, isLinkedPeptideA, false);
        Collections.sort(result, CPeptideIon.Ion_ASC_mass_order);
        assertEquals(5, result.size());

        assertEquals(526.31, result.get(0).getMass(), 0.05); //linker attached
        assertEquals(672.43, result.get(1).getMass(), 0.05);
        assertEquals(743.46, result.get(2).getMass(), 0.05);
//        assertEquals(858.49, result.get(3).getMass(), 0.05);
        assertEquals(945.52, result.get(3).getMass(), 0.05);
        assertEquals(1058.61, result.get(4).getMass(), 0.05);
    }

    /**
     * Test of get_redundant_linked_ions, of class CPeptides. //
     */
    @Test
    public void testGetlinked_ions_modifications() throws XmlPullParserException, IOException {
        String peptideSequence = "MLCSDAIK";
        // Importing PTMs
        File modsFile = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\compomics-utilities\\src/test/resources/experiment/mods.xml");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);
        // Getting one fixed PTMs
        ArrayList<String> theoreticPTMs = new ArrayList<String>();
        theoreticPTMs.add("acetylation of protein n-term");
//        theoreticPTMs.add("propionamide c");
//        theoreticPTMs.add("pyro-cmc");
//        theoreticPTMs.add("oxidation of m");
        ArrayList<ModificationMatch> result = GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true);
//        theoreticPTMs = new ArrayList<String>();
//        theoreticPTMs.add("propionamide c");
//        theoreticPTMs.add("oxidation of m");
        result.addAll(GetPTMs.getPTM(ptmFactory, theoreticPTMs, peptideSequence, true));
        Peptide peptideA = new Peptide(peptideSequence, result);

        for (ModificationMatch m : peptideA.getModificationMatches()) {
            System.out.println(m.getTheoreticPtm() + "\t" + m.getModificationSite());
        }

        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptideB = new Peptide("AIKNK", modifications_test);
        CrossLinker linker = new DSS();
        CPeptides instance = new CPeptides("ProteinA(20-25)", "ProteinB(20-25)", peptideA, peptideB, linker, 3, 2, FragmentationMode.CID, false);

        HashMap<Integer, ArrayList<Ion>> product_ions = IonFactory.getInstance().getFragmentIons(peptideA).get(0);
        assertEquals(7, product_ions.get(0).size());
    }

    /**
     * Test of get_redundant_linked_ions, of class CPeptides. //
     */
//    @Test
//    public void testget_redundant_linked_ions() {
//        
//        System.out.println("get_redundant_linked_ions");
//        String peptideAstr = "MLSDAK",
//                peptideBstr = "AIKNK";
//        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
//        Peptide peptideA = new Peptide(peptideAstr, modifications_test),
//                peptideB = new Peptide(peptideBstr, modifications_test);
//        CrossLinker linker = new DSS();
//        CPeptides instance = new CPeptides("ProteinA", "ProteinB", peptideA, peptideB, linker, 3, 2, FragmentationMode.CID, false);
//
//        // First get N-termini one with LinkedPeptideA!!
//        ArrayList<String> redundant_linked_ions = instance.get_redundant_linked_ions(PeptideFragmentIon.B_ION);
//        assertEquals(0, redundant_linked_ions.size());
//        
//        redundant_linked_ions = instance.get_redundant_linked_ions(PeptideFragmentIon.Y_ION);
//        assertEquals(1, redundant_linked_ions.size());
//        
//        redundant_linked_ions = instance.get_redundant_linked_ions(PeptideFragmentIon.Z_ION);
//        assertEquals(1, redundant_linked_ions.size());
//        
//        redundant_linked_ions = instance.get_redundant_linked_ions(PeptideFragmentIon.X_ION);
//        assertEquals(1, redundant_linked_ions.size());
//        
//        instance = new CPeptides("ProteinA", "ProteinB", peptideA, peptideB, linker, 2, 2, FragmentationMode.CID, false);
//        
//        redundant_linked_ions = instance.get_redundant_linked_ions(PeptideFragmentIon.B_ION);
//        assertEquals(1, redundant_linked_ions.size());
//        
//        redundant_linked_ions = instance.get_redundant_linked_ions(PeptideFragmentIon.A_ION);
//        assertEquals(1, redundant_linked_ions.size());
//        
//        redundant_linked_ions = instance.get_redundant_linked_ions(PeptideFragmentIon.C_ION);
//        assertEquals(1, redundant_linked_ions.size());
//        
//        redundant_linked_ions = instance.get_redundant_linked_ions(PeptideFragmentIon.Y_ION);
//        assertEquals(0, redundant_linked_ions.size());
//    }
//    
    /**
     * Test of getTheoterical_ions method, of class CPeptides.
     */
    public void testGetTheoterical_ions2() throws FileNotFoundException, IOException {
        System.out.println("getTheoterical_ions2");

        String peptideA_str = "KMK",
                peptideB_str = "KLEYLLGDAIIRK";
        ArrayList<String> parent_proteins_test = new ArrayList<String>();
        parent_proteins_test.add("Pro1");
        ArrayList<ModificationMatch> modifications_test = new ArrayList<ModificationMatch>();
        Peptide peptideA = new Peptide(peptideA_str, parent_proteins_test, modifications_test),
                peptideB = new Peptide(peptideB_str, parent_proteins_test, modifications_test);
        CrossLinker linker = new DSS();
        CPeptides o = new CPeptides("ProteinA(20-25)", "ProteinB(20-25)", peptideA, peptideB, linker, 0, 0, FragmentationMode.CID, false);
        HashSet<CPeptideIon> result = o.getTheoretical_ions();
    }

    public class TestIon {

        private double mz;
        private String name;

        public TestIon(double mz, String name) {
            this.mz = mz;
            this.name = name;
        }

        public double getMz() {
            return mz;
        }

        public void setMz(double mz) {
            this.mz = mz;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
    public static final Comparator<TestIon> Ion_ASC_mass_order
            = new Comparator<TestIon>() {
                @Override
                public int compare(TestIon o1, TestIon o2) {
                    return o1.getMz() < o2.getMz() ? -1 : o1.getMz() == o2.getMz() ? 0 : 1;
                }
            };

}
