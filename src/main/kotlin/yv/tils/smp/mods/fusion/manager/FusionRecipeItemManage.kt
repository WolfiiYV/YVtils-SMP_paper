package yv.tils.smp.mods.fusionCrafting.manager

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.persistence.PersistentDataType
import yv.tils.smp.mods.fusionCrafting.FusionKeys
import yv.tils.smp.mods.fusionCrafting.enchantments.DataTags
import yv.tils.smp.utils.color.ColorUtils
import yv.tils.smp.utils.inventory.CustomHeads
import yv.tils.smp.utils.inventory.GUIFiller
import yv.tils.smp.utils.inventory.HeadUtils
import java.util.*

class FusionRecipeItemManage {
    companion object {
        val fusionRecipeItemEdit = mutableMapOf<UUID, FusionRecipeItem>()
    }

    data class FusionRecipeItem(
        var material: Material,
        var name: String,
        var oldName: String = "",
        var amount: Int,
        var lore: List<Component>,
        var data: MutableList<String>,
        val type: String
    )

    fun parseDataToMap(player: Player) {
        val fusionRecipe = fusionRecipeItemEdit[player.uniqueId] ?: return
        val fusion = FusionManagerGUI.playerManager[player.uniqueId] ?: return

        val fusionInv = fusion.fusionInv
        val fusionInvCopy = fusionInv.toMutableMap()

        when (fusionRecipe.type) {
            "input" -> {
                for (f in fusionInvCopy) {
                    val fKey = f.key
                    val fSplited = fKey.split(".")
                    val fKey0 = fSplited[0]

                    if (fKey0 != "input") {
                        continue
                    }

                    val fKey1 = fSplited[1]

                    if (fKey1 != ColorUtils().strip(fusionRecipe.oldName)) {
                        continue
                    }

                    val value = f.value as MutableList<MutableMap<String, Any>>

                    for (v in value) {
                        if (v.containsKey("amount")) {
                            v["amount"] = fusionRecipe.amount.toString()
                        }

                        if (v.equals("data")) {
                            v["data"] = fusionRecipe.data.joinToString { it }
                        }
                    }

                    if (fusionRecipe.oldName != fusionRecipe.name) {
                        val newKey = fKey0 + "." + ColorUtils().strip(fusionRecipe.name) + "." + fSplited[2]
                        fusionInv.remove(fKey)
                        fusionInv[newKey] = value
                    }
                }
            }
            "output" -> {
                for (f in fusionInvCopy) {
                    val key = f.key
                    val keySplit = key.split(".")
                    val key0 = keySplit[0]

                    if (key0 != "output") {
                        continue
                    }

                    val vList: MutableMap<String, Any> = mutableMapOf()

                    for (v in f.value as MutableList<MutableMap<String, Any>>) {
                        if (v.containsKey("name")) {
                            vList["name"] = v["name"].toString()
                        }

                        if (v.containsKey("item")) {
                            vList["item"] = v["item"].toString()
                        }

                        if (v.containsKey("amount")) {
                            vList["amount"] = v["amount"].toString()
                        }

                        if (v.containsKey("lore")) {
                            vList["lore"] = v["lore"] as String
                        }

                        if (v.containsKey("data")) {
                            vList["data"] = v["data"] as String
                        }
                    }

                    if (vList["name"] == fusionRecipe.oldName) {
                        val vMap = mutableListOf<MutableMap<String, Any>>()
                        vMap.add(mutableMapOf("item" to fusionRecipe.material.toString()))
                        vMap.add(mutableMapOf("amount" to fusionRecipe.amount.toString()))
                        vMap.add(mutableMapOf("name" to fusionRecipe.name))

                        var loreJoined = ""

                        for (line in fusionRecipe.lore) {
                            loreJoined += ColorUtils().strip(line)
                        }

                        vMap.add(mutableMapOf("lore" to loreJoined))
                        vMap.add(mutableMapOf("data" to fusionRecipe.data.joinToString(separator = ";") { it }))

                        fusionInv.remove(key)
                        fusionInv[key] = vMap
                    }
                }
            }
            else -> return
        }

        fusionRecipeItemEdit.remove(player.uniqueId)
    }

    fun openInventory(player: Player, item: ItemStack, type: String) {
        var inv = Bukkit.createInventory(null, 9*3, ColorUtils().convert("<gold>Edit Item"))

        val fusionItem = if (fusionRecipeItemEdit[player.uniqueId] == null) {
            parseItem(player, item, type)
        } else {
            fusionRecipeItemEdit[player.uniqueId] ?: return
        }

        inv = when (type) {
            "input" -> inputInventory(inv, fusionItem)
            "output" -> outputInventory(inv, fusionItem)
            else -> return
        }

        fusionRecipeItemEdit[player.uniqueId] = fusionItem
        player.openInventory(inv)
    }

    private fun inputInventory(inv: Inventory, fusion: FusionRecipeItem): Inventory {
        var inv = inv

        val acceptedItems = ItemStack(Material.BARREL)
        val acceptedItemsMeta = acceptedItems.itemMeta
        val acceptedItemsLore = mutableListOf<Component>()

        acceptedItemsMeta.displayName(ColorUtils().convert("<gold>Usable Items"))

        acceptedItemsLore.add(ColorUtils().convert(" "))
        acceptedItemsLore.add(ColorUtils().convert("<gray>Click to view"))

        acceptedItemsMeta.lore(acceptedItemsLore)
        acceptedItems.itemMeta = acceptedItemsMeta
        inv.setItem(11, acceptedItems)


        val displayName = ItemStack(Material.NAME_TAG)
        val displayNameMeta = displayName.itemMeta
        val displayNameContent = fusion.name
        val displayNameLore = mutableListOf<Component>()

        displayNameMeta.displayName(ColorUtils().convert("<gold>Display Name"))

        displayNameLore.add(ColorUtils().convert(" "))
        displayNameLore.add(ColorUtils().convert("<aqua>$displayNameContent"))

        displayNameMeta.lore(displayNameLore)
        displayName.itemMeta = displayNameMeta
        inv.setItem(12, displayName)


        val amountItem = ItemStack(Material.PAPER)
        val amountItemMeta = amountItem.itemMeta
        val amountItemLore = mutableListOf<Component>()

        amountItemMeta.displayName(ColorUtils().convert("<gold>Amount"))

        amountItemLore.add(ColorUtils().convert(" "))
        amountItemLore.add(ColorUtils().convert("<gray>Amount: <aqua>${fusion.amount}"))

        amountItemMeta.lore(amountItemLore)
        amountItem.itemMeta = amountItemMeta
        amountItem.amount = fusion.amount
        inv.setItem(13, amountItem)


        val tags = ItemStack(Material.BOOK)
        val tagsMeta = tags.itemMeta
        val tagsLore = mutableListOf<Component>()

        tagsMeta.displayName(ColorUtils().convert("<gold>Data Tags"))

        tagsLore.add(ColorUtils().convert(" "))
        for (tag in fusion.data) {
            tagsLore.add(ColorUtils().convert("<gray>$tag"))
        }

        tagsMeta.lore(tagsLore)
        tags.itemMeta = tagsMeta
        inv.setItem(14, tags)

        inv = generalContent(inv)

        return inv
    }

    private fun outputInventory(inv: Inventory, fusion: FusionRecipeItem): Inventory {
        var inv = inv

        val thumbnail = ItemStack(fusion.material)
        val displayMeta: ItemMeta = thumbnail.itemMeta
        displayMeta.displayName(ColorUtils().convert("<gold>Display Item"))

        thumbnail.itemMeta = displayMeta
        inv.setItem(11, thumbnail)


        val displayName = ItemStack(Material.NAME_TAG)
        val displayNameMeta = displayName.itemMeta
        val displayNameContent = fusion.name
        val displayNameLore = mutableListOf<Component>()

        displayNameMeta.displayName(ColorUtils().convert("<gold>Display Name"))

        displayNameLore.add(ColorUtils().convert(" "))
        displayNameLore.add(ColorUtils().convert("<aqua>$displayNameContent"))

        displayNameMeta.lore(displayNameLore)
        displayName.itemMeta = displayNameMeta
        inv.setItem(12, displayName)


        val amountItem = ItemStack(Material.PAPER)
        val amountItemMeta = amountItem.itemMeta
        val amountItemLore = mutableListOf<Component>()

        amountItemMeta.displayName(ColorUtils().convert("<gold>Amount"))

        amountItemLore.add(ColorUtils().convert(" "))
        amountItemLore.add(ColorUtils().convert("<gray>Amount: <aqua>${fusion.amount}"))

        amountItemMeta.lore(amountItemLore)
        amountItem.itemMeta = amountItemMeta
        amountItem.amount = fusion.amount
        inv.setItem(13, amountItem)

        val description = ItemStack(Material.MAP)
        val descriptionMeta = description.itemMeta
        val descriptionLore = mutableListOf<Component>()

        descriptionMeta.displayName(ColorUtils().convert("<gold>Item Lore"))

        descriptionLore.add(ColorUtils().convert(" "))
        for (descLine in fusion.lore) {
            descriptionLore.add(ColorUtils().convert("<white>${ColorUtils().convert(descLine)}"))
        }

        descriptionMeta.lore(descriptionLore)
        description.itemMeta = descriptionMeta
        inv.setItem(14, description)


        val tags = ItemStack(Material.BOOK)
        val tagsMeta = tags.itemMeta
        val tagsLore = mutableListOf<Component>()

        tagsMeta.displayName(ColorUtils().convert("<gold>Data Tags"))

        tagsLore.add(ColorUtils().convert(" "))
        for (tag in fusion.data) {
            tagsLore.add(ColorUtils().convert("<gray>$tag"))
        }

        tagsMeta.lore(tagsLore)
        tags.itemMeta = tagsMeta
        inv.setItem(15, tags)

        inv = generalContent(inv)

        return inv
    }

    private fun generalContent(inv: Inventory): Inventory {
        var inv = inv

        val back = ItemStack(Material.TIPPED_ARROW)
        val backMeta = back.itemMeta as PotionMeta
        backMeta.color = Color.fromRGB(150, 85, 95)
        backMeta.displayName(ColorUtils().convert("<red>Back"))
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        back.itemMeta = backMeta

        inv.setItem(18, back)

        inv = GUIFiller().fillInventory(inv)

        for (i in 0 until inv.size) {
            val item = inv.getItem(i) ?: continue
            item.itemMeta.persistentDataContainer.set(FusionKeys.FUSION_GUI.key, PersistentDataType.STRING, "fusion")
            inv.setItem(i, item)
        }

        return inv
    }

    fun editDisplayItem(player: Player) {
        val displayItem = fusionRecipeItemEdit[player.uniqueId] ?: return
        val item = ItemStack(displayItem.material)
        var inv = Bukkit.createInventory(null, 9, ColorUtils().convert("<gold>Edit Display Item"))

        inv.setItem(4, item)

        val accept = ItemStack(Material.LIME_STAINED_GLASS_PANE)
        val acceptMeta = accept.itemMeta
        acceptMeta.displayName(ColorUtils().convert("<green>Update DisplayItem"))
        acceptMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)

        accept.itemMeta = acceptMeta
        inv.setItem(0, accept)

        inv = GUIFiller().fillInventory(inv)

        player.openInventory(inv)
    }

    fun editAcceptedItems(player: Player) {
        val itemList = parseItemList(player)

        var inv = Bukkit.createInventory(null, 9*5, ColorUtils().convert("<gold>Modify accepted items"))

        val itemSlots = mutableListOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34)

        try {
            for (i in 0 until itemList.size) {
                inv.setItem(itemSlots[i], itemList[i])
            }
        } catch (_: IndexOutOfBoundsException) {
            player.sendMessage(ColorUtils().convert("<red>There was an error parsing the items (Probably too many items, max 21)"))
            return
        }

        val back = ItemStack(Material.TIPPED_ARROW)
        val backMeta = back.itemMeta as PotionMeta
        backMeta.color = Color.fromRGB(150, 85, 95)
        backMeta.displayName(ColorUtils().convert("<red>Back"))
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        back.itemMeta = backMeta
        inv.setItem(40, back)

        inv = GUIFiller().fillInventory(inv, itemSlots)

        player.openInventory(inv)
    }

    fun editDisplayName(player: Player) {
        FusionCraftManage.playerListen[player.uniqueId] = "fusionRecipeItemDisplayName"

        val name = fusionRecipeItemEdit[player.uniqueId]?.name ?: return

        player.sendMessage(ColorUtils().convert(
            "<gold>Editing Fusion Recipe Item Name<newline>" +
                    "<gray>Current Name: <white>${name}<newline>" +
                    "<red>'c' to cancel"
        ))

        player.closeInventory()
    }

    fun editAmount(player: Player, clickType: ClickType, item: ItemStack): ItemStack {
        FusionCraftManage.playerListen[player.uniqueId] = "fusionRecipeItemAmount"

        val amount = fusionRecipeItemEdit[player.uniqueId]?.amount ?: return item

        when (clickType) {
            ClickType.LEFT -> {
                if (amount + 1 > 64) {
                    fusionRecipeItemEdit[player.uniqueId]?.amount = 64
                    player.sendMessage(ColorUtils().convert("<red>Amount cannot be more than 64"))
                } else {
                    fusionRecipeItemEdit[player.uniqueId]?.amount = amount + 1
                }
            }
            ClickType.RIGHT -> {
                if (amount - 1 < 1) {
                    fusionRecipeItemEdit[player.uniqueId]?.amount = 1
                    player.sendMessage(ColorUtils().convert("<red>Amount cannot be less than 1"))
                } else {
                    fusionRecipeItemEdit[player.uniqueId]?.amount = amount - 1
                }
            }
            ClickType.SHIFT_LEFT -> {
                if (amount + 10 > 64) {
                    fusionRecipeItemEdit[player.uniqueId]?.amount = 64
                    player.sendMessage(ColorUtils().convert("<red>Amount cannot be more than 64"))
                } else {
                    fusionRecipeItemEdit[player.uniqueId]?.amount = amount + 10
                }
            }
            ClickType.SHIFT_RIGHT -> {
                if (amount - 10 < 1) {
                    fusionRecipeItemEdit[player.uniqueId]?.amount = 1
                    player.sendMessage(ColorUtils().convert("<red>Amount cannot be less than 1"))
                } else {
                    fusionRecipeItemEdit[player.uniqueId]?.amount = amount - 10
                }
            }
            else -> return item
        }

        item.amount = fusionRecipeItemEdit[player.uniqueId]?.amount ?: return item

        val meta = item.itemMeta
        val lore = meta.lore() ?: return item
        val newLore = mutableListOf<Component>()

        for (line in lore) {
            val stringLine = ColorUtils().convert(line)

            if (stringLine.contains("Amount:")) {
                newLore.add(ColorUtils().convert("<gray>Amount: <aqua>${fusionRecipeItemEdit[player.uniqueId]?.amount}"))
            } else {
                newLore.add(line)
            }
        }

        meta.lore(newLore)
        item.itemMeta = meta

        return item
    }

    fun editLore(player: Player) {
        FusionCraftManage.playerListen[player.uniqueId] = "fusionRecipeItemLore"

        val lore = fusionRecipeItemEdit[player.uniqueId]?.lore ?: return
        val stringLore = mutableListOf<String>()

        stringLore.add("<gray>---")

        for (line in lore) {
            stringLore.add(ColorUtils().convert(line))
        }

        stringLore.add("<gray>---")

        player.sendMessage(ColorUtils().convert(
            "<gold>Editing Fusion Recipe Item Lore<newline>" +
                    "<gray>Current Lore:<newline>" +
                    "<white>${stringLore.joinToString("<newline>")}<newline>" +
                    "<red>'c' to cancel"
        ))

        player.closeInventory()
    }

    fun editDataTags(player: Player) {
        val dataTags = fusionRecipeItemEdit[player.uniqueId]?.data ?: return
        var inv = Bukkit.createInventory(null, 9*4, ColorUtils().convert("<gold>Edit Data Tags"))

        val tagSlots = mutableListOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25) // Support for 14 tags

        for (i in 0 until dataTags.size) {
            val tag = ItemStack(Material.PAPER)
            val tagMeta = tag.itemMeta
            val tagLore = mutableListOf<Component>()

            tagMeta.displayName(ColorUtils().convert("<gold>${dataTags[i]}"))

            tagLore.add(ColorUtils().convert(" "))
            tagLore.add(ColorUtils().convert("<gray>Click to remove"))

            tagMeta.lore(tagLore)
            tag.itemMeta = tagMeta
            inv.setItem(tagSlots[i], tag)
        }

        val createHead = HeadUtils().createCustomHead(CustomHeads.PLUS_CHARACTER, "<green>Create Fusion")
        inv.setItem(4, createHead)

        val back = ItemStack(Material.TIPPED_ARROW)
        val backMeta = back.itemMeta as PotionMeta
        backMeta.color = Color.fromRGB(150, 85, 95)
        backMeta.displayName(ColorUtils().convert("<red>Back"))
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        back.itemMeta = backMeta
        inv.setItem(31, back)

        inv = GUIFiller().fillInventory(inv, tagSlots)

        for (slot in tagSlots) {
            if (inv.getItem(slot) == null) {
                inv.setItem(slot, GUIFiller().secondaryFillerItem())
            }
        }

        player.openInventory(inv)
    }

    // TODO: Add support for custom data tags with no functionality
    fun appendDataTag(player: Player) {
        val dataTags = fusionRecipeItemEdit[player.uniqueId]?.data ?: return
        val availableDataTags = mutableListOf<String>()

        for (tag in DataTags.entries) {
            if (dataTags.contains(tag.key.key.toString())) {
                continue
            }

            availableDataTags.add(tag.toString())
        }

        var inv = Bukkit.createInventory(null, 9*4, ColorUtils().convert("<gold>Append Data Tag"))

        val tagSlots = mutableListOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25) // Support for 14 tags

        for (i in 0 until availableDataTags.size) {
            val tag = ItemStack(Material.PAPER)
            val tagMeta = tag.itemMeta
            val tagLore = mutableListOf<Component>()

            tagMeta.displayName(ColorUtils().convert("<gold>${availableDataTags[i]}"))

            tagLore.add(ColorUtils().convert(" "))
            tagLore.add(ColorUtils().convert("<gray>Click to add"))

            tagMeta.lore(tagLore)
            tag.itemMeta = tagMeta
            inv.setItem(tagSlots[i], tag)
        }

        val back = ItemStack(Material.TIPPED_ARROW)
        val backMeta = back.itemMeta as PotionMeta
        backMeta.color = Color.fromRGB(150, 85, 95)
        backMeta.displayName(ColorUtils().convert("<red>Back"))
        backMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)
        back.itemMeta = backMeta
        inv.setItem(31, back)

        inv = GUIFiller().fillInventory(inv, tagSlots)

        for (slot in tagSlots) {
            if (inv.getItem(slot) == null) {
                inv.setItem(slot, GUIFiller().secondaryFillerItem())
            }
        }

        player.openInventory(inv)
    }

    private fun parseItem(player: Player, item: ItemStack, type: String): FusionRecipeItem {
        val material = item.type
        val name = item.itemMeta.persistentDataContainer.get(FusionKeys.FUSION_ITEMNAME.key, PersistentDataType.STRING)
        val amount = item.amount
        val lore = item.itemMeta.lore()
        val data: MutableList<String> = mutableListOf()

        if (name == null || lore == null) {
            return FusionRecipeItem(Material.BARRIER, "null", "null", 0, listOf(Component.text("null")), mutableListOf("null"), "null")
        }

        val loreCopy = lore.toMutableList()

        for (line in loreCopy) {
            val stringLine = ColorUtils().convert(line)

            if (stringLine.contains("Item Data:")) {
                val split = stringLine.split(": ")
                val dataTags = split[1].split(";")

                for (tag in dataTags) {
                    data.add(tag.trim())
                }
            }

            if (ColorUtils().strip(line).startsWith("Item Data:") || ColorUtils().strip(line) == "Left click to edit" || ColorUtils().strip(line) == "Right click to remove") {
                lore.remove(line)
            }
        }

        val loreCopy2 = lore.toMutableList()

        kotlin.runCatching {
            for (line in loreCopy2) {
                val trimmedLine = ColorUtils().strip(line).trim()
                if (trimmedLine.isEmpty()) {
                    val index = lore.indexOf(line)

                    if (index == lore.size - 1) {
                        lore.removeAt(index)
                        continue
                    }

                    val nextLine = lore[index + 1]
                    val nextLineTrimmed = ColorUtils().strip(nextLine).trim()

                    if (nextLineTrimmed.isEmpty()) {
                        lore.removeAt(index)
                    }

                    try {
                        val previousLine = lore[index - 1]
                        val previousLineTrimmed = ColorUtils().strip(previousLine).trim()

                        if (previousLineTrimmed.isEmpty()) {
                            lore.removeAt(index)
                        }
                    } catch (_: IndexOutOfBoundsException) {
                        lore.removeAt(index)
                        continue
                    }
                }
            }
        }


        val fusionRecipeItem = FusionRecipeItem(material, name, name, amount, lore, data, type)

        fusionRecipeItemEdit[player.uniqueId] = fusionRecipeItem

        return fusionRecipeItem
    }

    private fun parseItemList(player: Player): MutableList<ItemStack> {
        val itemList = mutableListOf<ItemStack>()

        val fusion = FusionManagerGUI.playerManager[player.uniqueId] ?: return itemList
        val fusionRecipe = fusionRecipeItemEdit[player.uniqueId] ?: return itemList

        for (f in fusion.fusionInv) {
            val keySplit0 = f.key.split(".")[0]
            if (keySplit0 != "input") {
                continue
            }

            val keySplit1 = f.key.split(".")[1]
            val requiredKey = ColorUtils().strip(fusionRecipe.name)
            if (keySplit1 != requiredKey) {
                continue
            }

            val value = f.value as MutableList<MutableMap<String, Any>>

            for (v in value) {
                if (v.containsKey("item")) {
                    val item = ItemStack(Material.valueOf((v["item"] as String).uppercase()))
                    itemList.add(item)
                    break
                }
            }
        }

        return itemList
    }

    fun saveItemList(player: Player, inv: Inventory) {
        val itemList = mutableListOf<ItemStack>()
        val itemSlots = mutableListOf(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34)

        for (slot in itemSlots) {
            val item = inv.getItem(slot) ?: continue

            if (item.type == Material.AIR) {
                continue
            }

            itemList.add(item)
        }

        val fusionRecipe = fusionRecipeItemEdit[player.uniqueId] ?: return
        val fusion = FusionManagerGUI.playerManager[player.uniqueId] ?: return

        for (f in fusion.fusionInv) {
            val keySplit0 = f.key.split(".")[0]
            if (keySplit0 != "input") {
                continue
            }

            val keySplit1 = f.key.split(".")[1]
            val requiredKey = ColorUtils().strip(fusionRecipe.name)
            if (keySplit1 != requiredKey) {
                continue
            }

            val value = f.value as MutableList<MutableMap<String, Any>>

            for (v in value) {
                if (v.containsKey("item")) {
                    value.remove(v)
                    break
                }
            }
        }

        val keyPrefix = "input.${ColorUtils().strip(fusionRecipeItemEdit[player.uniqueId]?.name ?: "null")}"

        for ((keySuffix, i) in (0 until itemList.size).withIndex()) {
            val item = itemList[i]
            val key = "$keyPrefix.$keySuffix"

            val fusionInv = fusion.fusionInv

            val data = try {
                fusionInv[key] as MutableList<MutableMap<String, Any>>
            } catch (_: NullPointerException) {
                mutableListOf()
            }

            data.add(0, mutableMapOf("item" to item.type.toString().lowercase()))

            val containsAmount = data.any { it.containsKey("amount") }
            if (!containsAmount) {
                data.add(1, mutableMapOf("amount" to "1"))
            }

            val containsData = data.any { it.containsKey("data") }
            if (!containsData) {
                data.add(2, mutableMapOf("data" to ""))
            }

            fusionInv[key] = data
        }

        val fusionInv = fusion.fusionInv.toMap()

        for (f in fusionInv) {
            val keySplit0 = f.key.split(".")[0]
            if (keySplit0 != "input") {
                continue
            }

            val keySplit1 = f.key.split(".")[1]
            val requiredKey = ColorUtils().strip(fusionRecipe.name)
            if (keySplit1 != requiredKey) {
                continue
            }

            val value = f.value as MutableList<MutableMap<String, Any>>

            val containsItem = value.any { it.containsKey("item") }

            if (!containsItem) {
                fusion.fusionInv.remove(f.key)
            }
        }
    }

    fun deleteRecipeItem(player: Player, item: ItemStack, type: String) {
        val fusion = FusionManagerGUI.playerManager[player.uniqueId] ?: return
        val meta = item.itemMeta

        val itemName = if (meta.persistentDataContainer.has(FusionKeys.FUSION_ITEMNAME.key, PersistentDataType.STRING)) {
             meta.persistentDataContainer.get(FusionKeys.FUSION_ITEMNAME.key, PersistentDataType.STRING)!!
        } else {
            return
        }

        val fusionInv = fusion.fusionInv
        val fusionInvCopy = fusionInv.toMutableMap()

        if (type == "input") {
            val strippedItemName = ColorUtils().strip(itemName)

            var index = 0
            val key = "input.${strippedItemName}"

            for (f in fusionInvCopy) {
                if (f.key == "$key.$index") {
                    fusionInv.remove(f.key)
                    index++
                }
            }
        } else if (type == "output") {
            val key = "output"

            for (f in fusionInvCopy) {
                if (f.key.startsWith(key)) {
                    val value = f.value as MutableList<MutableMap<String, Any>>

                    for (v in value) {
                        if (v.containsKey("name")) {
                            if (v["name"] == itemName) {
                                fusionInv.remove(f.key)
                                break
                            }
                        }
                    }
                }
            }
        }
        FusionCraftManage().editFusionRecipe(player, fusion.fusionInv)
    }
}