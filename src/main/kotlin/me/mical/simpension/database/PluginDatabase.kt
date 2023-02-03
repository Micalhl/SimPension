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
package me.mical.simpension.database

import me.mical.simpension.ConfigReader
import me.mical.simpension.`object`.Child
import me.mical.simpension.util.parseString
import taboolib.common.platform.function.info
import taboolib.common.platform.function.severe
import taboolib.module.database.ColumnTypeSQL
import taboolib.module.database.HostSQL
import taboolib.module.database.Table

/**
 * SimPension
 * me.mical.simpension.database.PluginDatabase
 *
 * @author xiaomu
 * @since 2023/2/1 11:07 AM
 */
object PluginDatabase {

    private val host = HostSQL(ConfigReader.database)

    val table = Table("simpension_data", host) {
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

    val dataSource = host.createDataSource()

    fun init() {
        try {
            table.workspace(dataSource) { createTable(true) }.run()
            info("已初始化数据库.")
        } catch (ex: Throwable) {
            severe("加载 数据库 时遇到错误(${ex.message}).")
            if (host.user == "root" && host.password == "password" && host.database == "minecraft") {
                severe("请去配置更改默认数据库配置!")
                return
            }
            ex.printStackTrace()
        }
    }

    fun refresh(child: Child) {
        if (table.find(dataSource) {
            where {
                "husband" eq child.husband.toString()
                "wife" eq child.wife.toString()
                "uuid" eq child.uuid.toString()
            }
            }) {
            table.update(dataSource) {
                where {
                    "husband" eq child.husband.toString()
                    "wife" eq child.wife.toString()
                    "uuid" eq child.uuid.toString()
                }
                set("name", child.name)
                set("sex", child.sex)
                set("age", child.age)
                set("deadline", child.deadline)
                set("pregnant", child.pregnant)
                set("birthday", child.birthday)
                set("lastBirthday", child.lastBirthday)
                set("texture", child.texture)
                set("lastLocation", child.lastLocation.parseString())
                set("view", child.view)
                set("follow", child.follow)
                set("task", child.task)
                set("birthdayReal", child.birthdayReal)
            }
        } else {
            table.insert(dataSource, "husband", "wife", "uuid", "name", "sex", "age", "deadline", "pregnant", "birthday", "lastBirthday", "texture", "lastLocation", "view", "follow", "task", "birthdayReal") {
                value(child.husband.toString(), child.wife.toString(), child.uuid.toString(), child.name, child.sex, child.age, child.deadline, child.pregnant, child.birthday, child.lastBirthday, child.texture, child.lastLocation.parseString(), child.view, child.follow, child.task, child.birthdayReal)
            }
        }
    }
}