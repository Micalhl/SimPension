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
package me.mical.simpension.command

import me.mical.simpension.ui.ChildListUI
import org.bukkit.entity.Player
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common5.Mirror

/**
 * SimPension
 * me.mical.simpension.command.SimPension
 *
 * @author xiaomu
 * @since 2023/2/1 3:52 PM
 */
@CommandHeader(name = "simpension", aliases = ["sp", "pension"], permission = "simpension.use")
object SimPension {

    @CommandBody
    val main = mainCommand {
        execute<Player> { user, _, _ ->
            ChildListUI.open(user)
        }
    }

    @CommandBody
    val mirror = subCommand {
        execute<ProxyCommandSender> { sender, _, _ ->
            Mirror.report(sender)
        }
    }
}