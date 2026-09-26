package ssi.Adulteradores;

import ssi.Paquete.Paquete;

public class Adulterado_Sellado_Hacienda {
    public  static void main(){

        System.out.println("------------------ ataque en curso -----------------");
        Paquete paquete = new Paquete("paquete.pkt");
        paquete.actualizarBloque("fecha", ("23/03/2003").getBytes());
        paquete.escribirPaquete("paquete.pkt");

        System.out.println("------------------ fin del ataque -----------------");
    }
}