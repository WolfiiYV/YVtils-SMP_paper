package yv.tils.smp.mods.discord.embedManager.whitelist

import net.dv8tion.jda.api.EmbedBuilder
import yv.tils.smp.mods.discord.embedManager.EmbedVars
import yv.tils.smp.utils.color.ColorUtils
import yv.tils.smp.utils.configs.language.LangStrings
import yv.tils.smp.utils.configs.language.Language
import yv.tils.smp.utils.internalAPI.Placeholder

class AccountAdded {
    val builder = EmbedBuilder()

    fun embed(accName: String): EmbedBuilder {
        return builder
            .setTitle(ColorUtils().convert(Language().getMessage(LangStrings.EMBED_BUILDER_TITLE_NAME_ADD)))
            .setDescription(
                ColorUtils().convert(
                    Placeholder().replacer(
                        Language().getMessage(LangStrings.EMBED_BUILDER_DESCRIPTION_NAME_ADD),
                        listOf("accountName"),
                        listOf(accName)
                    )
                )
            )
            .setColor(EmbedVars.successColor)
            .setFooter(EmbedVars.footerText, EmbedVars.footerIcon)
            .setAuthor(EmbedVars.authorName, EmbedVars.authorLink, EmbedVars.authorIcon)
    }
}