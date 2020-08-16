package fenix31415.autotab;

import net.fabricmc.api.ModInitializer;

public class AutoTab implements ModInitializer {

    public static final String MOD_ID = "fenix";
    public SpecHUD hud;

    @Override
    public void onInitialize() {
        init_hud();
    }

    private void  init_hud() {
        hud = new SpecHUD();
    }
}
