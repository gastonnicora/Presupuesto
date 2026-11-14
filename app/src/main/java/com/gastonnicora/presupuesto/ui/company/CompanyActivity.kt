package com.gastonnicora.presupuesto.ui.company

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.gastonnicora.presupuesto.data.CompanyRepository
import com.gastonnicora.presupuesto.model.CompanyData
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.flow.first

/** Pantalla para editar los datos de la empresa. */
class CompanyActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = CompanyRepository(applicationContext)

        setContent {
            PresupuestoTheme {
                // null mientras se cargan los datos guardados
                var initial by remember { mutableStateOf<CompanyData?>(null) }
                LaunchedEffect(Unit) {
                    initial = repository.companyData.first()
                }

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Mis datos") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Volver"
                                    )
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    initial?.let { loaded ->
                        CompanyFormScreen(
                            initial = loaded,
                            repository = repository,
                            onSaved = { finish() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
