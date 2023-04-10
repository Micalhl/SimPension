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
package me.mical.simpension.listener

import me.mical.simpension.manager.ChildManager
import me.mical.simpension.manager.TextureManager
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * SimPension
 * me.mical.simpension.listener.ListenerParentAttack
 *
 * @author xiaomu
 * @since 2023/2/11 5:36 PM
 */
object ListenerParentAttack {

    @SubscribeEvent
    fun e(e: EntityDamageByEntityEvent) {
        if (e.damager !is Player) return
        val p = e.damager as Player
        val t = e.entity
        if (ChildManager.children.any { it.uuid == t.uniqueId }) return
        ChildManager.children
            .filter { it.husband == p.uniqueId || it.wife == p.uniqueId }
            .filter { TextureManager.entities.containsKey(it.uuid) }
            .filter {
                val inst = TextureManager.entities[it.uuid]!!
                inst.getLocation().world == p.world && inst.getLocation().distance(p.location) < 20.0
            }
            .forEach { it.entityAttacked = t } // 标记
    }
}