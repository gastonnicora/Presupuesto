package com.gastonnicora.presupuesto.model

/** Condición de venta que se muestra en el presupuesto. */
enum class SalesCondition(val label: String) {
    CASH("Contado"),
    DEBIT_CARD("Tarjeta de débito"),
    CREDIT_CARD("Tarjeta de crédito"),
    BANK_TRANSFER("Transferencia bancaria"),
    CURRENT_ACCOUNT("Cuenta corriente"),
    CHECK("Cheque"),
    VOUCHER("Ticket"),
    OTHER("Otra")
}
