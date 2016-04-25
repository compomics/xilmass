/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start.lucene;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * This class currently enable to parse of number range It is also possible to
 * parse with QueryParser which has the same analyzer as indexing.
 *
 * @author Sule
 */
public class CPeptideSearcher {

    private IndexSearcher cPeptideSearcher;
    private QueryParser cPeptideQueryParser;

    public CPeptideSearcher(File indexFile) throws IOException {
        // first creat this indexSearcher
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(indexFile));
        cPeptideSearcher = new IndexSearcher(reader);
        // CPeptidesIndex were constructed with search field called "content", therefore call content
        // Analyzer type needs to be specified while indexing and searching and must be THE SAME!
        // This defined default field is used if the query string does not specify the search field.
        cPeptideQueryParser = new QueryParser(FieldName.MASS, new StandardAnalyzer());
    }

    public IndexSearcher getcPeptideSearcher() {
        return cPeptideSearcher;
    }

    public QueryParser getcPeptideQueryParser() {
        return cPeptideQueryParser;
    }

    /**
     * This method returns TopDocs from a numberRangeQuery (with given from and
     * to including both borders).
     *
     * @param from the lower limit on the numberRangeQuery (inclusive)
     * @param to the upper limit on the numberRangeQuery (inclusive)
     * @param hits is initial value to return number of document for the given
     * query. If the total-hit is bigger than this value, hit value is set to
     * this total-hit and all matching documents are retrieved
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public TopDocs performMassRangeSearch(double from, double to, int hits) throws IOException, ParseException {
        Query numeric_query = NumericRangeQuery.newDoubleRange(FieldName.MASS, 6, from, to, true, true);
        TopDocs topDocs = cPeptideSearcher.search(numeric_query, hits);
        int totalhits = topDocs.totalHits;
        // making sure that all hits are retrieved
        if (totalhits > hits) {
            hits = totalhits;
            topDocs = cPeptideSearcher.search(numeric_query, hits);
        }
        return topDocs;
    }

    /**
     * This method returns a Document of given docId (document ID)
     *
     * @param docId
     * @return
     * @throws IOException
     */
    public Document getDocument(int docId) throws IOException {
        return cPeptideSearcher.doc(docId);
    }

}
