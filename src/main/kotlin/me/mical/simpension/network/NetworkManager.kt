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
package me.mical.simpension.network

import ink.ptms.adyeshach.Adyeshach
import ink.ptms.adyeshach.core.AdyeshachNetworkAPI
import ink.ptms.adyeshach.module.command.CommandAPI
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.io.newFile
import taboolib.common.io.newFolder
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submitAsync
import taboolib.common.platform.function.warning
import taboolib.module.configuration.Configuration
import taboolib.module.configuration.Type
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * SimPension
 * me.mical.simpension.network.NetworkManager
 *
 * @author xiaomu
 * @since 2023/2/3 10:54 PM
 */
object NetworkManager {

    private val handler = arrayListOf<UUID>() // 缓存一下, 避免开服之后重复下载.

    private const val ashconURL = "https://api.ashcon.app/mojang/v2/user/"

    private fun getSkinTextureUrl(username: String): String {
        val json = Configuration.loadFromString(URL("$ashconURL$username").openStream().readBytes().toString(StandardCharsets.UTF_8), Type.JSON)
        if (!json.contains("uuid")) {
            warning("无法从 Ashcon API 获取到玩家 $username 的皮肤.")
            return ""
        }
        return json.getString("textures.skin.url")!!
    }

    fun downloadPlayerSkinTexture(player: Player) {
        if (handler.contains(player.uniqueId)) return
        submitAsync {
            val textureUrl = getSkinTextureUrl(player.name)
            if (textureUrl.isEmpty()) return@submitAsync
            val url = URL(textureUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 20000
            val skin = newFolder(Adyeshach.plugin.dataFolder, "skin")
            val upload = newFolder(skin, "upload")
            val file = newFile(upload, "${player.name}.png")
            try {
                conn.inputStream.use {
                    val out = FileOutputStream(file)
                    val buff = ByteArray(1024)
                    var n: Int
                    while (it.read(buff).also { n = it } >= 0) {
                        out.write(buff, 0, n)
                    }
                }
                info("成功下载${player.name}的皮肤!正在尝试应用...")
                CommandAPI.uploadSkin(Bukkit.getConsoleSender(), "${player.name}.png", AdyeshachNetworkAPI.SkinModel.DEFAULT)
                handler.add(player.uniqueId)
            } catch (ex: IOException) {
                info("下载${player.name}的皮肤时出错,皮肤可能无法正常加载!")
                ex.printStackTrace()
            }
        }
    }
}