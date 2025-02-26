package com.pluto.tool

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pluto.tool.modules.currentScreen.CurrentScreenTool
import com.pluto.tool.modules.grid.GridViewTool
import com.pluto.tool.modules.ruler.RulerTool
import com.pluto.utilities.AppState

internal class ToolManager(private val application: Application, state: MutableLiveData<AppState>) {

    val tools: LinkedHashSet<PlutoTool> = linkedSetOf<PlutoTool>().apply {
        add(RulerTool())
        add(GridViewTool())
        add(CurrentScreenTool())
//        add(ScreenHistoryTool())
    }

    init {
        state.observeForever {
            if (it is AppState.Background) {
                tools.forEach { tool ->
                    tool.onToolUnselected()
                }
            }
        }
    }

    fun initialise() {
        tools.forEach {
            it.initialise(application)
        }
    }

    fun get(identifier: String): PlutoTool? {
        return tools.firstOrNull {
            it.id == identifier
        }
    }

    fun select(id: String) {
        get(id)?.onToolSelected()
    }
}
