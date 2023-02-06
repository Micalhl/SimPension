/**
 * MIT License
 *
 * Copyright (c) 2020 TabooLib
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.mical.simpension.editor

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import me.mical.simpension.ConfigReader
import me.mical.simpension.`object`.Child
import me.mical.simpension.util.serializeToBase64
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.library.xseries.XMaterial
import taboolib.module.chat.colored
import taboolib.module.ui.ClickType
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.buildItem
import taboolib.platform.util.isAir
import taboolib.platform.util.isNotAir
import taboolib.type.BukkitEquipment

/**
 * Adyeshach
 * ink.ptms.adyeshach.module.editor.meta.impl.MetaEquipment
 *
 * @author 坏黑
 * @since 2022/12/27 04:05
 */
object MetaEquipment {

    class EquipmentSlot(val slot: Int, val equipment: BukkitEquipment, val material: XMaterial)

    private val allowSlots = listOf(
        EquipmentSlot(13, BukkitEquipment.HEAD, XMaterial.LEATHER_HELMET),
        EquipmentSlot(22, BukkitEquipment.CHEST, XMaterial.LEATHER_CHESTPLATE),
        EquipmentSlot(31, BukkitEquipment.LEGS, XMaterial.LEATHER_LEGGINGS),
        EquipmentSlot(40, BukkitEquipment.FEET, XMaterial.LEATHER_BOOTS),
        EquipmentSlot(21, BukkitEquipment.HAND, XMaterial.WOODEN_SWORD),
        EquipmentSlot(23, BukkitEquipment.OFF_HAND, XMaterial.SHIELD)
    )

    fun open(entity: EntityInstance, player: Player, child: Child) {
        entity as AdyEntityLiving
        player.openMenu<Basic>(ConfigReader.equipmentEditorTitle.colored()) {
            handLocked(false)
            rows(6)
            onBuild { _, inventory ->
                allowSlots.forEach { eq ->
                    val equipment = entity.getEquipment(eq.equipment.bukkit)
                    if (equipment.isAir()) {
                        inventory.setItem(eq.slot, buildItem(eq.material) {
                            name = ConfigReader.equipmentEditorEmpty.colored()
                        })
                    } else {
                        inventory.setItem(eq.slot, equipment)
                    }
                }
            }
            onClick { e ->
                if (e.clickType == ClickType.DRAG && e.dragEvent().rawSlots.size > 1) {
                    e.isCancelled = true
                } else {
                    val rawSlot = if (e.clickType == ClickType.DRAG) e.dragEvent().rawSlots.firstOrNull() ?: -1 else e.rawSlot
                    // 点击箱子内部
                    if (rawSlot in 0..54) {
                        e.isCancelled = true
                        // 点击装备格子
                        val slot = allowSlots.firstOrNull { it.slot == rawSlot }
                        if (slot != null) {
                            val currentItem = entity.getEquipment(slot.equipment.bukkit)
                            val cursor = player.itemOnCursor
                            when {
                                // 放入
                                currentItem.isAir() && cursor.isNotAir() -> {
                                    player.setItemOnCursor(null)
                                    entity.setEquipment(slot.equipment.bukkit, cursor)
                                    e.inventory.setItem(rawSlot, cursor)
                                }
                                // 取出
                                currentItem.isNotAir() && cursor.isAir() -> {
                                    player.setItemOnCursor(currentItem)
                                    entity.setEquipment(slot.equipment.bukkit, ItemStack(Material.AIR))
                                    e.currentItem = buildItem(slot.material) {
                                        name = ConfigReader.equipmentEditorEmpty.colored()
                                    }
                                }
                                // 交换
                                currentItem.isNotAir() && currentItem.isNotAir() -> {
                                    player.setItemOnCursor(currentItem)
                                    entity.setEquipment(slot.equipment.bukkit, cursor)
                                    e.inventory.setItem(rawSlot, cursor)
                                }
                            }
                        }
                    } else if (e.clickType == ClickType.CLICK && e.clickEvent().isShiftClick) {
                        e.isCancelled = true
                    }
                }
            }
            onClose {
                child.head = entity.getEquipment(BKEquipment.HEAD)?.serializeToBase64() ?: ""
                child.chest = entity.getEquipment(BKEquipment.CHEST)?.serializeToBase64() ?: ""
                child.legs = entity.getEquipment(BKEquipment.LEGS)?.serializeToBase64() ?: ""
                child.boots = entity.getEquipment(BKEquipment.FEET)?.serializeToBase64() ?: ""
                child.hand = entity.getEquipment(BKEquipment.HAND)?.serializeToBase64() ?: ""
                child.hand = entity.getEquipment(BKEquipment.OFF_HAND)?.serializeToBase64() ?: ""
                child.save()
            }
        }
    }
}

typealias BKEquipment = EquipmentSlot