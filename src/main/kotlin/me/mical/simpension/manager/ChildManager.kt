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
package me.mical.simpension.manager

import me.mical.simpension.ConfigReader
import me.mical.simpension.database.PluginDatabase
import me.mical.simpension.`object`.Child
import me.mical.simpension.util.parseLoc
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.function.info
import taboolib.common.util.random
import taboolib.common5.mirrorNow
import taboolib.platform.util.sendLang
import java.util.UUID

/**
 * SimPension
 * me.mical.simpension.manager.ChildManager
 *
 * @author xiaomu
 * @since 2023/2/1 12:53 PM
 */
object ChildManager {

    val children = arrayListOf<Child>()

    fun init() {
        info("正在从数据库中加载数据...")
        with(PluginDatabase) {
            table.select(dataSource) {
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
                children.add(child)
            }
        }
        info("成功从数据库中加载数据!")
    }

    fun pregnant(p1: Player, p2: Player): Child {
        val wife = if (random(101) < 50) p1 else p2
        val husband = if (p1 == wife) p2 else p1
        val world = Bukkit.getWorld(ConfigReader.world)!!
        val time = world.fullTime
        return Child().apply {
            this.husband = husband.uniqueId
            this.wife = wife.uniqueId
            this.pregnant = time
            this.birthday = time + ConfigReader.pregnant
            this.lastBirthday = this.birthday
        }.also {
            it.save()
            children.add(it)
        }
    }

    fun tick() {
        mirrorNow("SimPension:Tick") {
            val world = Bukkit.getWorld(ConfigReader.world) ?: return@mirrorNow
            val time = world.fullTime
            children.filter { it.age == -1 }.forEach {
                if (it.birthday <= time) { // 要生了
                    it.age += 1
                    TextureManager.display(it)
                    it.husband().player?.sendLang("birth")
                    it.wife().player?.sendLang("birth")
                    it.birthdayReal = System.currentTimeMillis()
                    if (it.age >= it.deadline) {
                        it.husband().player?.sendLang("died", it.name)
                        it.wife().player?.sendLang("died", it.name)
                        it.view = false
                        TextureManager.destroy(it)
                    }
                    it.save()
                }
            }
            children.filter { it.age < it.deadline }.forEach {
                if ((it.lastBirthday + ConfigReader.year) <= time) { // 喝, 长大啦?
                    it.age += 1 // 喝, 必须的.
                    it.lastBirthday = time
                    if (it.age >= it.deadline) {
                        it.husband().player?.sendLang("died", it.name)
                        it.wife().player?.sendLang("died", it.name)
                        it.view = false
                        TextureManager.destroy(it)
                        return@mirrorNow
                    }
                    it.husband().player?.sendLang("birthday", it.name, it.age)
                    it.wife().player?.sendLang("birthday", it.name, it.age)
                    it.save()
                    if (it.age >= ConfigReader.adult) {
                        TextureManager.destroy(it)
                        TextureManager.display(it) // 重新显示一下, 因为成年需要从 AdyVillager 变为 AdyHuman
                    }
                }
            }
        }
    }
}