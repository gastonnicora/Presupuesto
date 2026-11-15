package com.gastonnicora.presupuesto.model

import java.text.ParseException
import java.text.SimpleDateFormat

/** Datos de la empresa que emite los presupuestos. */
data class CompanyData(
    val name: String,
    val email: String,
    val address: String,
    val phone: String,
    val cuit: String,
    val iibb: String,
    val startDate: String,
    val ivaCategory: IvaCategory? = null,
    val logoPath: String = ""
) {
    fun isNameValid(): Boolean = name.isNotBlank() && name.length >= 3

    fun isEmailValid(): Boolean = EMAIL_PATTERN.matches(email)

    fun isAddressValid(): Boolean = address.isNotBlank() && address.length >= 5

    fun isPhoneValid(): Boolean {
        val digitCount = phone.count { it.isDigit() }
        return digitCount in 10..13 && PHONE_PATTERN.matches(phone)
    }

    fun isIvaCategoryValid(): Boolean = ivaCategory != null

    /** CUIT: 11 dígitos con dígito verificador (algoritmo oficial de AFIP/ARCA). */
    fun isCuitValid(): Boolean = isValidCuit(cuit)

    fun isIibbValid(): Boolean = iibb.length in 1..12 && iibb.all { it.isDigit() }

    /** Fecha de inicio de actividad en formato dd/MM/yyyy. */
    fun isStartDateValid(): Boolean {
        // Chequeo barato primero: evita parsear mientras se escribe
        if (!DATE_PATTERN.matches(startDate)) return false
        return try {
            SimpleDateFormat("dd/MM/yyyy").apply { isLenient = false }.parse(startDate) != null
        } catch (e: ParseException) {
            false
        }
    }

    fun isValid(): Boolean =
        isNameValid() &&
                isEmailValid() &&
                isAddressValid() &&
                isPhoneValid() &&
                isCuitValid() &&
                isIibbValid() &&
                isStartDateValid() &&
                isIvaCategoryValid()

    /** Muestra el CUIT con guiones: 30718956001 -> 30-71895600-1 */
    fun formattedCuit(): String {
        val digits = cuit.filter { it.isDigit() }
        if (digits.length != 11) return cuit
        return "${digits.substring(0, 2)}-${digits.substring(2, 10)}-${digits.substring(10)}"
    }

    companion object {
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private val PHONE_PATTERN = Regex("^[+]?[0-9\\s\\-()]+$")
        private val DATE_PATTERN = Regex("^\\d{2}/\\d{2}/\\d{4}$")
        private val CUIT_WEIGHTS = intArrayOf(5, 4, 3, 2, 7, 6, 5, 4, 3, 2)
    }
}

/** Empresa de ejemplo para las vistas previas de Compose. */
internal fun sampleCompany() = CompanyData(
    name = "El vasquito",
    email = "ejemplo@gmail.com",
    address = "Calle Falsa (1234), La Plata, BS.AS",
    phone = "(1111) 111111",
    cuit = "11111111111",
    iibb = "11111111111",
    startDate = "24/09/2001"
)
