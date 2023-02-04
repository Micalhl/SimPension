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
package me.mical.simpension

import taboolib.library.configuration.ConfigurationSection
import taboolib.module.configuration.Config
import taboolib.module.configuration.ConfigNode
import taboolib.module.configuration.Configuration

/**
 * SimPension
 * me.mical.simpension.ConfigReader
 *
 * @author xiaomu
 * @since 2023/2/1 11:01 AM
 */
object ConfigReader {

    @Config(autoReload = true)
    lateinit var config: Configuration

    @ConfigNode("Limit")
    var limit = 3

    @ConfigNode("Money")
    var money = "100000+20000*more"

    @ConfigNode("Sex")
    var sex = 100

    @ConfigNode("Pregnant")
    var pregnant = 240000L

    @ConfigNode("Year")
    var year = 288000L

    @ConfigNode("World")
    var world = "techbedrock"

    @ConfigNode("Spawn")
    var spawn = "techbedrock~100,100,100"

    @ConfigNode("WalkRadius")
    var walkRadius = 15.0

    @ConfigNode("DateFormat")
    var dateForamt = "yyyy-DD-mm HH:mm:ss"

    @ConfigNode("Son")
    var son = "儿子"

    @ConfigNode("Daughter")
    var daughter = "女儿"

    @ConfigNode("Male")
    var male = "男"

    @ConfigNode("Female")
    var female = "女"

    @ConfigNode("DefaultName")
    var defaultName = "{0}与{1}的{2}"

    @ConfigNode("Adult")
    var adult = 18

    @ConfigNode("Died")
    var died = " &c(已去世)"

    @ConfigNode("Yes")
    var yes = "是"

    @ConfigNode("No")
    var no = "否"

    @ConfigNode("Book")
    var book = "&f请在书中输入孩子名称"

    @ConfigNode("State")
    var state = "&d[怀孕中]"

    val database: ConfigurationSection
        get() = config.getConfigurationSection("Database") ?: error("no database section")
}