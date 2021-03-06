package net.avocraft.avomod.recipe

import kotlinx.serialization.json.*
import net.avocraft.avomod.*
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.inventory.*
import org.bukkit.inventory.RecipeChoice.ExactChoice
import java.nio.file.Files
import kotlin.io.path.name
import kotlin.io.path.readText

object RecipeRegistry {
    private val keys = mutableMapOf<String, NamespacedKey>()

    // todo: error handling could be better on all of this, right now it just
    //  throws if anything is null. we could add a more helpful message or something.

    fun registerRecipes() {
        Files.walk(getResource("/recipes")).filter { it.name.endsWith(".json") }.forEach { file ->
            val json = Json.parseToJsonElement(file.readText()).jsonObject
            when (file.parent.name) {
                "furnace" -> {
                    val result = stackOf(json["result"]!!.toString())
                    Bukkit.addRecipe(
                        FurnaceRecipe(
                            getKey(result, "furnace"),
                            result,
                            ExactChoice(stackOf(json["input"]!!.toString())),
                            json["experience"]!!.jsonPrimitive.float,
                            json["time"]!!.jsonPrimitive.int
                        )
                    )
                }
                "blast_furnace" -> {
                    val result = stackOf(json["result"]!!.toString())
                    Bukkit.addRecipe(
                        BlastingRecipe(
                            getKey(result, "blast_furnace"),
                            result,
                            ExactChoice(stackOf(json["input"]!!.toString())),
                            json["experience"]!!.jsonPrimitive.float,
                            json["time"]!!.jsonPrimitive.int
                        )
                    )
                }
                "campfire" -> {
                    val result = stackOf(json["result"]!!.toString())
                    Bukkit.addRecipe(
                        CampfireRecipe(
                            getKey(result, "campfire"),
                            result,
                            ExactChoice(stackOf(json["input"]!!.toString())),
                            json["experience"]!!.jsonPrimitive.float,
                            json["time"]!!.jsonPrimitive.int
                        )
                    )
                }
                "smoker" -> {
                    val result = stackOf(json["result"]!!.toString())
                    Bukkit.addRecipe(
                        SmokingRecipe(
                            getKey(result, "smoker"),
                            result,
                            ExactChoice(stackOf(json["input"]!!.toString())),
                            json["experience"]!!.jsonPrimitive.float,
                            json["time"]!!.jsonPrimitive.int
                        )
                    )
                }
                "shapeless" -> {
                    val result = stackOf(json["result"]!!.toString())
                    Bukkit.addRecipe(ShapelessRecipe(getKey(result, "crafting"), result).apply {
                        json["ingredients"]!!.jsonObject.forEach { item, count ->
                            val stack = stackOf(item)
                            repeat(count.jsonPrimitive.int) {
                                addIngredient(ExactChoice(stack))
                            }
                        }
                    })
                }
                "smithing" -> {
                    val result = stackOf(json["result"]!!.toString())
                    Bukkit.addRecipe(
                        SmithingRecipe(
                            getKey(result, "smithing"),
                            result,
                            ExactChoice(stackOf(json["base"]!!.toString())),
                            ExactChoice(stackOf(json["addition"]!!.toString())),
                        )
                    )
                }
                "idk?? are we going to have shaped 3x3 and shaped 2x2 or how is this going to work" -> TODO()
            }
        }
    }

    private fun getKey(result: ItemStack, type: String) = keys.getOrPut(result.toString() + type) {
        NamespacedKey(PLUGIN, "${result.metaNameOrDefault()}_${type}_${Bukkit.getRecipesFor(result).size}")
    }

    fun unregisterRecipes() {
        keys.values.forEach(Bukkit::removeRecipe)
    }
}
