///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package analyse.CXPSM.prepareOutcome;
//
//import analyse.CXPSM.prepareOutcome.AnalyzeXilmass;
//import analyse.CXPSM.outcome.XilmassResult;
//import java.io.File;
//import java.io.IOException;
//import java.util.HashSet;
//import org.junit.After;
//import org.junit.AfterClass;
//import static org.junit.Assert.*;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
///**
// *
// * @author Sule
// */
//public class AnalyzeXilmassTest {
//
//    public AnalyzeXilmassTest() {
//    }
//
//    @BeforeClass
//    public static void setUpClass() {
//    }
//
//    @AfterClass
//    public static void tearDownClass() {
//    }
//
//    @Before
//    public void setUp() {
//    }
//
//    @After
//    public void tearDown() {
//    }
//
//    /**
//     * Test of run method, of class AnalyzeXilmass.
//     */
//    @Test
//    public void testRun() throws Exception {
//        System.out.println("run");
//        AnalyzeXilmass instance = null;
//        instance.run();
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getValidatedPSMs method, of class AnalyzeXilmass.
//     */
//    @Test
//    public void testGetValidatedPSMs() throws IOException {
//        System.out.println("getValidatedPSMs");
//        File xilmassFolder = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze\\xilmass_validated_testing/test_mc4_TMSAm_HCD_contaminants"),
//                output = new File("C:\\Users\\Sule\\Documents\\NetBeansProjects\\CrossLinkedPeptides\\Data\\Test\\analyze\\xilmass_validated_testingtesting_output.txt"),
//                predictionFile = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\structure\\predicted_cross_linking/xwalk_prediction_uniprot2.txt"),
//                psms_contaminant = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\XLinkData_Freiburg\\1_Cleaning\\SGRunsHCD_identification\\10FDRPSMs/hcd_dss_elite_cleaning_fdr10PSMs.txt");
//        double fdr = 0.05;
//        boolean isConventialFDR = false,
//                isMS1ErrPPM = true;
//        String target_names[] = {"P62158", "Q15149"};
//        AnalyzeXilmass instance = new AnalyzeXilmass(xilmassFolder, output, predictionFile, psms_contaminant, target_names, fdr, isConventialFDR, isMS1ErrPPM, false, false, "ScoreName");
//        instance.run();
//        HashSet<XilmassResult> result = instance.getValidatedPSMs();
//        assertEquals(29, result.size());
//
//        instance = new AnalyzeXilmass(xilmassFolder, output, predictionFile, psms_contaminant, target_names, 0.36, true, isMS1ErrPPM, false, false);
//        instance.run();
//        result = instance.getValidatedPSMs();
//        assertEquals(31, result.size());
//
//    }
//
//    /**
//     * Test of removeRedundant method, of class AnalyzeXilmass.
//     */
//    @Test
//    public void testRemoveRedundant() {
//        System.out.println("removeRedundant");
//        HashSet<XilmassResult> res = new HashSet<XilmassResult>();
//        String line = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.114.114.3 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=114\"	114	68.9317	2317.218906539033	3	6.316019317495397	6.316019317495397	HKPMLIDMNKVYR	4Q57:B(181-193)	[oxidation of m_4];	EDKK	4Q57:B_REVERSED(225-228)	-	2	3	182	227	interProtein	0.024814188006558446	2.302585092994046	2	2	[mz=489.2867432_intensity=150.1499481201, mz=640.3465576_intensity=153.1966400146]	[doublyCharged_pepB_y2_lepA_y4_mz=489.3107, doublyCharged_pepA_b5_mz=640.3393]	Light_Labeled_Linker	RIGHT_U";
//        String line2 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.202.202.8 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=202\"	202	127.4176	2703.390959263564	8	-11.54194745184775	11.54194745184775	GPMDNLYLAVLR	4Q57:B(1-12)	-	EKGRMRFHK	4Q57:B(72-80)	[oxidation of m_5];	1	2	1	73	intraProtein	1.1543637613055954E-6	0.6931471805599453	1	1	[mz=481.3194885_intensity=146.7265319824]	[doublyCharged_pepA_y8_mz=481.295]	Light_Labeled_Linker	SINGLE";
//        String line3 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.202.202.8 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=202\"	202	127.4176	2703.390959263564	8	-11.54194745184775	11.54194745184775	GPMDNLYLAVLR	4Q57:B(1-12)	[oxidation of m_3];	EKGRMRFHK	4Q57:B(72-80)	-	1	2	1	73	intraProtein	1.1543637613055954E-6	0.6931471805599453	1	1	[mz=481.3194885_intensity=146.7265319824]	[doublyCharged_pepA_y8_mz=481.295]	Light_Labeled_Linker	SINGLE";
//        String line4 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.214.214.6 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=214\"	214	134.7726	3624.1290229393762	6	8.43136286334384	8.43136286334384	DGHNLISLLEVLSGDSLPREKGR	4Q57:B(53-75)	-	QVKLVNIR	4Q57:B(95-102)	-	21	3	73	97	intraProtein	0.003620629608976198	2.1972245773362196	1	1	[mz=402.2706604_intensity=517.7467041016]	[singlyCharged_pepB_y3_mz=402.2459]	Heavy_Labeled_Linker	SINGLE";
//        String line5 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.347.347.19 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=347\"	347	224.2368	6460.211491859099	19	1.416192725400972	1.416192725400972	GDRWSTTFNDCRLGQYGEVMRQSWLLLKEK	4Q57:B_REVERSED(74-103)	[oxidation of m_20];	GPMDNLYLAVLRASEGKKDERDR	4Q57:B(1-23)	-	28	18	101	18	interProtein	3.6278125047519656E-6	4.143134726391533	1	1	[mz=1653.392944_intensity=167.9383850098]	[doublyCharged_pepB_y6_lepA_y19_mz=1653.3747]	Light_Labeled_Linker	SINGLE";
//        String line6 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.522.522.19 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=522\"	522	338.5816	6460.204533851295	19	-16.567340228687748	16.567340228687748	CDNFTTSWRDGRLFNAIIHRHKPMLIDMNKVYR	4Q57:B(161-193)	-	EDKKGESARLVALYLNDMPG	4Q57:B_REVERSED(225-244)	-	30	4	190	228	interProtein	1.2091274491663982E-5	4.418840607796598	1	1	[mz=245.0673523_intensity=573.1434936523]	[singlyCharged_pepB_b2_mz=245.0768]	Heavy_Labeled_Linker	SINGLE";
//        String line7 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.522.522.19 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=522\"	522	338.5816	6460.204533851295	19	-16.567340228687748	16.567340228687748	CDNFTTSWRDGRLFNAIIHRHKPMLIDMNKVYR	4Q57:B(161-193)	-	EDKKGESARLVALYLNDMPG	4Q57:B_REVERSED(225-244)	-	22	4	182	228	interProtein	1.2091274491663982E-5	4.418840607796598	1	1	[mz=245.0673523_intensity=573.1434936523]	[singlyCharged_pepB_b2_mz=245.0768]	Heavy_Labeled_Linker	SINGLE";
//        String line8 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.587.587.3 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=587\"	587	377.6975	4700.538547652315	3	13.427106214596861	13.427106214596861	GKERPLSDGSLVELLSILNHGDRLDEYLDSIHRQAK	4Q57:B_REVERSED(171-206)	-	EKGR	4Q57:B(72-75)	-	2	2	172	73	interProtein	2.3469710669712834E-4	0.0	1	1	[mz=1602.380249_intensity=153.4339141846]	[doublyCharged_pepB_y3_lepA_y23_mz=1602.3678]	Light_Labeled_Linker	SINGLE";
//        String line9 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.608.608.3 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=608\"	608	389.2386	3572.8622781210647	3	-7.168274797563835	7.168274797563835	GPMDNLYLAVLRASEGKKDERDR	4Q57:B(1-23)	-	DREDKK	4Q57:B_REVERSED(223-228)	-	17	5	17	227	interProtein	0.0021304917804565024	2.70805020110221	1	1	[mz=1308.643433_intensity=185.6875915527]	[singlyCharged_pepB_b5_lepA_b5_mz=1308.6278]	Heavy_Labeled_Linker	SINGLE";
//        String line10 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.608.608.3 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=608\"	608	389.2386	3572.8622781210647	3	-7.168274797563835	7.168274797563835	GPMDNLYLAVLRASEGKKDERDR	4Q57:B(1-23)	-	DREDKK	4Q57:B_REVERSED(223-228)	-	18	5	18	227	interProtein	0.0021304917804565024	2.70805020110221	1	1	[mz=1308.643433_intensity=185.6875915527]	[singlyCharged_pepB_b5_lepA_b5_mz=1308.6278]	Heavy_Labeled_Linker	SINGLE";
//        String line11 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.621.621.3 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=621\"	621	396.2541	3892.818943160126	3	-1.322565504008033	1.322565504008033	MKDTDSEEEIREAFR	P62158(77-91)	[oxidation of m_1];	MKDTDSEEEIREAFR	P62158(77-91)	[oxidation of m_1];	2	2	78	78	intraProtein	0.01565435352957429	1.9459101490553132	1	1	[mz=148.059433_intensity=151.1762237549]	[singlyCharged_pepA_b1_pepB_b1_mz=148.0426]	Heavy_Labeled_Linker	SINGLE";
//        String line12 = "Probe2_v_mc1_top15HCD-1.mgf	Probe2_v_mc1_top15HCD-1.202.202.8 File:\"Probe2_v_mc1_top15HCD-1.raw\", NativeID:\"controllerType=0 controllerNumber=1 scan=202\"	202	127.4176	2703.390959263564	8	-11.54194745184775	11.54194745184775	GPMDNLYLAVLR	4Q57:B(1-12)	[oxidation of m_3];	EKGRMRFHKK	4Q57:B(72-80)	-	1	2	1	73	intraProtein	1.1543637613055954E-6	0.6931471805599453	1	1	[mz=481.3194885_intensity=146.7265319824]	[doublyCharged_pepA_y8_mz=481.295]	Light_Labeled_Linker	SINGLE";
//
//        res.add(new XilmassResult(line, true, false));
//        res.add(new XilmassResult(line2, true, false));
//        res.add(new XilmassResult(line3, true, false));
//        res.add(new XilmassResult(line4, true, false));
//        res.add(new XilmassResult(line5, true, false));
//        res.add(new XilmassResult(line6, true, false));
//        res.add(new XilmassResult(line7, true, false));
//        res.add(new XilmassResult(line8, true, false));
//        res.add(new XilmassResult(line9, true, false));
//        res.add(new XilmassResult(line10, true, false));
//        res.add(new XilmassResult(line11, true, false));
//        res.add(new XilmassResult(line12, true, false));
//
//        for (XilmassResult r : res) {
//            if (r.getTarget_decoy().isEmpty()) {
//                String td = AnalyzeOutcomes.getTargetDecoy(r.getAccProteinA(), r.getAccProteinB());
//                r.setTarget_decoy(td);
//            }
//        }
//        HashSet<XilmassResult> result = AnalyzeXilmass.removeRedundant(res);
//        assertEquals(8, result.size());
//    }
//
//}
