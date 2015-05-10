/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playground;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

/**
 *
 * @author Sule
 */
public class Combinatorial {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Create an initial vector/set
        ICombinatoricsVector<String> initialSet = Factory.createVector(new String[]{"oxidation of m", "acetylation of n termini"});
        // Create an instance of the subset generator
        Generator<String> gen = Factory.createSubSetGenerator(initialSet);
        // Print the subsets
        for (ICombinatoricsVector<String> subSet : gen) {
            System.out.println(subSet);
            for(String s : subSet){
                System.out.println(s);
            }
        }
    }

}
