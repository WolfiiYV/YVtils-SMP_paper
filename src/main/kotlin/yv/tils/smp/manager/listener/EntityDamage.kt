package yv.tils.smp.manager.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import yv.tils.smp.manager.commands.FlyCMD
import yv.tils.smp.manager.commands.GodCMD
import yv.tils.smp.mods.other.SpawnElytra

class EntityDamage : Listener {
    @EventHandler
    fun onEvent(e: EntityDamageEvent) {
        FlyCMD().onLandingDamage(e)
        GodCMD().onDamage(e)
        SpawnElytra.getInstance().onLandDamage(e)
    }
}