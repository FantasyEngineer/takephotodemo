package com.cfg.takephotodemo;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;

/**
 * Created by cfg on 17-4-21.
 */

public class HString {

    public static SpannableString htmlToString(String s) {
        String rest = "";
        boolean contains = s.contains("<");
        if (contains) {//BAOHAN BIAOQIAN
            int first = s.indexOf('<');
            int second = s.indexOf('>');
            int third = s.lastIndexOf('<');
            int last = s.lastIndexOf('>');
            System.out.println(first);
            System.out.println(second);
            System.out.println(third);
            System.out.println(last);
            String yanse = s.substring(first + 1, second);
            String s1 = s.substring(0, first);
            String s2 = s.substring(second + 1, third);
            String s3 = s.substring(last + 1, s.length());
            System.out.println(s1);
            System.out.println(s2);
            System.out.println(yanse);
            System.out.println(s3);
            rest = s1 + s2 + s3;
            SpannableString msp = new SpannableString(rest);
            int colorvalue = Color.RED;
            if (yanse.equals("green")) {
                colorvalue = Color.parseColor(yanse);
            }
            //

            msp.setSpan(new ForegroundColorSpan(colorvalue), first, s2.length() + first, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  //设置背景色为青色

            return msp;
        } else {
            return new SpannableString(s);
        }

    }
}
