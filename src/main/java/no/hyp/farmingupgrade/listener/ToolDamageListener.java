package no.hyp.farmingupgrade.listener;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import no.hyp.farmingupgrade.FarmingUpgrade;
import no.hyp.farmingupgrade.container.FarmItemDataContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerItemMendEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ToolDamageListener implements Listener {
    private final NamespacedKey durabillityKey;
    private final FarmingUpgrade plugin;

    public ToolDamageListener(final FarmingUpgrade plugin) {
        this.plugin = plugin;
        durabillityKey = NamespacedKey.fromString("durability", plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onAnvilPrepare(final PrepareAnvilEvent event) {
        final ItemStack input = event.getInventory().getFirstItem();
        final ItemStack output = event.getResult();
        if (input == null || !(input.getItemMeta() instanceof Damageable) || output == null || !output.getType().isItem())
            return;
        plugin.getLogger().info("Anvil items are valid");
        FarmItemDataContainer data = plugin.getTools().get(input);
        if (data == null || data.durability == null) return;
        plugin.getLogger().info("Data found");
        double durabilityScale = getDurabilityScale(data, getMaxDurability(input));
        plugin.getLogger().info("DurabilityScale: " + durabilityScale);
        plugin.getLogger().info("DurabilityScale: " + durabilityScale);
        int fakeDurability = getFakeDurabilityFromRealDurability(output, durabilityScale);
        plugin.getLogger().info("New Durability: " + fakeDurability);
        setFakeDurabillity(output, fakeDurability, data.durability);
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onToolDamage(final PlayerItemDamageEvent event) {
        if (!(event.getItem().getItemMeta() instanceof Damageable)) return;
        event.setDamage(-getFakeDurabilityChange(event.getItem(), -event.getDamage()));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onToolDamage(final PlayerItemMendEvent event) {
        if (!(event.getItem().getItemMeta() instanceof Damageable)) return;
        event.setRepairAmount(getFakeDurabilityChange(event.getItem(), event.getRepairAmount()));
    }

    private int getFakeDurabilityChange(final ItemStack item, final int durabilityChange) {
        FarmItemDataContainer data = plugin.getTools().get(item);
        if (data == null || data.durability == null) return durabilityChange;

        int maxDurability = getMaxDurability(item);
        int realDurability = maxDurability - ((Damageable) item.getItemMeta()).getDamage();

        double durabilityScale = getDurabilityScale(data, maxDurability);
        int fakeDurability = getFakeDurability(item, durabilityScale);

        fakeDurability += durabilityChange;
        setFakeDurabillity(item, fakeDurability, data.durability);
        if (fakeDurability <= 0) {
            setFakeDurabillity(item, 0, data.durability);
            return realDurability;
        } else if (fakeDurability >= data.durability) {
            setFakeDurabillity(item, data.durability, data.durability);
            return maxDurability - realDurability;
        } else {
            int shouldBeDurabillity = Math.max((int) Math.round(fakeDurability * durabilityScale), 1);
            return shouldBeDurabillity - realDurability;
        }
    }

    private short getMaxDurability(ItemStack item) {
        return item.getType().getMaxDurability();
    }

    private double getDurabilityScale(FarmItemDataContainer data, int maxDurabillity) {
        return maxDurabillity / (double) data.durability;
    }

    private int getFakeDurability(final ItemStack item, double durabilityScale) {
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        if (persistentDataContainer.has(durabillityKey, PersistentDataType.INTEGER))
            return persistentDataContainer.get(durabillityKey, PersistentDataType.INTEGER);
        return getFakeDurabilityFromRealDurability(item, durabilityScale);
    }

    private int getFakeDurabilityFromRealDurability(ItemStack item, double durabilityScale) {
        int maxDurability = getMaxDurability(item);
        int realDurability = maxDurability - ((Damageable) item.getItemMeta()).getDamage();
        return (int) Math.round(realDurability / durabilityScale);
    }

    private void setFakeDurabillity(final ItemStack item, int fakeDurabillity, int maxFakeDurabillity) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(durabillityKey, PersistentDataType.INTEGER, fakeDurabillity);
        List<Component> lore = meta.lore();
        if(lore == null)
            lore = new ArrayList<>(1);
        lore.removeIf(comp -> comp instanceof TextComponent && ((TextComponent) comp).content().startsWith("Durability: "));
        if (fakeDurabillity < maxFakeDurabillity)
            lore.add(Component.text(String.format("Durability: %s / %s", fakeDurabillity, maxFakeDurabillity)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
    }
}
