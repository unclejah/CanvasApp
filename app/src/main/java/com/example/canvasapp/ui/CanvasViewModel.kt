package com.example.canvasapp.ui

import com.example.canvasapp.base.BaseViewModel
import com.example.canvasapp.base.Event
import com.example.canvasapp.data.COLOR
import com.example.canvasapp.data.SIZE
import com.example.canvasapp.data.TOOLS
import com.example.canvasapp.data.model.ToolItem

class CanvasViewModel : BaseViewModel<ViewState>() {
    override fun initialViewState(): ViewState = ViewState(

        colorList = enumValues<COLOR>().map { ToolItem.ColorModel(it.value) },
        toolsList = enumValues<TOOLS>().map { ToolItem.ToolModel(it) },
        sizeList = enumValues<SIZE>().map { ToolItem.SizeModel(it.value) },
        canvasViewState = CanvasViewState(
            color = COLOR.BLACK,
            size = SIZE.MEDIUM,
            tools = TOOLS.PALETTE
        ),
        isBrushSizeChangerVisible = false,
        isPaletteVisible = false,
        isToolsVisible = false,

        )

    init {
        processDataEvent(DataEvent.OnSetDefaultTools(tool = TOOLS.NORMAL, color = COLOR.BLACK))
    }

    override fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {
            is UiEvent.OnToolbarClicked -> {
                return previousState.copy(
                    isToolsVisible = !previousState.isToolsVisible,
                    isPaletteVisible = false
                )
            }

            is UiEvent.OnToolsClick -> {
                when (event.index) {

                    TOOLS.PALETTE.ordinal -> {
                        if (previousState.isBrushSizeChangerVisible == true)
                            return previousState.copy(
                                isPaletteVisible = !previousState.isPaletteVisible,
                                isBrushSizeChangerVisible = !previousState.isBrushSizeChangerVisible
                            )
                        return previousState.copy(
                            isPaletteVisible = !previousState.isPaletteVisible
                        )
                    }

                    TOOLS.SIZE.ordinal -> {
                        if (previousState.isPaletteVisible == true)
                            return previousState.copy(
                                isBrushSizeChangerVisible = !previousState.isBrushSizeChangerVisible,
                                isPaletteVisible = !previousState.isPaletteVisible
                            )
                        return previousState.copy(
                            isBrushSizeChangerVisible = !previousState.isBrushSizeChangerVisible
                        )
                    }
                    TOOLS.ERASER.ordinal -> {
                        return previousState.copy(
                            canvasViewState = previousState.canvasViewState.copy (
                                color = COLOR.WHITE,
                                tools = TOOLS.NORMAL)
                        )
                    }

                    else -> {

                        val toolsList = previousState.toolsList.mapIndexed() { index, model ->
                            if (index == event.index) {
                                model.copy(isSelected = true)
                            } else {
                                model.copy(isSelected = false)
                            }
                        }

                        return previousState.copy(
                            toolsList = toolsList,
                            canvasViewState = previousState.canvasViewState.copy(tools = TOOLS.values()[event.index])
                        )
                    }
                }
            }

            is UiEvent.OnPaletteClicked -> {
                val selectedColor = COLOR.values()[event.index]
                val toolsList = previousState.toolsList.map {
                    if (it.type == TOOLS.PALETTE) {
                        it.copy(selectedColor = selectedColor)
                    } else{
                        it
                    }
                }
                return previousState.copy(
                    toolsList = toolsList,
                    canvasViewState = previousState.canvasViewState.copy (color = selectedColor))
            }

            is UiEvent.OnSizeClick -> {
                val selectedSize = SIZE.values()[event.index]
                val toolsList = previousState.toolsList.map {
                    if (it.type == TOOLS.SIZE) {
                        it.copy(selectedSize = selectedSize)
                    } else{
                        it
                    }
                }
                return previousState.copy(
                    toolsList = toolsList,
                    canvasViewState = previousState.canvasViewState.copy (size = selectedSize))
            }

            is DataEvent.OnSetDefaultTools -> {
                val toolsList = previousState.toolsList.map { model ->
                    if (model.type == event.tool) {
                        model.copy(isSelected = true, selectedColor = event.color)
                    } else {
                        model.copy(isSelected = false)
                    }
                }
                return previousState.copy(toolsList = toolsList)
            }
            else -> return null
        }
    }
}