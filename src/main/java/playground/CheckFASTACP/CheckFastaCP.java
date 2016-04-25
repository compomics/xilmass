/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playground.CheckFASTACP;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Sule
 */
public class CheckFastaCP {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        File f = new File("C:\\Users\\Sule\\Documents\\PhD\\databases\\cam_plectin\\corrNterm/test_generic_Plec_Cmd_corrNterm_targetRDecoy_both.fastacp");
        BufferedReader br = new BufferedReader(new FileReader(f));
        System.out.println("Tested DB is " + f.getName());
        String line = "";
        int max_len = 0;
        String header = "",
                max_len_header = "",
                max_len_seq = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith(">")) {
                int tmp_len = line.length();
                if (max_len < tmp_len) {
                    max_len = tmp_len;
                    max_len_header = header;
                    max_len_seq = line;
                }
            } else {
                header = line;
            }
        }
        System.out.println("");
        System.out.println("Max-length=" + "\t" + max_len);
        System.out.println("Max-length header=" + "\t" + max_len_header);
        System.out.println("Max-length sequence=" + "\t" + max_len_seq);
        System.out.println("\n");

        br = new BufferedReader(new FileReader(f));
        line = "";
        header = "";
        max_len_header = "";
        max_len_seq = "";
        while ((line = br.readLine()) != null) {
            if (!line.startsWith(">")) {
                int tmp_len = line.length();
                if (tmp_len > max_len-2) {
                    System.out.println(tmp_len);
                    System.out.println(header);
                    System.out.println(line);
                }
                if (max_len < tmp_len) {
                    max_len = tmp_len;
                    max_len_header = header;
                    max_len_seq = line;
                }
                if (max_len == tmp_len) {
                    max_len = tmp_len;
                    max_len_header = header;
                    max_len_seq = line;
                }
            } else {
                header = line;
            }
        }

    }

}
