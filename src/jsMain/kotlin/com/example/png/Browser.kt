package com.example.png

import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import okio.Buffer
import okio.BufferedSource
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.File
import org.w3c.files.FileList
import org.w3c.files.FileReader
import react.*
import react.dom.*
import kotlin.browser.document


@ExperimentalUnsignedTypes
fun main() {
    render(document.getElementById("app")) {
        child(App::class) {}
    }
}

@ExperimentalUnsignedTypes
interface AppState : RState {
    var parserResults: List<Parser.Result>
}

@ExperimentalUnsignedTypes
class App : RComponent<RProps, AppState>() {
    val parser = Parser()
    override fun AppState.init() {
        parserResults = emptyList()
    }

    fun handleFileSelect(e: Event) {
        e.preventDefault()
        val files: FileList? = (e.target as? HTMLInputElement)?.files
        setState {
            parserResults = emptyList()
        }
        if (files != null) {
            println(files)
            for (i in 0.until(files.length)) {
                val file: File? = files.item(i)
                if (file != null) {
                    readFile(file) { source ->
                        setState {
                            val result = parser.parse(source)
                            parserResults += result
                        }
                    }
                }
            }
        }
    }

    private fun readFile(file: File, body: (BufferedSource) -> Unit) {
        val fileReader = FileReader()
        fileReader.onload = {
            val arrayBuffer = it.target.asDynamic().result as? ArrayBuffer
            if (arrayBuffer != null) {
                val uint8Array = Uint8Array(arrayBuffer)
                val buffer = Buffer()
                for (i in 0.until(uint8Array.byteLength)) {
                    buffer.writeByte(uint8Array[i].toInt())
                }
                body(buffer)
            }
        }
        fileReader.readAsArrayBuffer(file)
    }

    override fun RBuilder.render() {
        h1 {
            +"Png Parser"
        }
        div {
            input(InputType.file) {
                attrs {
                    onChangeFunction = {
                        handleFileSelect(it)
                    }
                }
            }
            state.parserResults.forEach { result ->
                table {
                    tbody {
                        result.chunks.forEach { chunk ->
                            tr {
                                td {
                                    +chunk.toString()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}