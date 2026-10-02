package ssi.Hacienda;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import ssi.Paquete.Paquete;
import ssi.Utils.Checker;
import ssi.Utils.PaqueteNoValidoException;

public class Hacienda {
    public static void main() throws Exception{
        main(Paths.get("keys", "Empresa.publica"));
    }

    public static void main(Path clavePublicaEmpresa) throws Exception{
        System.out.println("------------ inicio de Hacienda ------------");
        Paquete paquete = new Paquete("paquete.pkt");

        Security.addProvider(new BouncyCastleProvider());

        //lectura del paquete
        byte[] clave_cif=paquete.getContenidoBloque("clave cifrada");
        byte[] factura_cif=paquete.getContenidoBloque("factura cifrada");
        if(!Checker.selloChecker(paquete)){
            throw new PaqueteNoValidoException("el paquete no tiene un sello valido");
        }else{
            System.out.println("sello verificado correctamente:");
        }
        if(Checker.procedenciaChecker(paquete, clavePublicaEmpresa)){
            System.out.println("procedencia reconocida");
        }else {
            throw new PaqueteNoValidoException("este paquete fue adulterado o no procede de empresa");
        }
        Cipher cifradorAsimetrico = Cipher.getInstance("RSA", "BC");
        KeyFactory generadorClaves = KeyFactory.getInstance("RSA", "BC");
        //desencriptado de la factura
        //cargar contraseña privada de Hacienda
        byte[] clavePrivadaHaciendaBytes = Files.readAllBytes(Paths.get("keys/Hacienda.privada"));
        PrivateKey claveHaciendaPrivada= generadorClaves.generatePrivate(
                new PKCS8EncodedKeySpec(clavePrivadaHaciendaBytes)
        );
        cifradorAsimetrico.init(Cipher.DECRYPT_MODE, claveHaciendaPrivada);
        //descifrado de la contraseña simetrica
        byte[] claveSecretaDescifrada =cifradorAsimetrico.doFinal(clave_cif);
        SecretKeyFactory skFactory = SecretKeyFactory.getInstance("DES", "BC");
        SecretKey claveSecreta = skFactory.generateSecret(new SecretKeySpec(claveSecretaDescifrada, "DES") );

        Cipher cifradorSimetrico = Cipher.getInstance("DES", "BC");
        cifradorSimetrico.init(Cipher.DECRYPT_MODE, claveSecreta);
        byte[] datosFactura = cifradorSimetrico.doFinal(factura_cif);
        System.out.println("factura ha sido descifrada");
        System.out.println(new String(datosFactura));
        System.out.println("------------ fin de Hacienda ------------");
    }
}
