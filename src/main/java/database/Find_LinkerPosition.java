/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import crossLinker.CrossLinkerName;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Sule
 */
public class Find_LinkerPosition {

    public static HashMap<String, ArrayList<Integer>> find_possibly_linker_locations(String startSequence, CrossLinkerName crossLinker) {
        HashMap<String, ArrayList<Integer>> crossLinker_and_indices = new HashMap<String, ArrayList<Integer>>();
        switch (crossLinker) {
            case DSS:
                // k-k
                ArrayList<Integer> indices = new ArrayList<Integer>();
                for (int i = 0; i < startSequence.length(); i++) {
                    char charAt = startSequence.charAt(i);
                    if (charAt == 'K' && i != startSequence.length() - 1) {
                        indices.add(i);
                    }
                    crossLinker_and_indices.put("K", indices);
                }
                break;

            case EDC:
                ArrayList<Integer> indices_K = new ArrayList<Integer>(),
                 indices_E = new ArrayList<Integer>(),
                 indices_D = new ArrayList<Integer>();
                for (int i = 0; i < startSequence.length(); i++) {
                    char charAt = startSequence.charAt(i);
                    if (charAt == 'K' && i != startSequence.length() - 1) {
                        indices_K.add(i);
                    } else if (charAt == 'E' && i != startSequence.length() - 1) {
                        indices_E.add(i);
                    } else if (charAt == 'D' && i != startSequence.length() - 1) {
                        indices_D.add(i);
                    }
                }
                crossLinker_and_indices.put("K", indices_K);
                crossLinker_and_indices.put("D", indices_D);
                crossLinker_and_indices.put("E", indices_E);
                break;
        }
        return crossLinker_and_indices;
    }
}
