package ssi.Adulteradores;

import ssi.Paquete.Paquete;

public class Adulteredor_Empresa_Sellado {

    // Representa que alguien intercepta el paquete antes de llegar a la Autoridad de Sellado
    public static void main() {

        Paquete paquete = new Paquete("paquete.pkt");

        byte[] facturaCifrada = paquete.getContenidoBloque("factura cifrada");

        String stringCifrado = new String(facturaCifrada);

        System.out.println("------------------ ataque en curso -----------------");
        System.out.println("Esta es la factura cifrada:");
        System.out.println(stringCifrado);

        paquete.eliminarBloque("factura cifrada");
        paquete.anadirBloque(
                "factura cifrada",
                ("asdasda").getBytes()
        );

        paquete.escribirPaquete("paquete.pkt");

        System.out.println("------------------ fin del ataque -----------------");

    }
}