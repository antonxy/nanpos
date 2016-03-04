package de.unikarlsruhe.nan.pos.tui;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalRectangle;
import com.googlecode.lanterna.TerminalSize;
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
    private Runnable resultCallback;

    public BuyWindow(final User user) {
        this.user = user;
        verticalLayout = new VerticalLayout();
        verticalLayout.setParent(this);

        balanceLabel = new Label("Balance: " + user.getBalance());
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
    }

    private void clickedProduct(Product product) {
        try {
            user.buy(product);
            resultCallback.run();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            user = null;
        }
    }

    private void setResultCallback(Runnable resultCallback) {
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
}
