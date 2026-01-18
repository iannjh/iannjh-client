package cn.iannjhclient

import cn.iannjhclient.command.CommandModuleManager
import cn.iannjhclient.config.ConfigManager
import cn.iannjhclient.event.EventManager
import cn.iannjhclient.gui.ClickGui
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object iannjhclient : ModInitializer {
    private val logger = LoggerFactory.getLogger("iannjhclient")
    lateinit var clickGui: ClickGui

    override fun onInitialize() {
        logger.info("iannjhclient Initializing...")
        EventManager.init()

        ModuleManager.modules.find { it.name == "ToggleNotifications" }?.enable()
        
        clickGui = ClickGui()
        CommandModuleManager.register()
    }
}
