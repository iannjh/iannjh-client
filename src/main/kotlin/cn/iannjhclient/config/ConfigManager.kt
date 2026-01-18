
package cn.iannjhclient.config

import cn.iannjhclient.Module
import cn.iannjhclient.ModuleManager
import cn.iannjhclient.value.*
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * 配置管理器，负责管理所有模块的配置
 * 自动生成YAML配置文件并加载配置更改
 */
object ConfigManager {
    private const val CONFIG_DIR = "config/iannjhclient"
    private const val MODULES_CONFIG_FILE = "modules.yaml"

    private val yaml: Yaml by lazy {
        val options = DumperOptions()
        options.defaultFlowStyle = DumperOptions.FlowStyle.BLOCK
        options.isPrettyFlow = true
        Yaml(options)
    }

    private val configDir: Path by lazy {
        val path = Paths.get(CONFIG_DIR)
        if (!Files.exists(path)) {
            Files.createDirectories(path)
        }
        path
    }

    private val configFile: File by lazy {
        configDir.resolve(MODULES_CONFIG_FILE).toFile()
    }

    /**
     * 初始化配置管理器
     */
    fun init() {
        // 如果配置文件不存在，则生成默认配置
        if (!configFile.exists()) {
            generateDefaultConfig()
        } else {
            // 加载现有配置
            loadConfig()
        }
    }

    /**
     * 生成默认配置文件
     */
    fun generateDefaultConfig() {
        val configData = LinkedHashMap<String, Any>()

        ModuleManager.modules.forEach { module ->
            val moduleData = LinkedHashMap<String, Any>()

            // 添加模块基本信息
            moduleData["enabled"] = module.enabled
            moduleData["key"] = module.key
            moduleData["description"] = module.description
            moduleData["category"] = module.category.name

            // 添加模块的设置
            val settingsData = LinkedHashMap<String, Any>()
            module.settings.forEach { setting ->
                when (setting) {
                    is BooleanSetting -> settingsData[setting.name] = setting.value
                    is IntSetting -> {
                        val settingData = LinkedHashMap<String, Any>()
                        settingData["value"] = setting.value
                        settingData["min"] = setting.min
                        settingData["max"] = setting.max
                        settingData["step"] = setting.step
                        settingsData[setting.name] = settingData
                    }
                    is FloatSetting -> {
                        val settingData = LinkedHashMap<String, Any>()
                        settingData["value"] = setting.value
                        settingData["min"] = setting.min
                        settingData["max"] = setting.max
                        settingData["step"] = setting.step
                        settingsData[setting.name] = settingData
                    }
                    is DoubleSetting -> {
                        val settingData = LinkedHashMap<String, Any>()
                        settingData["value"] = setting.value
                        settingData["min"] = setting.min
                        settingData["max"] = setting.max
                        settingData["step"] = setting.step
                        settingsData[setting.name] = settingData
                    }
                    is StringSetting -> settingsData[setting.name] = setting.value
                    is ModeSetting -> {
                        val settingData = LinkedHashMap<String, Any>()
                        settingData["value"] = setting.value
                        settingData["modes"] = setting.modes
                        settingsData[setting.name] = settingData
                    }
                }
            }

            moduleData["settings"] = settingsData
            configData[module.name] = moduleData
        }

        // 写入配置文件
        FileWriter(configFile).use { writer ->
            yaml.dump(configData, writer)
        }
    }

    /**
     * 加载配置文件并应用到模块
     */
    fun loadConfig() {
        if (!configFile.exists()) {
            return
        }

        try {
            FileInputStream(configFile).use { input ->
                val configData = yaml.load(input) as Map<String, Map<String, Any>>

                configData.forEach { (moduleName, moduleData) ->
                    val module = ModuleManager.modules.find { it.name == moduleName }
                    if (module != null) {
                        // 应用模块配置
                        moduleData["enabled"]?.let {
                            if (it is Boolean) {
                                if (it && !module.enabled) {
                                    module.enable()
                                } else if (!it && module.enabled) {
                                    module.disable()
                                }
                            }
                        }

                        moduleData["key"]?.let {
                            if (it is Int) {
                                module.key = it
                            }
                        }

                        // 应用模块设置
                        val settingsData = moduleData["settings"] as? Map<String, Any>
                        if (settingsData != null) {
                            settingsData.forEach { (settingName, settingValue) ->
                                val setting = module.settings.find { it.name == settingName }
                                if (setting != null) {
                                    when (setting) {
                                        is BooleanSetting -> {
                                            if (settingValue is Boolean) {
                                                setting.value = settingValue
                                            }
                                        }
                                        is IntSetting -> {
                                            if (settingValue is Map<*, *>) {
                                                val value = settingValue["value"]
                                                if (value is Int) {
                                                    setting.value = value
                                                }
                                            }
                                        }
                                        is FloatSetting -> {
                                            if (settingValue is Map<*, *>) {
                                                val value = settingValue["value"]
                                                if (value is Float) {
                                                    setting.value = value
                                                }
                                            }
                                        }
                                        is DoubleSetting -> {
                                            if (settingValue is Map<*, *>) {
                                                val value = settingValue["value"]
                                                if (value is Double) {
                                                    setting.value = value
                                                }
                                            }
                                        }
                                        is StringSetting -> {
                                            if (settingValue is String) {
                                                setting.value = settingValue
                                            }
                                        }
                                        is ModeSetting -> {
                                            if (settingValue is Map<*, *>) {
                                                val value = settingValue["value"]
                                                if (value is String) {
                                                    setting.value = value
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 保存当前模块配置到文件
     */
    fun saveConfig() {
        generateDefaultConfig()
    }
}
