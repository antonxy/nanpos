package de.unikarlsruhe.nan.pos.tui;

import de.unikarlsruhe.nan.pos.objects.User;

/**
 * @author Anton Schirg
 */
public class RecyclingBuyWindowFactoryBuilder {
    public static RecyclingBuyWindowFactory build() {
        return new RecyclingBuyWindowFactory();
    }

    public static class RecyclingBuyWindowFactory {
        public BuyWindow factorarte(User user) {
            return new BuyWindow(user);
        }
    }
}
