package com.example.sensores

import android.annotation.SuppressLint
import android.graphics.Color
import android.hardware.*
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var detalle: TextView
    private lateinit var sensorManager: SensorManager
    private var sensorProximidad: Sensor? = null
    private var sensorMagnetico: Sensor? = null
    private var sensorMovimiento: Sensor? = null
    private var sensorPosicion: Sensor? = null
    private var sensorLuz: Sensor? = null
    private lateinit var imagen1: ImageView
    private lateinit var imagen2: ImageView
    private var existeSensorProximidad: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Asignación de componentes gráficos
        detalle = findViewById(R.id.txtDetalle)
        imagen1 = findViewById(R.id.imgSensor1)
        imagen2 = findViewById(R.id.imgSensor2)

        // Gestionar sensores del dispositivo
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        // Asociar botones con funciones
        findViewById<Button>(R.id.btnListado).setOnClickListener { clickListado() }
        findViewById<Button>(R.id.btnMagnetico).setOnClickListener { clickMagnetico() }
        findViewById<Button>(R.id.btnProximidad).setOnClickListener { clickProximidad() }
        findViewById<Button>(R.id.btnMovimiento).setOnClickListener { clickMovimiento() }
        findViewById<Button>(R.id.btnPosicion).setOnClickListener { clickPosicion() }
        findViewById<Button>(R.id.btnLuz).setOnClickListener { clickLuz() }
    }

    @SuppressLint("SetTextI18n")
    private fun clickListado() {
        val listadoSensores = sensorManager.getSensorList(Sensor.TYPE_ALL)
        detalle.setBackgroundColor(Color.WHITE)
        detalle.text = "Lista de sensores del dispositivo:"
        for (sensor in listadoSensores) {
            detalle.append("\nNombre: ${sensor.name}\nVersión: ${sensor.version}")
        }
    }

    @SuppressLint("SetTextI18n")
    private fun clickMagnetico() {
        sensorMagnetico = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        if (sensorMagnetico != null) {
            Toast.makeText(this, "El dispositivo tiene sensor magnético.", Toast.LENGTH_SHORT).show()
            detalle.setBackgroundColor(Color.GRAY)
            detalle.text = "Propiedades del sensor Magnético:\nNombre: ${sensorMagnetico!!.name}\nVersión: ${sensorMagnetico!!.version}\nFabricante: ${sensorMagnetico!!.vendor}"
        } else {
            Toast.makeText(this, "El dispositivo no cuenta con sensor magnético.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickProximidad() {
        sensorProximidad = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        if (sensorProximidad != null) {
            existeSensorProximidad = true
            detalle.text = "El dispositivo tiene sensor de proximidad: ${sensorProximidad!!.name}"
            detalle.setBackgroundColor(Color.GREEN)
            sensorManager.registerListener(this, sensorProximidad, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            detalle.text = "No se cuenta con sensor de proximidad"
            detalle.setBackgroundColor(Color.RED)
            existeSensorProximidad = false
        }
    }

    // Sensor de movimiento (acelerómetro)
    private fun clickMovimiento() {
        sensorMovimiento = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (sensorMovimiento != null) {
            sensorManager.registerListener(this, sensorMovimiento, SensorManager.SENSOR_DELAY_NORMAL)
            detalle.text = "Sensor de Movimiento Activado"
        } else {
            detalle.text = "No se cuenta con sensor de acelerómetro"
        }
    }

    // Sensor de posición (rotación vectorial)
    private fun clickPosicion() {
        sensorPosicion = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (sensorPosicion != null) {
            sensorManager.registerListener(this, sensorPosicion, SensorManager.SENSOR_DELAY_NORMAL)
            detalle.text = "Sensor de Posición Activado"
        } else {
            detalle.text = "No se cuenta con sensor de rotación"
        }
    }

    // Sensor de luz ambiental
    private fun clickLuz() {
        sensorLuz = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        if (sensorLuz != null) {
            sensorManager.registerListener(this, sensorLuz, SensorManager.SENSOR_DELAY_NORMAL)
            detalle.text = "Sensor de Luz Activado"
        } else {
            detalle.text = "No se cuenta con sensor de luz"
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        when (event.sensor.type) {
            Sensor.TYPE_PROXIMITY -> {
                val valorCambio = event.values[0]
                if (valorCambio < 1.0) {
                    detalle.textSize = 30f
                    detalle.setBackgroundColor(Color.BLUE)
                    detalle.setTextColor(Color.WHITE)
                    detalle.text = "\nCERCA $valorCambio"
                } else {
                    detalle.textSize = 14f
                    detalle.setBackgroundColor(Color.GREEN)
                    detalle.setTextColor(Color.BLACK)
                    detalle.text = "\nLEJOS $valorCambio"
                }
            }

            Sensor.TYPE_ACCELEROMETER -> {
                val movimiento = event.values[0] + event.values[1] + event.values[2]
                if (movimiento > 15) {
                    detalle.setBackgroundColor(Color.YELLOW)
                } else {
                    detalle.setBackgroundColor(Color.WHITE)
                }
            }

            Sensor.TYPE_ROTATION_VECTOR -> {
                val x = event.values[0]
                if (x > 0.5) {
                    imagen1.setImageResource(R.drawable.img_inclinacion_derecha)
                    detalle.text = "Inclinación Derecha"
                } else if (x < -0.5) {
                    imagen1.setImageResource(R.drawable.img_inclinacion_izquierda)
                    detalle.text = "Inclinación Izquierda"
                } else {
                    imagen1.setImageResource(R.drawable.img_neutral)
                    detalle.text = "Dispositivo en Posición Neutra"
                }
            }

            Sensor.TYPE_LIGHT -> {
                val luz = event.values[0]
                if (luz < 10) {
                    detalle.setBackgroundColor(Color.DKGRAY)
                    detalle.setTextColor(Color.WHITE)
                    imagen2.setImageResource(R.drawable.img_noche)
                } else {
                    detalle.setBackgroundColor(Color.WHITE)
                    detalle.setTextColor(Color.BLACK)
                    imagen2.setImageResource(R.drawable.img_dia)
                }
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}