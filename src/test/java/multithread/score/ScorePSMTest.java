/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithread.score;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import crossLinker.type.DSS;
import java.util.ArrayList;
import java.util.HashSet;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import scoringFunction.ScoreName;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;
import theoretical.CrossLinking;
import theoretical.FragmentationMode;

/**
 *
 * @author Sule
 */
public class ScorePSMTest {

    public ScorePSMTest() {
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
     * Test of updateResults method, of class ScorePSM.
     */
    @Test
    public void testUpdateResults() {
        System.out.println("updateResults");
        MSnSpectrum ms = new MSnSpectrum();
        ms.setScanNumber("3805");
        ArrayList<Charge> ch = new ArrayList<Charge>();
        ch.add(new Charge(+1, 3));
        Precursor p = new Precursor(10, 100, ch);
        ms.setPrecursor(p);
        CPeptides cp = new CPeptides("proA(1-5)", "proB(2-5)",
                new Peptide("pep", new ArrayList<ModificationMatch>()), new Peptide("pep", new ArrayList<ModificationMatch>()),
                new DSS(), 1, 2, FragmentationMode.HCD_all, true);
        Result r = new Result(ms, cp, ScoreName.TheoMSAmandaDWeighted, 10, 0, new HashSet<Peak>(), new HashSet<CPeptidePeak>(), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false),
                r1 = new Result(ms, cp, ScoreName.TheoMSAmandaDWeighted, 8, 0, new HashSet<Peak>(), new HashSet<CPeptidePeak>(), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false),
                r2 = new Result(ms, cp, ScoreName.TheoMSAmandaDWeighted, 7, 0, new HashSet<Peak>(), new HashSet<CPeptidePeak>(), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false),
                r3 = new Result(ms, cp, ScoreName.TheoMSAmandaDWeighted, 6, 0, new HashSet<Peak>(), new HashSet<CPeptidePeak>(), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false);
        ArrayList<Result> rs = new ArrayList<Result>();
        rs.add(r);
        rs.add(r1);
        rs.add(r2);
        rs.add(r3);

        double lnNumSp = 1.38;
        rs = ScorePSM.updateResults(rs, lnNumSp);
        assertEquals(1, rs.size());
        assertEquals(10, rs.get(0).getScore(), 0);
        assertEquals(2.0 / 10.0, rs.get(0).getDeltaScore(), 0);
        assertEquals(1.38, rs.get(0).getLnNumSpec(), 0);

        r = new Result(ms, cp, ScoreName.TheoMSAmandaDWeighted, 10, 0, new HashSet<Peak>(), new HashSet<CPeptidePeak>(), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false);
        r1 = new Result(ms, cp, ScoreName.TheoMSAmandaDWeighted, 10, 0, new HashSet<Peak>(), new HashSet<CPeptidePeak>(), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false);
        r2 = new Result(ms, cp, ScoreName.TheoMSAmandaDWeighted, 7, 0, new HashSet<Peak>(), new HashSet<CPeptidePeak>(), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false);
        r3 = new Result(ms, cp, ScoreName.TheoMSAmandaDWeighted, 6, 0, new HashSet<Peak>(), new HashSet<CPeptidePeak>(), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false);
        rs = new ArrayList<Result>();
        rs.add(r);
        rs.add(r1);
        rs.add(r2);
        rs.add(r3);

        lnNumSp = 1.38;
        rs = ScorePSM.updateResults(rs, lnNumSp);
        assertEquals(2, rs.size());
        assertEquals(10, rs.get(0).getScore(), 0);
        assertEquals(10, rs.get(1).getScore(), 0);
        assertEquals(3.0 / 10.0, rs.get(0).getDeltaScore(), 0);
        assertEquals(3.0 / 10.0, rs.get(1).getDeltaScore(), 0);
        assertEquals(1.38, rs.get(0).getLnNumSpec(), 0);
        assertEquals(1.38, rs.get(1).getLnNumSpec(), 0);

        r = new Result(ms, cp, ScoreName.TheoMSAmandaDWeighted, 10, 0, new HashSet<Peak>(), new HashSet<CPeptidePeak>(), 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, false, false);
        rs = new ArrayList<Result>();
        rs.add(r);

        lnNumSp = 1.38;
        rs = ScorePSM.updateResults(rs, lnNumSp);
        assertEquals(1, rs.size());
        assertEquals(10, rs.get(0).getScore(), 0);
        assertEquals(10.0 / 10.0, rs.get(0).getDeltaScore(), 0);
        assertEquals(1.38, rs.get(0).getLnNumSpec(), 0);
    }

    /**
     * Test of updateResults method, of class ScorePSM.
     */
    @Test
    public void testGetLnNumSp() {
        System.out.println("getLnNumSp");
        ArrayList<CrossLinking> selectedCPeptides = new ArrayList<CrossLinking>();

        selectedCPeptides.add(new CPeptides("proA(1-5)", "proB(2-5)",
                new Peptide("pep", new ArrayList<ModificationMatch>()), new Peptide("pepB", new ArrayList<ModificationMatch>()),
                new DSS(), 1, 2, FragmentationMode.HCD_all, true));
        selectedCPeptides.add(new CPeptides("proA(1-5)", "proB(2-5)",
                new Peptide("pepB", new ArrayList<ModificationMatch>()), new Peptide("pep", new ArrayList<ModificationMatch>()),
                new DSS(), 1, 2, FragmentationMode.HCD_all, true));

        double lnNumSp = ScorePSM.getLnNumSp(selectedCPeptides);
        assertEquals(Math.log(1), lnNumSp, 0.001);

        selectedCPeptides.add(new CPeptides("proA(1-5)", "proB(2-5)",
                new Peptide("pep", new ArrayList<ModificationMatch>()), new Peptide("pep", new ArrayList<ModificationMatch>()),
                new DSS(), 1, 2, FragmentationMode.HCD_all, true));
        selectedCPeptides.add(new CPeptides("proA(1-5)", "proB(2-5)",
                new Peptide("pepB", new ArrayList<ModificationMatch>()), new Peptide("pep", new ArrayList<ModificationMatch>()),
                new DSS(), 1, 2, FragmentationMode.HCD_all, true));

        lnNumSp = ScorePSM.getLnNumSp(selectedCPeptides);
        assertEquals(Math.log(2), lnNumSp, 0.001);

        ArrayList<ModificationMatch> mods = new ArrayList<ModificationMatch>();
        mods.add(new ModificationMatch("Oxidation M", true, 2));
        selectedCPeptides.add(new CPeptides("proA(1-5)", "proB(2-5)",
                new Peptide("pep", new ArrayList<ModificationMatch>()), new Peptide("pep", mods),
                new DSS(), 1, 2, FragmentationMode.HCD_all, true));
        selectedCPeptides.add(new CPeptides("proA(1-5)", "proB(2-5)",
                new Peptide("pep", new ArrayList<ModificationMatch>()), new Peptide("pep", new ArrayList<ModificationMatch>()),
                new DSS(), 1, 2, FragmentationMode.HCD_all, true));

        lnNumSp = ScorePSM.getLnNumSp(selectedCPeptides);
        assertEquals(Math.log(2), lnNumSp, 0.001);

    }
}
