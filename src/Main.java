import logic.Core;
import logic.LinearFieldAlgorithm;
import gui.StartFX;

/**
 * Created by joey on 2016.10.05..
 */
public class Main {

    public static void main(String[] args) {
        Core core = new LinearFieldAlgorithm();

        StartFX.setCore(core);
        StartFX.start();
    }

}
