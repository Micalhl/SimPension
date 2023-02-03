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
import me.mical.simpension.SimPension
import me.mical.simpension.util.MathUtils
import me.mical.simpension.util.putIfPresent
import org.bukkit.Bukkit
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import taboolib.common.platform.event.EventPriority
import taboolib.common.platform.event.SubscribeEvent
import taboolib.platform.util.isMainhand
import taboolib.platform.util.sendLang
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * 写的有点儿垃圾
 *
 * SimPension
 * me.mical.simpension.manager.SexManager
 *
 * @author xiaomu
 * @since 2023/2/1 11:25 AM
 */
object SexManager {

    // 如果 MarriageMaster 能公开访问到 Marry ID 我就不这么费劲了!
    private val handler = ConcurrentHashMap<Pair<UUID, UUID>, Int>()
    val planning = arrayListOf<Pair<UUID, UUID>>() // 计划生育

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun e(e: PlayerInteractEntityEvent) {
        if (!e.isMainhand()) return
        if (e.rightClicked !is Player) return
        if (Bukkit.getOfflinePlayers().none { it.uniqueId == e.rightClicked.uniqueId }) return // 防止右键 NPC 报错 (Citizens)
        if (!e.player.isSneaking) return
        val target = e.rightClicked as Player
        val player = SimPension.marriage().getPlayerData(e.player)
        if (!player.isMarried) return // 单身狗滚
        val partner = SimPension.marriage().getPlayerData(target)
        if (!player.isPartner(partner)) return // 跟别人对象做你妈爱呢?
        val data1 = player.uuid to partner.uuid
        val data2 = partner.uuid to player.uuid
        val amount = ChildManager.childs.filter { it.age < it.deadline }
            .filter { (it.husband == player.uuid && it.wife == partner.uuid) || (it.husband == partner.uuid && it.wife == player.uuid) }
            .size + 1 // +1 是因为算上要生的这个孩子
        if (amount > ConfigReader.limit && !planning.contains(data1) && !planning.contains(data2)) {
            e.player.sendLang("more", ConfigReader.limit, MathUtils.calculate(ConfigReader.money, "more", amount - ConfigReader.limit), amount - ConfigReader.limit, e.rightClicked.uniqueId.toString())
            e.rightClicked.sendLang("more", ConfigReader.limit, MathUtils.calculate(ConfigReader.money, "more", amount - ConfigReader.limit), amount - ConfigReader.limit, e.player.uniqueId.toString())
            return
        }
        var click = handler.computeIfAbsent(data1) { handler.getOrElse(data2) { 0 } }
        click++
        handler.putIfPresent(data1, click)
        handler.putIfPresent(data2, click) // 存在双方互射情况
        e.player.spawnParticle(Particle.HEART, e.player.location, 50, 1.0, 1.0, 1.0)
        target.spawnParticle(Particle.HEART, e.player.location, 50, 1.0, 1.0, 1.0)
        if (click == ConfigReader.sex) {
            val child = ChildManager.pregnant(e.player, target)
            child.husband().player!!.sendLang("pregnant", child.wife().name!!)
            child.wife().player!!.sendLang("pregnant-wife", child.husband().name!!)
            handler.remove(data1)
            handler.remove(data2)
            planning.remove(data1)
            planning.remove(data2)
        }
        e.isCancelled = true
    }
}