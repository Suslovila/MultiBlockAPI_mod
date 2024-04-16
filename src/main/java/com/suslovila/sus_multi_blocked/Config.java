package com.suslovila.sus_multi_blocked;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

public class Config {
    public static boolean pvpLiteEnabled = false;
    public static int arenaWorldId;

    public static boolean consumeEldritchDiaryAfterUse;
    public static String structureOutputPath = "my_structure.json";


    public static void registerServerConfig(File modCfg) {
        Configuration cfg = new Configuration(modCfg);
        try {

            //EXAMPLES

            pvpLiteEnabled = cfg.getBoolean(
				"EnablePvPLite",
	            "PvPLite",
	            false,
	            "Включить ограничения для PvPLite мира");

            arenaWorldId = cfg.getInt(
				"PvPLiteWorldID",
	            "PvPLite",
	            666,
	            0,
	            Integer.MAX_VALUE,
	            "ID PvPLite Мира"
            );

            structureOutputPath = cfg.getString(
				"ModPrefix",
	            "core",
                    structureOutputPath,
	            "Путь файла структур"
            );
        } catch (Exception var8) {
	        System.out.println("what");
        } finally {
            cfg.save();
        }
    }
}
