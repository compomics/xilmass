/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.shared;

import java.util.Comparator;

/**
 *
 * @author Sule
 */
public class InformationQValue extends Information {

    protected double qvalue,
            posterior;

    public InformationQValue(String foundBy, String fileName, String scanNumber,
            String proteinA, String peptideA, String modA, String linkA,
            String proteinB, String peptideB, String modB, String linkB, String label, String td, String predicted, String euclidean_alpha, String euclidean_beta,
            double score, double qvalue, double posterior) {
        super(foundBy, fileName, scanNumber, proteinA, peptideA, modA, linkA, proteinB, peptideB, modB, linkB, label, td, predicted, euclidean_alpha, euclidean_beta, score);
        this.qvalue = qvalue;
        this.posterior = posterior;
    }

    public double getQvalue() {
        return qvalue;
    }

    public void setQvalue(double qvalue) {
        this.qvalue = qvalue;
    }

    public static final Comparator<InformationQValue> ScoreASC
            = new Comparator<InformationQValue>() {
                @Override
                public int compare(InformationQValue o1, InformationQValue o2) {
                    return o1.getQvalue() < o2.getQvalue() ? -1 : o1.getQvalue() == o2.getQvalue() ? 0 : 1;
                }
            };

}
