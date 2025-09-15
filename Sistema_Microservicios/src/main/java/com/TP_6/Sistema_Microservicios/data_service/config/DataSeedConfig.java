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
    CommandLineRunner seed(CategoriaRepository categoriaRepo, ProductoRepository productoRepo) {
        return args -> {
            if (productoRepo.count() > 0) return;

            Categoria cat = new Categoria();
            cat.setNombre("Tecnología");
            cat.setDescripcion("Productos tech");
            categoriaRepo.save(cat);

            Producto p = new Producto();
            p.setNombre("Mouse Gamer");
            p.setDescripcion("RGB 7200 DPI");
            p.setPrecio(new BigDecimal("9999.90"));
            p.setCategoria(cat);

            Inventario inv = new Inventario();
            inv.setCantidad(15);
            inv.setStockMinimo(5);
            inv.setFechaActualizacion(LocalDateTime.now());
            // vinculación bidireccional
            p.setInventario(inv);

            productoRepo.save(p);
        };
    }
}
