package ssi.Autoridad_de_sellado;

import ssi.Paquete.Paquete;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import ssi.Utils.Checker;

import java.security.MessageDigest;
import java.security.Security;
import java.time.LocalDate;

public class AutoridadDeSellado  {
    public static void  main() throws Exception{
        System.out.println("------------ inicio de sellado ------------");
        Security.addProvider(new BouncyCastleProvider());

        LocalDate  now = LocalDate.now();
        Paquete paquete = new Paquete("paquete.pkt");

        System.out.println("añadida fecha");
        paquete.anadirBloque("fecha", now.toString().getBytes());

        if(Checker.procedenciaChecker("Empresa", paquete)){
            System.out.println("el paquete procede de empresa");
        }else{
            System.out.println("el paquete no procede de empresa o fue adulterado");
            throw new Exception();
        }
        byte[] clave_cif=paquete.getContenidoBloque("clave cifrada");
        byte[] factura_cif=paquete.getContenidoBloque("factura cifrada");
        byte[] firma=paquete.getContenidoBloque("firma");

        MessageDigest hashMaker = MessageDigest.getInstance("SHA256", "BC");
        hashMaker.update(clave_cif);
        hashMaker.update(factura_cif);
        hashMaker.update(firma);
        hashMaker.update(now.toString().getBytes());
        paquete.anadirBloque("sello", hashMaker.digest());
        System.out.println("sellado el paquete");
        paquete.escribirPaquete("paquete.pkt");
        System.out.println("------------ fin de Sellado ------------");
    }
}