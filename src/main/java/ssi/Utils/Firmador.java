package ssi.Utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.Cipher;

public class Firmador {
    public static byte[] firmar(String entidad, byte[] hash) throws Exception{
        //leer nuestra clave privada
        //se podría utilizar java.security.signature pero esto es mas explicito
        KeyFactory generadorClaves = KeyFactory.getInstance("RSA", "BC");
        byte[] clavePrivadaBytes = Files.readAllBytes(Paths.get("keys/"+entidad+".privada"));
        PrivateKey clavePrivada = generadorClaves.generatePrivate(
                new PKCS8EncodedKeySpec(clavePrivadaBytes)
        );
        Cipher cifradorAsimetrico = Cipher.getInstance("RSA", "BC");
        cifradorAsimetrico.init(Cipher.ENCRYPT_MODE, clavePrivada);
        return cifradorAsimetrico.doFinal(hash);
    }
}
