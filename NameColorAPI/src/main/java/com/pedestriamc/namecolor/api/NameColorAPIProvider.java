package com.pedestriamc.namecolor.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

public final class NameColorAPIProvider {
    private static NameColorAPI api;

    private NameColorAPIProvider(){}

    public static void setInstance(NameColorAPI nameColorAPI, Plugin plugin) throws SecurityException{
        if(!NameColorAPIProvider.class.getClassLoader().equals(nameColorAPI.getClass().getClassLoader())){
            throw new SecurityException("Unauthorized attempt to load NameColor API.");
        }
        if(api == null){
            api = nameColorAPI;
        }
        Bukkit.getServer().getServicesManager().register(NameColorAPI.class, api, plugin, ServicePriority.Highest);
    }

    public static NameColorAPI get(){
        if(api == null){
            throw new IllegalStateException("NameColor API provider not initialized.");
        }
        return api;
    }

}
