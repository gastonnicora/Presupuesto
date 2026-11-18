package com.gastonnicora.presupuesto.ui.pdf

import com.conamobile.pdfkmp.dsl.ContainerScope
import com.conamobile.pdfkmp.geometry.ContentScale
import com.conamobile.pdfkmp.geometry.Padding
import com.conamobile.pdfkmp.geometry.PageSize
import com.conamobile.pdfkmp.layout.BoxAlignment
import com.conamobile.pdfkmp.layout.HorizontalAlignment
import com.conamobile.pdfkmp.layout.HorizontalArrangement
import com.conamobile.pdfkmp.layout.PageBreakStrategy
import com.conamobile.pdfkmp.layout.VerticalAlignment
import com.conamobile.pdfkmp.pdf
import com.conamobile.pdfkmp.style.BorderStroke
import com.conamobile.pdfkmp.style.LineStyle
import com.conamobile.pdfkmp.style.PdfColor
import com.conamobile.pdfkmp.style.TableBorder
import com.conamobile.pdfkmp.style.TableColumn
import com.conamobile.pdfkmp.style.TextAlign
import com.conamobile.pdfkmp.style.TextStyle
import com.conamobile.pdfkmp.unit.dp
import com.conamobile.pdfkmp.unit.sp
import com.gastonnicora.presupuesto.dao.BudgetLine
import com.gastonnicora.presupuesto.model.Budget
import com.gastonnicora.presupuesto.model.Client
import com.gastonnicora.presupuesto.model.CompanyData
import com.gastonnicora.presupuesto.model.formatPrice


fun generatePDF(
    logo: ByteArray? = null,
    budget: Budget,
    company: CompanyData,
    client: Client,
    items: List<BudgetLine>,
) = pdf {

    metadata {
        title = "Presupuesto ${budget.id}"
        author = company.name
    }
    defaultTextStyle = TextStyle(fontSize = 8.sp, color = PdfColor.DarkGray)
    defaultPagePadding = Padding.symmetric(horizontal = 40.dp, vertical = 56.dp)
    defaultPageBreakStrategy = PageBreakStrategy.Slice

    page(size = PageSize.A4) {
        spacing = 6.dp
        header { headerPdf(logo, budget, company) }
        footer { footerPdf() }

        spacer(height = 16.dp)
        dataClient(client, budget)

        divider(thickness = 0.5.dp, color = PdfColor.LightGray)

        tableItems(items)

        divider(thickness = 0.5.dp, color = PdfColor.Black)
        total(formatPrice(budget.total))

    }

}

private fun ContainerScope.headerPdf(logo: ByteArray?, budget: Budget, company: CompanyData) {
    box(
        cornerRadius = 12.dp,
        border = BorderStroke(1.dp, PdfColor.Gray, LineStyle.Dashed),
        height = 100.dp
    ) {
        row(horizontalArrangement = HorizontalArrangement.SpaceBetween) {
            box() {
                if (logo != null) {
                    box(opacity = 0.15f) {
                        image(
                            bytes = logo,
                            width = 261.dp,
                            height = 100.dp,
                            contentScale = ContentScale.Fit
                        )
                    }
                }
                aligned(BoxAlignment.BottomCenter) {
                    column(
                        horizontalAlignment = HorizontalAlignment.Center,
                        padding = Padding.symmetric(horizontal = 15.dp)
                    ) {
                        text(company.name.uppercase()) { bold = true; fontSize = 18.sp }
                        spacer(height = 12.dp)
                        text(company.email.ifBlank { "-" }) { fontSize = 10.sp }
                        spacer(height = 8.dp)
                        text(company.address.uppercase().ifBlank { "-" }) { fontSize = 10.sp }
                        spacer(height = 8.dp)
                        text(company.phone.ifBlank { "-" }) { fontSize = 10.sp }
                        spacer(height = 8.dp)
                    }
                }

            }
            box {
                aligned(BoxAlignment.BottomStart) {
                    text("Presupuesto N°: ${budget.id}") { bold = true; fontSize = 14.sp }
                    spacer(height = 5.dp)
                    text("Fecha: ${budget.date}") { bold = true; fontSize = 10.sp }
                    row {
                        text("CUIT:  ") { bold = true; fontSize = 10.sp }
                        text(company.cuit.ifBlank { "-" }) { fontSize = 10.sp }
                    }
                    row {
                        text("IIBB:  ") { bold = true; fontSize = 10.sp }
                        text(company.iibb.ifBlank { "-" }) { fontSize = 10.sp }
                    }
                    row {
                        text("Inicio Act:  ") { bold = true; fontSize = 10.sp }
                        text(company.startDate.ifBlank { "-" }) { fontSize = 10.sp }
                    }
                    text(company.ivaCategory?.label ?: "-") { bold = true; fontSize = 12.sp }
                }
                divider(thickness = 1.dp, color = PdfColor.Black)
            }
        }
    }

}

private fun ContainerScope.footerPdf() {
    divider(thickness = 0.5.dp, color = PdfColor.LightGray)
    text("Orientación al Consumidor de la Pcia. Bs. As. 0800-222-9042") {
        fontSize = 8.sp
        color = PdfColor.Gray
        align = TextAlign.Center
    }
}

private fun ContainerScope.dataClient(client: Client, budget: Budget) {

    row(horizontalArrangement = HorizontalArrangement.SpaceBetween) {
        box(261.dp) {
            aligned(BoxAlignment.BottomCenter) {
                column(
                    horizontalAlignment = HorizontalAlignment.Center,
                    padding = Padding.symmetric(horizontal = 15.dp)
                ) {
                    row {
                        text("Cliente:  ") { fontSize = 14.sp; bold = true }
                        text("(${client.id}) ${client.name.uppercase()}") { fontSize = 14.sp; }
                    }
                    spacer(height = 8.dp)
                    row {
                        text("Dirección:  ") { bold = true }
                        text(client.address.uppercase().ifBlank { "-" }) { }
                    }
                    spacer(height = 8.dp)
                    row {
                        text("Categ.IVA:  ") { bold = true }
                        text(client.ivaCategory.label) { }
                    }
                    spacer(height = 8.dp)
                    row {
                        text("Condiciones de Venta:  ") { bold = true }
                        text(budget.salesCondition.label) { }
                    }
                    spacer(height = 8.dp)
                }
            }
        }
        box(261.dp) {
            aligned(BoxAlignment.BottomCenter) {
                column(
                    horizontalAlignment = HorizontalAlignment.Center,
                    padding = Padding.symmetric(horizontal = 15.dp)
                ) {
                    row {
                        text("Localidad:  ") { bold = true }
                        text(client.locality.ifBlank { "-" }) { }
                    }
                    spacer(height = 8.dp)
                    row {
                        text("CUIT:  ") { bold = true }
                        text(client.cuit.ifBlank { "-" }) { }
                    }
                }
            }
        }
    }

}

private fun ContainerScope.tableItems(items: List<BudgetLine>) {

    table(
        columns = listOf(
            TableColumn.Fixed(50.dp),     // fixed width
            TableColumn.Weight(1.5f),
            TableColumn.Fixed(60.dp),
            TableColumn.Fixed(80.dp),
            TableColumn.Fixed(100.dp),
        ),
        border = TableBorder(color = PdfColor.fromRgb(0xCFD8DC), width = 0.dp),
        cornerRadius = 10.dp,
        cellPadding = Padding.symmetric(horizontal = 8.dp, vertical = 5.dp),


        ) {
        header(background = PdfColor.fromRgb(0xECEFF1)) {
            cell("Código") { bold = true }
            cell("Descripción") { bold = true }
            cell("Cantidad") { bold = true }
            cell("Pre.Unit.") { bold = true }
            cell("Importe", horizontalAlignment = HorizontalAlignment.End) { bold = true }
        }

        items.forEachIndexed { i, item ->
            val value = item.quantity * item.unitPriceCents
            val price = formatPrice(value)
            row() {
                cell(item.itemId.toString())
                cell(item.name)
                cell(item.quantity.toString())
                cell(formatPrice(item.unitPriceCents))
                cell(
                    price,
                    horizontalAlignment = HorizontalAlignment.End,
                    verticalAlignment = VerticalAlignment.Center
                )
            }
        }
    }
}

private fun ContainerScope.total(total: String) {

    text("Observaciones: PRECIOS CON IVA INCLUIDO") { fontSize = 8.sp; color = PdfColor.Gray }

    row(horizontalArrangement = HorizontalArrangement.SpaceBetween) {
        text("TOTAL") { bold = true; fontSize = 12.sp }
        text(total) { bold = true; fontSize = 12.sp }
    }

    text("ORIGINAL") { align = TextAlign.End; fontSize = 8.sp; color = PdfColor.Gray }
}
