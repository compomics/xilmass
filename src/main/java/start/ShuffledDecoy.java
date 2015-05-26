/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package start;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This class is one of the first attempt to shuffle targets to create decoys..
 * 
 * @author Sule
 */
public class ShuffledDecoy {

    private String target;
    private List<String> letters;
    private StringBuilder shuffled;

    public ShuffledDecoy(String target) {
        this.target = target;
        letters = Arrays.asList(target.split(""));
        shuffled = new StringBuilder();
    }

    public StringBuilder getShuffled() {
        if (shuffled.length() == 0) {
            shuffled = shuffle(target);
        }
        return shuffled;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        shuffled = new StringBuilder();
        this.target = target;
        letters = Arrays.asList(target.split(""));
    }

    public static char[] r(String target) {
        char[] array = target.toCharArray();
        int currentIndex = array.length,
                randomIndex;
        char temporaryValue;

        // While there remain elements to shuffle...
        while (0 != currentIndex) {

            // Pick a remaining element...
            randomIndex = (int) Math.floor(Math.random() * currentIndex);
            currentIndex -= 1;

            // And swap it with the current element.
            temporaryValue = array[currentIndex];
            array[currentIndex] = array[randomIndex];
            array[randomIndex] = temporaryValue;
        }

        return array;
    }

    /**
     * This method shuffles a given target, then makes sure that each index has
     * different aminoacid than before
     *
     * @param target
     * @return
     */
    public StringBuilder shuffle(String target) {
        boolean isSame = true;
        while (isSame) {
        shuffled = new StringBuilder();
        Collections.shuffle(letters);
        for (String letter : letters) {
            shuffled.append(letter);
        }
        if(!shuffled.toString().equals(target)){
            isSame=false;
        }
//            int numDiff = 0;
//            for (int i = 0; i < target.length(); i++) {
//                char tmpChar = target.charAt(i),
//                        shuffledChar = shuffled.charAt(i);
//                if (tmpChar != shuffledChar) {
//                    numDiff++;
//                }
//            }
//            if (numDiff < target.length()) {
//                isSame = true;
//            } else if (numDiff == target.length()) {
//                isSame = false;
//            }
        }
        return shuffled;
    }

}
