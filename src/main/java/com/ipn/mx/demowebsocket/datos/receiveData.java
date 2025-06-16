package com.ipn.mx.demowebsocket.datos;

import com.ipn.mx.demowebsocket.basedatos.domain.entity.Combate;
import com.ipn.mx.demowebsocket.basedatos.domain.repository.CombateRepository;
import com.ipn.mx.demowebsocket.basedatos.service.CombateService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;

/*
Agregar la anotacion Component
Component ==>Indica que la clase es un "bean" (objeto gestionado por Spring)
y será detectada automáticamente durante el escaneo del classpath
(siempre y cuando se encuentre habilitado @ComponentScan es decir siempre).
 */
// Se agrega la anotación que crea un Constructor con argumento cero para propiciar la inyección de dependencias
@NoArgsConstructor
@Component
public class receiveData {
    /*
      @Autowired ==> se utiliza para inyectar dependencias automáticamente en beans gestionados por el contenedor de Spring (IoC).
        La Inversión de Control (IoC) es un principio de diseño de software en el que el flujo de ejecución de un programa
        se delega a un contenedor o framework (en este caso, Spring), en lugar de ser controlado directamente por el desarrollador.
     */
    @Autowired
    private CombateRepository combateRepository;

    @Autowired
    private CombateService combateService;

    @Getter
    private String message;
    @Getter
    private int numericValue;

    private Integer id = 10;

  //Se modifica el constructir por defecto par ejecutar el metodo
  // procesar mensaje que reciba el texto correspondiente al payload o las datos útiles
  public receiveData(TextMessage message) {
    processMessage( message.getPayload());
  }

  public void processMessage(String payload) {
    this.message = payload;
    try {
      numericValue = (int) Double.parseDouble(message);
      guardarMensaje();
    } catch (NumberFormatException e) {
      System.err.println("Error: Mensaje no es número válido: " + message);
    }
  }


  private void guardarMensaje() {
      Combate combate = combateService.read(id);
    // Asigna el valor numérico
      combate.setPuntajeCompetidorUno(numericValue);

      combateService.save(combate);
    // Almacena en la BD MySQL
    System.out.println("Dato guardado en BD: " + numericValue);
  }
    /*
        public recibirDatos(TextMessage message) {
        this.mensaje = message.getPayload();
        procesarMensaje();
    }


    private void procesarMensaje() {
        try {
            // Convertimos el mensaje a un valor double
            valorNumerico = Double.parseDouble(mensaje);

            // Guardamos en el archivo XML (o simplemente texto)
            guardarMensaje();

            // Agregamos el valor a la lista de datos para graficar
            datosGraficables.add(valorNumerico);

        } catch (NumberFormatException e) {
            System.err.println("Error: El mensaje recibido no es un número válido: " + mensaje);
        }
    }

    private void guardarMensaje() {
        try (FileWriter fw = new FileWriter("Mensaje.xml", true)) {
            fw.write("<dato>" + valorNumerico + "</dato>\r\n");
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el mensaje", e);
        }
    }
    */


}
