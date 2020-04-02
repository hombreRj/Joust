package gg.scenarios.joust;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;


@Getter
public class Joust extends JavaPlugin {

    private static Joust instance;


    @Override
    public void onEnable(){
        instance = this;
    }
}
