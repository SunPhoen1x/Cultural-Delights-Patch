package com.phoen1x.culturaldelightpatch.impl.res;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.Pair;
import com.phoen1x.culturaldelightpatch.impl.CulturalDelightsPatch;
import eu.pb4.factorytools.api.util.LazyItemStack;
import eu.pb4.factorytools.api.virtualentity.ItemDisplayElementUtil;
import eu.pb4.polymer.resourcepack.api.AssetPaths;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import eu.pb4.polymer.resourcepack.api.ResourcePackBuilder;
import eu.pb4.sgui.api.elements.GuiElementBuilder;
import it.unimi.dsi.fastutil.chars.Char2IntMap;
import it.unimi.dsi.fastutil.chars.Char2IntOpenHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public class UiResourceCreator {
    public static final String MOD_ID = "culturaldelightspatch";
    public static final String BASE_MODEL = "minecraft:item/generated";
    public static final String X32_MODEL = MOD_ID + ":sgui/button_32";
    public static final String X32_VAT_MODEL = MOD_ID + ":sgui/button_32_vat";
    public static final String X32_OVEN_MODEL = MOD_ID + ":sgui/button_32_oven";
    public static final String X32_POLYDEX_MODEL = MOD_ID + ":sgui/button_polydex";

    public static final Style STYLE = Style.EMPTY.withColor(0xFFFFFF)
            .withFont(new StyleSpriteSource.Font(CulturalDelightsPatch.id("gui")));

    private static final String ITEM_TEMPLATE = """
            {
              "parent": "|BASE|",
              "textures": {
                "layer0": "|ID|"
              }
            }
            """.replace(" ", "").replace("\n", "");

    private static final String BUTTON_32_JSON = """
            {
              "parent": "minecraft:item/generated",
              "display": {
                "gui": {
                  "rotation": [ 0, 0, 0 ],
                  "translation": [ 0, 1.5, 0 ],
                  "scale": [ 2.9375, 2.9375, 2.9375 ]
                }
              }
            }
            """;

    private static final String BUTTON_32_VAT_JSON = """
            {
              "parent": "minecraft:item/generated",
              "display": {
                "gui": {
                  "rotation": [ 0, 0, 0 ],
                  "translation": [ -12, 0, 0 ],
                  "scale": [ 2.0, 2.0, 2.0 ]
                }
              }
            }
            """;

    private static final String BUTTON_32_OVEN_JSON = """
            {
              "parent": "minecraft:item/generated",
              "display": {
                "gui": {
                  "rotation": [ 0, 0, 0 ],
                  "translation": [ -6, 0, 0 ],
                  "scale": [ 2.0, 2.0, 2.0 ]
                }
              }
            }
            """;

    private static final String BUTTON_32_POLYDEX_JSON = """
            {
              "parent": "minecraft:item/generated",
              "display": {
                "gui": {
                  "rotation": [ 0, 0, 0 ],
                  "translation": [ 0, 0, 0 ],
                  "scale": [ 2.0, 2.0, 2.0 ]
                }
              }
            }
            """;

    private static final List<SlicedTexture> HORIZONTAL_PROGRESS = new ArrayList<>();
    private static final List<SimpleModel> SIMPLE_MODEL = new ArrayList<>();
    private static final Char2IntMap SPACES = new Char2IntOpenHashMap();
    private static final List<FontTexture> FONT_TEXTURES = new ArrayList<>();
    private static char character = 'a';

    private static final char CHEST_SPACE0 = character++;
    private static final char CHEST_SPACE1 = character++;

    private static Identifier elementPath(String path) {
        return CulturalDelightsPatch.id("sgui/elements/" + path);
    }

    public static Supplier<GuiElementBuilder> icon16(String path) {
        var model = genericIconRaw(Items.ALLIUM, path, BASE_MODEL);
        return () -> new GuiElementBuilder(model.get()).setName(Text.empty()).hideDefaultTooltip();
    }

    public static Supplier<GuiElementBuilder> icon32(String path) {
        var model = genericIconRaw(Items.ALLIUM, path, X32_MODEL);
        return () -> new GuiElementBuilder(model.get()).setName(Text.empty()).hideDefaultTooltip();
    }

    public static IntFunction<GuiElementBuilder> horizontalProgressVat(String path, int start, int stop, boolean reverse) {
        return horizontalProgressCustom(path, start, stop, reverse, X32_VAT_MODEL);
    }

    public static IntFunction<GuiElementBuilder> horizontalProgressOven(String path, int start, int stop, boolean reverse) {
        return horizontalProgressCustom(path, start, stop, reverse, X32_OVEN_MODEL);
    }

    public static Supplier<GuiElementBuilder> iconPolydex(String path) {
        var model = genericIconRaw(Items.ALLIUM, path, X32_POLYDEX_MODEL);
        return () -> new GuiElementBuilder(model.get()).setName(Text.empty()).hideDefaultTooltip();
    }

    private static IntFunction<GuiElementBuilder> horizontalProgressCustom(String path, int start, int stop, boolean reverse, String modelType) {
        var models = new LazyItemStack[stop - start];
        HORIZONTAL_PROGRESS.add(new SlicedTexture(path, start, stop, reverse));
        for (var i = start; i < stop; i++) {
            models[i - start] = genericIconRaw(Items.ALLIUM, "gen/" + path + "_" + i, modelType);
        }
        return (i) -> new GuiElementBuilder(models[i].get()).setName(Text.empty()).hideDefaultTooltip();
    }

    public record TextPair(Text left, Text right) {}

    public static TextPair polydexBackground(String path) {
        var c = character++;
        var d = character++;

        FONT_TEXTURES.add(new FontTexture(CulturalDelightsPatch.id("sgui/polydex/" + path), -4, 128, new char[][]{{c}, {d}}));

        return new TextPair(
                Text.literal(Character.toString(c)).setStyle(STYLE),
                Text.literal(Character.toString(d)).setStyle(STYLE)
        );
    }

    public static LazyItemStack genericIconRaw(Item item, String path, String base) {
        var texturePath = elementPath(path);
        SIMPLE_MODEL.add(new SimpleModel(texturePath, texturePath, base));
        return ItemDisplayElementUtil.getModel(texturePath);
    }

    public static Function<Text, Text> background(String path) {
        var builder = new StringBuilder().append(CHEST_SPACE0);
        var c = (character++);
        builder.append(c);
        builder.append(CHEST_SPACE1);

        FONT_TEXTURES.add(new FontTexture(CulturalDelightsPatch.id("sgui/" + path), 12, 256, new char[][]{{c}}));
        var baseText = Text.literal(builder.toString()).setStyle(STYLE);
        return (text) -> Text.empty().append(baseText).append(text);
    }

    public static void setup() {
        SPACES.put(CHEST_SPACE0, -8);
        SPACES.put(CHEST_SPACE1, -168);
        PolymerResourcePackUtils.RESOURCE_PACK_AFTER_INITIAL_CREATION_EVENT.register(UiResourceCreator::generateAssets);
    }

    private static byte[] findModTexture(ResourcePackBuilder builder, String... paths) {
        for (String path : paths) {
            byte[] data = builder.getDataOrSource(path);
            if (data != null) return data;
            for (var mod : FabricLoader.getInstance().getAllMods()) {
                for (Path root : mod.getRootPaths()) {
                    Path file = root.resolve(path);
                    if (Files.exists(file)) {
                        try {
                            return Files.readAllBytes(file);
                        } catch (Exception ignored) {}
                    }
                }
            }
        }
        return null;
    }

    private static void sliceModGuiSprites(ResourcePackBuilder builder) {
        try {
            if (builder.getData(AssetPaths.texture(MOD_ID, "sgui/elements/empty.png")) == null) {
                BufferedImage empty = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                writeImage(builder, AssetPaths.texture(MOD_ID, "sgui/elements/empty.png"), empty);
            }

            byte[] vatBgBytes = findModTexture(builder,
                    "assets/" + MOD_ID + "/textures/sgui/vat.png",
                    "assets/culturaldelights/textures/gui/vat_gui.png",
                    "assets/culturaldelights/textures/gui/container/vat_gui.png"
            );
            byte[] vatOrigBytes = findModTexture(builder,
                    "assets/culturaldelights/textures/gui/vat_gui.png",
                    "assets/culturaldelights/textures/gui/container/vat_gui.png"
            );

            if (vatBgBytes != null) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(vatBgBytes));
                BufferedImage cleanVat = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
                cleanVat.getGraphics().drawImage(src, 0, 0, 176, 166, 0, 0, 176, 166, null);
                writeImage(builder, "assets/" + MOD_ID + "/textures/sgui/vat.png", cleanVat);
            }

            if (vatOrigBytes != null) {
                BufferedImage vatSrc = ImageIO.read(new ByteArrayInputStream(vatOrigBytes));

                BufferedImage vatArrow;
                byte[] customVatArrow = builder.getDataOrSource(AssetPaths.texture(MOD_ID, "sgui/elements/progress_vat.png"));
                if (customVatArrow != null) {
                    vatArrow = ImageIO.read(new ByteArrayInputStream(customVatArrow));
                } else {
                    vatArrow = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                    vatArrow.getGraphics().drawImage(vatSrc, 4, 7, 28, 24, 176, 14, 200, 31, null);
                    writeImage(builder, AssetPaths.texture(MOD_ID, "sgui/elements/progress_vat.png"), vatArrow);
                }
                generateProgressFramesFor(builder, "progress_vat", vatArrow);

                if (builder.getDataOrSource(AssetPaths.texture(MOD_ID, "sgui/elements/temp_cold.png")) == null) {
                    extractThermometer(builder, vatSrc, 176, 31, "temp_cold");
                }
                if (builder.getDataOrSource(AssetPaths.texture(MOD_ID, "sgui/elements/temp_normal.png")) == null) {
                    extractThermometer(builder, vatSrc, 189, 31, "temp_normal");
                }
                if (builder.getDataOrSource(AssetPaths.texture(MOD_ID, "sgui/elements/temp_hot.png")) == null) {
                    extractThermometer(builder, vatSrc, 202, 31, "temp_hot");
                }
            }

            byte[] ovenBgBytes = findModTexture(builder,
                    "assets/" + MOD_ID + "/textures/sgui/oven.png",
                    "assets/culturaldelights/textures/gui/oven_gui.png",
                    "assets/culturaldelights/textures/gui/container/oven_gui.png"
            );
            byte[] ovenOrigBytes = findModTexture(builder,
                    "assets/culturaldelights/textures/gui/oven_gui.png",
                    "assets/culturaldelights/textures/gui/container/oven_gui.png"
            );

            if (ovenBgBytes != null) {
                BufferedImage src = ImageIO.read(new ByteArrayInputStream(ovenBgBytes));
                BufferedImage cleanOven = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
                cleanOven.getGraphics().drawImage(src, 0, 0, 176, 166, 0, 0, 176, 166, null);
                writeImage(builder, "assets/" + MOD_ID + "/textures/sgui/oven.png", cleanOven);
            }

            if (ovenOrigBytes != null) {
                BufferedImage ovenSrc = ImageIO.read(new ByteArrayInputStream(ovenOrigBytes));

                BufferedImage ovenArrow;
                byte[] customOvenArrow = builder.getDataOrSource(AssetPaths.texture(MOD_ID, "sgui/elements/progress_oven.png"));
                if (customOvenArrow != null) {
                    ovenArrow = ImageIO.read(new ByteArrayInputStream(customOvenArrow));
                } else {
                    ovenArrow = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                    ovenArrow.getGraphics().drawImage(ovenSrc, 4, 7, 28, 24, 176, 14, 200, 31, null);
                    writeImage(builder, AssetPaths.texture(MOD_ID, "sgui/elements/progress_oven.png"), ovenArrow);
                }
                generateProgressFramesFor(builder, "progress_oven", ovenArrow);

                if (builder.getDataOrSource(AssetPaths.texture(MOD_ID, "sgui/elements/heated.png")) == null) {
                    BufferedImage heated = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
                    heated.getGraphics().drawImage(ovenSrc, 0, 0, 16, 15, 176, 31, 192, 46, null);
                    writeImage(builder, AssetPaths.texture(MOD_ID, "sgui/elements/heated.png"), heated);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractThermometer(ResourcePackBuilder builder, BufferedImage vatSrc, int srcX, int srcY, String name) throws Exception {
        BufferedImage img = new BufferedImage(47, 47, BufferedImage.TYPE_INT_ARGB);
        img.getGraphics().drawImage(vatSrc, 17, 0, 30, 47, srcX, srcY, srcX + 13, srcY + 47, null);
        writeImage(builder, AssetPaths.texture(MOD_ID, "sgui/elements/" + name + ".png"), img);
    }

    private static void generateProgressFramesFor(ResourcePackBuilder builder, String targetPath, BufferedImage sourceImage) throws Exception {
        for (var pair : HORIZONTAL_PROGRESS) {
            if (!pair.path.equals(targetPath)) continue;

            var image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int height = image.getHeight();
            int mult = pair.reverse ? -1 : 1;
            int offset = pair.reverse ? pair.stop + pair.start - 1 : 0;

            for (int y = pair.start; y < pair.stop; y++) {
                var path = elementPath("gen/" + pair.path + "_" + y);
                int pos = offset + y * mult;
                if (pos >= 0 && pos < image.getWidth()) {
                    for (int x = 0; x < height; x++) {
                        image.setRGB(pos, x, sourceImage.getRGB(pos, x));
                    }
                }
                writeImage(builder, AssetPaths.texture(path.getNamespace(), path.getPath() + ".png"), image);
            }
        }
    }

    private static void writeImage(ResourcePackBuilder builder, String path, BufferedImage img) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(img, "png", out);
        builder.addData(path, out.toByteArray());
    }

    public static void generateAssets(ResourcePackBuilder builder) {
        builder.addData("assets/" + MOD_ID + "/models/sgui/button_32.json", BUTTON_32_JSON.getBytes(StandardCharsets.UTF_8));
        builder.addData("assets/" + MOD_ID + "/models/sgui/button_32_vat.json", BUTTON_32_VAT_JSON.getBytes(StandardCharsets.UTF_8));
        builder.addData("assets/" + MOD_ID + "/models/sgui/button_32_oven.json", BUTTON_32_OVEN_JSON.getBytes(StandardCharsets.UTF_8));
        builder.addData("assets/" + MOD_ID + "/models/sgui/button_polydex.json", BUTTON_32_POLYDEX_JSON.getBytes(StandardCharsets.UTF_8));
        sliceModGuiSprites(builder);

        for (var texture : SIMPLE_MODEL) {
            builder.addData("assets/" + texture.modelPath.getNamespace() + "/models/" + texture.modelPath.getPath() + ".json",
                    ITEM_TEMPLATE.replace("|ID|", texture.texturePath.toString()).replace("|BASE|", texture.base).getBytes(StandardCharsets.UTF_8));
        }

        var fontBase = new JsonObject();
        var providers = new JsonArray();

        var spaces = new JsonObject();
        spaces.addProperty("type", "space");
        var advances = new JsonObject();
        SPACES.char2IntEntrySet().stream()
                .sorted(Comparator.comparing(Char2IntMap.Entry::getCharKey))
                .forEach(c -> advances.addProperty(Character.toString(c.getCharKey()), c.getIntValue()));
        spaces.add("advances", advances);
        providers.add(spaces);

        FONT_TEXTURES.forEach(entry -> {
            var bitmap = new JsonObject();
            bitmap.addProperty("type", "bitmap");
            bitmap.addProperty("file", entry.path + ".png");
            bitmap.addProperty("ascent", entry.ascent);
            bitmap.addProperty("height", entry.height);
            var chars = new JsonArray();
            for (var a : entry.chars) {
                chars.add(new String(a));
            }
            bitmap.add("chars", chars);
            providers.add(bitmap);
        });

        fontBase.add("providers", providers);
        builder.addData("assets/" + MOD_ID + "/font/gui.json", fontBase.toString().getBytes(StandardCharsets.UTF_8));
    }

    public record SlicedTexture(String path, int start, int stop, boolean reverse) {}
    public record FontTexture(Identifier path, int ascent, int height, char[][] chars) {}
    public record SimpleModel(Identifier texturePath, Identifier modelPath, String base) {}
}