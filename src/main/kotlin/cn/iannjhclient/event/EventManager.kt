package cn.iannjhclient.event

import cn.iannjhclient.ModuleManager.modules
import cn.iannjhclient.command.CommandManager
import cn.iannjhclient.event.events.ChatEvent
import cn.iannjhclient.event.events.DrawEvent
import cn.iannjhclient.event.events.KeyboardEvent
import cn.iannjhclient.event.events.TickEvent
import cn.iannjhclient.modules.TabGUI

object EventManager {
    fun init() {
        EventBus.register(this)
    }

    @Listener
    fun onTick(event: TickEvent) {
        modules
            .filter { it.enabled}
            .forEach { it.onTick() }
    }

    @Listener
    fun onKey(event: KeyboardEvent) {
        // 处理TabGUI的键盘事件
        TabGUI.onKey(event)

        // 处理模块切换的键盘事件
        modules.forEach {
            if(it.key == event.key) {
                it.toggle()
            }
        }
    }

    @Listener
    fun onDraw(event: DrawEvent) {
        modules
            .filter { it.enabled }
            .forEach {
                it.onDraw(event)
            }
    }

    @Listener
    fun onChat(event: ChatEvent) {
        if(event.content.startsWith(CommandManager.commandPrefix)) {
            event.cancelled = true
            if(event.content.length > 1) {
                val args = event.content.removePrefix(CommandManager.commandPrefix).split(" ")
                CommandManager.commands.forEach {
                    if(it.command == args[0]) {
                        it.run(args.drop(1).toTypedArray())
                    }
                }

            }
        }
    }

}