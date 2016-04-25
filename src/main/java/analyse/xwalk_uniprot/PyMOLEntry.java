/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package analyse.xwalk_uniprot;

/**
 *
 * This class contains information from a cross-linked peptide to prepare a
 * PyMol script
 *
 * @author Sule
 */
public class PyMOLEntry {

    private String pdb,
            atomA,
            atomB,
            idDis,
            type,
            uniprotAccA,
            uniprotIndexA,
            uniprotAccB,
            uniprotIndexB,
            sas,
            betaDist,
            alphaDist,
            command = "",
            crossLinkingSite;

    public PyMOLEntry(String pdb, String name, String atomA, String atomB, String idDis, String type, String uniprotAccA, String uniprotIndexA, String uniprotAccB, String uniprotIndexB,
            String sas, String betaDist, String alphaDist, boolean isCarbonAlpha, boolean isPredicted,String crossLinkingSite) {
        this.pdb = pdb;
        this.atomA = atomA;
        this.atomB = atomB;
        this.idDis = idDis;
        this.type = type;
        this.uniprotAccA = uniprotAccA;
        this.uniprotIndexA = uniprotIndexA;
        this.uniprotAccB = uniprotAccB;
        this.uniprotIndexB = uniprotIndexB;
        this.sas = sas;
        this.betaDist = betaDist;
        this.alphaDist = alphaDist;
        this.crossLinkingSite =crossLinkingSite;
        String structure = pdb.substring(0, pdb.indexOf(".pdb"));
        String[] firsts,
                seconds;
        if (isPredicted) {
            firsts = atomA.split("-");
            seconds = atomB.split("-");
        } else {
            firsts = atomA.split("_");
            seconds = atomB.split("_");
        }
        String firstInfo = "/" + structure + "//" + firsts[2] + "/" + firsts[0] + "`" + firsts[1] + "/" + "CA",
                secondInfo = "/" + structure + "//" + seconds[2] + "/" + seconds[0] + "`" + seconds[1] + "/" + "CA";
        if (!isCarbonAlpha) {
            firstInfo = "/" + structure + "//" + firsts[2] + "/" + firsts[0] + "`" + firsts[1] + "/" + "CB";
            secondInfo = "/" + structure + "//" + seconds[2] + "/" + seconds[0] + "`" + seconds[1] + "/" + "CB";
        }
        // write also pymol command here...
        command = "dst=cmd.distance('" + name+ "_"+crossLinkingSite + "','" + firstInfo + "','" + secondInfo + "')";
    }

    public String getCrossLinkingSite() {
        return crossLinkingSite;
    }

    public void setCrossLinkingSite(String crossLinkingSite) {
        this.crossLinkingSite = crossLinkingSite;
    }

    
    public String getPdb() {
        return pdb;
    }

    public void setPdb(String pdb) {
        this.pdb = pdb;
    }

    public String getAtomA() {
        return atomA;
    }

    public void setAtomA(String atomA) {
        this.atomA = atomA;
    }

    public String getAtomB() {
        return atomB;
    }

    public void setAtomB(String atomB) {
        this.atomB = atomB;
    }

    public String getIdDis() {
        return idDis;
    }

    public void setIdDis(String idDis) {
        this.idDis = idDis;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUniprotAccA() {
        return uniprotAccA;
    }

    public void setUniprotAccA(String uniprotAccA) {
        this.uniprotAccA = uniprotAccA;
    }

    public String getUniprotIndexA() {
        return uniprotIndexA;
    }

    public void setUniprotIndexA(String uniprotIndexA) {
        this.uniprotIndexA = uniprotIndexA;
    }

    public String getUniprotAccB() {
        return uniprotAccB;
    }

    public void setUniprotAccB(String uniprotAccB) {
        this.uniprotAccB = uniprotAccB;
    }

    public String getUniprotIndexB() {
        return uniprotIndexB;
    }

    public void setUniprotIndexB(String uniprotIndexB) {
        this.uniprotIndexB = uniprotIndexB;
    }

    public String getSas() {
        return sas;
    }

    public void setSas(String sas) {
        this.sas = sas;
    }

    public String getBetaDist() {
        return betaDist;
    }

    public void setBetaDist(String betaDist) {
        this.betaDist = betaDist;
    }

    public String getAlphaDist() {
        return alphaDist;
    }

    public void setAlphaDist(String alphaDist) {
        this.alphaDist = alphaDist;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public String toString() {
        return "PyMOLEntry{" + "pdb=" + pdb + ", atomA=" + atomA + ", atomB=" + atomB + ", idDis=" + idDis + ", type=" + type + ", uniprotAccA=" + uniprotAccA + ", uniprotIndexA=" + uniprotIndexA + ", uniprotAccB=" + uniprotAccB + ", uniprotIndexB=" + uniprotIndexB + ", sas=" + sas + ", betaDist=" + betaDist + ", alphaDist=" + alphaDist + ", command=" + command + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + (this.pdb != null ? this.pdb.hashCode() : 0);
        hash = 43 * hash + (this.atomA != null ? this.atomA.hashCode() : 0);
        hash = 43 * hash + (this.atomB != null ? this.atomB.hashCode() : 0);
        hash = 43 * hash + (this.uniprotAccA != null ? this.uniprotAccA.hashCode() : 0);
        hash = 43 * hash + (this.uniprotIndexA != null ? this.uniprotIndexA.hashCode() : 0);
        hash = 43 * hash + (this.uniprotIndexB != null ? this.uniprotIndexB.hashCode() : 0);
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
        final PyMOLEntry other = (PyMOLEntry) obj;
        if ((this.pdb == null) ? (other.pdb != null) : !this.pdb.equals(other.pdb)) {
            return false;
        }
        if ((this.atomA == null) ? (other.atomA != null) : !this.atomA.equals(other.atomA)) {
            return false;
        }
        if ((this.atomB == null) ? (other.atomB != null) : !this.atomB.equals(other.atomB)) {
            return false;
        }
        if ((this.uniprotAccA == null) ? (other.uniprotAccA != null) : !this.uniprotAccA.equals(other.uniprotAccA)) {
            return false;
        }
        if ((this.uniprotIndexA == null) ? (other.uniprotIndexA != null) : !this.uniprotIndexA.equals(other.uniprotIndexA)) {
            return false;
        }
        if ((this.uniprotAccB == null) ? (other.uniprotAccB != null) : !this.uniprotAccB.equals(other.uniprotAccB)) {
            return false;
        }
        if ((this.uniprotIndexB == null) ? (other.uniprotIndexB != null) : !this.uniprotIndexB.equals(other.uniprotIndexB)) {
            return false;
        }
        return true;
    }
}
