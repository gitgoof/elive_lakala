package com.lakala.shoudan.util;

import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;

/**
 * Created by HUASHO on 2015/1/20.
 * 格式化输入卡号   XXXX-XXXX-XXXX-XXXX
 */
public class CardNumberHelper implements TextWatcher{
    private boolean mFormatting;
    private boolean mDeletingHyphen;
    private int mHyphenStart;
    private boolean mDeletingBackward;

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,int after) {
        // Check if the user is deleting a hyphen
        if (!mFormatting) {
            // Make sure user is deleting one char, without a selection
            final int selStart = Selection.getSelectionStart(s);
            final int selEnd = Selection.getSelectionEnd(s);
            if (s.length() > 1 // Can delete another character
                    && count == 1 // Deleting only one character
                    && after == 0 // Deleting
                    && s.charAt(start) == ' ' // a hyphen
                    && selStart == selEnd) { // no selection
                mDeletingHyphen = true;
                mHyphenStart = start;
                // Check if the user is deleting forward or backward
                if (selStart == start + 1) {
                    mDeletingBackward = true;
                } else {
                    mDeletingBackward = false;
                }
            } else {
                mDeletingHyphen = false;
            }
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable text) {
        // Make sure to ignore calls to afterTextChanged caused by the work done
        // below
        if (!mFormatting) {
            mFormatting = true;

            // If deleting the hyphen, also delete the char before or after that
            if (mDeletingHyphen && mHyphenStart > 0) {
                if (mDeletingBackward) {
                    if (mHyphenStart - 1 < text.length()) {
                        text.delete(mHyphenStart - 1, mHyphenStart);
                    }
                } else if (mHyphenStart < text.length()) {
                    text.delete(mHyphenStart, mHyphenStart + 1);
                }
            }
            formatNanpNumber(text);
            mFormatting = false;
        }
    }

    public void formatNanpNumber(Editable text) {
        int length = text.length();
        if (length <= 4) {
            // The string is too long to be formatted
            return;
        }

        // Strip the dashes first, as we're going to add them back
        int p = 0;
        while (p < text.length()) {
            if (text.charAt(p) == ' ') {
                text.delete(p, p + 1);
            } else {
                p++;
            }
        }
        length = text.length();

        // When scanning the number we record where dashes need to be added,
        // if they're non-0 at the end of the scan the dashes will be added in
        // the proper places.
        int dashPositions[] = new int[text.length() / 4];
        int numDashes = 0;

        for (int i = 0; i < length; i++) {
            if ((i+1)%4==0) {
                dashPositions[numDashes++] = i+1;
            }
        }

        // Actually put the dashes in place
        for (int i = 0; i < numDashes; i++) {
            int pos = dashPositions[i];
            text.replace(pos + i, pos + i, " ");
        }

        // Remove trailing dashes
        int len = text.length();
        while (len > 0) {
            if (text.charAt(len - 1) == ' ') {
                text.delete(len - 1, len);
                len--;
            } else {
                break;
            }
        }
    }
}
