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

import me.mical.simpension.manager.ChildManager
import me.mical.simpension.`object`.Child
import me.mical.simpension.ui.util.applyChild
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.serverct.parrot.parrotx.mechanism.Reloadable
import org.serverct.parrot.parrotx.ui.MenuComponent
import org.serverct.parrot.parrotx.ui.config.MenuConfiguration
import org.serverct.parrot.parrotx.ui.feature.util.MenuFunctionBuilder
import taboolib.common5.mirrorNow
import taboolib.module.chat.colored
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.sendLang

/**
 * SimPension
 * me.mical.simpension.ui.ChildsUI
 *
 * @author xiaomu
 * @since 2023/2/1 3:53 PM
 */
@MenuComponent("Child")
object ChildListUI {

    @Config("gui/main.yml")
    private lateinit var source: Configuration
    private lateinit var config: MenuConfiguration

    @Reloadable
    fun reload() {
        source.reload()
        config = MenuConfiguration(source)
    }

    fun open(player: Player) {
        if (!::config.isInitialized) {
            config = MenuConfiguration(source)
        }
        player.sendLang("open-menu")
        mirrorNow("SimPension:OpenUI:ChildList") {
            player.openMenu<Linked<Child>>(config.title().colored()) {
                virtualize()
                val (shape, templates) = config
                rows(shape.rows)
                val slots = shape["Child\$information"].toList()
                slots(slots)
                elements { ChildManager.children.filter { it.husband == player.uniqueId || it.wife == player.uniqueId } }

                onBuild { _, it ->
                    shape.all("Child\$information", "Previous", "Next", "Close") { slot, index, item, _ ->
                        it.setItem(slot, item(slot, index))
                    }
                }

                val template = templates.require("Child\$information")
                onGenerate { _, member, index, slot ->
                    template(slot, index, member)
                }

                onClick { event, member ->
                    template.handle(event, member)
                }

                shape["Previous"].first().let { slot ->
                    setPreviousPage(slot) { it, _ ->
                        templates("Previous", slot, it)
                    }
                }

                shape["Next"].first().let { slot ->
                    setNextPage(slot) { it, _ ->
                        templates("Next", slot, it)
                    }
                }

                onClick { event ->
                    event.isCancelled = true
                    if (event.rawSlot in shape && event.rawSlot !in slots) {
                        templates[event.rawSlot]?.handle(event)
                    }
                }
            }
        }
    }

    @MenuComponent
    private val information = MenuFunctionBuilder {
        onBuild { (_, _, _, _, icon, args) ->
            icon applyChild args[0] as Child
        }

        onClick { (_, _, event, args) ->
            val child = args[0] as Child
            if (event.virtualEvent().clickType == ClickType.LEFT) {
                if (child.age >= child.deadline) return@onClick
                if (child.age == -1) return@onClick
                ChildControlUI.open(event.clicker, child)
            }
        }
    }
}