package org.psyrioty.magicLeaders.Objects;

import org.bukkit.OfflinePlayer;

public class Placeholder {
    private String placeholder;
    private OfflinePlayer offlinePlayer;
    private double value;

    public Placeholder(
            String placeholder,
            OfflinePlayer offlinePlayer,
            double value
    ){
        this.placeholder = placeholder;
        this.offlinePlayer = offlinePlayer;
        this.value = value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }
}
