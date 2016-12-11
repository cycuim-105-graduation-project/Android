package com.androidapp.beconnect.beconnect;

import android.support.design.widget.CoordinatorLayout;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

import java.util.LinkedHashSet;
import java.util.Set;

import static android.R.id.list;

/**
 * Created by mitour on 2016/12/5.
 */

public class Values {

    // 接收儲存解碼完的 advertisedId
    public static Set<String> ID = new LinkedHashSet<>(list);

    public static Multimap<String, String> attachment = ArrayListMultimap.create();

    public static CoordinatorLayout container;

    // 報到
    public static String CheckInNode = "20:91:48:35:94:D5";
    public static boolean nodeInRange = false;
    public static boolean ifCheckIn[] = new boolean[2];
}
