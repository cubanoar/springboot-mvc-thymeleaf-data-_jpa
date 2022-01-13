package com.cubanoar.springboot.app.models.dao;


import org.springframework.data.repository.PagingAndSortingRepository;

import com.cubanoar.springboot.app.models.entity.Cliente;

public interface IClienteDao extends PagingAndSortingRepository<Cliente, Long>{//nombre clase <entity, tipo id>

	/*Podemos crear otros metodos con consultas personalizadas*/
}
