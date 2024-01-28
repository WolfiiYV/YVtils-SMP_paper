package yv.tils.smp.manager.startup

import dev.jorel.commandapi.CommandAPI
import yv.tils.smp.manager.commands.*

class Commands {
    fun unregisterCommands() {
        CommandAPI.unregister("gamemode", true);
        CommandAPI.unregister("seed", true);
    }

    fun registerCommands() {
        GamemodeCMD()
        FlyCMD()
        SpeedCMD()
        HealCMD()
        GlobalMuteCMD()
        MaintenanceCMD()
        SeedCMD()
    }
}