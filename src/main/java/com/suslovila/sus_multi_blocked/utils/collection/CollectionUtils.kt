package com.suslovila.sus_multi_blocked.utils.collection

import java.util.*

fun <E> List<E>.cycledFrom(element: E): MutableList<E>? {
    if (this.isEmpty() || !this.contains(element)) return null
    val indexFrom = this.indexOf(element)
    val resultList = this.subList(fromIndex = indexFrom, toIndex = size).toMutableList()
    resultList.addAll(
        this.subList(fromIndex = 0, toIndex = indexFrom)
    )
    return resultList
}

fun <E> Collection<E>.nextCycled(element: E): E? {
    val index = indexOf(element)
    if(index == -1) return null
    val indexOfNext = (index + 1) % size
    return this.toList().getOrNull(indexOfNext)
}

fun <E> Collection<E>.previousCycled(element: E): E? {
    val currentIndex = indexOf(element)
    if(currentIndex == -1) return null
    val indexOfNext = if(currentIndex - 1 < 0) (size - 1) else currentIndex - 1
    return this.toList().getOrNull(indexOfNext)
}

fun getNumbersInRange(start: Int, end: Int): List<Int>? {
    val result: MutableList<Int> = ArrayList()
    for (i in start until end) {
        result.add(i)
    }
    return result
}

fun <T> shuffle(list: List<T?>?): List<T?>? {
    Collections.shuffle(list)
    return list
}

