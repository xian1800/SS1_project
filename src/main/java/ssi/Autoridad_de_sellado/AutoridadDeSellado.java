package ssi.Autoridad_de_sellado;

import java.security.Security;
import java.time.LocalDate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import ssi.Paquete.Paquete;
import ssi.Utils.Checker;
import ssi.Utils.Firmador;
import ssi.Utils.PaqueteNoValidoException;
import ssi.Utils.Resumenes;

public class AutoridadDeSellado  {
    public static void  main() throws Exception{
        System.out.println("------------ inicio de sellado ------------");
        Security.addProvider(new BouncyCastleProvider());

        Paquete paquete = new Paquete("paquete.pkt");

        if(Checker.procedenciaChecker("Empresa", paquete)){
            System.out.println("el paquete procede de empresa");
        }else{
            throw new PaqueteNoValidoException("el paquete no procede de empresa o fue adulterado");
        }
        LocalDate  now = LocalDate.now();
        System.out.println("añadida fecha");
        paquete.anadirBloque("fecha", now.toString().getBytes());

        byte[] clave_cif=paquete.getContenidoBloque("clave cifrada");
        byte[] factura_cif=paquete.getContenidoBloque("factura cifrada");
        byte[] firma=paquete.getContenidoBloque("firma");

        byte[] resumen = Resumenes.calcularResumenSellado(
            clave_cif,
            factura_cif,
            firma,
            now.toString().getBytes()
        );
        paquete.anadirBloque("sello", Firmador.firmar("AutoridadDeSellado", resumen));
        System.out.println("sellado el paquete");
        paquete.escribirPaquete("paquete.pkt");
        System.out.println("------------ fin de Sellado ------------");
    }
}