package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ArchivoTareas {
    private final Path ruta;

    public ArchivoTareas(Path ruta) {
        this.ruta = ruta;
    }

    public List<Tarea> cargar() throws PersistenciaException {
        if (!Files.exists(ruta)) {
            return new ArrayList<>();
        }

        try {
            List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
            List<Tarea> tareas = new ArrayList<>();
            for (int indice = 0; indice < lineas.size(); indice++) {
                tareas.add(parsearTarea(lineas.get(indice), indice + 1));
            }
            return tareas;
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo leer " + ruta + ".", e);
        }
    }

    public void guardar(Collection<Tarea> tareas) throws PersistenciaException {
        List<String> lineas = new ArrayList<>();
        for (Tarea tarea : tareas) {
            lineas.add(tarea.getId() + ";" + tarea.isCompletada() + ";" + tarea.getDescripcion());
        }

        try {
            Files.write(ruta, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo escribir " + ruta + ".", e);
        }
    }

    private Tarea parsearTarea(String linea, int numeroLinea) throws PersistenciaException {
        String[] campos = linea.split(";", 3);
        if (campos.length != 3 || (!campos[1].equals("true") && !campos[1].equals("false"))
                || campos[2].isBlank()) {
            throw new PersistenciaException("Formato invalido en la linea " + numeroLinea + ".");
        }

        try {
            int id = Integer.parseInt(campos[0]);
            if (id <= 0) {
                throw new PersistenciaException("ID invalido en la linea " + numeroLinea + ".");
            }
            return new Tarea(id, campos[2], Boolean.parseBoolean(campos[1]));
        } catch (NumberFormatException e) {
            throw new PersistenciaException("ID invalido en la linea " + numeroLinea + ".", e);
        }
    }
}