/*
 *  Copyright (C) <2023>  <Mical>
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package me.mical.simpension.ui

import ink.ptms.adyeshach.core.event.AdyeshachEntityInteractEvent
import me.mical.simpension.manager.ChildManager
import me.mical.simpension.manager.TextureManager
import me.mical.simpension.`object`.Child
import me.mical.simpension.util.deserializeInventoryFromBase64
import me.mical.simpension.util.serializeToBase64
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.chat.colored
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic

/**
 * SimPension
 * me.mical.simpension.ui.ChildInventoryUI
 *
 * @author xiaomu
 * @since 2023/2/6 11:00 AM
 */
object ChildInventoryUI {

    @SubscribeEvent
    fun e(e: AdyeshachEntityInteractEvent) {
        if (!e.player.isSneaking) return
        val uuid = e.entity.uniqueId
        if (TextureManager.entities.none { it.value.uniqueId == uuid }) return
        val child = ChildManager.children.first { it.uuid == TextureManager.entities.filter { it.value.uniqueId == uuid }.keys.first() }
        if (child.husband != e.player.uniqueId && child.wife != e.player.uniqueId) return
        open(e.player, child)
    }

    fun open(player: Player, child: Child) {
        player.openMenu<Basic>(child.name.colored()) {
            rows(2)
            handLocked(false)
            onBuild { _, inventory ->
                val childInv: Inventory? = if (child.inventory.isEmpty()) null else child.inventory.deserializeInventoryFromBase64()
                if (childInv != null) {
                    inventory.contents = childInv.contents
                }
            }
            onClose {
                if (it.inventory.isEmpty) {
                    child.inventory = ""
                } else {
                    child.inventory = it.inventory.serializeToBase64()
                }
                child.save()
            }
        }
    }
}