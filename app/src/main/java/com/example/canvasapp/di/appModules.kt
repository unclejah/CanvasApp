package com.example.canvasapp.di
import com.example.canvasapp.ui.CanvasViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val module = module {
    viewModel{
        CanvasViewModel()
    }
}