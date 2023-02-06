package me.mical.simpension.util

import org.bukkit.inventory.Inventory
import taboolib.platform.util.deserializeToInventory
import taboolib.platform.util.serializeToByteArray
import java.util.Base64

/**
 * SimPension
 * me.mical.simpension.util.InventoryUtil
 *
 * @author xiaomu
 * @since 2023/2/6 10:53 AM
 */
fun Inventory.serializeToBase64(): String {
    return Base64.getEncoder().encodeToString(serializeToByteArray())
}

fun String.deserializeInventoryFromBase64(): Inventory {
    return Base64.getDecoder().decode(this).deserializeToInventory()
}