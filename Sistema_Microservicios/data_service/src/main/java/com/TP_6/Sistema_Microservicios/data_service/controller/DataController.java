package com.TP_6.Sistema_Microservicios.data_service.controller;

import com.TP_6.Sistema_Microservicios.data_service.dto.InventarioDTO;
import com.TP_6.Sistema_Microservicios.data_service.dto.ProductoRequest;
import com.TP_6.Sistema_Microservicios.data_service.dto.ProductoResponse;
import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.entity.Inventario;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.service.CategoriaService;
import com.TP_6.Sistema_Microservicios.data_service.service.InventarioService;
import com.TP_6.Sistema_Microservicios.data_service.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/data")
public class DataController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final InventarioService inventarioService;

    public DataController(ProductoService productoService,
                          CategoriaService categoriaService,
                          InventarioService inventarioService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.inventarioService = inventarioService;
    }

    // ===================== PRODUCTOS =====================

    @GetMapping("/productos")
    public List<ProductoResponse> listarProductos() {
        return productoService.obtenerTodos()
                .stream()
                .map(this::toProductoResponse)
                .toList();
    }

    @GetMapping("/productos/{id}")
    public ProductoResponse productoPorId(@PathVariable Long id) {
        Producto p = productoService.buscarPorId(id);
        return toProductoResponse(p);
    }

    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductoResponse crearProducto(@RequestBody ProductoRequest req) {
        // Buscar o crear categoría por nombre (si viene)
        Categoria categoria = null;
        if (req.getCategoriaNombre() != null && !req.getCategoriaNombre().isBlank()) {
            categoria = categoriaService.buscarPorNombre(req.getCategoriaNombre().trim());
        }

        // Crear entidad Producto
        Producto p = new Producto();
        p.setNombre(req.getNombre());
        p.setDescripcion(req.getDescripcion());
        p.setPrecio(req.getPrecio());
        p.setCategoria(categoria);

        // Crear inventario y vincular en ambos sentidos
        Inventario inv = new Inventario();
        inv.setCantidad(req.getStock() != null ? req.getStock() : 0);
        inv.setStockMinimo(5);
        inv.setFechaActualizacion(LocalDateTime.now());

        p.setInventario(inv);
        inv.setProducto(p);

        // Guardar usando el método que acepta nombre de categoría
        Producto guardado = productoService.guardar(p, categoria != null ? categoria.getNombre() : null);
        return toProductoResponse(guardado);
    }

    @PutMapping("/productos/{id}")
    public ProductoResponse actualizarProducto(@PathVariable Long id, @RequestBody ProductoRequest req) {
        Producto existente = productoService.buscarPorId(id);

        if (req.getNombre() != null) existente.setNombre(req.getNombre());
        if (req.getDescripcion() != null) existente.setDescripcion(req.getDescripcion());
        if (req.getPrecio() != null) existente.setPrecio(req.getPrecio());

        if (req.getCategoriaNombre() != null && !req.getCategoriaNombre().isBlank()) {
            Categoria categoria = categoriaService.buscarPorNombre(req.getCategoriaNombre().trim());
            existente.setCategoria(categoria);
        }

        if (req.getStock() != null) {
            if (existente.getInventario() == null) {
                Inventario inv = new Inventario();
                inv.setCantidad(req.getStock());
                inv.setStockMinimo(5);
                inv.setFechaActualizacion(LocalDateTime.now());
                existente.setInventario(inv);
                inv.setProducto(existente);
            } else {
                existente.getInventario().setCantidad(req.getStock());
                existente.getInventario().setFechaActualizacion(LocalDateTime.now());
            }
        }

        Producto actualizado = productoService.actualizar(id, existente);
        return toProductoResponse(actualizado);
    }

    @DeleteMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long id) {
        productoService.eliminar(id);
    }

    @GetMapping("/productos/categoria/{nombre}")
    public List<ProductoResponse> productosPorCategoria(@PathVariable String nombre) {
        return productoService.buscarPorCategoria(nombre)
                .stream()
                .map(this::toProductoResponse)
                .toList();
    }

    // ===================== CATEGORÍAS =====================

    @GetMapping("/categorias")
    public List<Categoria> listarCategorias() {
        return categoriaService.obtenerTodas();
    }

    // ===================== INVENTARIO =====================

    @GetMapping("/inventario/stock-bajo")
    public List<InventarioDTO> stockBajo(@RequestParam(defaultValue = "5") Integer umbral) {
        return inventarioService.obtenerProductosConStockBajo(umbral)
                .stream()
                .map(inv -> new InventarioDTO(
                        inv.getId(),
                        inv.getProducto() != null ? inv.getProducto().getId() : null,
                        inv.getCantidad(),
                        inv.getStockMinimo()
                ))
                .toList();
    }

    // ===================== MAPPER =====================

    private ProductoResponse toProductoResponse(Producto p) {
        Integer stock = (p.getInventario() != null) ? p.getInventario().getCantidad() : null;
        Integer min   = (p.getInventario() != null) ? p.getInventario().getStockMinimo() : null;
        boolean stockBajo = (stock != null && min != null) && stock <= min;

        return ProductoResponse.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .descripcion(p.getDescripcion())
                .precio(p.getPrecio())
                .categoriaNombre(p.getCategoria() != null ? p.getCategoria().getNombre() : null)
                .stock(stock)
                .stockBajo(stockBajo)
                .build();
    }
}
