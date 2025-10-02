package com.example.sigdistrak

data class Arista(val destino: String, val peso: Int)

class Grafo {
    private val adyacencias = mutableMapOf<String, MutableList<Arista>>()

    fun agregarNodo(nodo: String) {
        if (!adyacencias.containsKey(nodo)) {
            adyacencias[nodo] = mutableListOf()
        }
    }

    fun agregarArista(origen: String, destino: String, peso: Int) {
        agregarNodo(origen)
        agregarNodo(destino)
        adyacencias[origen]?.add(Arista(destino, peso))
    }

    fun obtenerNodos(): Set<String> = adyacencias.keys

    fun obtenerVecinos(nodo: String): List<Arista> = adyacencias[nodo] ?: emptyList()

    fun obtenerTodasLasAristas(): List<Triple<String, String, Int>> {
        val aristas = mutableListOf<Triple<String, String, Int>>()
        adyacencias.forEach { (origen, listaAristas) ->
            listaAristas.forEach { arista ->
                aristas.add(Triple(origen, arista.destino, arista.peso))
            }
        }
        return aristas
    }

    companion object {
        fun crearGrafoEjemplo(): Grafo {
            val grafo = Grafo()

            // Basado en la imagen correcta del problema
            grafo.agregarArista("A", "B", 5)
            grafo.agregarArista("A", "D", 5)
            grafo.agregarArista("A", "E", 5)

            grafo.agregarArista("B", "C", 6)

            grafo.agregarArista("C", "F", 5)

            grafo.agregarArista("D", "F", 6)

            grafo.agregarArista("E", "F", 8)
            grafo.agregarArista("E", "G", 4)

            grafo.agregarArista("F", "I", 9)
            grafo.agregarArista("F", "H", 6)

            grafo.agregarArista("G", "H", 2)

            grafo.agregarArista("H", "J", 3)

            return grafo
        }
    }
}