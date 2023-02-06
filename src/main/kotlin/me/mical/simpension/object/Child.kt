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
package me.mical.simpension.`object`

import me.mical.simpension.ConfigReader
import me.mical.simpension.database.PluginDatabase
import me.mical.simpension.manager.TextureManager
import me.mical.simpension.util.parseLoc
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import taboolib.common.util.random
import taboolib.common.util.replaceWithOrder
import taboolib.module.chat.colored
import java.util.UUID

/**
 * SimPension
 * me.mical.simpension.`object`.Child
 *
 * @author xiaomu
 * @since 2023/2/1 1:10 PM
 */
class Child {

    var husband: UUID = UUID.randomUUID()
    var wife: UUID = UUID.randomUUID()
    var uuid: UUID = UUID.randomUUID()
    var name = ConfigReader.defaultName
        get() = field.replaceWithOrder(husband().name!!, wife().name!!, sexName()).colored()
    var sex = if (random(101) < 50) "MALE" else "FEMALE"
    var age = -1
    var deadline = random(101)
    var pregnant = 0L
    var birthday = 0L
    var lastBirthday = 0L
    var texture = ""
    var lastLocation = ConfigReader.spawn.parseLoc()
    var view = true
    var follow = true
    var task = ""
    var birthdayReal = 0L
    var inventory = ""
    var head = ""
    var chest = ""
    var legs = ""
    var boots = ""
    var hand = ""
    var offhand = ""

    fun save() {
        if (TextureManager.entities.containsKey(uuid)) {
            lastLocation = TextureManager.entities[uuid]!!.getLocation()
        }
        PluginDatabase.refresh(this)
    }

    fun husband(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(husband)
    }

    fun wife(): OfflinePlayer {
        return Bukkit.getOfflinePlayer(wife)
    }

    private fun sexName(): String {
        return when (sex.uppercase()) {
            "MALE" -> ConfigReader.son
            "FEMALE" -> ConfigReader.daughter
            else -> ""
        }
    }

    fun sex(): String {
        return when (sex.uppercase()) {
            "MALE" -> ConfigReader.male
            "FEMALE" -> ConfigReader.female
            else -> ""
        }
    }
}