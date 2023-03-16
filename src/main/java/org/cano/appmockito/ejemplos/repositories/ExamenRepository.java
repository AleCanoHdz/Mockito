package org.cano.appmockito.ejemplos.repositories;

import org.cano.appmockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {

    //Este repositorio se encarga de acceder a los datos
    Examen guardar(Examen examen);
    List<Examen> findAll();
}
