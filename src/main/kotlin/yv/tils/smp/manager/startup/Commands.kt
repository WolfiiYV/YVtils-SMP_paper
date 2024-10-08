package yv.tils.smp.manager.startup

import dev.jorel.commandapi.CommandAPI
import yv.tils.smp.manager.commands.register.*
import yv.tils.smp.mods.admin.invSee.EcSee
import yv.tils.smp.mods.admin.invSee.InvSee
import yv.tils.smp.mods.admin.moderation.cmd.*
import yv.tils.smp.mods.admin.vanish.Vanish
import yv.tils.smp.mods.fusionCrafting.FusionOverview
import yv.tils.smp.mods.multiMine.BlockManage
import yv.tils.smp.mods.other.message.MSGCommand
import yv.tils.smp.mods.other.message.ReplyCommand
import yv.tils.smp.mods.server.maintenance.MaintenanceCMD
import yv.tils.smp.mods.sit.SitCommand
import yv.tils.smp.mods.status.StatusCommand
import yv.tils.smp.mods.waypoints.WaypointCommand

class Commands {
    fun unregisterCommands() {
        CommandAPI.unregister("gamemode")
        CommandAPI.unregister("seed")
        CommandAPI.unregister("ban")
        CommandAPI.unregister("pardon")
        CommandAPI.unregister("kick")
        CommandAPI.unregister("w")
        CommandAPI.unregister("whisper")
        CommandAPI.unregister("msg")
        CommandAPI.unregister("tell")
    }

    fun registerCommands() {
        GamemodeCMD()
        FlyCMD()
        SpeedCMD()
        HealCMD()
        GlobalMuteCMD()
        SeedCMD()
        GodCMD()

        modulesCommands()
    }

    private fun modulesCommands() {
        FusionOverview()

        StatusCommand()

        SitCommand()

        MaintenanceCMD()

        MSGCommand()
        ReplyCommand()

        Vanish()

        InvSee()
        EcSee()

        Kick()
        Ban()
        TempBan()
        Unban()
        Mute()
        TempMute()
        Unmute()

        BlockManage()

        WaypointCommand()
    }
}