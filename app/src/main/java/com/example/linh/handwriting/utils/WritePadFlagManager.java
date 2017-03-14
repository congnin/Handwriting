package com.example.linh.handwriting.utils;

import android.content.Context;

import com.example.linh.handwriting.MainSettings;
import com.example.linh.handwriting.recoInterface.WritePadAPI;
import com.example.linh.handwriting.utils.WritePadManager;

/**
 * Created by Linh on 3/8/2017.
 */

public class WritePadFlagManager {
    public static void initialize(Context context) {
        int flags = WritePadManager.recoGetFlags();
        flags = setRecoFlag(flags, false, WritePadAPI.FLAG_CORRECTOR);
        Context _context = context;

        flags = setRecoFlag(flags, MainSettings.isSeparateLetterModeEnabled(_context), WritePadAPI.FLAG_SEPLET);
        flags = setRecoFlag(flags, false, WritePadAPI.FLAG_ONLYDICT);
        flags = setRecoFlag(flags, MainSettings.isSingleWordEnabled(_context), WritePadAPI.FLAG_SINGLEWORDONLY);
        flags = setRecoFlag(flags, true, WritePadAPI.HW_SPELL_USERDICT);
        flags = setRecoFlag(flags, false, WritePadAPI.FLAG_NOSPACE);
        WritePadManager.recoSetFlags(flags);
    }

    public static int setRecoFlag(int flags, boolean value, int flag) {
        boolean isEnabled = 0 != (flags & flag);
        if (value && !isEnabled) {
            flags |= flag;
        } else if (!value && isEnabled) {
            flags &= ~flag;
        }
        return flags;
    }
}
