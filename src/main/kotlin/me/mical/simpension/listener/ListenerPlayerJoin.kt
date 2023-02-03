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

import me.mical.simpension.network.NetworkManager
import org.bukkit.event.player.PlayerJoinEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * SimPension
 * me.mical.simpension.listener.ListenerPlayerJoin
 *
 * @author xiaomu
 * @since 2023/2/1 3:38 PM
 */
object ListenerPlayerJoin {

    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        NetworkManager.downloadPlayerSkinTexture(e.player)
    }
}