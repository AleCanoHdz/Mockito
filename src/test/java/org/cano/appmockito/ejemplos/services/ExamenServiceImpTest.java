package org.cano.appmockito.ejemplos.services;

import org.cano.appmockito.ejemplos.models.Examen;
import org.cano.appmockito.ejemplos.repositories.ExamenRepository;
import org.cano.appmockito.ejemplos.repositories.PreguntaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class) //Esta anotacion nos permite habilitar las anotaciones y permita la inyeccion de dependencias
class ExamenServiceImpTest {



    @Mock //Anotacion para la inyeccion de dependencias
    ExamenRepository repository;
    @Mock
    PreguntaRepository preguntaRepository;
    @InjectMocks //Crear la instancia del service y ademas inyecta los dos objetos
    ExamenServiceImp service; //La inyeccion del servicio tiene que ser de la implementacion de la clase 

    @BeforeEach
    void setUp() {
        //Habilitar anotaciones
        //MockitoAnnotations.openMocks(this);
        //repository = mock(ExamenRepository.class);
        //preguntaRepository = mock(PreguntaRepository.class);
        //service = new ExamenServiceImp(repository, preguntaRepository);

    }

    @Test
    void findExamenPorNombre() {

        when(repository.findAll()).thenReturn(Datos.EXAMENES);

        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertTrue(examen.isPresent());
        assertEquals(5L, examen.orElseThrow().getId());
        assertEquals("Matematicas", examen.get().getNombre());

    }

    @Test
    void findExamenPorNombreListaVacia() {

        List<Examen> datos = Collections.emptyList();

        when(repository.findAll()).thenReturn(datos);

        Optional<Examen> examen = service.findExamenPorNombre("Matematicas");

        assertFalse(examen.isPresent());


    }

    @Test
    void testPreguntaExamen() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Historia");
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("integrales"));

    }

    @Test
    void testPreguntaExamenVerify() { //verify permite saber si se ejecuto un metodo del repositiorio
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Historia");
        assertEquals(5,examen.getPreguntas().size());
        assertTrue(examen.getPreguntas().contains("integrales"));

        verify(repository).findAll(); //Verifica que del objeto mock repository se invoca el metodo findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());

    }

    @Test
    void testNoExisteExamenVerify() {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);

        Examen examen = service.findExamenPorNombreConPreguntas("Historia");

        assertNull(examen);

        //Simulacion de falla cuando la lista esta vacia
        verify(repository).findAll(); //Verifica que del objeto mock repository se invoca el metodo findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(anyLong());

    }

    @Test
    void testGuardarExamen() {
        //Entorno impulsado al comportamiento

        //Given (Precondiciones en el entorno de prueba)
        Examen newExamen = Datos.EXAMEN;
        newExamen.setPreguntas(Datos.PREGUNTAS);
        when(repository.guardar(any(Examen.class))).then(new Answer<Examen>(){
            Long secuencia = 8L; //Numero que tendra el id

            @Override
            public Examen answer(InvocationOnMock invocation) throws Throwable {
                Examen examen = invocation.getArgument(0);
                examen.setId(secuencia++); //Incrementar el id partiendo de seciencia

                return examen;
            }
        });

        //When (Cuando ejecutamos el metodo que queremos probar)
        Examen examen = service.guardar(newExamen);

        //Then (Entonces validamos )

        assertNotNull(examen.getId());
        assertEquals(8L, examen.getId());
        assertEquals("Fisica", examen.getNombre());

        verify(repository).guardar(any(Examen.class));
        verify(preguntaRepository).guardarVarias(anyList());

    }

    //Manejo de excepciones

    @Test
    void testManejoExcepciones() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NULL);
        when(preguntaRepository.findPreguntasPorExamenId(isNull())).thenThrow(IllegalArgumentException.class); //En lugar de retornar algo se lanza una excepcion

        //Verificamos que la excepcion se lance
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            service.findExamenPorNombreConPreguntas("Matematicas");
        });

        assertEquals(IllegalArgumentException.class, exception.getClass());

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(isNull());
    }

    //Argument matcher es una caracteristica de mockito que nos permite saber si el valor que se pasa por argumentos coincide con el parametro

    @Test
    void testArgumentMatchers() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(arg -> arg != null && arg >=5L));
    }

    @Test
    void testArgumentMatchers2() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVO);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat(new MiArgsMatchers()));
    }

    @Test
    void testArgumentMatchers3() {
        when(repository.findAll()).thenReturn(Datos.EXAMENES_ID_NEGATIVO);
        when(preguntaRepository.findPreguntasPorExamenId(anyLong())).thenReturn(Datos.PREGUNTAS);
        service.findExamenPorNombreConPreguntas("Matematicas");

        verify(repository).findAll();
        verify(preguntaRepository).findPreguntasPorExamenId(argThat((aLong) -> aLong != null && aLong > 0));
    }

    public static class MiArgsMatchers implements ArgumentMatcher<Long>{

        private Long argument;

        @Override
        public boolean matches(Long aLong) {
            this.argument = aLong;
            return aLong != null && aLong > 0;
        }

        @Override
        public String toString() {
            return "es para un mensaje personzalizado de " +
                    "error que imprime mockito en caso de " +
                    "fallar el test " + argument + " debe ser positivo";
        }
    }


}