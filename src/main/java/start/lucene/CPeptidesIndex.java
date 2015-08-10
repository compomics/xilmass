/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start.lucene;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.xmlpull.v1.XmlPullParserException;

/**
 * To index for Lucene searching
 *
 * @author Sule
 */
public class CPeptidesIndex {

    private IndexWriter indexWriter;
    private File indexFile,
            indexDirectory; // a directory which contains all indexFiles   
    private boolean isConstructed = true;

    public CPeptidesIndex(File index, File folder) {
        this.indexFile = index;
        indexDirectory = folder;
    }

    public IndexWriter getIndexWriter() throws IOException {
        if (!isConstructed || indexWriter == null) {
            Directory indexDir = FSDirectory.open(indexDirectory); 
            Analyzer a = new StandardAnalyzer();
            // vairous types of analyzers
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_3, a);
            // now each index file is reconstructed from scrath
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            indexWriter = new IndexWriter(indexDir, config);
            indexWriter.getConfig().setUseCompoundFile(false);
            isConstructed = true;
        }
        return indexWriter;
    }

    public void writeIndexFile() throws FileNotFoundException, IOException, XmlPullParserException {
        // read each entry on a file to store on an index
        BufferedReader br = new BufferedReader(new FileReader(indexFile));
        String line = "";
        while ((line = br.readLine()) != null) {
            getEachIndex(line);
        }
        // now close the writer
        getIndexWriter().close();
    }

    public void getEachIndex(String line) throws IOException {

        Document doc = new Document();
        String[] sp = line.split("\t");
        doc.add(new StringField("proteinA", sp[0], Field.Store.YES)); // proteinA name
        doc.add(new StringField("proteinB", sp[1], Field.Store.YES)); // proteinB name

        doc.add(new StringField("peptideAseq", sp[2], Field.Store.YES)); // peptideA sequence
        doc.add(new StringField("peptideBseq", sp[3], Field.Store.YES)); // peptideB sequence

        doc.add(new StringField("linkA", sp[4], Field.Store.YES)); // proteinA name
        doc.add(new StringField("linkB", sp[5], Field.Store.YES)); // proteinB name

        doc.add(new StringField("fixModA", sp[6], Field.Store.YES)); // linkerPeptideA
        doc.add(new StringField("fixModB", sp[7], Field.Store.YES)); // linkerPeptideB

        doc.add(new StringField("varModA", sp[8], Field.Store.YES)); // ModificationsPeptideA
        doc.add(new StringField("varModB", sp[9], Field.Store.YES)); // ModificationsPeptideB

        doc.add(new StringField("mass", sp[10], Field.Store.YES)); // Mass

        if (sp.length > 11) {
            doc.add(new StringField("type", sp[11], Field.Store.YES)); //Type
            doc.add(new StringField("label", sp[12], Field.Store.YES)); // Labeling-true:Heavylabeled
        }
        // I want full-text indexing
        String fullSearchableText = sp[0] + "_" + sp[1] + "_" + sp[2] + "_"
                + sp[3] + "_" + sp[4] + "_" + sp[5] + "_"
                + sp[6] + "_" + sp[7] + "_" + sp[8] + "_"
                + sp[9] + "_" + sp[10];
        if (sp.length > 11) {
            fullSearchableText += "_" + sp[11] + "_" + sp[12];
        }
        doc.add(new TextField("content", fullSearchableText, Field.Store.NO)); // FieldStore _ to save index space
        getIndexWriter().addDocument(doc);
    }

}
