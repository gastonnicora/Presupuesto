package com.gastonnicora.presupuesto.ui.home

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.gastonnicora.presupuesto.model.CompanyData
import com.gastonnicora.presupuesto.model.sampleCompany
import com.gastonnicora.presupuesto.ui.client.ClientActivity
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme

private data class HomeMenuItem(
    val icon: ImageVector,
    val label: String,
    val onClick: () -> Unit = {}
)

/** Pantalla principal: datos de la empresa arriba y el menú de secciones abajo. */
@Composable
fun HomeScreen(
    company: CompanyData,
    onEditCompany: () -> Unit,
    onOpenClients: () -> Unit,
    onOpenItems: () -> Unit,
    onOpenBudget: () -> Unit,
    onOpenPDF:()->Unit,
    onExportCopy: () -> Unit,
    onImportCopy: () -> Unit,
    modifier: Modifier = Modifier
) {


    // TODO: conectar cada opción con su pantalla cuando estén creadas
    val menuItems = listOf(
        HomeMenuItem(Icons.Default.Person, "Clientes", onOpenClients),
        HomeMenuItem(Icons.Default.FormatListNumbered, "Productos", onOpenItems),
        HomeMenuItem(Icons.Default.AddShoppingCart, "Presupuestos", onOpenBudget),
        HomeMenuItem(Icons.Default.PictureAsPdf, "Presupuestos PDF",onOpenPDF),
        HomeMenuItem(Icons.Default.Backup, "Exportar Datos",onExportCopy),
                HomeMenuItem(Icons.Default.Restore, "Importar datos",onImportCopy)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CompanyHeader(company = company, onClick = onEditCompany)

        Text(
            text = "Menú",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        menuItems.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowItems.forEach { item ->
                    HomeMenuButton(
                        icon = item.icon,
                        label = item.label,
                        onClick = item.onClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    PresupuestoTheme {
        HomeScreen(company = sampleCompany(), onEditCompany = {}, {}, {}, {},{},{},{})
    }
}
