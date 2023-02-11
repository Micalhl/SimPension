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

import ink.ptms.adyeshach.core.entity.EntityInstance
import ink.ptms.adyeshach.core.entity.controller.Controller

/**
 * SimPension
 * me.mical.simpension.controller.ControllerAttack
 *
 * @author xiaomu
 * @since 2023/2/11 5:29 PM
 */
class ControllerAttack(entity: EntityInstance) : Controller(entity) {

    override fun id(): String {
        return "ATTACK"
    }

    override fun shouldExecute(): Boolean {
        TODO("Not yet implemented")
    }
}