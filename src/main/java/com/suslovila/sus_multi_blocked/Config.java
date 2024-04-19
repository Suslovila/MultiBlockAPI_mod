package com.suslovila.sus_multi_blocked;

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.nio.file.Paths;

public class Config {
    public static String structureOutputPath = "";

    public static void registerServerConfig(File modCfg) {
        Configuration cfg = new Configuration(modCfg);
        try {

            structureOutputPath = cfg.getString(
				"Structure default output path",
	            "core",
                    Paths.get(".").toAbsolutePath() + "/sus_multi_blocked/",
	            "the path is searched down from game folder"
            );
        } catch (Exception var8) {
	        System.out.println("error init config for mod: " + SusMultiBlocked.MOD_ID);
        } finally {
            cfg.save();
        }
    }
}
