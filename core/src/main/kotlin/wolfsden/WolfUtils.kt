package wolfsden

import com.badlogic.gdx.utils.XmlReader


fun <T : Comparable<T>> T.between(low: T, high: T): Boolean {
    return when {
        this < low -> false
        this > high -> false
        else -> true
    }
}

fun Int?.nz(value: Int = 0) = this ?: value

fun XmlReader.Element.nz(attr: String, block: () -> Unit) {
    if (this.hasAttribute(attr) || this.hasChild(attr)) block()
}
