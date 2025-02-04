package net.frostlightgames.lobbyPlugin;

import net.frostlightgames.lobbyPlugin.Functions.Functions;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

// ToDo
// [ ] Bei Serverstart position aller Structur_void Blöcken in eine JSON Datei laden ("config/position.json")
// [ ] Wenn key nicht existiert wird neuer eintrag hinzugefügt
// {
//    "20.0.10":"lobby",
// }

public final class LobbyPlugin extends JavaPlugin {


    @Override
    public void onEnable() {
        Bukkit.getLogger().log(Level.INFO,"Plugin Start");
        Functions functions = new Functions(this);
        functions.getStructureVoids();
        functions.setParticle();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
