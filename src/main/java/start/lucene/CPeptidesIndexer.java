/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start.lucene;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xmlpull.v1.XmlPullParserException;

/**
 * This class is used to index the given database entries (cPeptideEntries)
 *
 * Lucene performs inverted-indexing and each numeric value is indexed as a "trie structure"
 *
 * @author Sule
 */
public class CPeptidesIndexer {

    private IndexWriter indexWriter;
    private File indexDirPath; // a directory which contains all indexFiles   
    private boolean isConstructed = false; // to check if an object for indexing already constructed
    private HashSet<StringBuilder> cPeptideEntries = new HashSet<StringBuilder>();
    private final FieldType indexedFieldType = new FieldType();
    private int totalDoc = 0; // total number of stored indexes

    /**
     * This method constructs a CPeptidesIndex object from a given list of
     * entries and locates index files on given folder
     *
     * @param cPeptideEntries a list of StringBuilder information for each
     * cross-linked peptide entry
     * @param folder a location where index files are stored
     */
    public CPeptidesIndexer(HashSet<StringBuilder> cPeptideEntries, File folder) {
        indexDirPath = folder;
        this.cPeptideEntries = cPeptideEntries;
        // the default value is 4 but the ideal value in most cases for 64 bit data types (long, double) is 6 or 8. 
        indexedFieldType.setNumericPrecisionStep(6);
        indexedFieldType.setStored(true);
        indexedFieldType.setIndexed(true);
        indexedFieldType.setNumericType(FieldType.NumericType.DOUBLE);
    }

    /**
     * This method returns IndexWriter, by making sure that an object has been
     * already constructed
     *
     * @return
     * @throws IOException
     */
    public IndexWriter getIndexWriter() throws IOException {
        if (!isConstructed || indexWriter == null) {
            // Explanation from Lucene-Package explanation...
            // Unfortunately, because of system peculiarities, there is no single overall best implementation. 
            // Therefore, we've added the open(java.io.File) method, to allow Lucene to choose the best FSDirectory implementation given your environment, 
            //and the known limitations of each implementation. For users who have no reason to prefer a specific implementation, it's best to simply use open(java.io.File).
            Directory dir = FSDirectory.open(indexDirPath);
            // The following is to create an in-memory index: Directory index = new RAMDirectory();

            // vairous types of analyzers but only standardAnalyzer is used
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_4, new StandardAnalyzer());
            // now each index file is reconstructed from scrath
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(dir, config);
            // disable merging all segments 
            indexWriter.getConfig().setUseCompoundFile(false);
            isConstructed = true;
        }
        return indexWriter;
    }

    /**
     * This method creates a Document of each entry from given cPeptideEntries
     * and then it adds every Document to an indexWriter. After indexing the
     * last entry, the current indexWriter is closed.
     *
     * @throws FileNotFoundException
     * @throws IOException
     * @throws XmlPullParserException
     */
    public void index() throws FileNotFoundException, IOException, XmlPullParserException {
        for (StringBuilder cPeptideEntry : cPeptideEntries) {
            Document doc = getDocument(cPeptideEntry);
            getIndexWriter().addDocument(doc);
            totalDoc++;
        }
        getIndexWriter().close();
    }

    /**
     * This method prepares a Document from a given line for indexing
     *
     * @param line
     * @return
     * @throws IOException
     */
    public Document getDocument(StringBuilder line) throws IOException {
        Document doc = new Document();
        int maxDoc = getIndexWriter().maxDoc(),
                id = maxDoc++;
        // Fill document like "name-value" pair.
        doc.add(new IntField(FieldName.ID, id, Field.Store.YES)); // proteinA name
        String[] sp = line.toString().split("\t");
        // Except mass, all is StringField but not TextField because
        // A text field is a sequence of terms that has been tokenized and punctuation and spacing are ignored-good for keyword search
        // while a string field is a single term with literal character strings with all punctuation, and cannot tokenized (only for atomic values), spacing,and case preserved
        // StringField is always indexed since Lucene4.0
        // StoredField is for storing but not indexing at all (and so, is not searchable).
        // StoredField(String name, String value)creates a stored-only field with the given string value       
        doc.add(new StoredField(FieldName.PROTEINA, sp[0]));
        doc.add(new StoredField(FieldName.PROTEINB, sp[1])); // proteinB name
        doc.add(new StoredField(FieldName.PEPTIDEA, sp[2])); // peptideA sequence
        doc.add(new StoredField(FieldName.PEPTIDEB, sp[3])); // peptideB sequence
        doc.add(new StoredField(FieldName.LINKA, sp[4])); // proteinA name
        doc.add(new StoredField(FieldName.LINKB, sp[5])); // proteinB name
        doc.add(new StoredField(FieldName.FIXMODA, sp[6])); // linkerPeptideA
        doc.add(new StoredField(FieldName.FIXMODB, sp[7])); // linkerPeptideB
        doc.add(new StoredField(FieldName.VARMODA, sp[8])); // ModificationsPeptideA
        doc.add(new StoredField(FieldName.VARMODB, sp[9])); // ModificationsPeptideB
        //doc.add(new StringField("mass", sp[10], Field.Store.YES)); // Mass    
        doc.add(new DoubleField(FieldName.MASS, Double.parseDouble(sp[10]), indexedFieldType));
        if (sp.length > 11) {
            doc.add(new StoredField(FieldName.TYPE, sp[11])); //Type
            doc.add(new StoredField(FieldName.LABEL, sp[12])); // Labeling-true:Heavylabeled
        }
        return doc;
    }

    /**
     * This method returns the total number of stored documents
     *
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     * @throws XmlPullParserException
     */
    public int getTotalDoc() throws IOException, FileNotFoundException, XmlPullParserException {
        if (!isConstructed || indexWriter == null) {
            getIndexWriter();
            index();
        }
        return totalDoc;
    }

    public File getIndexDirPath() {
        return indexDirPath;
    }

    public boolean isIsConstructed() {
        return isConstructed;
    }

    public HashSet<StringBuilder> getcPeptideEntries() {
        return cPeptideEntries;
    }

    public FieldType getIndexedFieldType() {
        return indexedFieldType;
    }

}
