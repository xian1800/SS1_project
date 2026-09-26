package ssi.Utils;

import ssi.Paquete.Paquete;

import javax.crypto.Cipher;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class Checker {
    public static boolean   procedenciaChecker(String procedente, Paquete paquete) throws Exception{
        byte[] clave_cif=paquete.getContenidoBloque("clave cifrada");
        byte[] factura_cif=paquete.getContenidoBloque("factura cifrada");
        byte[] firma=paquete.getContenidoBloque("firma");
        //convertir en hash el resultado
        MessageDigest hashMaker = MessageDigest.getInstance("SHA256", "BC");
        hashMaker.update(factura_cif);
        hashMaker.update(clave_cif);
        //conseguir la clave publica de empresa y desencriptar la firma
        byte[] clavePublicaEmpresaBytes = Files.readAllBytes(Paths.get("keys/" +procedente +".publica"));
        KeyFactory generadorClaves = KeyFactory.getInstance("RSA", "BC");
        PublicKey clavePublicaEmpresa = generadorClaves.generatePublic(
                new X509EncodedKeySpec(clavePublicaEmpresaBytes)
        );
        //desencriptado de la firma
        Cipher cifradorAsimetrico = Cipher.getInstance("RSA", "BC");
        cifradorAsimetrico.init(Cipher.DECRYPT_MODE, clavePublicaEmpresa);
        byte[] firmaDecripted = cifradorAsimetrico.doFinal(firma);
        return (Arrays.equals(firmaDecripted, hashMaker.digest()));
    }
}
