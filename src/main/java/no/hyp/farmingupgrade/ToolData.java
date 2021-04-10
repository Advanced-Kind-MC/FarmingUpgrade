package no.hyp.farmingupgrade;

import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ToolData implements ConfigurationSerializable {
    public final @Nullable Material material;
    public final @Nullable Integer customModel;

    public ToolData(@Nullable Material material, @Nullable Integer customModel) {
        this.material = material;
        this.customModel = customModel;
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> result = new HashMap<>();
        if (material != null) result.put("material", material);
        if (customModel != null) result.put("custom-model", customModel);
        return result;
    }

    public static ToolData deserialize(Map map) {
        Material material = Material.matchMaterial(map.get("material").toString());
        Integer customModel = (Integer) map.get("custom-model");
        return new ToolData(material, customModel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ToolData) {
            ToolData toolData = (ToolData) o;
            return material == toolData.material && Objects.equals(customModel, toolData.customModel);
        } else if (o instanceof ItemStack) {
            ItemStack itemStack = (ItemStack) o;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (material != null && material != itemStack.getType()) return false;
            if (customModel != null && (!itemMeta.hasCustomModelData() || customModel != itemMeta.getCustomModelData()))
                return false;
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, customModel);
    }
}
