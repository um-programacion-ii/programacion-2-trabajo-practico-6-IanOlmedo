package com.TP_6.Sistema_Microservicios.data_service.config;

import com.TP_6.Sistema_Microservicios.data_service.entity.Categoria;
import com.TP_6.Sistema_Microservicios.data_service.entity.Inventario;
import com.TP_6.Sistema_Microservicios.data_service.entity.Producto;
import com.TP_6.Sistema_Microservicios.data_service.repository.CategoriaRepository;
import com.TP_6.Sistema_Microservicios.data_service.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Configuration
public class DataSeedConfig {

    @Bean
    CommandLineRunner seed(ProductoRepository productoRepo, CategoriaRepository categoriaRepo) {
        return args -> {
            if (productoRepo.count() > 0) return;

            // Categoria
            Categoria cat = new Categoria();
            cat.setNombre("Tecnología");
            cat.setDescripcion("Productos tecnológicos");
            categoriaRepo.save(cat);

            // Producto
            Producto p = new Producto();
            p.setNombre("Notebook X");
            p.setDescripcion("Equipo de alta performance");
            p.setPrecio(new BigDecimal("9999.90"));
            p.setCategoria(cat);

            // Inventario
            Inventario inv = new Inventario();
            inv.setCantidad(15);
            inv.setStockMinimo(5);
            inv.setFechaActualizacion(LocalDateTime.now());


            p.setInventario(inv);
            inv.setProducto(p);

            // Persistencia
            productoRepo.save(p);
        };
    }
}
