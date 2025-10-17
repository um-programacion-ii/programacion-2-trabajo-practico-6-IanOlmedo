package com.TP_6.Sistema_Microservicios.data_service.service.impl;

import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.repository.CategoriaRepository;
import com.TP_6.Sistema_Microservicios.data_service.repository.ProductoRepository;
import com.TP_6.Sistema_Microservicios.data_service.service.ProductoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoServiceImpl(ProductoRepository productoRepository,
                               CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Producto buscarPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado: " + id));
    }

    @Override
    public Producto guardar(Producto producto, String categoriaNombre) {
        if (categoriaNombre != null && !categoriaNombre.isBlank()) {
            Categoria categoria = categoriaRepository
                    .findByNombreIgnoreCase(categoriaNombre.trim())
                    .orElseGet(() -> {
                        Categoria c = new Categoria();
                        c.setNombre(categoriaNombre.trim());
                        c.setDescripcion(null);
                        return categoriaRepository.save(c);
                    });
            producto.setCategoria(categoria);
        }
        return productoRepository.save(producto);
    }

    @Override
    public Producto actualizar(Long id, Producto productoActualizado) {
        Producto existente = buscarPorId(id);

        if (productoActualizado.getNombre() != null) {
            existente.setNombre(productoActualizado.getNombre());
        }
        if (productoActualizado.getDescripcion() != null) {
            existente.setDescripcion(productoActualizado.getDescripcion());
        }
        if (productoActualizado.getPrecio() != null) {
            existente.setPrecio(productoActualizado.getPrecio());
        }
        if (productoActualizado.getCategoria() != null) {
            existente.setCategoria(productoActualizado.getCategoria());
        }
        if (productoActualizado.getInventario() != null) {
            existente.setInventario(productoActualizado.getInventario());
        }

        return productoRepository.save(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado: " + id);
        }
        productoRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> buscarPorCategoria(String nombreCategoria) {
        if (nombreCategoria == null || nombreCategoria.isBlank()) {
            return List.of();
        }
        return productoRepository.findByCategoria_NombreIgnoreCase(nombreCategoria.trim());
    }
}
