package com.gastonnicora.presupuesto.model

/**
 * Condiciones frente al IVA con los códigos oficiales de ARCA (ex AFIP).
 * Los nombres se mantienen en español porque son los términos fiscales oficiales.
 */
enum class IvaCategory(val arcaCode: Int, val label: String) {
    RESPONSABLE_INSCRIPTO(1, "IVA Responsable Inscripto"),
    EXENTO(4, "IVA Sujeto Exento"),
    CONSUMIDOR_FINAL(5, "Consumidor Final"),
    MONOTRIBUTISTA(6, "Responsable Monotributo"),
    NO_CATEGORIZADO(7, "Sujeto No Categorizado"),
    PROVEEDOR_EXTERIOR(8, "Proveedor del Exterior"),
    CLIENTE_EXTERIOR(9, "Cliente del Exterior"),
    IVA_LIBERADO(10, "IVA Liberado"),
    MONOTRIBUTISTA_SOCIAL(13, "Monotributista Social"),
    NO_ALCANZADO(15, "IVA No Alcanzado"),
    MONOTRIBUTO_TRABAJADOR_INDEPENDIENTE_PROMOVIDO(
        16,
        "Monotributo Trabajador Independiente Promovido"
    );

    companion object {
        fun fromArcaCode(code: Int): IvaCategory? = entries.find { it.arcaCode == code }
    }
}
