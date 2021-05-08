package no.hyp.farmingupgrade.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import no.hyp.farmingupgrade.FarmingUpgrade;
import no.hyp.farmingupgrade.container.FarmItemDataContainer;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ToolDamageUtils {
    private static final NamespacedKey DURABILLITY_KEY = NamespacedKey.fromString("durability", JavaPlugin.getPlugin(FarmingUpgrade.class));

    public static int getFakeDurabilityChange(final @NotNull FarmingUpgrade plugin, final @NotNull ItemStack item, final int durabilityChange) {
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
            return -realDurability;
        } else if (fakeDurability >= data.durability) {
            setFakeDurabillity(item, data.durability, data.durability);
            return maxDurability - realDurability;
        } else {
            int shouldBeDurabillity = Math.max((int) Math.round(fakeDurability * durabilityScale), 1);
            return shouldBeDurabillity - realDurability;
        }
    }

    public static short getMaxDurability(ItemStack item) {
        return item.getType().getMaxDurability();
    }

    public static double getDurabilityScale(FarmItemDataContainer data, int maxDurabillity) {
        return maxDurabillity / (double) data.durability;
    }

    public static int getFakeDurability(final ItemStack item, double durabilityScale) {
        PersistentDataContainer persistentDataContainer = item.getItemMeta().getPersistentDataContainer();
        if (persistentDataContainer.has(DURABILLITY_KEY, PersistentDataType.INTEGER))
            return persistentDataContainer.get(DURABILLITY_KEY, PersistentDataType.INTEGER);
        return getFakeDurabilityFromRealDurability(item, durabilityScale);
    }

    public static int getFakeDurabilityFromRealDurability(ItemStack item, double durabilityScale) {
        int maxDurability = getMaxDurability(item);
        int realDurability = maxDurability - ((Damageable) item.getItemMeta()).getDamage();
        return (int) Math.round(realDurability / durabilityScale);
    }

    public static void setFakeDurabillity(final ItemStack item, int fakeDurabillity, int maxFakeDurabillity) {
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(DURABILLITY_KEY, PersistentDataType.INTEGER, fakeDurabillity);
        List<Component> lore = meta.lore();
        if (lore == null)
            lore = new ArrayList<>(1);
        else
            lore.removeIf(comp -> comp instanceof TextComponent && ((TextComponent) comp).content().startsWith("Durability: "));
        if (fakeDurabillity < maxFakeDurabillity)
            lore.add(Component.text(String.format("Durability: %s / %s", fakeDurabillity, maxFakeDurabillity)).color(NamedTextColor.WHITE).decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);
        item.setItemMeta(meta);
    }
}
