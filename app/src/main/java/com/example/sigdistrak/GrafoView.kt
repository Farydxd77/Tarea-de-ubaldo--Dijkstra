package com.example.sigdistrak

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class GrafoView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var grafo: Grafo? = null
    private var caminoOptimo: List<String> = emptyList()

    private val paintNodo = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val paintNodoBorde = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 4f
        isAntiAlias = true
        color = Color.WHITE
    }

    private val paintTextoNodo = Paint().apply {
        color = Color.WHITE
        textSize = 48f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
    }

    private val paintArista = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 6f
        isAntiAlias = true
        color = Color.parseColor("#607D8B")
    }

    private val paintAristaOptima = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 10f
        isAntiAlias = true
        color = Color.parseColor("#4CAF50")
    }

    private val paintPeso = Paint().apply {
        color = Color.parseColor("#FF5722")
        textSize = 32f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val paintPesoFondo = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    // Posiciones de los nodos (x, y) en porcentaje de la pantalla
    private val posicionesNodos = mapOf(
        "A" to Pair(0.15f, 0.40f),
        "B" to Pair(0.35f, 0.20f),
        "C" to Pair(0.60f, 0.15f),
        "D" to Pair(0.35f, 0.45f),
        "E" to Pair(0.25f, 0.65f),
        "F" to Pair(0.60f, 0.45f),
        "G" to Pair(0.40f, 0.75f),
        "H" to Pair(0.60f, 0.75f),
        "I" to Pair(0.80f, 0.45f),
        "J" to Pair(0.80f, 0.75f)
    )

    fun setGrafo(grafo: Grafo) {
        this.grafo = grafo
        invalidate()
    }

    fun setCaminoOptimo(camino: List<String>) {
        this.caminoOptimo = camino
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (grafo == null) return

        val width = width.toFloat()
        val height = height.toFloat()

        // Dibujar aristas primero
        grafo?.obtenerTodasLasAristas()?.forEach { (origen, destino, peso) ->
            val posOrigen = posicionesNodos[origen]
            val posDestino = posicionesNodos[destino]

            if (posOrigen != null && posDestino != null) {
                val x1 = posOrigen.first * width
                val y1 = posOrigen.second * height
                val x2 = posDestino.first * width
                val y2 = posDestino.second * height

                // Verificar si esta arista está en el camino óptimo
                val esOptima = estaEnCaminoOptimo(origen, destino)
                val paint = if (esOptima) paintAristaOptima else paintArista

                // Dibujar línea
                canvas.drawLine(x1, y1, x2, y2, paint)

                // Dibujar flecha
                dibujarFlecha(canvas, x1, y1, x2, y2, paint)

                // Dibujar peso
                val xPeso = (x1 + x2) / 2
                val yPeso = (y1 + y2) / 2

                // Fondo blanco para el peso
                canvas.drawCircle(xPeso, yPeso, 25f, paintPesoFondo)

                // Número del peso
                canvas.drawText(
                    peso.toString(),
                    xPeso,
                    yPeso + 12f,
                    paintPeso
                )
            }
        }

        // Dibujar nodos encima
        posicionesNodos.forEach { (nodo, pos) ->
            val x = pos.first * width
            val y = pos.second * height

            // Color del nodo
            paintNodo.color = when {
                nodo == "A" -> Color.parseColor("#4CAF50") // Verde para inicio
                nodo == "J" -> Color.parseColor("#F44336") // Rojo para fin
                caminoOptimo.contains(nodo) -> Color.parseColor("#2196F3") // Azul para camino
                else -> Color.parseColor("#1976D2") // Azul oscuro para otros
            }

            // Dibujar círculo del nodo
            canvas.drawCircle(x, y, 50f, paintNodo)
            canvas.drawCircle(x, y, 50f, paintNodoBorde)

            // Dibujar letra del nodo
            canvas.drawText(nodo, x, y + 18f, paintTextoNodo)
        }
    }

    private fun estaEnCaminoOptimo(origen: String, destino: String): Boolean {
        for (i in 0 until caminoOptimo.size - 1) {
            if (caminoOptimo[i] == origen && caminoOptimo[i + 1] == destino) {
                return true
            }
        }
        return false
    }

    private fun dibujarFlecha(
        canvas: Canvas,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        paint: Paint
    ) {
        val angle = Math.atan2((y2 - y1).toDouble(), (x2 - x1).toDouble())
        val arrowLength = 30f
        val arrowAngle = Math.toRadians(25.0)

        // Calcular punto de inicio de la flecha (antes del círculo del nodo destino)
        val distance = 50f // radio del nodo
        val arrowX = x2 - distance * cos(angle).toFloat()
        val arrowY = y2 - distance * sin(angle).toFloat()

        // Puntas de la flecha
        val x3 = arrowX - arrowLength * cos(angle - arrowAngle).toFloat()
        val y3 = arrowY - arrowLength * sin(angle - arrowAngle).toFloat()
        val x4 = arrowX - arrowLength * cos(angle + arrowAngle).toFloat()
        val y4 = arrowY - arrowLength * sin(angle + arrowAngle).toFloat()

        canvas.drawLine(arrowX, arrowY, x3, y3, paint)
        canvas.drawLine(arrowX, arrowY, x4, y4, paint)
    }
}