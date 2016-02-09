/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

/**
 * This class holds information about the peptide tolerance mass window
 *
 * @author Sule
 */
public final class PeptideTol {

    private boolean is_peptide_tol_PPM; // either peptide tolerance mass window in ppm or Da.
    private double peptide_tol, // peptide tolerance mass window size (absolute)
            peptide_tol_base; // the center of peptide tolerance mass window (in Da)
    private double upper_limit, // upper mass off set of peptide tolerance mass window (in Da)
            lower_limit; // lower mass off set of peptide tolerance mass window (in Da)
    private String peptide_tol_name;

    /**
     * Construct an instance holding values of peptide tolerance mass window
     *
     * @param is_peptide_tol_PPM (true: peptide tolerance is in PPM, false:
     * peptide tolerance is in Dalton)
     * @param peptide_tol double value of peptide mass tolerance window
     * @param peptide_tol_base the center of peptide mass tolerance (in Dalton)
     * @param peptide_tol_name nameof the peptide_tol mass window
     */
    public PeptideTol(boolean is_peptide_tol_PPM, double peptide_tol, double peptide_tol_base, String peptide_tol_name) {
        this.is_peptide_tol_PPM = is_peptide_tol_PPM;
        this.peptide_tol = peptide_tol;
        this.peptide_tol_base = peptide_tol_base;
        this.peptide_tol_name = peptide_tol_name;
        calculateLimits(); // find upper and lower mass offset of the peptide tolerance mass window (in Da)
    }

    /**
     * Returns true if a peptide_tol mass window is given as ppm by the user
     *
     * @return
     */
    public boolean isPPM() {
        return is_peptide_tol_PPM;
    }

    /**
     * Returns the size of a peptide_tol mass window. This can be in either ppm
     * or Da, set by the user In other words, this is exactly the same as given
     * by the user
     *
     * @return
     */
    public double getPeptide_tol() {
        return peptide_tol;
    }

    /**
     * Returns the center of a peptide_tol mass window. This can be in either
     * ppm or Da, set by the user In other words, this is exactly the same as
     * given by the user
     *
     * @return
     */
    public double getPeptide_tol_base() {
        return peptide_tol_base;
    }

    /**
     * Returns a upper mass offset on a given peptide tolerance mass window (in
     * Da)
     *
     * @return
     */
    public double getUpper_limit() {
        return upper_limit;
    }

    /**
     * Sets a upper mass offset on a given peptide tolerance mass window (in Da)
     * @param upper_limit
     */
    public void setUpper_limit(double upper_limit) {
        this.upper_limit = upper_limit;
    }

    /**
     * Returns a lower mass offset on a given peptide tolerance mass window (in
     * Da)
     *
     * @return
     */
    public double getLower_limit() {
        return lower_limit;
    }

    /**
     * Sets a lower mass offset on a given peptide tolerance mass window (in Da)
     * @param lower_limit
     */
    public void setLower_limit(double lower_limit) {
        this.lower_limit = lower_limit;
    }

    /**
     * Returns the name of peptide_tol mass window (key in xLink.properties
     * file)
     *
     * @return
     */
    public String getPeptide_tol_name() {
        return peptide_tol_name;
    }

    /**
     * Sets the name of peptide_tol mass window (key in xLink.properties file)
     *
     * @param peptide_tol_name
     */
    public void setPeptide_tol_name(String peptide_tol_name) {
        this.peptide_tol_name = peptide_tol_name;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.is_peptide_tol_PPM ? 1 : 0);
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.peptide_tol) ^ (Double.doubleToLongBits(this.peptide_tol) >>> 32));
        hash = 31 * hash + (int) (Double.doubleToLongBits(this.peptide_tol_base) ^ (Double.doubleToLongBits(this.peptide_tol_base) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PeptideTol other = (PeptideTol) obj;
        if (this.is_peptide_tol_PPM != other.is_peptide_tol_PPM) {
            return false;
        }
        if (Double.doubleToLongBits(this.peptide_tol) != Double.doubleToLongBits(other.peptide_tol)) {
            return false;
        }
        return Double.doubleToLongBits(this.peptide_tol_base) == Double.doubleToLongBits(other.peptide_tol_base);
    }

    /**
     * This method calculates upper and lower mass offset of the peptide_tol
     * mass window.
     */
    public void calculateLimits() {
        double tol = peptide_tol;
        if (is_peptide_tol_PPM) {
            // convert to dalton..
            // a rough estimation.. as 10ppm=0.01Da
            tol = (double) peptide_tol / (double) 1000;
        }
        upper_limit = peptide_tol_base + tol;
        lower_limit = peptide_tol_base - tol;
    }

}
