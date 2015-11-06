/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;

/**
 *
 * @author Sule
 */
public class WriteCXDB {

    private static final Logger LOGGER = Logger.getLogger(WriteCXDB.class);

    /**
     * This class is used to write a CXDB database (with .fastacp extension)
     * from already genereated header and sequence Make sure that hashmap is
     * filled
     *
     * @param header_sequence cxdb header and sequence
     * @param cxDBName is a name of a constructed cxdb file
     * @throws IOException
     */
    public static void writeCXDB(HashMap<String, String> header_sequence, String cxDBName) throws IOException {
        File crossLinkedDB = new File(cxDBName + ".fastacp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(crossLinkedDB));
        for (String header : header_sequence.keySet()) {
            String sequence = header_sequence.get(header);
            bw.write(">" + header + "\n" + sequence + "\n");
        }
        bw.close();
        LOGGER.info("CX database is constructed and stored in " + crossLinkedDB.getName()+ "!");
    }

}
