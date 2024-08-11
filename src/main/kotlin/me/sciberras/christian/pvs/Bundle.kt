package me.sciberras.christian.pvs

import com.intellij.DynamicBundle
import org.jetbrains.annotations.NonNls
import org.jetbrains.annotations.PropertyKey
import java.util.function.Supplier

@NonNls
private const val BUNDLE = "messages.PVSBundle"

internal object Bundle {
    private val INSTANCE = DynamicBundle(Bundle::class.java, BUNDLE)

    fun message(key: String, vararg params: Any): String {
        return INSTANCE.getMessage(key, *params)
    }

    fun lazyMessage(@PropertyKey(resourceBundle = BUNDLE) key: String, vararg params: Any): Supplier<String> {
        return INSTANCE.getLazyMessage(key, *params)
    }
}
