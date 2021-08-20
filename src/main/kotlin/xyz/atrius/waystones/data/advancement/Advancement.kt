package xyz.atrius.waystones.data.advancement

import com.google.gson.annotations.SerializedName
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.intellij.lang.annotations.Language
import xyz.atrius.waystones.data.json.Json
import xyz.atrius.waystones.plugin
import xyz.atrius.waystones.utility.toKey
import org.bukkit.advancement.Advancement as SpigotAdvancement

data class Text(
    val text: String
)

data class Icon(
    val item: String,
    @Language("JSON") val nbt: String? = null,
)

data class Trigger(
    val trigger: String
)

class Criteria(vararg items: Pair<String, String>) : Map<String, Trigger>
    by items.associate({ (name, trig) -> name to Trigger(trig) })

data class Display(
    val title: Text,
    val description: Text,
    val icon: Icon,
    @SerializedName("show_toast")
    val showToast: Boolean = true,
    @SerializedName("announce_to_chat")
    val announceToChat: Boolean = true,
    val hidden: Boolean = false
) {
    val background = "minecraft:textures/block/chiseled_quartz_block.png"
}

data class Advancement(
    val display: Display,
    val criteria: Criteria,
    val parent: String? = null
) : Json<Advancement> {

    constructor(
        item: Material,
        title: String,
        description: String,
        parent: Advancement? = null
    ) : this(
        Display(
            Text(title),
            Text(description),
            Icon(item.key.toString())
        ),
        IMPOSSIBLE,
        parent?.paired()?.first.toString()
    )

    fun toInstance(): SpigotAdvancement =
        plugin.server.getAdvancement(display.title.text.toKey())!!

    fun paired(): Pair<NamespacedKey, Advancement> =
        display.title.text.toLowerCase().replace(" ", "_").toKey() to this

    companion object {
        val IMPOSSIBLE = Criteria(
            "command" to "minecraft:impossible"
        )
    }
}
