package org.example;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class GestorTareas {
    private final Map<Integer, Tarea> tareasExistentes = new LinkedHashMap<>();
    private final ArchivoTareas archivoTareas;
    private int nuevoId = 1;

    public GestorTareas(ArchivoTareas archivoTareas) {
        this.archivoTareas = archivoTareas;
    }

    public void cargarTareas() throws PersistenciaException {
        List<Tarea> tareasGuardadas = archivoTareas.cargar();
        Map<Integer, Tarea> tareasCargadas = new LinkedHashMap<>();
        int maximoId = 0;

        for (Tarea tarea : tareasGuardadas) {
            if (tareasCargadas.put(tarea.getId(), tarea) != null) {
                throw new PersistenciaException("Hay IDs duplicados en el archivo de tareas.");
            }
            maximoId = Math.max(maximoId, tarea.getId());
        }

        tareasExistentes.clear();
        tareasExistentes.putAll(tareasCargadas);
        nuevoId = maximoId + 1;
    }

    public Tarea agregarTarea(String descripcion)
            throws ValidacionTareaException, PersistenciaException {
        Predicate<String> descripcionValida = texto -> texto != null
                && !texto.isBlank()
                && !texto.contains("\n")
                && !texto.contains("\r");
        if (!descripcionValida.test(descripcion)) {
            throw new ValidacionTareaException("La descripcion no puede estar vacia ni tener saltos de linea.");
        }

        Tarea nuevaTarea = new Tarea(nuevoId, descripcion, false);
        tareasExistentes.put(nuevoId, nuevaTarea);
        nuevoId++;
        guardarTareas();
        return nuevaTarea;
    }

    public Collection<Tarea> listarTareas() {
        return tareasExistentes.values();
    }

    public boolean eliminarTarea(int id) throws PersistenciaException {
        if (tareasExistentes.remove(id) == null) {
            return false;
        }
        guardarTareas();
        return true;
    }

    public boolean completarTarea(int id) throws PersistenciaException {
        Tarea tarea = tareasExistentes.get(id);
        if (tarea == null) {
            return false;
        }

        tarea.setCompletada(true);
        guardarTareas();
        return true;
    }

    public void guardarTareas() throws PersistenciaException {
        archivoTareas.guardar(tareasExistentes.values());
    }
}