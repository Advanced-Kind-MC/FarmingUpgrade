package no.hyp.farmingupgrade;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import com.advancedkind.plugin.utils.container.ItemDataContainer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
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
        sender.sendMessage(plugin.messages.getChatMessage("commands.reload.start"));
        plugin.reloadConfig();
        sender.sendMessage(plugin.messages.getChatMessage("commands.reload.finish"));
    }

    @Subcommand("get")
    @CommandPermission("farmingupgrade.command.get")
    @CommandCompletion("@farmtools")
    public void onGet(Player player, @Values("@farmtools") String itemKey) {
        ItemDataContainer itemData = new FarmItemDataContainer(plugin.getConfig().getConfigurationSection("tools." + itemKey));
        ItemStack item = itemData.createItemStack();
        player.getInventory().addItem(item);

        Component component = plugin.messages.getChatMessage("commands.get");
        Component itemName =
                item.getItemMeta().hasDisplayName() ?
                        item.getItemMeta().displayName() :
                        item.getItemMeta().hasLocalizedName() ?
                                Component.text(item.getItemMeta().getLocalizedName()) :
                                Component.translatable(item.getType().getTranslationKey());
        itemName = itemName.hoverEvent(item.asHoverEvent());
        component = component.replaceText(TextReplacementConfig.builder().match("%item%").replacement(itemName).build());
        component = component.replaceText(TextReplacementConfig.builder().match("%key%").replacement(itemKey).build());
        player.sendMessage(component);
    }

    @Subcommand("give")
    @CommandPermission("farmingupgrade.command.give")
    @CommandCompletion("@players @farmtools")
    public void onGive(CommandSender sender, OnlinePlayer player, @Values("@farmtools") String itemKey) {
        Player receiver = player.getPlayer();
        ItemDataContainer itemData = new FarmItemDataContainer(plugin.getConfig().getConfigurationSection("tools." + itemKey));
        ItemStack item = itemData.createItemStack();
        receiver.getInventory().addItem(item);

        Component component = plugin.messages.getChatMessage("commands.give");
        Component playerName = receiver.displayName().hoverEvent(receiver.asHoverEvent());
        component = component.replaceText(TextReplacementConfig.builder().match("%player%").replacement(playerName).build());
        Component itemName =
                item.getItemMeta().hasDisplayName() ?
                        item.getItemMeta().displayName() :
                        item.getItemMeta().hasLocalizedName() ?
                                Component.text(item.getItemMeta().getLocalizedName()) :
                                Component.translatable(item.getType().getTranslationKey());
        itemName = itemName.hoverEvent(item.asHoverEvent());
        component = component.replaceText(TextReplacementConfig.builder().match("%item%").replacement(itemName).build());
        component = component.replaceText(TextReplacementConfig.builder().match("%key%").replacement(itemKey).build());
        sender.sendMessage(component);
    }
}
