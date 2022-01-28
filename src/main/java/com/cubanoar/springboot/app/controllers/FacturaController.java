package com.cubanoar.springboot.app.controllers;

import java.util.List;

import javax.persistence.PrePersist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cubanoar.springboot.app.models.entity.Cliente;
import com.cubanoar.springboot.app.models.entity.Factura;
import com.cubanoar.springboot.app.models.entity.Producto;
import com.cubanoar.springboot.app.models.service.IClienteService;

@Controller
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {

	@Autowired
	private IClienteService clienteService;
	
	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable Long clienteId, Model model, RedirectAttributes flash) {
		Cliente cliente = clienteService.findOne(clienteId);
		/*Si el cliente es null redireccionamos a listar*/
		if(cliente == null) {
			flash.addFlashAttribute("error", "El cliente no existe");
			return "redirect:/listar";
		}
		
		/*Relacionando la factura con el cliente*/
		Factura factura = new Factura();
		factura.setCliente(cliente);
		
		model.addAttribute("factura", factura);
		model.addAttribute("titulo", "Crear Factura");

		return "factura/form";
	}
	
	@GetMapping(value="/cargar-productos/{term}", produces= {"application/json"})
	public @ResponseBody List<Producto> cargarProductos(@PathVariable String term){
		return clienteService.findByNombre(term);
		
	}
	 
}
