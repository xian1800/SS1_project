package ssi.Inner;

import ssi.Adulteradores.Adulterado_Sellado_Hacienda;
import ssi.Autoridad_de_sellado.AutoridadDeSellado;
import ssi.Empresa.Empresa;
import ssi.Hacienda.Hacienda;
import ssi.Utils.PaqueteNoValidoException;

public class BadWayDespuesDeAutoridad {
    public static void main(String[] args) throws Exception {
        Empresa.main();
        AutoridadDeSellado.main();
        Adulterado_Sellado_Hacienda.main();
        try {
            Hacienda.main();
        } catch (PaqueteNoValidoException exception) {
            System.out.println("Paquete rechazado por Hacienda: " + exception.getMessage());
        }
    }
}
