package com.nonnonstop.fixchmate

import android.annotation.SuppressLint
import android.os.Build
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class FixChMate : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val field = XposedHelpers.findField(Build::class.java, "BRAND")
        modifyField(field, "FUJITSU")
    }

    @SuppressLint("DiscouragedPrivateApi")
    private fun modifyField(field: Field, value: String) {
        val modifiersField = try {
            Field::class.java.getDeclaredField("accessFlags")
        } catch (e: NoSuchFieldException) {
            try {
                Field::class.java.getDeclaredField("modifiers")
            } catch (e: NoSuchFieldException) {
                null
            }
        }
        modifiersField?.run {
            isAccessible = true
            setInt(field, field.modifiers and Modifier.FINAL.inv())
        }

        field.run {
            val oldIsAccessible = isAccessible
            isAccessible = true
            field.set(null, value)
            isAccessible = oldIsAccessible
        }

        modifiersField?.run {
            setInt(field, field.modifiers or Modifier.FINAL)
            isAccessible = false
        }
    }
}