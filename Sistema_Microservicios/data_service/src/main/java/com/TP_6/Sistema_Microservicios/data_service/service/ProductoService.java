package com.TP_6.Sistema_Microservicios.data_service.service;


import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.repository.CategoriaRepository;
import com.TP_6.Sistema_Microservicios.data_service.repository.ProductoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;

    }

    public List<Producto> obtenerTodos(){
        return productoRepository.findAll();
    }

    public Producto buscarPorId(long id){
        return productoRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("Producto con el id: " + id +" no fue encontrado"));
    }
    public List<Producto> buscarPorCategoria(String nombre) {
        return productoRepository.findByCategoria_NombreIgnoreCase(nombre);
    }

    public Producto guardar(Producto producto, String nombreCategoria) {
        if (nombreCategoria != null) {
            Categoria categoria = categoriaRepository.findByNombreIgnoreCase(nombreCategoria)
                    .orElseThrow(() -> new EntityNotFoundException("Categoría no encontrada: " + nombreCategoria));
            producto.setCategoria(categoria);
        }
        return productoRepository.save(producto);
    }

    public Producto actualizar(Long id, Producto producto) {
        Producto existente = buscarPorId(id);
        existente.setNombre(producto.getNombre());
        existente.setDescripcion(producto.getDescripcion());
        existente.setPrecio(producto.getPrecio());
        if (producto.getCategoria() != null) {
            existente.setCategoria(producto.getCategoria());
        }
        return existente;
    }

    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new EntityNotFoundException("Producto no encontrado con id: " + id);
        }
        productoRepository.deleteById(id);
    }
}
