/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start.lucene;

import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import crossLinker.CrossLinkerType;
import crossLinker.GetCrossLinker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.xmlpull.v1.XmlPullParserException;
import start.GetPTMs;
import theoretical.CPeptides;
import theoretical.Contaminant;
import theoretical.CrossLinking;
import theoretical.FragmentationMode;
import theoretical.MonoLinkedPeptides;

/**
 * This class first checks if an already constructed index folder exists. If
 * not, it creates index files by constructing a CPeptidesIndexer object (by
 * calling index()). After making sure that index is ready, it calls
 * CPeptidesSearcher object to query in order to select a list of CPeptides.
 *
 * @author Sule
 */
public class IndexAndSearch {

    private CPeptideSearcher cPeptideSearcher;
    private PTMFactory ptmFactory;
    private FragmentationMode fragMode;
    private CrossLinker heavyLinker,
            lightLinker;

    /**
     *
     * @param headers a list of CPeptides entries
     * @param ptmFactory
     * @param linkerName - just name of linker to create both heavy and light
     * labeled versions
     * @param fragMode fragmentation mode
     * @param folder index folder
     * @throws IOException
     * @throws Exception
     */
    public IndexAndSearch(HashSet<StringBuilder> headers, File folder, PTMFactory ptmFactory, FragmentationMode fragMode, String linkerName) throws IOException, Exception {
        // canCrosslinkerAttach if index files exist on given folder
        boolean reader = DirectoryReader.indexExists(FSDirectory.open(folder));
        // if an index folder is absent, first index 
        if (!reader) {
            CPeptidesIndexer indexer = new CPeptidesIndexer(headers, folder);
            indexer.index();
        }
        cPeptideSearcher = new CPeptideSearcher(folder);
        this.ptmFactory = ptmFactory;
        this.fragMode = fragMode;
        heavyLinker = GetCrossLinker.getCrossLinker(linkerName, true);
        lightLinker = GetCrossLinker.getCrossLinker(linkerName, false);
    }

    public CPeptideSearcher getCpSearch() {
        return cPeptideSearcher;
    }

    /**
     * This method returns a list of CPeptides at their masses within a given
     * mass range (inclusive lower and upper mass)
     *
     * @param from smaller value (inclusive)
     * @param to bigger value (inclusive)
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws XmlPullParserException
     */
    public ArrayList<CrossLinking> getCPeptidesFromGivenMassRange(double from, double to) throws IOException, ParseException, XmlPullParserException, IOException {
        ArrayList<CrossLinking> selected = new ArrayList<CrossLinking>();
        int topSearch = FieldName.MAX_SEARCH; // how many top number matches is allowed for querying
        TopDocs topDocs = cPeptideSearcher.performMassRangeSearch(from, to, topSearch);
        ScoreDoc[] res = topDocs.scoreDocs;
        for (ScoreDoc re : res) {
            Document doc = cPeptideSearcher.getDocument(re.doc);
            // to select only cross-linked objects
            CrossLinking cp = getCPeptides(doc);
            if (cp instanceof CPeptides) {
                selected.add(cp);
            }
            // also add Contaminant-derived objects
            if (cp instanceof Contaminant) {
                selected.add(cp);
            }
            // also add Monolinked peptides 
            if (cp instanceof MonoLinkedPeptides) {
                selected.add(cp);
            }
        }
        return selected;
    }

    /**
     * This method generates a CPeptides object after reading a file
     *
     * @param line
     * @param ptmFactory
     * @param linker
     * @param fragMode
     * @param isContrastLinkedAttachmentOn
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private CrossLinking getCPeptides(Document doc) throws XmlPullParserException, IOException {
        CrossLinking selected = null;
        CrossLinker selectedLinker = lightLinker;
        String proteinA = doc.get(FieldName.PROTEINA),
                proteinB = doc.get(FieldName.PROTEINB), // proteinB name
                peptideAseq = doc.get(FieldName.PEPTIDEA),
                peptideBseq = doc.get(FieldName.PEPTIDEB),
                linkA = doc.get(FieldName.LINKA),
                linkB = doc.get(FieldName.LINKB),
                fixedModA = doc.get(FieldName.FIXMODA),
                fixedModB = doc.get(FieldName.FIXMODB),
                variableModA = doc.get(FieldName.VARMODA),
                variableModB = doc.get(FieldName.VARMODB);
        boolean isContrastLinkedAttachmentOn = false,
                isPossible = false;
        if (!proteinA.startsWith("contaminant")) {
            String labelInfo = doc.get(FieldName.LABEL).replace("\n", "");
            if (labelInfo.equalsIgnoreCase("heavyLabeled")) {
                selectedLinker = heavyLinker;
            }
        }
        // linker positions...
        // This means a cross linked peptide is here...
        if (!proteinB.equals("-")) {
            Integer linkerPosPeptideA = Integer.parseInt(linkA),
                    linkerPosPeptideB = Integer.parseInt(linkB);
            int linkerPosProteinA = Integer.parseInt(proteinA.substring(proteinA.indexOf("(") + 1, proteinA.lastIndexOf("-"))) + linkerPosPeptideA,
                    linkerPosProteinB = Integer.parseInt(proteinB.substring(proteinB.indexOf("(") + 1, proteinB.lastIndexOf("-"))) + linkerPosPeptideB;
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
            Peptide peptideA = new Peptide(peptideAseq, ptms_peptideA),
                    peptideB = new Peptide(peptideBseq, ptms_peptideB);
            boolean canLinkToPeptideA = canCrosslinkerAttach(peptideA, selectedLinker, linkerPosPeptideA, linkerPosProteinA),
                    canLinkToPeptideB = canCrosslinkerAttach(peptideB, selectedLinker, linkerPosPeptideB, linkerPosProteinB);
            if (canLinkToPeptideA && canLinkToPeptideB) {
                isPossible = true;
            }
            if (peptideA.getSequence().length() > peptideB.getSequence().length() && isPossible) {
                // now generate peptide...
                CPeptides tmpCpeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, selectedLinker, linkerPosPeptideA, linkerPosPeptideB, fragMode, isContrastLinkedAttachmentOn);
                selected = tmpCpeptide;
            } else if (isPossible) {
                CPeptides tmpCpeptide = new CPeptides(proteinB, proteinA, peptideB, peptideA, selectedLinker, linkerPosPeptideB, linkerPosPeptideA, fragMode, isContrastLinkedAttachmentOn);
                selected = tmpCpeptide;
            }
        } // This means only monolinked peptide...    
        else if (!proteinA.contains("contaminant") && proteinB.equals("-")) {
            Integer linkerPosPeptideA = Integer.parseInt(linkA);
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseq, ptms_peptideA);
            int linkerPosProteinA = Integer.parseInt(proteinA.substring(proteinA.indexOf("(") + 1, proteinA.lastIndexOf("-"))) + linkerPosPeptideA;
            isPossible = canCrosslinkerAttach(peptideA, selectedLinker, linkerPosPeptideA, linkerPosProteinA);
            if (isPossible) {
                MonoLinkedPeptides mP = new MonoLinkedPeptides(peptideA, proteinA, linkerPosPeptideA, selectedLinker, fragMode);
                selected = mP;
            }
        } else if (proteinA.contains("contaminant")) {
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseq, ptms_peptideA);
            Contaminant mP = new Contaminant(peptideA, proteinA, fragMode);
            selected = mP;
        }
        return selected;
    }

    /**
     * This method checks given peptide if PTM allows making a covalent bound
     * with cross-linker. Currently, only Amine-to-Amine cross-linkers are
     * supporting. Modification on protein N-terminus and peptide N-terminus
     * target amine-group, therefore these PTMs do not allow any cross-linking
     * by Amine-to-Amine cross-linkers.
     *
     *
     * @param peptide a Peptide with PTMs
     * @param crossLinker a cross-linker
     * @param linkPositionPeptide the position on a peptide that a cross-linker
     * attaches
     * @param linkPosProtein the position on a protein that a cross-linker
     * attaches
     * @return true if cross-linking is possible for a given peptide; false if a
     * cross-linking is not possible for a given peptide
     *
     */
    public static boolean canCrosslinkerAttach(Peptide peptide, CrossLinker crossLinker, int linkPositionPeptide, int linkPosProtein) {
        boolean possible = true;
        PTMFactory ptmFactory = PTMFactory.getInstance();
        if (crossLinker.getType().equals(CrossLinkerType.AMINE_TO_AMINE)) {
            // canCrosslinkerAttach protein and peptide n-termini cases..
            ArrayList<ModificationMatch> modifications = peptide.getModificationMatches();
            if (peptide.getModificationMatches() != null) {
                for (ModificationMatch modificationMatch : modifications) {
                    if (modificationMatch.getModificationSite() == 1) {
                        PTM ptm = ptmFactory.getPTM(modificationMatch.getTheoreticPtm());
                        if (ptm.getType() == PTM.MODN && linkPosProtein == 1) {
                            possible = false;
                        }
                        if (ptm.getType() == PTM.MODNP && linkPositionPeptide == 1) {
                            possible = false;
                        }
                    }
                }
            }
        } else {
            throw new UnsupportedOperationException("Contact to a developer! Given crosslinker is not supported yet, only amine-to-amine cross-linkers");
        }
        return possible;
    }

}
