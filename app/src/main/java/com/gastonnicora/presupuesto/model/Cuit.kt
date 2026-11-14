package com.gastonnicora.presupuesto.model

/** Valida un CUIT de 11 dígitos con el dígito verificador oficial de AFIP/ARCA. */
fun isValidCuit(cuit: String): Boolean {
    val digits = cuit.filter { it.isDigit() }
    if (digits.length != 11) return false

    val weights = intArrayOf(5, 4, 3, 2, 7, 6, 5, 4, 3, 2)
    val weightedSum = weights.indices.sumOf { weights[it] * digits[it].digitToInt() }
    val remainder = 11 - (weightedSum % 11)
    val checkDigit = when (remainder) {
        11 -> 0
        10 -> 9
        else -> remainder
    }
    return checkDigit == digits[10].digitToInt()
}