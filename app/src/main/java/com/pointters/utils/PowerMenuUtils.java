package com.pointters.utils;

import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.pointters.R;
import com.skydoves.powermenu.CustomPowerMenu;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.OnMenuItemClickListener;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

/**
 * Developed by skydoves on 2017-10-29.
 * Copyright (c) 2017 skydoves rights reserved.
 */

public class PowerMenuUtils {

    public static PowerMenu getHamburgerPowerMenu(Context context, LifecycleOwner lifecycleOwner, OnMenuItemClickListener onMenuItemClickListener) {
        return new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("Showing All Transactions", true))
                .addItem(new PowerMenuItem("Purchases", false))
                .addItem(new PowerMenuItem("Sales Earning", true))
                .addItem(new PowerMenuItem("Active Order Purchases", false))
                .addItem(new PowerMenuItem("Completed Order Purchases", false))
                .addItem(new PowerMenuItem("Personal Balance", false))
                .setLifecycleOwner(lifecycleOwner)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setWith(800)
                .setTextColor(context.getResources().getColor(R.color.card_title_text_color))
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedEffect(true)
                .setSelectedMenuColor(context.getResources().getColor(R.color.colorAccent))
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build();
    }

    public static PowerMenu getProfilePowerMenu(Context context, LifecycleOwner lifecycleOwner, OnMenuItemClickListener onMenuItemClickListener) {
        return new PowerMenu.Builder(context)
                .addItem(new PowerMenuItem("Profile", false))
                .addItem(new PowerMenuItem("Board", false))
                .addItem(new PowerMenuItem("Logout", false))
                .setLifecycleOwner(lifecycleOwner)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setMenuRadius(10f)
                .setMenuShadow(10f)
                .setTextColor(context.getResources().getColor(R.color.card_title_text_color))
                .setMenuColor(Color.WHITE)
                .setSelectedEffect(false)
                .setOnMenuItemClickListener(onMenuItemClickListener)
                .build();
    }

//    public static CustomPowerMenu getWritePowerMenu(Context context, LifecycleOwner lifecycleOwner, OnMenuItemClickListener onMenuItemClickListener) {
//        ColorDrawable drawable = new ColorDrawable(context.getResources().getColor(R.color.md_blue_grey_300));
//        return new CustomPowerMenu.Builder<>(context, new CenterMenuAdapter())
//                .addItem("Novel")
//                .addItem("Poetry")
//                .addItem("Art")
//                .addItem("Journals")
//                .addItem("Travel")
//                .setLifecycleOwner(lifecycleOwner)
//                .setAnimation(MenuAnimation.FADE)
//                .setMenuRadius(10f)
//                .setMenuShadow(10f)
//                .setDivider(drawable)
//                .setDividerHeight(1)
//                .setOnMenuItemClickListener(onMenuItemClickListener)
//                .build();
//    }
//
//    public static CustomPowerMenu getAlertPowerMenu(Context context, LifecycleOwner lifecycleOwner, OnMenuItemClickListener onMenuItemClickListener) {
//        return new CustomPowerMenu.Builder<>(context, new CenterMenuAdapter())
//                .addItem("You need to login!")
//                .setLifecycleOwner(lifecycleOwner)
//                .setAnimation(MenuAnimation.ELASTIC_CENTER)
//                .setMenuRadius(10f)
//                .setMenuShadow(10f)
//                .setFocusable(true)
//                .setOnMenuItemClickListener(onMenuItemClickListener)
//                .setOnBackgroundClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                    }
//                }).build();
//    }

//    public static CustomPowerMenu getIconPowerMenu(Context context, LifecycleOwner lifecycleOwner, OnMenuItemClickListener onMenuItemClickListener) {
//        return new CustomPowerMenu.Builder<>(context, new IconMenuAdapter())
//                .addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.ic_wechat), "WeChat"))
//                .addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.ic_facebook), "Facebook"))
//                .addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.ic_twitter), "Twitter"))
//                .addItem(new IconPowerMenuItem(context.getResources().getDrawable(R.drawable.ic_line), "Line"))
//                .setLifecycleOwner(lifecycleOwner)
//                .setOnMenuItemClickListener(onMenuItemClickListener)
//                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
//                .setMenuRadius(10f)
//                .setMenuShadow(10f)
//                .build();
//    }
//
//    public static PowerMenu getDialogPowerMenu(Context context, LifecycleOwner lifecycleOwner) {
//        return new PowerMenu.Builder(context)
//                .setHeaderView(R.layout.layout_dialog_header)
//                .setFooterView(R.layout.layout_dialog_footer)
//                .addItem(new PowerMenuItem("This is DialogPowerMenu", false))
//                .setLifecycleOwner(lifecycleOwner)
//                .setAnimation(MenuAnimation.SHOW_UP_CENTER)
//                .setMenuRadius(10f)
//                .setMenuShadow(10f)
//                .setWith(600)
//                .setSelectedEffect(false)
//                .build();
//    }
//
//    public static CustomPowerMenu getCustomDialogPowerMenu(Context context, LifecycleOwner lifecycleOwner) {
//        return new CustomPowerMenu.Builder<>(context, new CustomDialogMenuAdapter())
//                .setHeaderView(R.layout.layout_custom_dialog_header)
//                .setFooterView(R.layout.layout_custom_dialog_footer)
//                .addItem(new NameCardMenuItem(context.getResources().getDrawable(R.drawable.face3), "Sophie", context.getString(R.string.board3)))
//                .setLifecycleOwner(lifecycleOwner)
//                .setAnimation(MenuAnimation.SHOW_UP_CENTER)
//                .setWith(800)
//                .setMenuRadius(10f)
//                .setMenuShadow(10f)
//                .build();
//    }
}
