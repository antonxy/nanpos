package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import de.unikarlsruhe.nan.pos.PS2BarcodeScanner;
import de.unikarlsruhe.nan.pos.objects.Product;
import de.unikarlsruhe.nan.pos.objects.User;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Anton Schirg
 */
public class BuyWindow extends Component {
    private User user;
    private final VerticalLayout verticalLayout;
    private final Label balanceLabel;
    private BuyWindowResultHandler resultCallback;
    private final CenterLayout centerLayout;

    public BuyWindow(final User user) {
        this.user = user;
        centerLayout = new CenterLayout();
        centerLayout.setParent(this);
        verticalLayout = new VerticalLayout();
        centerLayout.addChild(verticalLayout);

        final double balance = ((double) user.getBalance()) / 100;
        balanceLabel = new Label("Balance: " + balance);
        verticalLayout.addChild(balanceLabel);
        GridLayout gridLayout = new GridLayout(4);
        verticalLayout.addChild(gridLayout);

        try {
            List<Product> allProducts = Product.getAllProducts();
            for (final Product product : allProducts) {
                gridLayout.addChild(new Button(product.getName(), new Runnable() {
                    @Override
                    public void run() {
                        clickedProduct(product);
                    }
                }));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        gridLayout.addChild(new Button("Back", new Runnable() {
            @Override
            public void run() {
                resultCallback.handle("Exited by user", TextColor.ANSI.BLUE);
            }
        }));

        PS2BarcodeScanner.getInstance().setBarcodeListener(new PS2BarcodeScanner.BarcodeListener() {
            @Override
            public void barcodeScanned(long barCode) {
                System.err.println("Scanned " + barCode);
                try {
                    Product byEAN = Product.getByEAN(barCode);
                    if (byEAN != null) {
                        clickedProduct(byEAN);
                    } else {
                        resultCallback.handle("Unknown product", TextColor.ANSI.RED);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void clickedProduct(Product product) {
        PS2BarcodeScanner.getInstance().removeBarcodeListener();
        try {
            boolean success = user.buy(product);
            resultCallback.handle(success ? "Big success - you have bought the product" : "Fatal error - could not buy the product",
                    success ? TextColor.ANSI.GREEN : TextColor.ANSI.RED);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            user = null;
        }
    }

    public void setResultCallback(BuyWindowResultHandler resultCallback) {
        this.resultCallback = resultCallback;
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

    public interface BuyWindowResultHandler {
        public void handle(String result, TextColor color);
    }
}
