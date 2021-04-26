package no.hyp.farmingupgrade.listener;

import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.container.ActionType;
import no.hyp.farmingupgrade.FarmingUpgrade;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;

public class JobsListener implements Listener {
    private final static String CROP_MODIFIER_DAYLIGHT_MIN_STRENGTH = "crop-payment-modifier.daylight-min-strength";
    private final static String CROP_MODIFIER_DAYLIGHT = "crop-payment-modifier.daylight";
    private final static String CROP_MODIFIER_DARKNESS = "crop-payment-modifier.darkness";

    private final FarmingUpgrade plugin;

    public JobsListener(FarmingUpgrade plugin) {
        this.plugin = plugin;
    }


    @EventHandler(ignoreCancelled = true)
    public void onJobPay(JobsPrePaymentEvent event) {
        if (event.getActionInfo().getType() != ActionType.BREAK) return;
        if(event.getBlock().hasMetadata(FarmingUpgrade.ADJACENT_HARVESTED_CROP)){
            MetadataValue data = event.getBlock().getMetadata(FarmingUpgrade.ADJACENT_HARVESTED_CROP).stream().filter(m -> plugin.equals(m.getOwningPlugin())).findFirst().orElse(null);
            if (data != null) {
                event.setAmount(event.getAmount() * data.asDouble());
                event.setPoints(event.getPoints() * data.asDouble());
            }
        }
        if(plugin.getHarvestableCrops().containsKey(event.getBlock().getType())) {
            if (event.getBlock().getLightFromSky() >= plugin.getConfig().getInt(CROP_MODIFIER_DAYLIGHT_MIN_STRENGTH)) {
                double daylightModifier = plugin.getConfig().getDouble(CROP_MODIFIER_DAYLIGHT);
                event.setAmount(event.getAmount() * daylightModifier);
                event.setPoints(event.getPoints() * daylightModifier);
            } else {
                double darknessModifier = plugin.getConfig().getDouble(CROP_MODIFIER_DARKNESS);
                event.setAmount(event.getAmount() * darknessModifier);
                event.setPoints(event.getPoints() * darknessModifier);
            }
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onJobPayCrop(JobsPrePaymentEvent event) {

    }
}
