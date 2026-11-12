package org.example;

import java.util.Scanner;
import java.nio.file.Path;

public class MenuTareas {
    private final Scanner sc = new Scanner(System.in);
    private final GestorTareas gestorTareas = new GestorTareas(
            new ArchivoTareas(Path.of("tareas.txt")));

    public void iniciar() {
        try {
            gestorTareas.cargarTareas();
            System.out.println("Tareas cargadas desde tareas.txt.");
        } catch (PersistenciaException e) {
            System.out.println("No se pudieron cargar las tareas: " + e.getMessage());
        }

        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opcion: ");
            recibirOpcion(opcion);
        } while (opcion != 5);
        sc.close();
    }

    private void mostrarMenu() {
        System.out.println("\n============= MENU =============");
        System.out.println("1. Agregar nueva tarea");
        System.out.println("2. Listar todas las tareas");
        System.out.println("3. Eliminar tarea por ID");
        System.out.println("4. Marcar tarea como completada");
        System.out.println("5. Salir");
        System.out.println("================================");
    }

    private void recibirOpcion(int opcionElegida) {
        switch (opcionElegida) {
            case 1:
                agregarTarea();
                break;
            case 2:
                listarTareas();
                break;
            case 3:
                eliminarTarea();
                break;
            case 4:
                completarTarea();
                break;
            case 5:
                System.out.println("Chau...");
                break;
            default:
                System.out.println("Opcion invalida, pruebe nuevamente");
                break;
        }
    }

    private void listarTareas() {
        gestorTareas.listarTareas().forEach(tarea -> System.out.println(
                "ID: " + tarea.getId() + " | Tarea: " + tarea.getDescripcion()
                        + " | Completado: " + tarea.isCompletada()));
    }

    private void eliminarTarea() {
        int idEliminar = getIdTarea();
        try {
            if (gestorTareas.eliminarTarea(idEliminar)) {
                System.out.println("Se elimino la tarea con el id: " + idEliminar);
            } else {
                System.out.println("No existe la tarea con el id: " + idEliminar);
            }
        } catch (PersistenciaException e) {
            mostrarErrorPersistencia(e);
        }
    }

    private void agregarTarea() {
        System.out.print("Escriba la descripcion de la tarea: ");
        String descripcion = sc.nextLine();
        try {
            Tarea nuevaTarea = gestorTareas.agregarTarea(descripcion);
            System.out.println("Se creo la nueva tarea '" + descripcion + "', se le asigno el id: "
                    + nuevaTarea.getId());
        } catch (ValidacionTareaException e) {
            System.out.println("Entrada invalida: " + e.getMessage());
        } catch (PersistenciaException e) {
            mostrarErrorPersistencia(e);
        }
    }

    private void completarTarea() {
        int idCompletar = getIdTarea();
        try {
            if (gestorTareas.completarTarea(idCompletar)) {
                System.out.println("La tarea id: " + idCompletar + " fue marcada como completada!");
            } else {
                System.out.println("La tarea id: " + idCompletar + " no existe");
            }
        } catch (PersistenciaException e) {
            mostrarErrorPersistencia(e);
        }
    }

    private int getIdTarea() {
        int idTarea;
        do {
            idTarea = leerEntero("Escriba el ID de la tarea: ");
            if (idTarea <= 0) {
                System.out.println("El ID debe ser un numero positivo.");
            }
        } while (idTarea <= 0);
        return idTarea;
    }

    private int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un numero entero.");
            return -1;
        }
    }

    private void mostrarErrorPersistencia(PersistenciaException e) {
        System.out.println("No se pudo guardar el archivo de tareas: " + e.getMessage());
    }
}