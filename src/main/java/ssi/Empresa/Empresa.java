package ssi.Empresa;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import ssi.Paquete.Paquete;
import ssi.Utils.Firmador;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Empresa {

    public static void main() throws Exception{
        System.out.println("------------ inicio de empresa ------------");
        //creacion del paquete:
        Paquete paquete = new Paquete();
        Security.addProvider(new BouncyCastleProvider());

        //lectura de la factura:
        byte[] datosFactura = Files.readAllBytes(Paths.get("factura.json"));
        System.out.println("leyendo factura...");
        System.out.println("Bytes leídos: " + datosFactura.length);

        System.out.println("generando clave secreta...");
        KeyGenerator generadorClave = KeyGenerator.getInstance("DES", "BC");
        SecretKey claveSecreta = generadorClave.generateKey();

        System.out.println("encriptando la factura...");
        Cipher cifradorSimetrico = Cipher.getInstance("DES", "BC");
        cifradorSimetrico.init(Cipher.ENCRYPT_MODE, claveSecreta);
        byte[] facturaCifrada = cifradorSimetrico.doFinal(datosFactura);
        System.out.println("factura encriptada y añadida al paquete con exito...");
        paquete.anadirBloque("factura cifrada", facturaCifrada);

        //lectura de la clave publica de hacienda:
        byte[] clavePublicaHaciendaBytes = Files.readAllBytes(Paths.get("keys/Hacienda.publica"));
        KeyFactory generadorClaves = KeyFactory.getInstance("RSA", "BC");
        PublicKey clavePublicaHacienda = generadorClaves.generatePublic(
                new X509EncodedKeySpec(clavePublicaHaciendaBytes)
        );

        //encriptado de la contraseña secreta
        Cipher cifradorAsimetrico = Cipher.getInstance("RSA", "BC");
        cifradorAsimetrico.init(Cipher.ENCRYPT_MODE, clavePublicaHacienda);
        byte[] claveSecretaCifrada = cifradorAsimetrico.doFinal(claveSecreta.getEncoded());
        paquete.anadirBloque("clave cifrada", claveSecretaCifrada);
        System.out.println("añadida clave secreta encriptada");

        //creacion de la firma
        System.out.println("generando resumen hash...");
        MessageDigest resumenHash = MessageDigest.getInstance("SHA256", "BC");
        resumenHash.update(facturaCifrada);
        resumenHash.update(claveSecretaCifrada);
        byte[] resumen = resumenHash.digest();


        paquete.anadirBloque("firma", Firmador.firmar("Empresa", resumen));
        System.out.println("firma añadida");

        paquete.escribirPaquete("paquete.pkt");
        System.out.println("paquete generado");
        System.out.println("------------ fin de empresa ------------");

    }
}