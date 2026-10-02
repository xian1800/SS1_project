package ssi.Inner;

import ssi.Adulteradores.Adulteredor_Empresa_Sellado;
import ssi.Autoridad_de_sellado.AutoridadDeSellado;
import ssi.Empresa.Empresa;
import ssi.Utils.PaqueteNoValidoException;

public class BadWayAntesDeAutoridad {
    public static void main(String[] args) throws Exception {
        Empresa.main();
        Adulteredor_Empresa_Sellado.main();
        try {
            AutoridadDeSellado.main();
        } catch (PaqueteNoValidoException exception) {
            System.out.println("Paquete rechazado antes del sellado: " + exception.getMessage());
        }
    }
}
