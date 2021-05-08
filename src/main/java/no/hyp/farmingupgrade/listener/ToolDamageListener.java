package no.hyp.farmingupgrade.listener;

import no.hyp.farmingupgrade.FarmingUpgrade;
import no.hyp.farmingupgrade.container.FarmItemDataContainer;
import no.hyp.farmingupgrade.utils.ToolDamageUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class ToolDamageListener implements Listener {
    //private final NamespacedKey durabillityKey;
    private final FarmingUpgrade plugin;

    public ToolDamageListener(final FarmingUpgrade plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnvilPrepare(final PrepareAnvilEvent event) {
        final ItemStack input = event.getInventory().getFirstItem();
        final ItemStack output = event.getResult();
        if (input == null || !(input.getItemMeta() instanceof Damageable) || output == null || !output.getType().isItem())
            return;
        FarmItemDataContainer data = plugin.getTools().get(input);
        if (data == null || data.durability == null) return;
        double durabilityScale = ToolDamageUtils.getDurabilityScale(data, ToolDamageUtils.getMaxDurability(input));
        int fakeDurability = ToolDamageUtils.getFakeDurabilityFromRealDurability(output, durabilityScale);
        ToolDamageUtils.setFakeDurabillity(output, fakeDurability, data.durability);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onToolDamage(final PlayerItemDamageEvent event) {
        if (!(event.getItem().getItemMeta() instanceof Damageable)) return;
        event.setDamage(-ToolDamageUtils.getFakeDurabilityChange(plugin, event.getItem(), -event.getDamage()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onToolDamage(final PlayerItemMendEvent event) {
        if (!(event.getItem().getItemMeta() instanceof Damageable)) return;
        event.setRepairAmount(ToolDamageUtils.getFakeDurabilityChange(plugin, event.getItem(), event.getRepairAmount()));
    }


}
