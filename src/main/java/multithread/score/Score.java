/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package multithread.score;

import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import crossLinker.CrossLinker;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.Callable;
import matching.MatchAndScore;
import org.xmlpull.v1.XmlPullParserException;
import scoringFunction.ScoreName;
import start.GetPTMs;
import theoretical.CPeptidePeak;
import theoretical.CPeptides;
import theoretical.Contaminant;
import theoretical.CrossLinkedPeptides;
import theoretical.FragmentationMode;
import theoretical.MonoLinkedPeptides;

/**
 *
 * @author Sule
 */
public class Score implements Callable<ArrayList<Result>> {

    private ArrayList<MSnSpectrum> selectedSpectra; // all selected CPeptides objects
    private CrossLinkedPeptides cPeptide; // a MSnSpectrum compared to CPeptides object
    private ScoreName scoreName; // a ScoreName
    private double fragTol, // fragment tolerance, requiring for MatchAndScore instantiation.
            massWindow; // mass window, requiring for MatchAndScore instantiation.
    private int intensityOptionForMSAmanda, // fragment tolerance, requiring for MatchAndScore instantiation.
            minFilteredPeakNumber, // min number of filtered peak per window,, requiring for MatchAndScore instantiation.
            maxFilteredPeakNumber; // max number of filtered peak per window, requiring for MatchAndScore instantiation.
    private String line;
    private CrossLinker linker;
    private FragmentationMode fragMode;
    private PTMFactory ptmFactory;
    private boolean isBranching,
            isContrastLinkedAttachmentOn,
            doesFindAllMatchedPeaks;

    // A constructor for multithreading
    public Score(ArrayList<MSnSpectrum> selectedSpectra, String line, ScoreName scoreName, PTMFactory ptmFactory, CrossLinker linker, FragmentationMode fragMode,
            double fragTol, int intensityOptionForMSAmanda, int minFilteredPeakNumber, int maxFilteredPeakNumber, double massWindow,
            boolean isBranching, boolean isContrastLinkedAttachmentOn, boolean doesFindAllMatchedPeaks) {
        this.selectedSpectra = selectedSpectra;
        this.line = line;
        this.scoreName = scoreName;
        this.ptmFactory = ptmFactory;
        this.linker = linker;
        this.fragMode = fragMode;
        this.fragTol = fragTol;
        this.intensityOptionForMSAmanda = intensityOptionForMSAmanda;
        this.minFilteredPeakNumber = minFilteredPeakNumber;
        this.maxFilteredPeakNumber = maxFilteredPeakNumber;
        this.massWindow = massWindow;
        this.isBranching = isBranching;
        this.isContrastLinkedAttachmentOn = isContrastLinkedAttachmentOn;
        this.doesFindAllMatchedPeaks = doesFindAllMatchedPeaks;
    }

    /**
     * A call method for multithreading. It calculates score for a given
     * MSnSpectrum against all CPeptides object
     *
     * @return a list of Result object containing MSMS/CPeptides/ScoreName/Score
     * @throws Exception
     */
    @Override
    public ArrayList<Result> call() throws Exception {
        cPeptide = getCPeptides();
        ArrayList<Result> results = new ArrayList<Result>();
        InnerIteratorSync<MSnSpectrum> iteratorCPeptides = new InnerIteratorSync(selectedSpectra.iterator());
        while (iteratorCPeptides.iter.hasNext()) {
            MSnSpectrum tmpMSMS = (MSnSpectrum) iteratorCPeptides.iter.next();
            synchronized (tmpMSMS) {
                // First generate CPeptides object.  
                MatchAndScore obj = new MatchAndScore(tmpMSMS, scoreName, cPeptide, fragTol, intensityOptionForMSAmanda, minFilteredPeakNumber, maxFilteredPeakNumber, massWindow, doesFindAllMatchedPeaks);
                double tmpScore = obj.getCXPSMScore(),
                        weight = obj.getWeight();
                HashSet<Peak> matchedPeaks = obj.getMatchedPeaks();
                HashSet<CPeptidePeak> matchedTheoreticalCPeaks = obj.getMatchedTheoreticalCPeaks();
                int matchedTheoA = obj.getMatchedTheoPepAs(),
                        matchedTheoB = obj.getMatchedTheoPepBs();
                Result r = new Result(tmpMSMS, cPeptide, scoreName, tmpScore, matchedPeaks, matchedTheoreticalCPeaks, weight, matchedTheoA, matchedTheoB);
                results.add(r);
            }
        }
        return results;
    }

    private CrossLinkedPeptides getCPeptides() throws XmlPullParserException, IOException {
        CrossLinkedPeptides selected = null;
        String[] split = line.split("\t");
        String proteinA = split[0],
                proteinB = split[1],
                peptideAseqFile = split[2],
                peptideBseqFile = split[3],
                fixedModA = split[6],
                fixedModB = split[7],
                variableModA = split[8],
                variableModB = split[9];
        // linker positions...
        // This means a cross linked peptide is here...
        if (!proteinB.equals("-")) {
            if (proteinB.contains("inverted")) {
            }
            Integer linkerPosPeptideA = Integer.parseInt(split[4]),
                    linkerPosPeptideB = Integer.parseInt(split[5]);
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false),
                    fixedPTM_peptideB = GetPTMs.getPTM(ptmFactory, fixedModB, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA),
                    ptms_peptideB = new ArrayList<ModificationMatch>(fixedPTM_peptideB);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true),
                    variablePTM_peptideB = GetPTMs.getPTM(ptmFactory, variableModB, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            ptms_peptideB.addAll(variablePTM_peptideB);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseqFile, ptms_peptideA),
                    peptideB = new Peptide(peptideBseqFile, ptms_peptideB);
            // now generate peptide...
            CPeptides tmpCpeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, linker, linkerPosPeptideA, linkerPosPeptideB, fragMode, isBranching, isContrastLinkedAttachmentOn);
            selected = tmpCpeptide;
            // This means only monolinked peptide...
        } else if (!proteinA.startsWith("contaminant")) {
            Integer linkerPosPeptideA = Integer.parseInt(split[4]);
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseqFile, ptms_peptideA);
            MonoLinkedPeptides mP = new MonoLinkedPeptides(peptideA, proteinA, linkerPosPeptideA, linker, fragMode, isBranching);
            selected = mP;
        } else if (proteinA.startsWith("contaminant")) {
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseqFile, ptms_peptideA);
            Contaminant mP = new Contaminant(peptideA, proteinA, fragMode, isBranching);
            selected = mP;
        }
        return selected;
    }

    /**
     * Simple wrapper class to allow synchronisation on the hasNext() and next()
     * methods of the iterator.
     */
    private class InnerIteratorSync<T> {

        private Iterator<T> iter = null;

        public InnerIteratorSync(Iterator<T> aIterator) {
            iter = aIterator;
        }

        public synchronized T next() {
            T result = null;
            if (iter.hasNext()) {
                result = iter.next();
            }
            return result;
        }
    }
}
