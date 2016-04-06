package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import de.unikarlsruhe.nan.pos.CardReader;
import de.unikarlsruhe.nan.pos.PS2BarcodeScanner;
import de.unikarlsruhe.nan.pos.objects.Product;
import de.unikarlsruhe.nan.pos.objects.User;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * @author Anton Schirg
 */
public class ScanCardWindow extends Window {

    private CenterLayout centerLayout;

    public ScanCardWindow(final CardReader.CardReaderListener listener) {
        centerLayout = new CenterLayout();
        setCentralComponent(centerLayout);

        VerticalLayout verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        verticalLayout.addChild(new Label("Scan card..."));
        verticalLayout.addChild(new Button("Cancel", new Runnable() {
            @Override
            public void run() {
                listener.onCardDetected(null, null);
            }
        }));

        CardReader.getInstance().setListener(new CardReader.CardReaderListener() {
            @Override
            public boolean onCardDetected(String cardnr, String uid) {
                if (ScanCardWindow.this.isVisible()) {
                    return listener.onCardDetected(cardnr, uid);
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    TerminalSize getPreferredSize() {
        return centerLayout.getPreferredSize();
    }

    @Override
    protected void layout(TerminalRectangle position) {
        centerLayout.layout(position);
    }

    @Override
    void redraw() {
        centerLayout.redraw();
    }

    @Override
    protected void onClick(TerminalPosition position) {
        centerLayout.onClick(position);
    }
}
