package com.pointters.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jkc on 3/22/18.
 */

public class DateTimeData {
    private static final Map<String, List<String>> datas = new LinkedHashMap<>();
    private static void init(){
        if (!datas.isEmpty()){
            return;
        }
        ArrayList<String> days = new ArrayList<>();
        ArrayList<String> weeks = new ArrayList<>();
        ArrayList<String> hours = new ArrayList<>();
        days.add("1");
        days.add("2");
        days.add("3");
        days.add("4");
        days.add("5");
        days.add("6");
        days.add("10");
        days.add("20");
        days.add("30");

        weeks.add("1");
        weeks.add("2");
        weeks.add("3");
        weeks.add("4");
        weeks.add("5");

        hours.add("1");
        hours.add("2");
        hours.add("3");
        hours.add("4");
        hours.add("5");
        hours.add("6");
        hours.add("7");
        hours.add("8");
        hours.add("9");
        hours.add("10");
        hours.add("11");
        hours.add("12");
        hours.add("13");
        hours.add("14");
        hours.add("15");
        hours.add("16");
        hours.add("17");
        hours.add("18");
        hours.add("19");
        hours.add("20");
        hours.add("22");
        hours.add("23");

        datas.put("hours", hours);
        datas.put("days", days);
        datas.put("weeks", weeks);

    }
    public static Map<String, List<String>> getAll(){
        init();
        return new HashMap<>(datas);
    }
}
