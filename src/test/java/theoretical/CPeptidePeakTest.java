/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package theoretical;

import com.compomics.util.experiment.massspectrometry.Peak;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Sule
 */
public class CPeptidePeakTest {

    public CPeptidePeakTest() {
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
     * Test of getName method, of class CPeptidePeak.
     */
    @Test
    public void testCompare() {
        System.out.println("toCompare");

        CPeptidePeak p1 = new CPeptidePeak(1487.790, 1000, "pepB_y8_lepA_y14"),
                p2 = new CPeptidePeak(1487.790583, 1000, "pepA_y13");
        ArrayList<CPeptidePeak> ps = new ArrayList<CPeptidePeak>();
        ps.add(p1);
        ps.add(p2);
        Collections.sort(ps, CPeptidePeak.order_CPeptidePeak);
        assertEquals("pepA_y13", ps.get(0).getName());
        assertEquals("pepB_y8_lepA_y14", ps.get(1).getName());

        ps = new ArrayList<CPeptidePeak>();
        ps.add(p2);
        ps.add(p1);
        Collections.sort(ps, CPeptidePeak.order_CPeptidePeak);
        assertEquals("pepA_y13", ps.get(0).getName());
        assertEquals("pepB_y8_lepA_y14", ps.get(1).getName());

        CPeptidePeak p3 = new CPeptidePeak(1487.890583, 1000, "pepB_y13");
        ps = new ArrayList<CPeptidePeak>();
        ps.add(p2);
        ps.add(p1);
        ps.add(p3);
        Collections.sort(ps, CPeptidePeak.order_CPeptidePeak);
        assertEquals("pepB_y13", ps.get(0).getName());
        assertEquals("pepA_y13", ps.get(1).getName());
        assertEquals("pepB_y8_lepA_y14", ps.get(2).getName());
    }

}
