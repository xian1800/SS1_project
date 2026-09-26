package ssi.Hacienda;

import ssi.Paquete.Paquete;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import ssi.Utils.Checker;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

public class Hacienda {
    public static void main() throws Exception{
        System.out.println("------------ inicio de Hacienda ------------");
        Paquete paquete = new Paquete("paquete.pkt");

        Security.addProvider(new BouncyCastleProvider());

        //lectura del paquete
        byte[] clave_cif=paquete.getContenidoBloque("clave cifrada");
        byte[] factura_cif=paquete.getContenidoBloque("factura cifrada");
        byte[] firma=paquete.getContenidoBloque("firma");
        byte[] fecha=paquete.getContenidoBloque("fecha");
        byte[] sello=paquete.getContenidoBloque("sello");

        if(fecha == null || sello ==null){
            System.out.println("este paquete no paso por la autoridad de sellado");

        }else{
            //confirmar validez del sello
            MessageDigest hashMaker = MessageDigest.getInstance("SHA256", "BC");
            hashMaker.update(clave_cif);
            hashMaker.update(factura_cif);
            hashMaker.update(firma);
            hashMaker.update(fecha);

            if(!Arrays.equals(sello, hashMaker.digest())){
                System.out.println("problemas con el sello no es una factura valida");
            }else {
                System.out.println("sello verificado correctamente:");
            }
        }
        if(Checker.procedenciaChecker("Empresa", paquete)){
            System.out.println("procedencia reconocida");
        }else {
            System.out.println("este paquete fue adulterado o no procede de empresa");
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
