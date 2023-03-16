package org.cano.appmockito.ejemplos.services;

import org.cano.appmockito.ejemplos.models.Examen;
import org.cano.appmockito.ejemplos.repositories.ExamenRepository;
import org.cano.appmockito.ejemplos.repositories.PreguntaRepository;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class ExamenServiceImp implements ExamenService{
    
    //Esto permite interactuar con el repositorio 
    private ExamenRepository examenRepository;
    private PreguntaRepository preguntaRepository;

    public ExamenServiceImp(ExamenRepository examenRepository, PreguntaRepository preguntaRepository) {
        this.examenRepository = examenRepository;
        this.preguntaRepository = preguntaRepository;
    }

    @Override
    public Optional<Examen> findExamenPorNombre(String nombre) {
         return examenRepository.findAll().stream().filter(e -> e.getNombre().contains(nombre))
                .findFirst(); //Se utiliza el api stream para asi poder filtrar los datos, stream api hace uso de expresiones lambda
    }

    @Override
    public Examen findExamenPorNombreConPreguntas(String nombre) {
        Optional<Examen> examenOptional = findExamenPorNombre(nombre);
        Examen examen = null;
        if(examenOptional.isPresent()){
            examen = examenOptional.orElseThrow();
            List<String> preguntas = preguntaRepository.findPreguntasPorExamenId(examen.getId());
            examen.setPreguntas(preguntas);
        }
        return examen;
    }

    @Override
    public Examen guardar(Examen examen) {
        if(!examen.getPreguntas().isEmpty()){ //si el examen es diferente de vacio
            preguntaRepository.guardarVarias(examen.getPreguntas());
        }
        return examenRepository.guardar(examen);
    }
}
