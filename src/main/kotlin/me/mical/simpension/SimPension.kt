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

import at.pcgamingfreaks.MarriageMaster.Bukkit.API.MarriageMasterPlugin
import me.mical.simpension.database.PluginDatabase
import me.mical.simpension.manager.ChildManager
import me.mical.simpension.manager.TextureManager
import me.mical.simpension.network.NetworkManager
import org.bukkit.Bukkit
import taboolib.common.platform.Plugin
import taboolib.common.platform.function.info
import taboolib.common.platform.function.pluginVersion
import taboolib.common.platform.function.submitAsync
import taboolib.module.database.Database
import taboolib.platform.util.onlinePlayers

object SimPension : Plugin() {

//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖保佑             永无BUG
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？

    override fun onEnable() {
        info("正在加载模拟养老,版本$pluginVersion...")
        PluginDatabase.init()
        ChildManager.init()
        info("插件启动成功!")
    }

    override fun onActive() {
        onlinePlayers.forEach { NetworkManager.downloadPlayerSkinTexture(it) }
        info("正在为所有玩家的在家门外的孩子生成实体...")
        ChildManager.childs.filter { it.age != -1 && it.view }.forEach {
            try {
                TextureManager.display(it)
                info("已生成${it.husband().name}和${it.wife().name}的孩子实体:${it.name}")
            } catch (ex: Throwable) {
                info("生成${it.husband().name}和${it.wife().name}的孩子(${it.name})失败!请查看下方报错!")
                ex.printStackTrace()
            }
        }
        Database.prepareClose {
            info("正在保存数据...")
            ChildManager.childs.forEach { it.save() }
            info("正在销毁所有孩子的实体...")
            TextureManager.entities.values.forEach { it.remove() }
        }
        submitAsync(period = 20L) {
            ChildManager.tick()
        }
    }

    override fun onDisable() {
        info("插件关闭成功!")
    }

    fun marriage(): MarriageMasterPlugin {
        return Bukkit.getPluginManager().getPlugin("MarriageMaster") as MarriageMasterPlugin
    }
}