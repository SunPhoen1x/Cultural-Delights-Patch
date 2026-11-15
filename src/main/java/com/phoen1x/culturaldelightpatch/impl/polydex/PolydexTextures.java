package com.phoen1x.culturaldelightpatch.impl.polydex;

import net.minecraft.text.Text;

import static com.phoen1x.culturaldelightpatch.impl.res.UiResourceCreator.polydexBackground;

public class PolydexTextures {
    public static final Text OVEN;
    public static final Text VAT;

    public static void register() {
    }

    static {
        var t1 = polydexBackground("1");
        OVEN = t1.left();
        VAT = t1.right();
    }
}