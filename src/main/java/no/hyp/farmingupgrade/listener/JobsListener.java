package no.hyp.farmingupgrade.listener;

import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.container.ActionType;
import no.hyp.farmingupgrade.FarmingUpgrade;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;

import java.util.EnumSet;

public class JobsListener implements Listener {
    private final static EnumSet<Material> CROPS = EnumSet.of(
            Material.TUBE_CORAL_FAN,
            Material.BRAIN_CORAL_FAN,
            Material.BUBBLE_CORAL_FAN,
            Material.FIRE_CORAL_FAN,
            Material.HORN_CORAL_FAN,
            Material.KELP_PLANT,
            Material.KELP,
            Material.SEAGRASS,
            Material.TALL_SEAGRASS,
            Material.SEA_PICKLE,
            Material.CHORUS_PLANT,
            Material.CHORUS_FLOWER,
            Material.BEETROOTS,
            Material.WHEAT,
            Material.CARROTS,
            Material.POTATOES,
            Material.PUMPKIN,
            Material.MELON,
            Material.SUGAR_CANE,
            Material.COCOA,
            Material.LILY_PAD,
            Material.DANDELION,
            Material.POPPY,
            Material.BLUE_ORCHID,
            Material.ALLIUM,
            Material.AZURE_BLUET,
            Material.RED_TULIP,
            Material.ORANGE_TULIP,
            Material.WHITE_TULIP,
            Material.PINK_TULIP,
            Material.OXEYE_DAISY,
            Material.BROWN_MUSHROOM,
            Material.RED_MUSHROOM,
            Material.VINE,
            Material.CACTUS
    );

    private final static String CROP_MODIFIER_DAYLIGHT_MIN_STRENGTH = "crop-modifier.daylight-min-strength";
    private final static String CROP_MODIFIER_PAYMENT_DAYLIGHT = "crop-modifier.payment-daylight";
    private final static String CROP_MODIFIER_PAYMENT_DARKNESS = "crop-modifier.payment-darkness";
    private final static String CROP_MODIFIER_EXPERIENCE_DAYLIGHT = "crop-modifier.experience-daylight";
    private final static String CROP_MODIFIER_EXPERIENCE_DARKNESS = "crop-modifier.experience-darkness";

    private final FarmingUpgrade plugin;

    public JobsListener(FarmingUpgrade plugin) {
        this.plugin = plugin;
    }


    @EventHandler(ignoreCancelled = true)
    public void onJobPay(JobsPrePaymentEvent event) {
        if (event.getActionInfo().getType() != ActionType.BREAK) return;
        if (event.getBlock().hasMetadata(FarmingUpgrade.CROP_HARVEST_PAYMENT_MULTIPLIER)) {
            MetadataValue data = event.getBlock().getMetadata(FarmingUpgrade.CROP_HARVEST_PAYMENT_MULTIPLIER).stream().filter(m -> plugin.equals(m.getOwningPlugin())).findFirst().orElse(null);
            if (data != null) {
                event.setAmount(event.getAmount() * data.asDouble());
            }
        }
        if (event.getBlock().hasMetadata(FarmingUpgrade.CROP_HARVEST_EXPERIENCE_MULTIPLIER)) {
            MetadataValue data = event.getBlock().getMetadata(FarmingUpgrade.CROP_HARVEST_EXPERIENCE_MULTIPLIER).stream().filter(m -> plugin.equals(m.getOwningPlugin())).findFirst().orElse(null);
            if (data != null) {
                event.setPoints(event.getPoints() * data.asDouble());
            }
        }
        if (CROPS.contains(event.getBlock().getType())) {
            if (event.getBlock().getLightFromSky() >= plugin.getConfig().getInt(CROP_MODIFIER_DAYLIGHT_MIN_STRENGTH)) {
                event.setAmount(event.getAmount() * plugin.getConfig().getDouble(CROP_MODIFIER_PAYMENT_DAYLIGHT,1));
                event.setPoints(event.getPoints() * plugin.getConfig().getDouble(CROP_MODIFIER_EXPERIENCE_DAYLIGHT,1));
            } else {
                event.setAmount(event.getAmount() * plugin.getConfig().getDouble(CROP_MODIFIER_PAYMENT_DARKNESS,1));
                event.setPoints(event.getPoints() * plugin.getConfig().getDouble(CROP_MODIFIER_EXPERIENCE_DARKNESS,1));
            }
        }
    }
}
