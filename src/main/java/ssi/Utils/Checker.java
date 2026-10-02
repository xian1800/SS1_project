package ssi.Utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;

import ssi.Paquete.Paquete;

public class Checker {
    public static boolean   procedenciaChecker(String procedente, Paquete paquete) throws Exception{
        return procedenciaChecker(paquete, Paths.get("keys", procedente + ".publica"));
    }

    public static boolean procedenciaChecker(
            Paquete paquete,
            Path clavePublica
    ) throws Exception {
        byte[] clave_cif=paquete.getContenidoBloque("clave cifrada");
        byte[] factura_cif=paquete.getContenidoBloque("factura cifrada");
        byte[] firma=paquete.getContenidoBloque("firma");
        if(clave_cif == null || factura_cif == null || firma == null){
            return false;
        }
        //convertir en hash el resultado
        MessageDigest hashMaker = MessageDigest.getInstance("SHA256", "BC");
        hashMaker.update(factura_cif);
        hashMaker.update(clave_cif);
        return comprobarFirma(clavePublica, firma, hashMaker.digest());
    }

    public static boolean selloChecker(Paquete paquete) throws Exception{
        byte[] clave_cif=paquete.getContenidoBloque("clave cifrada");
        byte[] factura_cif=paquete.getContenidoBloque("factura cifrada");
        byte[] firma=paquete.getContenidoBloque("firma");
        byte[] fecha=paquete.getContenidoBloque("fecha");
        byte[] sello=paquete.getContenidoBloque("sello");
        if(clave_cif == null || factura_cif == null || firma == null || fecha == null || sello == null){
            return false;
        }
        byte[] resumen = Resumenes.calcularResumenSellado(clave_cif, factura_cif, firma, fecha);
        return comprobarFirma("AutoridadDeSellado", sello, resumen);
    }

    private static boolean comprobarFirma(String procedente, byte[] firma, byte[] resumen) throws Exception{
        return comprobarFirma(Paths.get("keys", procedente + ".publica"), firma, resumen);
    }

    private static boolean comprobarFirma(Path rutaClavePublica, byte[] firma, byte[] resumen) throws Exception{
        byte[] clavePublicaEmpresaBytes = Files.readAllBytes(rutaClavePublica);
        KeyFactory generadorClaves = KeyFactory.getInstance("RSA", "BC");
        PublicKey clavePublicaEmpresa = generadorClaves.generatePublic(
                new X509EncodedKeySpec(clavePublicaEmpresaBytes)
        );
        //desencriptado de la firma
        Cipher cifradorAsimetrico = Cipher.getInstance("RSA", "BC");
        cifradorAsimetrico.init(Cipher.DECRYPT_MODE, clavePublicaEmpresa);
        try {
            byte[] firmaDecripted = cifradorAsimetrico.doFinal(firma);
            return Arrays.equals(firmaDecripted, resumen);
        } catch (GeneralSecurityException ex) {
            return false;
        }
    }
}
