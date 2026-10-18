package titanmastery.client;

import net.minecraft.class_1921;
import net.minecraft.class_2960;
import net.minecraft.class_4597;
import net.minecraft.class_5617;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;
import titanmastery.TitanMastery;
import titanmastery.founding.FoundingTitanEntity;

/**
 * Renders the Founding Titan: a 4-block model drawn at 5x scale (20 blocks tall), with glowing veins and eyes.
 * Uses a culling render type so the Founder, inside the head, sees out through it.
 */
public class FoundingTitanRenderer extends GeoEntityRenderer<FoundingTitanEntity> {
    public FoundingTitanRenderer(class_5617.class_5618 ctx) {
        super(ctx, new DefaultedEntityGeoModel<>(class_2960.method_60655(TitanMastery.MOD_ID, "founding_titan"), true));
        withScale(5.0f);
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
        this.field_4673 = 3.0f; // shadow radius
    }

    @Override
    public class_1921 getRenderType(FoundingTitanEntity animatable, class_2960 texture, class_4597 buffers, float partialTick) {
        return class_1921.method_23576(texture); // entityCutout (back faces culled)
    }
}
