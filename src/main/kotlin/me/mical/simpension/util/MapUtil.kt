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
package me.mical.simpension.util

import java.util.concurrent.ConcurrentHashMap

/**
 * SimPension
 * me.mical.simpension.util.MapUtil
 *
 * @author xiaomu
 * @since 2023/2/1 12:44 PM
 */
inline fun <reified T, R> ConcurrentHashMap<T, R>.putIfPresent(key: T, value: R) {
    if (containsKey(key)) {
        this[key] = value
    }
}