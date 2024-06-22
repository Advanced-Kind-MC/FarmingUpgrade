package no.hyp.farmingupgrade.container;

import com.advancedkind.plugin.utils.container.ItemDataContainer;
import com.advancedkind.plugin.utils.utils.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FarmItemDataContainer extends ItemDataContainer {
    public final Integer range;
    public final int rangeDelay;
    public final double paymentMultiplier;
    public final double experienceMultiplier;
    public final Integer durability;

    public FarmItemDataContainer(@NotNull ConfigurationSection config) {
        super(config);
        this.range = ConfigUtils.getInteger(config, "aoe");
        this.durability = ConfigUtils.getInteger(config, "durability");
        this.paymentMultiplier = config.getDouble("payment-multiplier", 1);
        this.experienceMultiplier = config.getDouble("experience-multiplier", 1);
        Double delay = ConfigUtils.getDouble(config, "delay");
        if (delay != null)
            this.rangeDelay = (int) Math.round(delay * 20);
        else
            this.rangeDelay = 0;
    }

    public int getRange(Player player, ItemStack item) {
        if (range == null || player.hasCooldown(item.getType())) return 0;

        player.setCooldown(item.getType(), rangeDelay);
        return range;
    }

    @Override
    public void log(JavaPlugin plugin) {
        super.log(plugin);
        if(range != null) {
            plugin.getLogger().info("aoe range: " + range);
            plugin.getLogger().info("aoe delay: " + rangeDelay);
        }
        if(durability != null)
            plugin.getLogger().info("durability: " + durability);
        plugin.getLogger().info("payment multiplier: " + paymentMultiplier);
    }
}
