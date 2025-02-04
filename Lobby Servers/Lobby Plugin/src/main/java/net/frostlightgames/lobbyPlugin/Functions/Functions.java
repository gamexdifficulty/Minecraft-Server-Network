package net.frostlightgames.lobbyPlugin.Functions;

import net.frostlightgames.lobbyPlugin.LobbyPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.bukkit.entity.*;
import org.bukkit.Particle.Trail;

import java.io.IOException;
import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

public class Functions {

    private LobbyPlugin plugin;

    public Functions(LobbyPlugin plugin) {this.plugin = plugin;}

    public void getStructureVoids() {
        World world = plugin.getServer().getWorld("world");
        JSONParser parser = new JSONParser();
        File file;
        String dateiName = "config/position.json";
        try {
            file = new File(dateiName);
            if (! file.exists()){
                file.createNewFile();
            }

            Object obj = parser.parse(new FileReader(dateiName));
            JSONObject json = (JSONObject) obj;

            for (double x = -30; x < 30; x++) {
                for (double z = -30; z < 30; z++) {
                    for (double y = 0; y < 30; y++) {
                        Location location = new Location(world, x, y, z);
                        Block block = world.getBlockAt(location);
                        if (block.getType().name().equals("STRUCTURE_VOID")){
                            String key = Double.toString(x) + "," + Double.toString(y) + "," + Double.toString(z);
                            json.putIfAbsent(key,"");
                        }
                    }
                }
            }
            BufferedWriter output = new BufferedWriter(new FileWriter(file));
            output.write(json.toJSONString());
            output.close();

        } catch (IOException | ParseException e){
            Bukkit.getLogger().log(Level.INFO,"Fehler beim lesen der Structure Void Blöcke " + e.toString());
            Bukkit.broadcastMessage("Fehler beim lesen der Structure Void Blöcke " + e.toString());
        }
    }



    public void setParticle(){
        try{
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(new FileReader("config/position.json"));
            JSONObject json = (JSONObject) obj;

            for (Object key : json.keySet()){
                String value = (String) json.get(key);
                String[] coord = ((String) key).split(",");
                Bukkit.getLogger().log(Level.INFO, Arrays.toString(coord));

                double x = Double.parseDouble(coord[0]) + 0.5;
                double y = Double.parseDouble(coord[1]) + 1.5;
                double z = Double.parseDouble(coord[2]) + 0.5;
                Location location = new Location(Bukkit.getServer().getWorld("world"),x,y,z);

                spawnParticles(location);
            }

        } catch (IOException | ParseException e){
            Bukkit.getLogger().log(Level.INFO,"Fehler beim erzeugen der Pixel bei Structure Void Blöcken: " + e.toString());
        }
    }

    public void spawnParticles(Location location) {
        //Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "particle minecraft:happy_villager" + location.x() + location.y() + location.z() + "0.5 1 0.5 0.1 1 normal");
        // Partikel mit Dust
        Random random = new Random();
        //Random yChange = new Random();
        //Random zChange = new Random();

        Location newLocation = new Location(Bukkit.getServer().getWorld("world"),location.x() + random.nextGaussian()*0.4-0.2,location.y() + random.nextGaussian()*0.8,location.z() + random.nextGaussian()*0.4-0.2);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(0, 177, 41), 0.6f);
        Bukkit.getServer().getWorld("world").spawnParticle(Particle.DUST, newLocation, 10, dustOptions);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {spawnParticles(location);}, 2);
    }

    public void colorChangeHsvToRgb(){

    }
}