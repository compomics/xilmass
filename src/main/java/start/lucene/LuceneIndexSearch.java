/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start.lucene;

import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.ModificationMatch;
import crossLinker.CrossLinker;
import crossLinker.GetCrossLinker;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.log4j.Logger;
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
import theoretical.CrossLinkedPeptides;
import theoretical.FragmentationMode;
import theoretical.MonoLinkedPeptides;

/**
 * This class first checks if there are index files constructed before. If these
 * were not constructed before, it creates index files by construction of
 * CPeptidesIndex object and calling writeIndexFile() method. After making sure
 * that there are index files, by Search object to call query to select
 * CPeptides
 *
 * @author Sule
 */
public class LuceneIndexSearch {

    private static final Logger LOGGER = Logger.getLogger(LuceneIndexSearch.class);
    private CPeptideSearch cpSearch; // A searching class to run queries
    private PTMFactory ptmFactory;
    private FragmentationMode fragMode;
    private boolean isBranching,
            isContrastLinkedAttachmentOn;
    private CrossLinker heavyLinker,
            lightLinker;

    /**
     *
     * @param indexFile a crosslinked peptide-mass index file an index file with given protein mass, sequences to
     * construct CPeptides objects
     * @param ptmFactory
     * @param linkerName - just name of linker to create both heavy and light
     * labeled versions
     * @param fragMode
     * @param folder
     * @param isBranching
     * @param isContrastLinkedAttachmentOn
     * @throws IOException
     * @throws Exception
     */
    public LuceneIndexSearch(File indexFile, File folder, PTMFactory ptmFactory, FragmentationMode fragMode,
            boolean isBranching, boolean isContrastLinkedAttachmentOn, String linkerName) throws IOException, Exception {
        // check if index files exist on given folder
        boolean reader = DirectoryReader.indexExists(FSDirectory.open(folder));
        // if it is not, then write index files
        if (!reader) {
            CPeptidesIndex obj = new CPeptidesIndex(indexFile, folder);
            obj.writeIndexFile();
        }
        cpSearch = new CPeptideSearch(folder);
        this.ptmFactory = ptmFactory;
        this.fragMode = fragMode;
        this.isBranching = isBranching;
        this.isContrastLinkedAttachmentOn = isContrastLinkedAttachmentOn;
        heavyLinker = GetCrossLinker.getCrossLinker(linkerName, true);
        lightLinker = GetCrossLinker.getCrossLinker(linkerName, false);
    }

    public CPeptideSearch getCpSearch() {
        return cpSearch;
    }

    // range search
    //mod_date:[20020101 TO 20030101] - must be small and bigger
    /**
     * Return selected of CrossLinkedPeptides within a given mass range
     * (inclusive lower and upper mass)
     *
     * @param from smaller value
     * @param to bigger value (must be)
     * @return
     * @throws IOException
     * @throws ParseException
     * @throws XmlPullParserException
     */
    public ArrayList<CrossLinkedPeptides> getQuery(double from, double to) throws IOException, ParseException, XmlPullParserException, IOException {
        ArrayList<CrossLinkedPeptides> selected = new ArrayList<CrossLinkedPeptides>();
        String query = "mass:[" + from + " TO " + to + "]";
        int topSearch = 100; // how many topX number of query needs to be called
        TopDocs topDocs = cpSearch.performSearch(query, topSearch);
        ScoreDoc[] res = topDocs.scoreDocs;
        while (res.length == topSearch) {
            topSearch += 100;
            LOGGER.debug("indeed full.." + topSearch);
            topDocs = cpSearch.performSearch(query, topSearch);
            res = topDocs.scoreDocs;
        }
        LOGGER.debug("total result=" + res.length);
        for (ScoreDoc re : res) {
            Document doc = cpSearch.getDocument(re.doc);
            // to select only cross-linked objects
            CrossLinkedPeptides cp = getCPeptides(doc);
            if (cp instanceof CPeptides) {
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
     * @param isBranching
     * @param isContrastLinkedAttachmentOn
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private CrossLinkedPeptides getCPeptides(Document doc) throws XmlPullParserException, IOException {
        CrossLinkedPeptides selected = null;
        CrossLinker selectedLinker = lightLinker;
        String proteinA = doc.get("proteinA"),
                proteinB = doc.get("proteinB"), // proteinB name
                peptideAseq = doc.get("peptideAseq"),
                peptideBseq = doc.get("peptideBseq"),
                linkA = doc.get("linkA"),
                linkB = doc.get("linkB"),
                fixedModA = doc.get("fixModA"),
                fixedModB = doc.get("fixModB"),
                variableModA = doc.get("varModA"),
                variableModB = doc.get("varModB");
        if (!proteinA.startsWith("contaminant")) {
            boolean isHeavy = Boolean.parseBoolean(doc.get("label"));
            if (isHeavy) {
                selectedLinker = heavyLinker;
            }
        }
        // linker positions...
        // This means a cross linked peptide is here...
        if (!proteinB.equals("-")) {
            Integer linkerPosPeptideA = Integer.parseInt(linkA),
                    linkerPosPeptideB = Integer.parseInt(linkB);
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
            if (peptideA.getSequence().length() > peptideB.getSequence().length()) {
                // now generate peptide...
                CPeptides tmpCpeptide = new CPeptides(proteinA, proteinB, peptideA, peptideB, selectedLinker, linkerPosPeptideA, linkerPosPeptideB, fragMode, isBranching, isContrastLinkedAttachmentOn);
                selected = tmpCpeptide;
            } else {
                CPeptides tmpCpeptide = new CPeptides(proteinB, proteinA, peptideB, peptideA, selectedLinker, linkerPosPeptideB, linkerPosPeptideA, fragMode, isBranching, isContrastLinkedAttachmentOn);
                selected = tmpCpeptide;
            }
        } // This means only monolinked peptide...    
        else if (!proteinA.startsWith("contaminant")) {
            Integer linkerPosPeptideA = Integer.parseInt(linkA);
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseq, ptms_peptideA);
            MonoLinkedPeptides mP = new MonoLinkedPeptides(peptideA, proteinA, linkerPosPeptideA, selectedLinker, fragMode, isBranching);
            selected = mP;
        } else if (proteinA.startsWith("contaminant")) {
            ArrayList<ModificationMatch> fixedPTM_peptideA = GetPTMs.getPTM(ptmFactory, fixedModA, false);
            // Start putting them on a list which will contain also variable PTMs
            ArrayList<ModificationMatch> ptms_peptideA = new ArrayList<ModificationMatch>(fixedPTM_peptideA);
            // Add variable PTMs and also a list of several fixed PTMs
            ArrayList<ModificationMatch> variablePTM_peptideA = GetPTMs.getPTM(ptmFactory, variableModA, true);
            ptms_peptideA.addAll(variablePTM_peptideA);
            // First peptideA
            Peptide peptideA = new Peptide(peptideAseq, ptms_peptideA);
            Contaminant mP = new Contaminant(peptideA, proteinA, fragMode, isBranching);
            selected = mP;
        }
        return selected;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        File indexFile = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\databases\\test\\lucene/target_Rdecoy_cam_plectin_cxm_both_index.txt"),
                modsFile = new File("C:/Users/Sule/Documents/NetBeansProjects/CrossLinkedPeptides/src/resources/mods.xml"),
                folder = new File("C:\\Users\\Sule\\Documents\\PhD\\XLinked\\databases\\test\\lucene/lucene");
        PTMFactory ptmFactory = PTMFactory.getInstance();
        ptmFactory.importModifications(modsFile, false);
        FragmentationMode fragMode = FragmentationMode.HCD;
        boolean isBranching = false,
                isContrastLinkedAttachmentOn = false;

        LuceneIndexSearch o = new LuceneIndexSearch(indexFile, folder, ptmFactory, fragMode, isBranching, isContrastLinkedAttachmentOn, "DSS");
        ArrayList<CrossLinkedPeptides> query = o.getQuery(1500, 1700);
        for (CrossLinkedPeptides q : query) {
            System.out.println(q.toPrint());
        }

    }
}
