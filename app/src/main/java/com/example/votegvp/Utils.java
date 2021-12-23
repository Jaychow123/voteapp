package com.example.votegvp;

import android.content.Context;

import java.io.File;

public class Utils {

    public static File getDpsFolder(Context context) {
        File folder = new File(context.getFilesDir(), "dps");
        if (!folder.exists()) folder.mkdir();
        return folder;
    }

    public static File getDpFile(Context context, long userId) {
        return new File(getDpsFolder(context), userId + ".png");
    }

}
