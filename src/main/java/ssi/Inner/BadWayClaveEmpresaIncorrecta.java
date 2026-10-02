package ssi.Inner;

import java.nio.file.Paths;

import ssi.Autoridad_de_sellado.AutoridadDeSellado;
import ssi.Empresa.Empresa;
import ssi.Hacienda.Hacienda;
import ssi.Utils.PaqueteNoValidoException;

public class BadWayClaveEmpresaIncorrecta {
    public static void main(String[] args) throws Exception {
        Empresa.main();
        AutoridadDeSellado.main();
        try {
            Hacienda.main(Paths.get("keys", "Hacienda.publica"));
        } catch (PaqueteNoValidoException exception) {
            System.out.println("Paquete rechazado por Hacienda: " + exception.getMessage());
        }
    }
}
