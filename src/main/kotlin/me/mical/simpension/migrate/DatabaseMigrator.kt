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
package me.mical.simpension.migrate

import me.mical.simpension.ConfigReader
import me.mical.simpension.manager.ChildManager
import me.mical.simpension.manager.TextureManager
import me.mical.simpension.network.NetworkManager
import me.mical.simpension.`object`.Child
import me.mical.simpension.util.parseLoc
import me.mical.simpension.util.serializeToBase64
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.serverct.parrot.parrotx.function.textured
import taboolib.common.platform.function.info
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table
import java.util.*

/**
 * SimPension
 * me.mical.simpension.migrate.DatabaseMigrator
 *
 * @author xiaomu
 * @since 2023/2/6 9:01 AM
 */
object DatabaseMigrator {

    private val host = HostSQL(ConfigReader.database)

    private val deprecatedTable = Table("simpension_data", host) {
        add("husband") {
            type(ColumnTypeSQL.VARCHAR, 255)
        }
        add("wife") {
            type(ColumnTypeSQL.VARCHAR, 255)
        }
        add("uuid") {
            type(ColumnTypeSQL.VARCHAR, 255)
        }
        add("name") {
            type(ColumnTypeSQL.TEXT)
        }
        add("sex") {
            type(ColumnTypeSQL.TEXT)
        }
        add("age") {
            type(ColumnTypeSQL.INT)
        }
        add("deadline") {
            type(ColumnTypeSQL.INT)
        }
        add("pregnant") {
            type(ColumnTypeSQL.BIGINT)
        }
        add("birthday") {
            type(ColumnTypeSQL.BIGINT)
        }
        add("lastBirthday") {
            type(ColumnTypeSQL.BIGINT)
        }
        add("texture") {
            type(ColumnTypeSQL.TEXT)
        }
        add("lastLocation") {
            type(ColumnTypeSQL.TEXT)
        }
        add("view") {
            type(ColumnTypeSQL.BOOLEAN)
        }
        add("follow") {
            type(ColumnTypeSQL.BOOLEAN)
        }
        add("task") {
            type(ColumnTypeSQL.TEXT)
        }
        add("birthdayReal") {
            type(ColumnTypeSQL.BIGINT)
        }
    }

    private val deprecatedDataSource = host.createDataSource()

    fun migrate() {
        // 连接到旧数据表
        deprecatedTable.workspace(deprecatedDataSource) { createTable(true) }.run()
        // 所有旧数据
        val deprecatedChildren = arrayListOf<Child>()
        deprecatedTable.select(deprecatedDataSource) {
        }.forEach {
            val husband = UUID.fromString(getString("husband"))
            val wife = UUID.fromString(getString("wife"))
            val uuid = UUID.fromString(getString("uuid"))
            val name = getString("name")
            val sex = getString("sex")
            val age = getInt("age")
            val deadline = getInt("deadline")
            val pregnant = getLong("pregnant")
            val birthday = getLong("birthday")
            val lastBirthday = getLong("lastBirthday")
            val texture = getString("texture")
            val lastLocation = getString("lastLocation").parseLoc()
            val view = getBoolean("view")
            val follow = getBoolean("follow")
            val task = getString("task")
            val birthdayReal = getLong("birthdayReal")
            val child = Child().apply {
                this.husband = husband
                this.wife = wife
                this.uuid = uuid
                this.name = name
                this.sex = sex
                this.age = age
                this.deadline = deadline
                this.pregnant = pregnant
                this.birthday = birthday
                this.lastBirthday = lastBirthday
                this.texture = texture
                this.lastLocation = lastLocation
                this.view = view
                this.follow = follow
                this.task = task
                this.birthdayReal = birthdayReal
            }
            if (child.texture.isNotEmpty()) {
                val head = ItemStack(Material.PLAYER_HEAD, 1)
                val uuid0 = Bukkit.getOfflinePlayer(child.texture).uniqueId
                if (NetworkManager.handler.containsKey(uuid0)) {
                    head textured NetworkManager.getTextureUrlEnd(uuid0)
                }
                child.head = head.serializeToBase64()
            }
            deprecatedChildren.add(child)
        }
        deprecatedChildren.forEach { it.save() }
        ChildManager.init()
        info("正在为所有玩家的在家门外的孩子生成实体...")
        ChildManager.children.filter { it.age != -1 && it.view }.forEach {
            try {
                TextureManager.display(it)
                info("已生成${it.husband().name}和${it.wife().name}的孩子实体:${it.name}")
            } catch (ex: Throwable) {
                info("生成${it.husband().name}和${it.wife().name}的孩子(${it.name})失败!请查看下方报错!")
                ex.printStackTrace()
            }
        }
    }
}