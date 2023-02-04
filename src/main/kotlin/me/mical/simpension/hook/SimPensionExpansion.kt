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
package me.mical.simpension.hook

import me.mical.simpension.ConfigReader
import me.mical.simpension.manager.ChildManager
import org.bukkit.OfflinePlayer
import taboolib.common.platform.function.pluginId
import taboolib.module.chat.colored
import taboolib.platform.compat.PlaceholderExpansion

/**
 * SimPension
 * me.mical.simpension.hook.SimPensionExpansion
 *
 * @author xiaomu
 * @since 2023/2/4 10:35 AM
 */
object SimPensionExpansion : PlaceholderExpansion {

    override val identifier: String
        get() = pluginId.lowercase()

    override fun onPlaceholderRequest(player: OfflinePlayer?, args: String): String {
        if (player == null) return ""
        return when (args.lowercase()) {
            "state" -> {
                return if (ChildManager.childs.any { it.age == -1 && it.wife == player.uniqueId }) ConfigReader.state.colored() else ""
            }
            else -> ""
        }
    }
}