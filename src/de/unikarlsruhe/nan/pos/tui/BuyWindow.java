package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
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

    public BuyWindow(final User user) {
        this.user = user;
        verticalLayout = new VerticalLayout();
        verticalLayout.setParent(this);

        double balance = ((double) user.getBalance()) / 100;
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

        PS2BarcodeScanner.getInstance().setBarcodeListener(new PS2BarcodeScanner.BarcodeListener() {
            @Override
            public void barcodeScanned(int barCode) {
                try {
                    Product byEAN = Product.getByEAN(barCode);
                    if (byEAN != null) {
                        clickedProduct(byEAN);
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
            resultCallback.handle(success ? "Big success - you have bought the product" : "Fatal error - could not buy the product");
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
        return verticalLayout.getPreferredSize();
    }

    @Override
    protected void layout(TerminalRectangle position) {
        verticalLayout.layout(position);
    }

    @Override
    void redraw() {
        verticalLayout.redraw();
    }

    @Override
    protected void onClick(TerminalPosition position) {
        verticalLayout.onClick(position);
    }

    public interface BuyWindowResultHandler {
        public void handle(String result);
    }
}
