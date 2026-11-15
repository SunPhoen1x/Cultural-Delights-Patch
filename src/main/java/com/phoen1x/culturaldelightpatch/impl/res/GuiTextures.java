package com.phoen1x.culturaldelightpatch.impl.res;

import com.phoen1x.culturaldelightpatch.impl.polydex.PolydexCompat;
import com.phoen1x.culturaldelightpatch.impl.polydex.PolydexTextures;
import eu.pb4.sgui.api.elements.GuiElement;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import net.minecraft.text.Text;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import static com.phoen1x.culturaldelightpatch.impl.res.UiResourceCreator.*;

public class GuiTextures {
    public static final Function<Text, Text> VAT = background("vat");
    public static final Function<Text, Text> OVEN = background("oven");

    public static final Supplier<GuiElementBuilder> EMPTY_BUILDER = icon16("empty");

    public static final Supplier<GuiElementBuilder> HEATED = icon16("heated");
    public static final Supplier<GuiElementBuilder> TEMP_COLD = icon32("temp_cold");
    public static final Supplier<GuiElementBuilder> TEMP_NORMAL = icon32("temp_normal");
    public static final Supplier<GuiElementBuilder> TEMP_HOT = icon32("temp_hot");
    public static final Supplier<GuiElementBuilder> POLYDEX_BUTTON = iconPolydex("polydex");

    public static final Progress PROGRESS_VAT = Progress.createVat("progress_vat", 4, 28, false);
    public static final Progress PROGRESS_OVEN = Progress.createOven("progress_oven", 4, 28, false);

    public static void register() {
        PolydexTextures.register();
        PolydexCompat.register();
    }

    public record Progress(IntFunction<GuiElementBuilder> builderFactory, int size) {
        public GuiElement getCeil(float progress) {
            int index = Math.min((int) Math.ceil(progress * size), size);
            if (index <= 0) {
                return EMPTY_BUILDER.get().hideDefaultTooltip().build();
            }
            return builderFactory.apply(index - 1).hideDefaultTooltip().build();
        }

        public static Progress createVat(String path, int start, int stop, boolean reverse) {
            return new Progress(horizontalProgressVat(path, start, stop, reverse), stop - start);
        }

        public static Progress createOven(String path, int start, int stop, boolean reverse) {
            return new Progress(horizontalProgressOven(path, start, stop, reverse), stop - start);
        }
    }
}