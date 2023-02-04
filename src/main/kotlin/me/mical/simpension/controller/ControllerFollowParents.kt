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
import me.mical.simpension.ConfigReader
import me.mical.simpension.`object`.Child

/**
 * SimPension
 * me.mical.simpension.controller.ControllerFollowParents
 *
 * @author xiaomu
 * @since 2023/2/1 2:24 PM
 */
class ControllerFollowParents(entity: EntityInstance, private val child: Child, @Expose val probability: Double) : Controller(entity) {

    constructor(entity: EntityInstance, child: Child) : this(entity, child, 0.1)

    override fun id(): String {
        return "MOVE_FOLLOW_PARENTS"
    }

    override fun group(): String {
        return "WALK"
    }

    override fun shouldExecute(): Boolean {
        if (entity == null) return false
        val father = child.husband().player
        val mother = child.wife().player
        val distanceFather = if (father == null) -1.0 else entity!!.getLocation().distance(father.location)
        val distanceMother = if (mother == null) -1.0 else entity!!.getLocation().distance(mother.location)
        if (distanceFather == -1.0 && distanceMother == -1.0) return false
        return (distanceFather < ConfigReader.walkRadius || distanceMother < ConfigReader.walkRadius) &&
                entity!!.random().nextDouble() < probability
    }

    override fun priority(): Int {
        return 0
    }

    override fun continueExecute(): Boolean {
        return false
    }

    override fun start() {
        if (entity != null) {
            val distanceFather by lazy {
                if (child.husband().player == null) return@lazy 999.0
                child.husband().player!!.location.distance(entity!!.getLocation())
            }
            val distanceMother by lazy {
                if (child.wife().player == null) return@lazy 999.0
                child.wife().player!!.location.distance(entity!!.getLocation())
            }
            when {
                distanceMother < distanceFather -> {
                    child.wife().player?.let { entity!!.controllerMoveTo(it.location) }
                }
                distanceFather <= distanceMother -> {
                    child.husband().player?.let { entity!!.controllerMoveTo(it.location) }
                }
                else -> {
                }
            }
        }
    }

    override fun toString(): String {
        return "${id()}:${"%.2f".format(probability)}"
    }
}