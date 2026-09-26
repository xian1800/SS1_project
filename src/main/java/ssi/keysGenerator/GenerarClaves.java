package ssi.keysGenerator;

import java.io.*;

import java.security.*;
import java.util.Scanner;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

// Necesario para usar el provider Bouncy Castle (BC)
//    Para compilar incluir el fichero JAR en el classpath

public class GenerarClaves {
	public static void main(String[] args) throws Exception {
		String ruta = "keys/";
  		String prefijo;
		Scanner scn = new Scanner(System.in);
		System.out.println("introduzca el prefijo de la clave");
		prefijo=scn.nextLine();

		// Anadir provider  (el provider por defecto no soporta RSA)
		Security.addProvider(new BouncyCastleProvider()); // Cargar el provider BC

		/*** Crear claves RSA 512 bits  */
		KeyPairGenerator generadorRSA = KeyPairGenerator.getInstance("RSA", "BC"); // Hace uso del provider BC
		generadorRSA.initialize(512);
		KeyPair clavesRSA = generadorRSA.generateKeyPair();
		PrivateKey clavePrivada = clavesRSA.getPrivate();
		PublicKey clavePublica = clavesRSA.getPublic();

		/*** 1 Volcar clave privada  a fichero */
		// 1.1 Recuperar de la clave su codificaciÃ³n en formato PKS8 (necesario para escribirla a disco)
		byte[] encodedPKCS8 = clavePrivada.getEncoded();

		// 1.2 Escribirla a fichero binario
		FileOutputStream out = new FileOutputStream(ruta+prefijo+ ".privada");
		out.write(encodedPKCS8);
		out.close();

		/*** 3 Volcar clave publica  a fichero */
		// 3.1  Recuperar de la clave su codificaciÃ³n en formato X509 (necesario para escribirla a disco)
		byte[] encodedX509 = clavePublica.getEncoded();

		// 3.2 Escribirla a fichero binario
		out = new FileOutputStream(ruta+prefijo + ".publica");
		out.write(encodedX509);
		out.close();

		System.out.println("Generadas claves RSA publica y privada de 512 bits en ficheros "+prefijo + ".publica"+ " y "+prefijo + ".privada");

	}

	public static void mensajeAyuda() {
		System.out.println("Generador de pares de clave RSA de 512 bits");
		System.out.println("\tSintaxis:   java GenerarClaves prefijo");
		System.out.println();
	}
}