package com.example.sigdistrak

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var containerCalculadora: ScrollView
    private lateinit var containerGrafo: LinearLayout
    private lateinit var grafoView: GrafoView

    private lateinit var spinnerInicio: Spinner
    private lateinit var spinnerFin: Spinner
    private lateinit var btnCalcular: Button
    private lateinit var tvResultado: TextView
    private lateinit var tvPasos: TextView
    private lateinit var grafo: Grafo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            inicializarVistas()
            configurarGrafo()
            configurarTabs()
            configurarSpinners()
            configurarBoton()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun inicializarVistas() {
        tabLayout = findViewById(R.id.tabLayout)
        containerCalculadora = findViewById(R.id.containerCalculadora)
        containerGrafo = findViewById(R.id.containerGrafo)
        grafoView = findViewById(R.id.grafoView)

        spinnerInicio = findViewById(R.id.spinnerInicio)
        spinnerFin = findViewById(R.id.spinnerFin)
        btnCalcular = findViewById(R.id.btnCalcular)
        tvResultado = findViewById(R.id.tvResultado)
        tvPasos = findViewById(R.id.tvPasos)
    }

    private fun configurarGrafo() {
        grafo = Grafo.crearGrafoEjemplo()
        grafoView.setGrafo(grafo)
    }

    private fun configurarTabs() {
        tabLayout.addTab(tabLayout.newTab().setText("📊 Visualización"))
        tabLayout.addTab(tabLayout.newTab().setText("🔍 Calculadora"))

        // Mostrar la visualización por defecto
        mostrarVisualizacion()

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> mostrarVisualizacion()
                    1 -> mostrarCalculadora()
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun mostrarVisualizacion() {
        containerGrafo.visibility = View.VISIBLE
        containerCalculadora.visibility = View.GONE
    }

    private fun mostrarCalculadora() {
        containerGrafo.visibility = View.GONE
        containerCalculadora.visibility = View.VISIBLE
    }

    private fun configurarSpinners() {
        val nodos = grafo.obtenerNodos().sorted()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nodos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerInicio.adapter = adapter
        spinnerFin.adapter = adapter

        // Configurar valores por defecto
        val posInicio = nodos.indexOf("A")
        val posFin = nodos.indexOf("J")
        if (posInicio >= 0) spinnerInicio.setSelection(posInicio)
        if (posFin >= 0) spinnerFin.setSelection(posFin)
    }

    private fun configurarBoton() {
        btnCalcular.setOnClickListener {
            calcularRuta()
        }
    }

    private fun calcularRuta() {
        try {
            val inicio = spinnerInicio.selectedItem.toString()
            val fin = spinnerFin.selectedItem.toString()

            if (inicio == fin) {
                Toast.makeText(this, "El nodo de inicio y fin deben ser diferentes", Toast.LENGTH_SHORT).show()
                return
            }

            val dijkstra = Dijkstra(grafo)
            val resultado = dijkstra.encontrarRutaMasCorta(inicio, fin)

            mostrarResultado(resultado)

            // Actualizar el grafo visual con el camino óptimo
            grafoView.setCaminoOptimo(resultado.camino)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Error al calcular: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun mostrarResultado(resultado: ResultadoDijkstra) {
        if (resultado.camino.isEmpty()) {
            tvResultado.text = "❌ No existe un camino entre los nodos seleccionados"
            tvPasos.text = ""
            return
        }

        val textoResultado = buildString {
            append("✅ RUTA ÓPTIMA ENCONTRADA\n\n")
            append("📍 Camino: ${resultado.camino.joinToString(" → ")}\n\n")
            append("📏 Distancia total: ${resultado.distanciaTotal}\n\n")
            append("═".repeat(40))
        }

        tvResultado.text = textoResultado
        tvPasos.text = resultado.pasos.joinToString("\n")
    }
}