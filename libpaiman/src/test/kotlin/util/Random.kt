package util

import net.bytebuddy.utility.RandomString
import java.nio.charset.Charset
import java.util.Random

/**
 * Frequent used random extensions
 * @author x4fyr
 * Created on 3/5/17.
 */

fun Random.nextString(length: Int = 100): String = RandomString.make(length)


fun Random.nextPositiveInt(limit: Int): Int {
    val i = this.nextInt(limit)
    return if (i < 0) -i else i
}

fun Random.nextPositiveInt(): Int {
    val i = this.nextInt()
    return if (i < 0) -i else i
}

fun Random.nextPositiveLong(): Long {
    val l = this.nextLong()
    return if (l < 0) -l else l
}

fun Random.nextPositiveDouble(): Double {
    val d = this.nextDouble()
    return if (d < 0) -d else d
}

