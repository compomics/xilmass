/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import database.CreateDatabaseTest;
import database.FASTACPDBLoaderTest;
import matching.FilteringTest;
import matching.MatchAndScore;
import matching.MatchAndScoreTest;
import multithread.score.ScorePSMTest;
import naming.DefineIdCPeptideFragmentationPatternTest;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import scoringFunction.Andromeda_derivedTest;
import scoringFunction.CumulativeBinomialProbabilityBasedScoringTest;
import scoringFunction.MSAmanda_derivedTest;
import start.CalculateMS1ErrTest;
import start.GetPTMsTest;
import start.StartTest;
import start.lucene.CPeptideSearcherTest;
import start.lucene.CPeptidesIndexerTest;
import start.lucene.IndexAndSearchTest;
import theoretical.CPeptideIonTest;
import theoretical.CPeptidesTest;
import theoretical.CrossLinkingTest;
import theoretical.LinkedPeptideFragmentIonTest;
import theoretical.MonoLinkedPeptidesTest;

/**
 * JUnit Test suite to run selected JUnit test class
 *
 * @author Sule
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    // matching package
    CreateDatabaseTest.class,
    FASTACPDBLoaderTest.class,
    FilteringTest.class,
    MatchAndScoreTest.class,
    ScorePSMTest.class,
    DefineIdCPeptideFragmentationPatternTest.class,
    Andromeda_derivedTest.class,
    CumulativeBinomialProbabilityBasedScoringTest.class,
    MSAmanda_derivedTest.class,
    // CPeptideSearcherTest.class,
    // CPeptidesIndexerTest.class,
    // IndexAndSearchTest.class,
    CalculateMS1ErrTest.class,
    GetPTMsTest.class,
    StartTest.class,
    CPeptideIonTest.class,
    CPeptidesTest.class,
    CrossLinkingTest.class,
    LinkedPeptideFragmentIonTest.class,
    MonoLinkedPeptidesTest.class
})
public class XilmassTestSuite {

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

}
