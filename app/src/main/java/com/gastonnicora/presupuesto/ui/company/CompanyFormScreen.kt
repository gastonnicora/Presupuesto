package com.gastonnicora.presupuesto.ui.company

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gastonnicora.presupuesto.data.CompanyRepository
import com.gastonnicora.presupuesto.model.CompanyData
import com.gastonnicora.presupuesto.model.IvaCategory
import com.gastonnicora.presupuesto.model.sampleCompany
import com.gastonnicora.presupuesto.ui.components.CompanyLogo
import com.gastonnicora.presupuesto.ui.components.EnumDropdown
import com.gastonnicora.presupuesto.ui.theme.PresupuestoTheme
import kotlinx.coroutines.launch

/** Formulario de datos de la empresa, dividido en secciones. */
@Composable
fun CompanyFormScreen(
    initial: CompanyData,
    repository: CompanyRepository,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    var company by remember { mutableStateOf(initial) }
    // El campo de fecha se maneja aparte para poder poner el cursor al final al formatear
    var startDateInput by remember { mutableStateOf(TextFieldValue(initial.startDate)) }
    val scope = rememberCoroutineScope()

    var ivaCategory by remember { mutableStateOf(initial?.ivaCategory) }

    val logoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val path = repository.importLogo(uri)
                company = company.copy(logoPath = path)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LogoSection(
            logoPath = company.logoPath,
            onPick = {
                logoPicker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            },
            onRemove = { company = company.copy(logoPath = "") }
        )

        SectionTitle("Datos generales")

        FormField(
            value = company.name,
            onValueChange = { company = company.copy(name = it) },
            label = "Empresa",
            isValid = company.isNameValid(),
            invalidMessage = "Mínimo 3 caracteres"
        )

        SectionTitle("Contacto")

        FormField(
            value = company.email,
            onValueChange = { company = company.copy(email = it) },
            label = "Email",
            isValid = company.isEmailValid(),
            invalidMessage = "Email inválido",
            keyboardType = KeyboardType.Email
        )

        FormField(
            value = company.address,
            onValueChange = { company = company.copy(address = it) },
            label = "Dirección",
            isValid = company.isAddressValid(),
            invalidMessage = "Dirección inválida"
        )

        FormField(
            value = company.phone,
            onValueChange = { company = company.copy(phone = it) },
            label = "Teléfono",
            isValid = company.isPhoneValid(),
            invalidMessage = "Teléfono inválido",
            keyboardType = KeyboardType.Phone
        )

        SectionTitle("Datos fiscales")

        FormField(
            value = company.cuit,
            onValueChange = { input ->
                // Solo dígitos, máximo 11
                if (input.length <= 11 && input.all { it.isDigit() }) {
                    company = company.copy(cuit = input)
                }
            },
            label = "CUIT",
            isValid = company.isCuitValid(),
            invalidMessage = "CUIT inválido",
            keyboardType = KeyboardType.Number
        )

        FormField(
            value = company.iibb,
            onValueChange = { input ->
                if (input.length <= 12 && input.all { it.isDigit() }) {
                    company = company.copy(iibb = input)
                }
            },
            label = "Ingresos Brutos (IIBB)",
            isValid = true,
            invalidMessage = "",
            keyboardType = KeyboardType.Number
        )

        OutlinedTextField(
            value = startDateInput,
            onValueChange = { input ->
                val formatted = formatDateDigits(input.text.filter { it.isDigit() }.take(8))
                startDateInput = TextFieldValue(formatted, selection = TextRange(formatted.length))
                company = company.copy(startDate = formatted)
            },
            label = { Text("Inicio de actividad (dd/mm/aaaa)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = company.startDate.isNotEmpty() && !company.isStartDateValid(),
            supportingText = {
                val message = when {
                    company.startDate.isEmpty() -> "Ej: 24/09/2001"
                    !company.isStartDateValid() -> "Formato: dd/mm/aaaa"
                    else -> null
                }
                if (message != null) Text(message)
            },
            modifier = Modifier.fillMaxWidth()
        )
        EnumDropdown(
            label = "Condición frente al IVA",
            options = IvaCategory.entries,
            selected = ivaCategory,
            optionLabel = { it.label },
            onSelected = { ivaCategory = it }
        )
        val copyCompany = company.copy(ivaCategory = ivaCategory)
        Button(
            onClick = {
                scope.launch {
                    repository.save(copyCompany)
                    onSaved()
                }
            },
            enabled = copyCompany.isValid(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Guardar")
        }
    }
}

@Composable
private fun LogoSection(
    logoPath: String,
    onPick: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CompanyLogo(
            logoPath = logoPath,
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            FilledTonalButton(onClick = onPick) {
                Text("Elegir logo")
            }
            if (logoPath.isNotEmpty()) {
                TextButton(onClick = onRemove) {
                    Text("Eliminar logo")
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(top = 8.dp)
    )
}

/** Campo de texto con validación: muestra un ejemplo, o el error si el valor no es válido. */
@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isValid: Boolean,
    invalidMessage: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        isError = value.isNotEmpty() && !isValid,
        supportingText = {
            val message = when {
                !isValid -> invalidMessage
                else -> null
            }
            if (message != null) Text(message)
        },
        modifier = Modifier.fillMaxWidth()
    )
}

// Inserta "/" después del día y del mes: "24092001" -> "24/09/2001"
private fun formatDateDigits(digits: String): String = buildString {
    digits.forEachIndexed { index, char ->
        if (index == 2 || index == 4) append('/')
        append(char)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CompanyFormPreview() {
    PresupuestoTheme {
        CompanyFormScreen(
            initial = sampleCompany(),
            repository = CompanyRepository(LocalContext.current),
            onSaved = {}
        )
    }
}
