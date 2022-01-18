package com.cubanoar.springboot.app.controllers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.cubanoar.springboot.app.models.entity.Cliente;
import com.cubanoar.springboot.app.models.service.IClienteService;
import com.cubanoar.springboot.app.models.service.IUploadFileService;
import com.cubanoar.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("cliente")
public class ClienteController {

	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@GetMapping("/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename){
		
		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ recurso.getFilename() +"\"")
				.body(recurso);
	}
	
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable Long id, Model model , RedirectAttributes flash) {
		
		Cliente cliente = clienteService.findOne(id);
		if (cliente==null) {
			flash.addFlashAttribute("error", "El cliente no existe");
			return "redirect:/listar";
		}
		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", "Cliente: " + cliente.getNombre() + " " + cliente.getApellido());
		
		return "ver";
	}
	
	@GetMapping("/listar")
	public String listar(@RequestParam(name="page", defaultValue = "0") int page, Model model) {
		/*Forma Antigua
		Pageable pageRequest = new PageRequest(page, size);*/
		
		Pageable pageRequest = PageRequest.of(page, 5);
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		
		PageRender<Cliente> pageRender = new PageRender<>("/listar", clientes);
		model.addAttribute("titulo", "Listado de clientes");
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		return "listar";
	}

	@GetMapping("/form")
	public String crear(Model model) {
		
		Cliente cliente = new Cliente();
		model.addAttribute("cliente", cliente);
		
		model.addAttribute("titulo", "Formulario de clientes");
		return "form";
	}
	
	@GetMapping("/form/{id}")
	public String editar(@PathVariable Long id, RedirectAttributes flash, Model model) {
		Cliente cliente = null;
		
		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (cliente == null) {
				flash.addFlashAttribute("error", "El id del cliente no existe en la BBDD!");
				return "redirect:/listar";
			}
		}else {
			flash.addFlashAttribute("error", "El id del cliente no puede ser 0!");
			return "redirect:/listar";
		}
		
		model.addAttribute("titulo", "Editar Cliente");
		model.addAttribute("cliente", cliente);
		return "form";		
	}

	@PostMapping("/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("file") MultipartFile foto,RedirectAttributes flash,SessionStatus status) {
		
		if(result.hasErrors()) {
			model.addAttribute("titulo", "Formulario de clientes");
			return "form";
		}
		
		if (!foto.isEmpty()) {
			
			if (cliente.getId() != null
					&& cliente.getId() > 0
					&& cliente.getFoto() != null
					&& cliente.getFoto().length() > 0) {
				
				uploadFileService.delete(cliente.getFoto());
				
				}
				
			 String uniqueFilename = null;
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			 flash.addFlashAttribute("info", "'" + uniqueFilename + "'" + " subido correctamente");
			
			 cliente.setFoto(uniqueFilename);
		}
		
		String mensajeFlash = (cliente.getId() != null)? "Cliente editado con éxito" : "Cliente creado con éxito";
		
		clienteService.save(cliente);
		status.setComplete();//elimina el objeto cliente de la sesion
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:/listar";
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);
			
			clienteService.delete(id);
			flash.addFlashAttribute("success", "Cliente eliminado con exito");
			
			
				if (uploadFileService.delete(cliente.getFoto())) {
					flash.addFlashAttribute("info", "Foto " + cliente.getFoto() + " eliminada con éxito");
				}
		}
		return "redirect:/listar";
	}
}
