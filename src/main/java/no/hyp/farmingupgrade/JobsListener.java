package no.hyp.farmingupgrade;

import com.gamingmesh.jobs.api.JobsPrePaymentEvent;
import com.gamingmesh.jobs.container.ActionType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;

public class JobsListener implements Listener {
    private final FarmingUpgrade plugin;

    public JobsListener(FarmingUpgrade plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onJobPay(JobsPrePaymentEvent event) {
        if (event.getActionInfo().getType() != ActionType.BREAK
                || !event.getBlock().hasMetadata(FarmingUpgrade.ADJACENT_HARVESTED_CROP))
            return;
        MetadataValue data = event.getBlock().getMetadata(FarmingUpgrade.ADJACENT_HARVESTED_CROP).stream().filter(m -> m.getOwningPlugin().equals(plugin)).findFirst().orElse(null);
        if (data != null) {
            event.setAmount(event.getAmount() * data.asDouble());
            event.setPoints(event.getPoints() * data.asDouble());
        }
    }
}
