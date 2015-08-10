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
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

/**
 * HOW TO QUERY: A general syntax given a list of keywords separated by AND OR +
 * or - required or prohibited
 *
 * A clause - keyword - nested query with paranthesis
 *
 * Exp1: name:Mari OR desc:Comf to find Mari on name field or Comf on desc field
 * Exp2: name:(+Mari +Res) to find an entry containing both words on the name
 * field
 *
 * Sample search example
 *
 * // retrieve top 100 matching document list for the query "Notre Dame museum"
 * TopDocs topDocs = se.performSearch("Notre Dame museum", 100);  *
 * // obtain the ScoreDoc (= documentID, relevanceScore) array from topDocs
 * ScoreDoc[] hits = topDocs.scoreDocs; *
 * // retrieve each matching document from the ScoreDoc arry for (int i = 0; i <
 * hits.length; i++) { Document doc = instance.getDocument(hits[i].doc); String
 * hotelName = doc.get("name"); ... }
 * 
 * range: [] inclusive {} exclusive
 * 
 * TUTORIAL:http://oak.cs.ucla.edu/cs144/projects/lucene/
 *
 * @author Sule
 */
public class CPeptideSearch {

    private IndexSearcher searcher;
    private QueryParser parser;

    public CPeptideSearch(File indexFile) throws IOException {
        // first creat this indexSearcher
        searcher = new IndexSearcher(DirectoryReader.open(FSDirectory.open(new File(indexFile.getParent()))));
        // CPeptidesIndex were constructed with search field called "content", therefore call content
        // Analyzer type needs to be specified while indexing and searching and must be THE SAME!
        parser = new QueryParser("content", new StandardAnalyzer());
    }

    /**
     * To return top docs according to the given query string. This query string
     * is used to parse and it is followed by performing a search
     *
     * @param queryString
     * @param n
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public TopDocs performSearch(String queryString, int n) throws IOException, ParseException {
        Query query = parser.parse(queryString);       
        return searcher.search(query, n);
    }

    /**
     * To return a Document of given docId
     *
     * @param docId
     * @return
     * @throws IOException
     */
    public Document getDocument(int docId) throws IOException {
        return searcher.doc(docId);
    }

}
