package com.cubanoar.springboot.app.view.pdf;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.cubanoar.springboot.app.models.entity.Factura;
import com.lowagie.text.Document;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView{

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		// Obtenemos del model la factura, debe ser el mismo nombre que en el controlador
		Factura factura = (Factura) model.get("factura");
		
		//Creando las tablas
		PdfPTable tabla = new PdfPTable(1);//le pasamos la cantidad de columnas
		tabla.setSpacingAfter(20f);//Para dar espacio despues de la tabla
		tabla.addCell("Datos del cliente");
		tabla.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		tabla.addCell(factura.getCliente().getEmail());
		
		PdfPTable tabla2 = new PdfPTable(1);
		tabla2.setSpacingAfter(20);
		tabla.addCell("Datos de la Factura");
		tabla.addCell("Folio: "  + factura.getId());
		tabla.addCell("Decripición: " + factura.getDescripcion());
		tabla.addCell("Fecha: " + factura.getCreateAt());
		
		//Añadiendo las tablas
		document.add(tabla);
		document.add(tabla2);
		
		
	}

}
