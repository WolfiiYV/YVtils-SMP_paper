package yv.tils.smp.mods.fusionCrafting.fusions.lightBlock

import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
import yv.tils.smp.utils.color.ColorUtils

class LightBlock {
    fun configFile(ymlFile: YamlConfiguration): YamlConfiguration {
        ymlFile.addDefault("enabled", true)
        ymlFile.addDefault("name", "Light Block")
        ymlFile.addDefault("displayItem", "LIGHT")
        ymlFile.addDefault("description", "<white>Craft an <gold>invisible <white>Light Source")

        val inputItems = inputItems()
        val outputItems = outputItems()

        for (i in 0 until inputItems.size) {
            for (j in 0 until inputItems.values.elementAt(i).size) {
                ymlFile.addDefault("input.${inputItems.keys.elementAt(i)}.$j", inputItems.values.elementAt(i)[j])
            }
        }

        for (i in 0 until outputItems.size) {
            ymlFile.addDefault("output.$i", outputItems[i])
        }

        ymlFile.options().copyDefaults(true)

        return ymlFile
    }

    private fun inputItems(): MutableMap<String, MutableList<MutableList<MutableMap<String, String>>>> {
        val items = mutableMapOf<String, MutableList<MutableList<MutableMap<String, String>>>>()

        val lantern = mutableMapOf("item" to "Lantern")
        val soulLantern = mutableMapOf("item" to "Soul_Lantern")
        val lanternAmount = mutableMapOf("amount" to "1")
        val lanternData = mutableMapOf("data" to "")

        items["Lantern / Soul Lantern"] = mutableListOf(
            mutableListOf(lantern, lanternAmount, lanternData),
            mutableListOf(soulLantern, lanternAmount, lanternData)
        )

        val glassPane = mutableMapOf("item" to "Glass_Pane")
        val whiteGlassPane = mutableMapOf("item" to "White_Glass_Pane")
        val orangeGlassPane = mutableMapOf("item" to "Orange_Glass_Pane")
        val magentaGlassPane = mutableMapOf("item" to "Magenta_Glass_Pane")
        val lightBlueGlassPane = mutableMapOf("item" to "Light_Blue_Glass_Pane")
        val yellowGlassPane = mutableMapOf("item" to "Yellow_Glass_Pane")
        val limeGlassPane = mutableMapOf("item" to "Lime_Glass_Pane")
        val pinkGlassPane = mutableMapOf("item" to "Pink_Glass_Pane")
        val grayGlassPane = mutableMapOf("item" to "Gray_Glass_Pane")
        val lightGrayGlassPane = mutableMapOf("item" to "Light_Gray_Glass_Pane")
        val cyanGlassPane = mutableMapOf("item" to "Cyan_Glass_Pane")
        val purpleGlassPane = mutableMapOf("item" to "Purple_Glass_Pane")
        val blueGlassPane = mutableMapOf("item" to "Blue_Glass_Pane")
        val brownGlassPane = mutableMapOf("item" to "Brown_Glass_Pane")
        val greenGlassPane = mutableMapOf("item" to "Green_Glass_Pane")
        val redGlassPane = mutableMapOf("item" to "Red_Glass_Pane")
        val blackGlassPane = mutableMapOf("item" to "Black_Glass_Pane")
        val glassPaneAmount = mutableMapOf("amount" to "4")
        val glassPaneData = mutableMapOf("data" to "")

        items["Any sort of Glass Pane"] = mutableListOf(
            mutableListOf(glassPane, glassPaneAmount, glassPaneData),
            mutableListOf(whiteGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(orangeGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(magentaGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(lightBlueGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(yellowGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(limeGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(pinkGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(grayGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(lightGrayGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(cyanGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(purpleGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(blueGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(brownGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(greenGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(redGlassPane, glassPaneAmount, glassPaneData),
            mutableListOf(blackGlassPane, glassPaneAmount, glassPaneData)
        )

        return items
    }

    private fun outputItems(): MutableList<MutableList<MutableMap<String, String>>> {
        val items: MutableList<MutableList<MutableMap<String, String>>> = mutableListOf()

        val item = mutableMapOf("item" to "LIGHT")
        val amount = mutableMapOf("amount" to "4")
        val name = mutableMapOf("name" to "<white>Light Block")
        val lore = mutableMapOf("lore" to "<white>Place this block to create an invisible light source")
        val data = mutableMapOf("data" to "")

        items.add(mutableListOf(item, amount, name, lore))

        return items
    }
}