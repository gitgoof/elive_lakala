package com.lakala.shoudan.activity.loan;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lakala.shoudan.R;
import com.sensetime.library.finance.liveness.NativeComplexity;
import com.sensetime.library.finance.liveness.NativeMotion;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created on 2016/10/18 17:34.
 *
 * @author Han Xu
 */
public enum Settings {
    INSTANCE;

    public int getDifficulty(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String difficultyString = preferences.getString(Constants.COMPLEXITY, "Normal");
        if ("Easy".equals(difficultyString)) {
            return NativeComplexity.WRAPPER_COMPLEXITY_EASY;
        } else if ("Normal".equals(difficultyString)) {
            return NativeComplexity.WRAPPER_COMPLEXITY_NORMAL;
        } else if ("Hard".equals(difficultyString)) {
            return NativeComplexity.WRAPPER_COMPLEXITY_HARD;
        } else if ("Hell".equals(difficultyString)) {
            return NativeComplexity.WRAPPER_COMPLEXITY_HELL;
        }
        return -1;
    }

    public int[] getSequencesInt(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constants.DETECTLIST, MODE_PRIVATE);
        String input = preferences.getString(Constants.DETECTLIST, Constants.DEFAULTDETECTLIST);
        String[] sequencesString = input.split("\\s+");
        int[] sequences = new int[sequencesString.length];
        for (int i = 0; i < sequencesString.length; i ++) {
            if ("BLINK".equals(sequencesString[i])) {
                sequences[i] = NativeMotion.CV_LIVENESS_BLINK;
            } else if ("MOUTH".equals(sequencesString[i])) {
                sequences[i] = NativeMotion.CV_LIVENESS_MOUTH;
            } else if ("YAW".equals(sequencesString[i])) {
                sequences[i] = NativeMotion.CV_LIVENESS_HEADYAW;
            } else if ("NOD".equals(sequencesString[i])) {
                sequences[i] = NativeMotion.CV_LIVENESS_HEADNOD;
            }
        }
        return sequences;
    }
}