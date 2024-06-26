package no.hyp.farmingupgrade.listener;

import no.hyp.farmingupgrade.FarmingUpgrade;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;

public class HoeGroundListener implements Listener {
    private final static EnumSet<Material> hoeableBlocks = EnumSet.of(Material.GRASS_BLOCK, Material.DIRT, Material.GRASS_PATH);
    private final FarmingUpgrade plugin;

    public HoeGroundListener(FarmingUpgrade plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onHoeGround(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        Block block = event.getClickedBlock();
        ItemStack item = event.getItem();
        if (block == null || item == null) return;
        if (hoeableBlocks.contains(block.getType()) && plugin.getTools().contains(item))
            event.setCancelled(true);
    }
}
