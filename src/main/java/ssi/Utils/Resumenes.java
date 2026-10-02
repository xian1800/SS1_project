package ssi.Utils;

import java.security.MessageDigest;

public class Resumenes {
    public static byte[] calcularResumenSellado(byte[] claveCifrada, byte[] facturaCifrada, byte[] firma, byte[] fecha) throws Exception{
        MessageDigest hashMaker = MessageDigest.getInstance("SHA256", "BC");
        hashMaker.update(claveCifrada);
        hashMaker.update(facturaCifrada);
        hashMaker.update(firma);
        hashMaker.update(fecha);
        return hashMaker.digest();
    }
}