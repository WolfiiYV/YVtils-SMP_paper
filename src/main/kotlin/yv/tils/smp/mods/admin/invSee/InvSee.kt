package yv.tils.smp.mods.admin.invSee

import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.playerArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import yv.tils.smp.utils.color.ColorUtils
import yv.tils.smp.utils.configs.language.LangStrings
import yv.tils.smp.utils.configs.language.Language
import yv.tils.smp.utils.internalAPI.Placeholder
import java.util.*

class InvSee {
    companion object {
        var invSee: MutableMap<UUID, UUID> = HashMap()
    }

    val command = commandTree("invsee") {
        withPermission("yvtils.smp.command.invsee")
        withUsage("invsee <player>")
        withAliases("inv")

        playerArgument("player") {
            playerExecutor { player, args ->
                val target = args[0] as Player
                invSee[player.uniqueId] = target.uniqueId
                player.openInventory(getInv(target))
            }
        }
    }

    private fun getInv(target: Player): Inventory {
        val inv = Bukkit.createInventory(
            null, 54,
            Placeholder().replacer(
                Language().getMessage(
                    LangStrings.MODULE_INVSEE_INVENTORY
                ),
                listOf("player"),
                listOf(target.name)
            )
        )

        val armour = target.inventory.armorContents
        val invContent = target.inventory.contents
        val offhand = target.inventory.itemInOffHand

        var j = 3
        for (i in 1..4) {
            inv.setItem(i, armour[j])
            j--
        }

        inv.setItem(7, offhand)

        j = 0
        for (i in 18..53) {
            if (i < 45) {
                inv.setItem(i, invContent[i - 9])
            } else {
                inv.setItem(i, invContent[j])
                j++
            }
        }


        // Filler
        val filler = ItemStack(Material.GRAY_STAINED_GLASS_PANE)
        val fillerMeta = filler.itemMeta
        fillerMeta.displayName(ColorUtils().convert(" "))
        fillerMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        filler.itemMeta = fillerMeta

        inv.setItem(0, filler)
        inv.setItem(5, filler)
        inv.setItem(6, filler)

        for (i in 8..17) {
            inv.setItem(i, filler)
        }

        return inv
    }

    fun onFillerInteract(e: InventoryClickEvent) {
        val player = e.whoClicked

        val invsee_invRaw = Language().getRawMessage(LangStrings.MODULE_INVSEE_INVENTORY)
        val invsee_inv = ColorUtils().convert(invsee_invRaw).toString()

        if (player.openInventory.title().toString()
                .startsWith(invsee_inv.split("<")[0]) && e.inventory.location == null
        ) {
            if (e.slot == 0 || e.slot in 5..6 || e.slot in 8..17) e.isCancelled = true
        }
    }
}