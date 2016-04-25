/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.compomics.util.experiment.biology.PTMFactory;
import crossLinker.CrossLinker;
import crossLinker.type.DSS;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import theoretical.CPeptides;
import theoretical.FragmentationMode;

/**
 *
 * @author Sule
 */
public class FASTACPDBLoaderTest {

    public FASTACPDBLoaderTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of checkProteinContainsProteinTermini method, of class
     * FASTACPDBLoader.
     */
    @Test
    public void testCheckProteinContainsProteinTermini() {
        System.out.println("checkProteinContainsProteinTermini");
        StringBuilder proteinA = new StringBuilder("P04233REVERSED(165-201)");
        boolean checkNTermini = true; // so checking if a tryptic peptide contains protein c-termini
        HashMap<String, Integer> acc_and_length = new HashMap<String, Integer>();
        acc_and_length.put("P04233REVERSED", 400);

        boolean result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA.toString(), !checkNTermini, acc_and_length);
        assertEquals(false, result);

        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA.toString(), checkNTermini, acc_and_length);
        assertEquals(false, result);

        proteinA = new StringBuilder("P04233REVERSED(165-400)");

        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA.toString(), checkNTermini, acc_and_length);
        assertEquals(false, result);

        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA.toString(), !checkNTermini, acc_and_length);
        assertEquals(true, result);

        proteinA = new StringBuilder("P04233REVERSED(1-40)");
        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA.toString(), checkNTermini, acc_and_length);
        assertEquals(true, result);

        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA.toString(), !checkNTermini, acc_and_length);
        assertEquals(false, result);
    }

    /**
     * Test of readFiletoGetCPeptideTheoMass method, of class FASTACPDBLoader.
     */
    @Test
    public void testReadFiletoGetCPeptideTheoMass() throws Exception {
        System.out.println("readFiletoGetCPeptideTheoMass");
        File file = null;
        PTMFactory ptmFactory = null;
        CrossLinker linker = null;
        FragmentationMode fragMode = null;
        boolean isContrastLinkedAttachmentOn = false;
        HashMap<CPeptides, Double> expResult = null;
        HashMap<CPeptides, Double> result = FASTACPDBLoader.readFiletoGetCPeptideTheoMass(file, ptmFactory, linker, fragMode, isContrastLinkedAttachmentOn);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generate_peptide_mass_index method, of class FASTACPDBLoader.
     */
    @Test
    public void testGenerate_peptide_mass_index_crossLinked() throws Exception {
        System.out.println("generate_peptide_mass_index");
        HashMap<String, String> header_sequence = new HashMap<String, String>();
        header_sequence.put("P17152(2-6)_1_P17152(2-6)_1", "A*AWGR|A*AWGR");
        header_sequence.put("P17152(1-6)_1_P17152(2-6)_1", "M*AAWGR|A*AWGR");
        header_sequence.put("P17152(2-7)_1_P17152(2-6)_1", "A*AWGRR|A*AWGR");
        header_sequence.put("P17152(1-6)_1_P17152(1-6)_1", "M*AAWGR|M*AAWGR");

        header_sequence.put("P17152(1-6)_1_P17152(2-7)_1", "M*AAWGR|A*AWGRR");
        header_sequence.put("P17152(1-7)_1_P17152(2-6)_1", "M*AAWGRR|A*AWGR");
        header_sequence.put("P17152(2-7)_1_P17152(2-7)_1", "A*AWGRR|A*AWGRR");
        header_sequence.put("P17152(1-7)_1_P17152(1-6)_1", "M*AAWGRR|M*AAWGR");
        header_sequence.put("P17152(1-7)_1_P17152(2-7)_1", "M*AAWGRR|A*AWGRR");
        header_sequence.put("P17152(1-7)_1_P17152(1-7)_1", "M*AAWGRR|M*AAWGRR");

        PTMFactory ptmFactory = PTMFactory.getInstance();
        ArrayList<String> fixedModifications = new ArrayList<String>(),
                variableModifications = new ArrayList<String>();
        CrossLinker linker = new DSS(true); // heavy labeled DSS
        FragmentationMode fragMode = FragmentationMode.HCD;
        boolean isContrastLinkedAttachmentOn = false;
        int max_mods_per_peptide = 0;
        HashMap<String, Integer> acc_and_length = new HashMap<String, Integer>();
        acc_and_length.put("P17152", 20);
        ArrayList<CPeptides> result = FASTACPDBLoader.generate_peptide_mass_index(header_sequence, ptmFactory, fixedModifications, variableModifications, max_mods_per_peptide, linker, fragMode, isContrastLinkedAttachmentOn, acc_and_length);
        assertEquals(10, result.size());
        Collections.sort(result, CPeptides.Crosslinking_xlinked_mass_ASC_order);
        assertEquals(1268.72, result.get(0).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1399.75, result.get(1).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1424.82, result.get(2).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1530.79, result.get(3).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1555.85, result.get(4).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1555.85, result.get(5).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1580.92, result.get(6).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1686.89, result.get(7).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1711.96, result.get(8).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1843.00, result.get(9).getTheoretical_xlinked_mass(), 0.05);

        // lets see the results with reversed sequences..;
        header_sequence = new HashMap<String, String>();
        header_sequence.put("P17152(2-6)_REVERSED_1_P17152(2-6)_1", "A*AWGR|A*AWGR");
        header_sequence.put("P17152(1-6)_REVERSED_1_P17152(2-6)_1", "M*AAWGR|A*AWGR");
        header_sequence.put("P17152(2-7)_1_P17152(2-6)_1", "A*AWGRR|A*AWGR");
        header_sequence.put("P17152(1-6)_1_P17152(1-6)_1", "M*AAWGR|M*AAWGR");

        header_sequence.put("P17152(1-6)_1_P17152(2-7)_REVERSED_1", "M*AAWGR|A*AWGRR");
        header_sequence.put("P17152(1-7)_1_P17152(2-6)_1", "M*AAWGRR|A*AWGR");
        header_sequence.put("P17152(2-7)_REVERSED_1_P17152(2-7)_1", "A*AWGRR|A*AWGRR");
        header_sequence.put("P17152(1-7)_1_P17152(1-6)_1", "M*AAWGRR|M*AAWGR");
        header_sequence.put("P17152(1-7)_1_P17152(2-7)_1", "M*AAWGRR|A*AWGRR");
        header_sequence.put("P17152(1-7)_1_P17152(1-7)_1", "M*AAWGRR|M*AAWGRR");

        acc_and_length.put("P17152_REVERSED", 20);
        result = FASTACPDBLoader.generate_peptide_mass_index(header_sequence, ptmFactory, fixedModifications, variableModifications, max_mods_per_peptide, linker, fragMode, isContrastLinkedAttachmentOn, acc_and_length);
        assertEquals(10, result.size());
        Collections.sort(result, CPeptides.Crosslinking_xlinked_mass_ASC_order);
        assertEquals(1268.72, result.get(0).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1399.75, result.get(1).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1424.82, result.get(2).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1530.79, result.get(3).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1555.85, result.get(4).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1555.85, result.get(5).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1580.92, result.get(6).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1686.89, result.get(7).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1711.96, result.get(8).getTheoretical_xlinked_mass(), 0.05);
        assertEquals(1843.00, result.get(9).getTheoretical_xlinked_mass(), 0.05);

    }

    /**
     * Test of generate_peptide_mass_index method, of class FASTACPDBLoader.
     */
    @Test
    public void testGenerate_peptide_mass_index_10args() throws Exception {
        System.out.println("generate_peptide_mass_index");
        BufferedWriter bw = null;
        HashMap<String, StringBuilder> header_sequence = null;
        PTMFactory ptmFactory = null;
        ArrayList<String> fixedModifications = null;
        ArrayList<String> variableModifications = null;
        CrossLinker linker = null;
        FragmentationMode fragMode = null;
        boolean isContrastLinkedAttachmentOn = false;
        int max_mods_per_peptide = 0;
        HashMap<String, Integer> acc_and_length = null;
        HashSet<StringBuilder> expResult = null;
        HashSet<StringBuilder> result = FASTACPDBLoader.generate_peptide_mass_index(bw, header_sequence, ptmFactory, fixedModifications, variableModifications, max_mods_per_peptide, linker, fragMode, isContrastLinkedAttachmentOn, acc_and_length);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generate_peptide_mass_index_for_contaminants method, of class
     * FASTACPDBLoader.
     */
    @Test
    public void testGenerate_peptide_mass_index_for_contaminants() throws Exception {
        System.out.println("generate_peptide_mass_index_for_contaminants");
        BufferedWriter bw = null;
        HashMap<String, StringBuilder> header_sequence = null;
        PTMFactory ptmFactory = null;
        ArrayList<String> fixedModifications = null;
        ArrayList<String> variableModifications = null;
        FragmentationMode fragMode = null;
        boolean isContrastLinkedAttachmentOn = false;
        int max_mods_per_peptide = 0;
        HashMap<String, Integer> acc_and_length = null;
        HashSet<StringBuilder> expResult = null;
        HashSet<StringBuilder> result = FASTACPDBLoader.generate_peptide_mass_index_for_contaminants(bw, header_sequence, ptmFactory, fixedModifications, variableModifications, max_mods_per_peptide, fragMode, isContrastLinkedAttachmentOn, acc_and_length);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of generate_peptide_mass_index_monoLink method, of class
     * FASTACPDBLoader.
     */
    @Test
    public void testGenerate_peptide_mass_index_monoLink() throws Exception {
        System.out.println("generate_peptide_mass_index_monoLink");
        BufferedWriter bw = null;
        HashMap<String, StringBuilder> header_sequence = null;
        PTMFactory ptmFactory = null;
        ArrayList<String> fixedModifications = null;
        ArrayList<String> variableModifications = null;
        CrossLinker linker = null;
        FragmentationMode fragMode = null;
        int max_mods_per_peptide = 0;
        HashMap<String, Integer> acc_and_length = null;
        HashSet<StringBuilder> expResult = null;
        HashSet<StringBuilder> result = FASTACPDBLoader.generate_peptide_mass_index_monoLink(bw, header_sequence, ptmFactory, fixedModifications, variableModifications, max_mods_per_peptide, linker, fragMode, acc_and_length);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
