package net.frostlightgames.lobbyPlugin.Functions;

import net.frostlightgames.lobbyPlugin.LobbyPlugin;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.*;
import java.util.Arrays;
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
                double startColorHSV[] = new double[3];
                startColorHSV[0] = 134;
                startColorHSV[1] = 100;
                startColorHSV[2] = 69;
                spawnParticles(location, startColorHSV);
            }

        } catch (IOException | ParseException e){
            Bukkit.getLogger().log(Level.INFO,"Fehler beim erzeugen der Pixel bei Structure Void Blöcken: " + e.toString());
        }
    }

    public void spawnParticles(Location location, double[] startColorHSV) {
        // Partikel mit Dust
        Random random = new Random();
        int color[] = new int[3];
        color = colorChangeHsvToRgb( startColorHSV[0],startColorHSV[1], startColorHSV[2], color);
        startColorHSV[0] = (startColorHSV[0]+1)%360;

        Location newLocation = new Location(Bukkit.getServer().getWorld("world"),location.x() + random.nextGaussian()*0.4-0.2,location.y() + random.nextGaussian()*0.8,location.z() + random.nextGaussian()*0.4-0.2);
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.fromRGB(color[0], color[1], color[2]), 0.6f);
        Bukkit.getServer().getWorld("world").spawnParticle(Particle.DUST, newLocation, 10, dustOptions);

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {spawnParticles(location, startColorHSV);}, 2);
    }

    public int[] colorChangeHsvToRgb(double hue, double saturation, double value, int[] color){
        double normalizeSaturation;
        double normalizeValue;

        if (saturation > 1){
            normalizeSaturation = saturation/100;
        } else {
            normalizeSaturation = saturation;
        }
        if (value > 1){
            normalizeValue = value/100;
        } else {
            normalizeValue = value;
        }

        double delta = normalizeSaturation * normalizeValue;
        double minRGB = normalizeValue - delta;

        double x = delta * ( 1 - Math.abs((hue/60)%2-1));
        double r,g,b;
        if ( hue >= 0 && hue < 60){
            r = delta;
            g = x;
            b = 0;
        } else if ( hue >= 60 && hue <120){
            r = x;
            g = delta;
            b = 0;
        } else if ( hue >= 120 && hue <180){
            r = 0;
            g = delta;
            b = x;
        } else if ( hue >= 180 && hue <240){
            r = 0;
            g = x;
            b = delta;
        } else if ( hue >= 240 && hue <300){
            r = x;
            g = 0;
            b = delta;
        } else {
            r = delta;
            g = 0;
            b = x;
        }
        color[0] = (int) Math.round((r + minRGB) *255);
        color[1] = (int) Math.round(( g + minRGB) *255);
        color[2] = (int) Math.round(( b + minRGB) *255);


        return color;
    }
}