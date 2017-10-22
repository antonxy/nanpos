package de.unikarlsruhe.nan.pos.objects;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author Anton Schirg
 */
public class Utils {
    public static String formatPrice(int cents) {
        double euros = ((double) cents) / 100;
        NumberFormat formatter = NumberFormat
                .getCurrencyInstance(Locale.GERMANY);
        return formatter.format(euros);
    }
}
