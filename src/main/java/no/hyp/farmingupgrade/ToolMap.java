package no.hyp.farmingupgrade;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ToolMap<K> {
    ArrayList<ToolData> tools = new ArrayList<>();
    ArrayList<K> values = new ArrayList<>();

    public int size() {
        return tools.size();
    }

    public boolean isEmpty() {
        return tools.isEmpty();
    }

    public boolean containsKey(Object key) {
        for (ToolData data : tools) if (data.equals(key)) return true;
        return false;
    }

    public boolean containsValue(Object value) {
        for (K v : values) if (v.equals(value)) return true;
        return false;
    }

    public K get(Object key) {
        for (int i = 0; i < size(); i++) if(tools.get(i).equals(key)) return values.get(i);
        return null;
    }

    @Nullable
    public Object put(ToolData key, K value) {
        int index = tools.indexOf(key);
        if (index == -1) {
            tools.add(key);
            values.add(value);
            return null;
        } else {
            K previous = values.get(index);
            values.set(index, value);
            return previous;
        }
    }

    public K remove(Object key) {
        int index = tools.indexOf(key);
        tools.remove(index);
        return values.remove(index);
    }

    public void clear() {
        tools.clear();
        values.clear();
    }

    @NotNull
    public List<ToolData> keySet() {
        return (List<ToolData>) tools.clone();
    }

    @NotNull
    public List<K> values() {
        return (List<K>) values.clone();
    }
}
