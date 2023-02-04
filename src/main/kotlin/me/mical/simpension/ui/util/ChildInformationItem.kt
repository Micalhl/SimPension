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
package me.mical.simpension.ui.util

import me.mical.simpension.ConfigReader
import me.mical.simpension.network.NetworkManager
import me.mical.simpension.`object`.Child
import org.bukkit.Bukkit
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.serverct.parrot.parrotx.function.singletons
import org.serverct.parrot.parrotx.function.textured
import taboolib.common.util.replaceWithOrder
import taboolib.module.chat.colored
import taboolib.platform.util.modifyMeta
import java.text.SimpleDateFormat
import java.util.*

/**
 * SimPension
 * me.mical.simpension.ui.util.ChildInformationItem
 *
 * @author xiaomu
 * @since 2023/2/3 4:10 PM
 */
val simpleDateFormat = SimpleDateFormat(ConfigReader.dateForamt)

infix fun ItemStack.applyChild(child: Child): ItemStack {
    val uuid = Bukkit.getOfflinePlayer(child.texture).uniqueId
    val result = singletons {
        when (it) {
            "name" -> child.name
            "age" -> child.age.toString()
            "sex" -> child.sex()
            "follow" -> if (child.follow) ConfigReader.yes else ConfigReader.no
            "view" -> if (child.view) ConfigReader.no else ConfigReader.yes
            "birth" -> if (child.birthdayReal == 0L) ConfigReader.haveNoBirthdayTip.colored() else simpleDateFormat.format(Date(child.birthdayReal))
            else -> null
        }
    }.modifyMeta<ItemMeta> {
        setDisplayName(displayName.replaceWithOrder(child.name, "name") + if (child.age >= child.deadline) ConfigReader.died.colored() else "")
    }
    if (child.texture.isNotEmpty() && NetworkManager.handler.containsKey(uuid)) {
        result textured NetworkManager.getTextureUrlEnd(Bukkit.getOfflinePlayer(child.texture).uniqueId)
    }
    return result
}