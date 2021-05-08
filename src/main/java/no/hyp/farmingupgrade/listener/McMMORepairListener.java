package no.hyp.farmingupgrade.listener;

import com.gmail.nossr50.events.skills.repair.McMMOPlayerRepairCheckEvent;
import no.hyp.farmingupgrade.FarmingUpgrade;
import no.hyp.farmingupgrade.container.FarmItemDataContainer;
import no.hyp.farmingupgrade.utils.ToolDamageUtils;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class McMMORepairListener implements Listener {
    private final FarmingUpgrade plugin;

    public McMMORepairListener(final FarmingUpgrade plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMcMMORepair(McMMOPlayerRepairCheckEvent event) {
        ItemStack item = event.getRepairedObject();
        FarmItemDataContainer data = plugin.getTools().get(item);
        if (data == null || data.durability == null) return;
        int maxDurability = ToolDamageUtils.getMaxDurability(item);
        double durabilityScale = ToolDamageUtils.getDurabilityScale(data, maxDurability);
        Bukkit.getScheduler().runTaskLater(plugin, () -> ToolDamageUtils.setFakeDurabillity(item, ToolDamageUtils.getFakeDurabilityFromRealDurability(item, durabilityScale), data.durability), 1);
    }
}
