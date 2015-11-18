/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.compomics.util.experiment.biology.PTMFactory;
import crossLinker.CrossLinker;
import java.io.BufferedWriter;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
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

        boolean result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA, !checkNTermini, acc_and_length);
        assertEquals(false, result);

        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA, checkNTermini, acc_and_length);
        assertEquals(false, result);
        
        proteinA = new StringBuilder("P04233REVERSED(165-400)");
        
        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA, checkNTermini, acc_and_length);
        assertEquals(false, result);

        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA, !checkNTermini, acc_and_length);
        assertEquals(true, result);
        
        proteinA = new StringBuilder("P04233REVERSED(1-40)");
        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA, checkNTermini, acc_and_length);
        assertEquals(true, result);

        result = FASTACPDBLoader.checkProteinContainsProteinTermini(proteinA, !checkNTermini, acc_and_length);
        assertEquals(false, result);
    }

}
