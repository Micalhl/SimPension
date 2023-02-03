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
package me.mical.simpension.ui

import ink.ptms.adyeshach.core.entity.type.AdyHuman
import ink.ptms.adyeshach.core.entity.type.AdyVillager
import ink.ptms.adyeshach.impl.entity.controller.ControllerRandomStrollLand
import ink.ptms.adyeshach.module.command.EntitySource
import ink.ptms.adyeshach.module.command.multiControl
import me.mical.simpension.ConfigReader
import me.mical.simpension.controller.ControllerFollowParents
import me.mical.simpension.manager.TextureManager
import me.mical.simpension.`object`.Child
import me.mical.simpension.ui.util.applyChild
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.platform.util.inputBook
import taboolib.platform.util.sendLang

/**
 * SimPension
 * me.mical.simpension.ui.ChildControlUI
 *
 * @author xiaomu
 * @since 2023/2/3 12:50 PM
 */
@MenuComponent("Control")
object ChildControlUI {

    @Config("gui/control.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player, child: Child) {
        if (!::config.isInitialized) {
            config = MenuConfiguration(source)
        }
        player.openMenu<Basic>(config.title("name" to { child.name }).colored()) {
            virtualize()
            val (shape, templates) = config
            rows(shape.rows)
            map(*shape.array)

            onBuild { _, inventory ->
                shape.all { slot, index, item, _ ->
                    inventory.setItem(slot, item(slot, index, child))
                }
            }

            onClick {
                it.isCancelled = true
                if (it.rawSlot in shape) {
                    templates[it.rawSlot]?.handle(it, child)
                }
            }
        }
    }

    @MenuComponent
    private val changeName = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            icon applyChild args[0] as Child
        }

        onClick { (_, _, event, args) ->
            val child = args[0] as Child
            if (child.age >= child.deadline) return@onClick
            if (event.virtualEvent().clickType == ClickType.LEFT) {
                event.clicker.closeInventory()
                event.clicker.inputBook(ConfigReader.book.colored()) { input ->
                    child.name = input.joinToString(separator = "")
                    child.save()
                    (TextureManager.entities[child.uuid] as? AdyHuman)?.setName(child.name.colored())
                    (TextureManager.entities[child.uuid] as? AdyVillager)?.setCustomName(child.name.colored())
                    open(event.clicker, child)
                }
            }
        }
    }

    @MenuComponent
    private val tp = MenuFunctionBuilder {
        onClick { (_, _, event, args) ->
            val child = args[0] as Child
            if (child.age >= child.deadline) return@onClick
            if (event.virtualEvent().clickType == ClickType.LEFT) {
                event.clicker.closeInventory()
                if (!child.view) {
                    event.clicker.sendLang("fail", child.name)
                    return@onClick
                }
                if (TextureManager.entities[child.uuid] == null) {
                    event.clicker.sendLang("internal-error")
                    return@onClick
                }
                event.clicker.teleport(TextureManager.entities[child.uuid]!!.getLocation())
                event.clicker.sendLang("tp", child.name)
            }
        }
    }

    @MenuComponent
    private val tphere = MenuFunctionBuilder {
        onClick { (_, _, event, args) ->
            val child = args[0] as Child
            if (child.age >= child.deadline) return@onClick
            if (event.virtualEvent().clickType == ClickType.LEFT) {
                event.clicker.closeInventory()
                if (!child.view) {
                    event.clicker.sendLang("fail", child.name)
                    return@onClick
                }
                if (TextureManager.entities[child.uuid] == null) {
                    event.clicker.sendLang("internal-error")
                    return@onClick
                }
                TextureManager.entities[child.uuid]!!.teleport(event.clicker.location)
                event.clicker.sendLang("tphere", child.name)
            }
        }
    }

    @MenuComponent
    private val walk = MenuFunctionBuilder {
        onClick { (_, _, event, args) ->
            val child = args[0] as Child
            if (child.age >= child.deadline) return@onClick
            if (event.virtualEvent().clickType == ClickType.LEFT) {
                event.clicker.closeInventory()
                if (!child.view) {
                    event.clicker.sendLang("fail", child.name)
                    return@onClick
                }
                if (TextureManager.entities[child.uuid] == null) {
                    event.clicker.sendLang("internal-error")
                    return@onClick
                }
                val entityInstance = TextureManager.entities[child.uuid]!!
                if (entityInstance.world != event.clicker.world) {
                    event.clicker.sendLang("different-world", child.name)
                    return@onClick
                }
                if (entityInstance.hasVehicle()) {
                    event.clicker.sendLang("in-vehicle", child.name)
                    return@onClick
                }
                entityInstance.moveTarget = event.clicker.location
                event.clicker.sendLang("move", child.name)
            }
        }
    }

    @MenuComponent
    private val view = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            icon applyChild args[0] as Child
        }

        onClick { (_, _, event, args) ->
            val child = args[0] as Child
            if (child.age >= child.deadline) return@onClick
            if (event.virtualEvent().clickType == ClickType.LEFT) {
                child.view = !child.view
                child.save()
                if (child.view) {
                    TextureManager.display(child)
                } else {
                    TextureManager.hide(child)
                }
                event.clicker.closeInventory()
                open(event.clicker, child)
            }
        }
    }

    @MenuComponent
    private val follow = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            icon applyChild args[0] as Child
        }

        onClick { (_, _, event, args) ->
            val child = args[0] as Child
            if (child.age >= child.deadline) return@onClick
            if (event.virtualEvent().clickType == ClickType.LEFT) {
                if (!child.view) {
                    event.clicker.sendLang("fail", child.name)
                    return@onClick
                }
                child.follow = !child.follow
                child.save()
                val entityInstance = TextureManager.entities[child.uuid]
                if (entityInstance == null) {
                    event.clicker.sendLang("internal-null")
                    return@onClick
                }
                if (child.follow) {
                    entityInstance.registerController(ControllerFollowParents(entityInstance, child))
                    entityInstance.unregisterController(ControllerRandomStrollLand::class.java)
                } else {
                    entityInstance.registerController(ControllerRandomStrollLand(entityInstance))
                    entityInstance.unregisterController(ControllerFollowParents::class.java)
                }
                event.clicker.updateInventory()
            }
        }
    }

    @MenuComponent
    private val backToList = MenuFunctionBuilder {
        onClick { (_, _, event, _) ->
            if (event.virtualEvent().clickType == ClickType.LEFT) {
                event.clicker.closeInventory()
                ChildListUI.open(event.clicker)
            }
        }
    }
}