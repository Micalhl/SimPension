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
package me.mical.simpension.manager

import ink.ptms.adyeshach.core.Adyeshach
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.EntityTypes
import ink.ptms.adyeshach.core.entity.type.AdyEntityLiving
import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.entity.type.AdyVillager
import ink.ptms.adyeshach.impl.entity.controller.ControllerRandomLookaround
import ink.ptms.adyeshach.impl.entity.controller.ControllerRandomStrollLand
import me.mical.simpension.ConfigReader
import me.mical.simpension.controller.ControllerFollowParents
import me.mical.simpension.`object`.Child
import me.mical.simpension.util.deserializeItemStackFromBase64
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.util.random
import taboolib.module.chat.colored
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * SimPension
 * me.mical.simpension.manager.TextureManager
 *
 * @author xiaomu
 * @since 2023/2/1 2:14 PM
 */
object TextureManager {

    val entities = ConcurrentHashMap<UUID, EntityInstance>()
    private val manager = Adyeshach.api().getPublicEntityManager()

    fun display(child: Child) {
        var new = false
        if (child.age >= ConfigReader.adult && entities.contains(child.uuid) && entities[child.uuid]!!.entityType == EntityTypes.VILLAGER) {
            child.lastLocation = entities[child.uuid]!!.getLocation()
            destroy(child)
        }
        val entityInstance = entities.computeIfAbsent(child.uuid) {
            val instance by lazy {
                if (child.age >= 18) {
                    val i = manager.create(EntityTypes.PLAYER, child.lastLocation) as AdyHuman
                    i.setName(child.name.colored())
                    i
                } else {
                    val i = manager.create(EntityTypes.VILLAGER, child.lastLocation) as AdyVillager
                    i.setBaby(true)
                    i.setCustomName(child.name.colored())
                    i.setCustomNameVisible(true)
                    i
                }
            }
            val texture = child.texture.ifEmpty { if (random(101) < 50) child.husband().name!! else child.wife().name!! }
            child.texture = texture
            (instance as? AdyHuman)?.setTexture(texture)
            instance.id = "SimPensionChild"
            if (child.follow) {
                instance.registerController(ControllerFollowParents(instance, child))
            } else {
                instance.registerController(ControllerRandomStrollLand(instance))
            }
            instance.registerController(ControllerRandomLookaround(instance))
            new = true
            instance
        }
        if (!new) {
            entityInstance.respawn()
        }

        if (entityInstance is AdyHuman) entityInstance.setTexture(child.texture) // 防止没有加载皮肤纹理
        child.save() // 如果修改了, 就保存一下

        if (child.head.isNotEmpty()) (entityInstance as AdyEntityLiving).setEquipment(EquipmentSlot.HEAD, child.head.deserializeItemStackFromBase64())
        if (child.chest.isNotEmpty()) (entityInstance as AdyEntityLiving).setEquipment(EquipmentSlot.CHEST, child.chest.deserializeItemStackFromBase64())
        if (child.legs.isNotEmpty()) (entityInstance as AdyEntityLiving).setEquipment(EquipmentSlot.LEGS, child.legs.deserializeItemStackFromBase64())
        if (child.boots.isNotEmpty()) (entityInstance as AdyEntityLiving).setEquipment(EquipmentSlot.FEET, child.boots.deserializeItemStackFromBase64())
        if (child.hand.isNotEmpty()) (entityInstance as AdyEntityLiving).setEquipment(EquipmentSlot.HAND, child.hand.deserializeItemStackFromBase64())
        if (child.offhand.isNotEmpty()) (entityInstance as AdyEntityLiving).setEquipment(EquipmentSlot.OFF_HAND, child.offhand.deserializeItemStackFromBase64())
    }

    fun destroy(child: Child) {
        entities[child.uuid]?.remove()
        entities.remove(child.uuid)
    }
}