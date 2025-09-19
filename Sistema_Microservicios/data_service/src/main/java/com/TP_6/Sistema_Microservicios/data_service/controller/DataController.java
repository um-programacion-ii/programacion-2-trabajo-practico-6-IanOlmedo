package com.TP_6.Sistema_Microservicios.data_service.controller;

import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.entity.Inventario;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.service.CategoriaService;
import com.TP_6.Sistema_Microservicios.data_service.service.InventarioService;
import com.TP_6.Sistema_Microservicios.data_service.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/health")
    public String health() { return "OK-DATA"; }

    // ------ PRODUCTOS ------
    @GetMapping("/productos")
    public List<Producto> listarProductos() { return productoService.obtenerTodos(); }

    @GetMapping("/productos/{id}")
    public Producto productoPorId(@PathVariable Long id) { return productoService.buscarPorId(id); }

    @GetMapping("/productos/categoria/{nombre}")
    public List<Producto> productosPorCategoria(@PathVariable String nombre) {
        return productoService.buscarPorCategoria(nombre);
    }

    // Permite vincular por ?categoria=NombreCategoria (acorde a tu ProductoService.guardar(p, categoria))
    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(@Valid @RequestBody Producto producto,
                                  @RequestParam(required = false) String categoria) {
        return productoService.guardar(producto, categoria);
    }

    @PutMapping("/productos/{id}")
    public Producto actualizarProducto(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        return productoService.actualizar(id, producto);
    }

    @DeleteMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarProducto(@PathVariable Long id) { productoService.eliminar(id); }

    // ------ CATEGORÍAS ------
    @GetMapping("/categorias")
    public List<Categoria> listarCategorias() { return categoriaService.obtenerTodas(); }

    // ------ INVENTARIO ------
    @GetMapping("/inventario/stock-bajo")
    public List<Inventario> stockBajo(@RequestParam(defaultValue = "5") Integer umbral) {
        return inventarioService.obtenerProductosConStockBajo(umbral);
    }
}
