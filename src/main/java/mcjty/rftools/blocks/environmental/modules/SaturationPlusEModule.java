package mcjty.rftools.blocks.environmental.modules;

import mcjty.rftools.PlayerBuff;
import mcjty.rftools.blocks.environmental.EnvironmentalConfiguration;

public class SaturationPlusEModule extends PotionEffectModule {

    public SaturationPlusEModule() {
        super("saturation", 2);
    }

    @Override
    public float getRfPerTick() {
        return (float) EnvironmentalConfiguration.SATURATIONPLUS_RFPERTICK.get();
    }

    @Override
    protected PlayerBuff getBuff() {
        return PlayerBuff.BUFF_SATURATIONPLUS;
    }
}
