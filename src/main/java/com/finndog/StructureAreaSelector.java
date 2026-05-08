package com.finndog;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StructureAreaSelector implements ModInitializer {
    public static final String MOD_ID = "structureareaselector";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("StructureAreaSelector initialized");
    }
}
