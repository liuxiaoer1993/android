package com.ls.Util;

import java.text.DecimalFormat;

/**
 * Created by qwe on 2016/5/12.
 */
public class FormatUtil {

    private static DecimalFormat dcmFmt = new DecimalFormat("0.0");

    public static String doubleFormat(double data){
        return dcmFmt.format(data);
    }
}
