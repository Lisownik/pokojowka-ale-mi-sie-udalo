package com.example.pokojowka_mobile.debug

import android.util.Log

enum class LogClasses(var color: String){
    NETWORK("NETWORK"),
    NOTHING("NOTHIN"),
    TEST("TEST"),
    DEBUG("DEBUG")
}

object Debug{


    var debugMode: Boolean = true
    var deepErrorsMode: Boolean = true

    fun isDebugMode(): Boolean {
        return debugMode
    }

    fun setDMode( mode: Boolean) {
        debugMode = mode
    }

    fun setEMode(mode: Boolean) {
        Log.d("Debug", "Setting error mode to $mode")
        deepErrorsMode = mode
    }




    fun debug(func: () -> Unit) {
        if (debugMode) {
            func()
        }
    }

    fun logDebug(logClasses: LogClasses, text: String) {
        debug {
            Log.i(logClasses.color, text)
        }
    }

    fun descError(func: () -> Unit){
        if(deepErrorsMode){
            func()
        }
    }
}
