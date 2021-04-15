package no.hyp.farmingupgrade;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.advancedkind.plugin.utils.container.ItemDataContainer;
import no.hyp.farmingupgrade.container.FarmItemDataContainer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("farmingupgrade|farmup")
@CommandPermission("farmingupgrade.command")
public class FarmingUpgradeCommand extends BaseCommand {
    private final FarmingUpgrade plugin;

    public FarmingUpgradeCommand(FarmingUpgrade farmingUpgrade) {
        this.plugin = farmingUpgrade;
    }

    @Subcommand("reload")
    @CommandPermission("farmingupgrade.command.reload")
    public void onReload(CommandSender sender) {
        sender.sendMessage("Reloading");
        plugin.reloadConfig();
        sender.sendMessage("Reloaded");
    }

    @Subcommand("get")
    @CommandPermission("farmingupgrade.command.reload")
    @CommandCompletion("@farmtools")
    public void onReload(Player player, @Values("@farmtools") String itemKey) {
        player.sendMessage(itemKey);
        ItemDataContainer itemData = new FarmItemDataContainer(plugin.getConfig().getConfigurationSection("tools." + itemKey));
        ItemStack item = itemData.createItemStack();
        player.getInventory().addItem(item);
    }
}
