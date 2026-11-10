package com.example.demo

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.example.demo.resources.Res
import com.example.demo.resources.app_name
import org.jetbrains.compose.resources.stringResource

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name)
    ) {
        App()
    }
}
