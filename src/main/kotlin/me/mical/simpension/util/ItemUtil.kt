package me.mical.simpension.util

import org.bukkit.inventory.ItemStack
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.serializeToByteArray
import java.util.Base64

/**
 * SimPension
 * me.mical.simpension.util.ItemUtil
 *
 * @author xiaomu
 * @since 2023/2/6 11:37 AM
 */
fun ItemStack.serializeToBase64(): String {
    return Base64.getEncoder().encodeToString(serializeToByteArray())
}

fun String.deserializeItemStackFromBase64(): ItemStack {
    return Base64.getDecoder().decode(this).deserializeToItemStack()
}