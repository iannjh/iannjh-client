package cn.iannjhclient

import cn.iannjhclient.command.CommandModuleManager
import cn.iannjhclient.config.ConfigManager
import cn.iannjhclient.event.EventManager

import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object iannjhclient : ModInitializer {
    private val logger = LoggerFactory.getLogger("iannjhclient")


    override fun onInitialize() {
        logger.info("iannjhclient Initializing...")
        EventManager.init()
        
        // 初始化配置管理器，加载或创建modules.yaml配置文件
        ConfigManager.init()

        ModuleManager.modules.find { it.name == "ToggleNotifications" }?.enable()

        CommandModuleManager.register()
    }
}
