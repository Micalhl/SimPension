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

import me.mical.simpension.ConfigReader
import me.mical.simpension.manager.SexManager
import me.mical.simpension.util.MathUtils
import me.xanium.gemseconomy.api.GemsEconomyAPI
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.int
import taboolib.common.platform.command.mainCommand
import taboolib.platform.util.sendLang
import java.util.*

/**
 * SimPension
 * me.mical.simpension.command.PensionAPI
 *
 * @author xiaomu
 * @since 2023/2/3 7:11 PM
 */
@CommandHeader(name = "simpensionapi", permission = "simpension.api")
object PensionAPI {

    private val gemsEconomy = GemsEconomyAPI()

    @CommandBody
    val main = mainCommand {
        int("more") {
            dynamic("uuid") {
                execute<Player> { sender, ctx, _ ->
                    val uuid = UUID.fromString(ctx["uuid"])
                    val money = MathUtils.calculate(ConfigReader.money, "more", ctx["more"].toInt())
                    if (SexManager.planning.contains(sender.uniqueId to uuid) || SexManager.planning.contains(uuid to sender.uniqueId)) return@execute
                    if (gemsEconomy.getBalance(sender.uniqueId) < money) {
                        sender.sendLang("money-fail")
                        return@execute
                    }
                    gemsEconomy.withdraw(sender.uniqueId, money)
                    SexManager.planning.add(sender.uniqueId to uuid)
                    sender.sendLang("money")
                }
            }
        }
    }
}