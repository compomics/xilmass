/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

/**
 * Some Maths related methods
 * 
 * @author Sule
 */
public class CMathUtil {

    /**
     * This method calculate combinations for C(n,r).
     * 
     * @param n 
     * @param r
     * @return
     * @throws Exception 
     */
    public static long calculateCombination(int n, int r) throws Exception {
        long score = 0;
        if (n >= r) {
            double upper = 1,
                    lower = 1;
            for (int i = n; i > n - r; i--) {
                upper = upper * i;
            }
            for (int i = r; i > 1; i--) {
                lower = lower * i;
            }
             score = (long) (upper / lower);             
        } else {
            throw new Exception("Error! n >= r");
        }
        return score;

    }
}
