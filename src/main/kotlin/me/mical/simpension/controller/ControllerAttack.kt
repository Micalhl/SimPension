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
package me.mical.simpension.controller

import com.google.gson.annotations.Expose
import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.controller.Controller
import me.mical.simpension.`object`.Child
import org.bukkit.entity.LivingEntity

/**
 * SimPension
 * me.mical.simpension.controller.ControllerAttack
 *
 * @author xiaomu
 * @since 2023/2/11 5:29 PM
 */
class ControllerAttack(entity: EntityInstance, private val child: Child, @Expose val probability: Double) : Controller(entity) {

    constructor(entity: EntityInstance, child: Child) : this(entity, child, 0.1)

    private var delay = 0

    override fun id(): String {
        return "ATTACK"
    }

    override fun priority(): Int {
        return 0
    }

    override fun shouldExecute(): Boolean {
        return entity != null && child.entityAttacked != null && !child.entityAttacked!!.isDead && entity!!.getLocation().world == child.entityAttacked!!.world && entity!!.getLocation().distance(child.entityAttacked!!.location) < 20.0 && entity!!.random().nextDouble() < probability
    }

    override fun start() {
        delay = 20
    }

    override fun continueExecute(): Boolean {
        return shouldExecute()
    }

    override fun tick() {
        val t = child.entityAttacked!! as LivingEntity
        entity!!.controllerMoveTo(t.location)
        if (entity!!.getLocation().distance(t.location) < 3.0) {
            if (delay == 20) {
                t.damage(1.5)
            } else if (delay == 0) {
                delay = 20
                return
            }
            delay--
        }
    }

    override fun stop() {
        child.entityAttacked = null
    }
}