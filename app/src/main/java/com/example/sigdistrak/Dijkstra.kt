package com.example.sigdistrak

import java.util.*

data class ResultadoDijkstra(
    val distancias: Map<String, Int>,
    val predecesores: Map<String, String?>,
    val camino: List<String>,
    val distanciaTotal: Int,
    val pasos: List<String>
)

class Dijkstra(private val grafo: Grafo) {

    fun encontrarRutaMasCorta(inicio: String, fin: String): ResultadoDijkstra {
        val distancias = mutableMapOf<String, Int>()
        val predecesores = mutableMapOf<String, String?>()
        val visitados = mutableSetOf<String>()
        val pasos = mutableListOf<String>()

        // Inicializar todas las distancias como infinito
        grafo.obtenerNodos().forEach { nodo ->
            distancias[nodo] = Int.MAX_VALUE
            predecesores[nodo] = null
        }
        distancias[inicio] = 0

        pasos.add("Iniciando desde el nodo: $inicio")
        pasos.add("Distancias iniciales: ${distancias.mapValues { if (it.value == Int.MAX_VALUE) "∞" else it.value }}")

        // Cola de prioridad para seleccionar el nodo con menor distancia
        val colaPrioridad = PriorityQueue<Pair<String, Int>>(compareBy { it.second })
        colaPrioridad.add(Pair(inicio, 0))

        var pasoNum = 1

        while (colaPrioridad.isNotEmpty()) {
            val (nodoActual, distActual) = colaPrioridad.poll()

            if (nodoActual in visitados) continue
            if (distActual > distancias[nodoActual]!!) continue

            visitados.add(nodoActual)
            pasos.add("\n--- Paso $pasoNum ---")
            pasos.add("Visitando nodo: $nodoActual (distancia actual: $distActual)")

            // Si llegamos al nodo final, podemos terminar
            if (nodoActual == fin) {
                pasos.add("¡Llegamos al nodo destino!")
                break
            }

            // Explorar vecinos
            val vecinos = grafo.obtenerVecinos(nodoActual)
            pasos.add("Vecinos de $nodoActual: ${vecinos.map { "${it.destino}(${it.peso})" }}")

            for (arista in vecinos) {
                val vecino = arista.destino
                if (vecino in visitados) continue

                val nuevaDistancia = distancias[nodoActual]!! + arista.peso

                if (nuevaDistancia < distancias[vecino]!!) {
                    pasos.add("  → Actualizando $vecino: ${distancias[vecino]} → $nuevaDistancia (vía $nodoActual)")
                    distancias[vecino] = nuevaDistancia
                    predecesores[vecino] = nodoActual
                    colaPrioridad.add(Pair(vecino, nuevaDistancia))
                } else {
                    pasos.add("  → $vecino: no se actualiza (${distancias[vecino]} ≤ $nuevaDistancia)")
                }
            }

            pasoNum++
        }

        // Reconstruir el camino
        val camino = reconstruirCamino(predecesores, inicio, fin)
        val distanciaTotal = distancias[fin] ?: Int.MAX_VALUE

        if (camino.isNotEmpty()) {
            pasos.add("\n=== RESULTADO ===")
            pasos.add("Camino más corto: ${camino.joinToString(" → ")}")
            pasos.add("Distancia total: $distanciaTotal")
        } else {
            pasos.add("\nNo existe un camino entre $inicio y $fin")
        }

        return ResultadoDijkstra(distancias, predecesores, camino, distanciaTotal, pasos)
    }

    private fun reconstruirCamino(
        predecesores: Map<String, String?>,
        inicio: String,
        fin: String
    ): List<String> {
        val camino = mutableListOf<String>()
        var nodoActual: String? = fin

        while (nodoActual != null) {
            camino.add(0, nodoActual)
            if (nodoActual == inicio) break
            nodoActual = predecesores[nodoActual]
        }

        return if (camino.isNotEmpty() && camino.first() == inicio) camino else emptyList()
    }

    fun obtenerTodasLasRutas(inicio: String): Map<String, ResultadoDijkstra> {
        val resultados = mutableMapOf<String, ResultadoDijkstra>()
        grafo.obtenerNodos().forEach { destino ->
            if (destino != inicio) {
                resultados[destino] = encontrarRutaMasCorta(inicio, destino)
            }
        }
        return resultados
    }
}