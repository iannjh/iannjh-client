package cn.iannjhclient.command.commands

import cn.iannjhclient.command.Command
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

object SayCommand: Command("say", arrayOf("No"))
{
    override fun run(args: Array<out String>) {
        MinecraftClient.getInstance().player!!.sendMessage(Text.of("ssss"))
    }
}