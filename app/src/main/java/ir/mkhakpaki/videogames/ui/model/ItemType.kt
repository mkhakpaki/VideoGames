package ir.mkhakpaki.videogames.ui.model

interface ItemType {
    val itemType: Int
}

open class ItemClass(override val itemType: Int) :ItemType