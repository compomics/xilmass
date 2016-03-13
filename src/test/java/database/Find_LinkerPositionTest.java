/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package database;

import com.compomics.util.protein.Protein;
import crossLinker.CrossLinker;
import java.util.ArrayList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Sule
 */
public class Find_LinkerPositionTest {
    
    public Find_LinkerPositionTest() {
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
     * Test of find_cross_linking_sites method, of class Find_LinkerPosition.
     */
    @Test
    public void testFind_cross_linking_sites() {
        System.out.println("find_cross_linking_sites");
        Protein protein = null;
        boolean firstPart = false;
        CrossLinker crossLinker = null;
        boolean doesContainProteinNtermini = false;
        boolean doesContainProteinCTermini = false;
        ArrayList<LinkedResidue> expResult = null;
        ArrayList<LinkedResidue> result = Find_LinkerPosition.find_cross_linking_sites(protein, firstPart, crossLinker, doesContainProteinNtermini, doesContainProteinCTermini,false);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get_carboxyl_groups method, of class Find_LinkerPosition.
     */
    @Test
    public void testGet_carboxyl_groups() {
        System.out.println("get_carboxyl_groups");
        Protein protein = null;
        boolean doesContainProteinNTermini = false;
        boolean doesContainProteinCTermini = false;
        ArrayList<LinkedResidue> expResult = null;
        ArrayList<LinkedResidue> result = Find_LinkerPosition.get_carboxyl_groups(protein, doesContainProteinNTermini, doesContainProteinCTermini);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get_amine_groups method, of class Find_LinkerPosition.
     */
    @Test
    public void testGet_amine_groups() {
        System.out.println("get_amine_groups");
        Protein protein = null;
        boolean doesContainProteinNTermini = false;
        boolean doesContainProteinCTermini = false;
        ArrayList<LinkedResidue> expResult = null;
        ArrayList<LinkedResidue> result = Find_LinkerPosition.get_amine_groups(protein, doesContainProteinNTermini, doesContainProteinCTermini,false);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
