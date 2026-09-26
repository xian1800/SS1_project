package ssi.Inner;

import ssi.Adulteradores.Adulterado_Sellado_Hacienda;
import ssi.Adulteradores.Adulteredor_Empresa_Sellado;
import ssi.Autoridad_de_sellado.AutoridadDeSellado;
import ssi.Empresa.Empresa;
import ssi.Hacienda.Hacienda;

public class BadWay  {
    public static void main()throws Exception{
        Empresa.main();
        //Adulteredor_Empresa_Sellado.main();
        AutoridadDeSellado.main();
        Adulterado_Sellado_Hacienda.main();
        Hacienda.main();
    }
}
