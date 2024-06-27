package com.example.schwabro.terminology;

import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AllTermsMap {
    public static final Map<String, TermEntity> ALL_TERMS_MAP = new TreeMap<>() {{
        put("Order", new TermEntity("Order", " An instruction sent to open or close a transaction if " +
                "the conditions specified by a user are satisfied. Orders can be market orders or pending orders " +
                "(limit and stop orders).", "https://www.investopedia.com/terms/o/order.asp"));
        put("Market Order", new TermEntity("Market Order", "An order instantly executed against a price " +
                "provided by the broker. It is executed at the current market price.",
                "https://www.investopedia.com/terms/m/marketorder.asp"));
        put("Stop Order", new TermEntity("Stop Order", "An order to buy or sell at a certain price in" +
                " the direction of the implied market movement. It becomes a market order once the stop price " +
                "is reached.", "https://www.investopedia.com/terms/s/stoporder.asp"));
        put("Order Group", new TermEntity("Order Group (or Conditional Order)", "A combination of single orders linked by " +
                "particular rules to achieve a specific result. Examples include OCO (One Cancels the Other) " +
                "and IF-THEN strategies.", "https://www.investopedia.com/terms/c/conditionalorder.asp"));
        put("IF-THEN Strategy", new TermEntity("IF-THEN Strategy (or Order-Sends-Order)", "A strategy where the execution of one " +
                "order triggers another order.",
                "https://www.investopedia.com/order-sends-order-oso-definition-5190104"));
        put("OCO", new TermEntity("OCO (One Cancels the Other)", "A pair of orders where the" +
                " execution of one order cancels the other.",
                "https://www.investopedia.com/terms/o/oco.asp"));
        put("Bracket Order", new TermEntity("Bracket Order", "An order designed to help limit loss and" +
                " lock in profit by \"bracketing\" an order with two opposite-side orders.",
                "https://www.investopedia.com/terms/b/bracketedsellorder.asp"));
        put("TIF", new TermEntity("Time in Force (TIF)", "Indicates how long an order will remain " +
                "active before it expires. Examples include GTC (Good Till Canceled), DAY, and GTD (Good Till Date).",
                "https://www.investopedia.com/terms/t/timeinforce.asp"));
        put("Execution", new TermEntity("Execution", "The process of completing an order. It can involve " +
                "various states such as NEW, ACCEPTED, WORKING, FILLED, CANCELED and REJECTED.",
                "https://www.investopedia.com/terms/e/execution.asp"));
        put("Limit Order", new TermEntity("Limit Order", "An order to buy or sell once the market reaches " +
                "a specified price", "https://www.investopedia.com/terms/l/limitorder.asp"));
        put("Trailing Stop Order", new TermEntity("Trailing Stop Order", "Order that moves with the market price " +
                "to lock in profits while limiting losses.",
                "https://www.investopedia.com/terms/t/trailingstop.asp"));
        put("Futures", new TermEntity("Futures", "A futures contract is a standardized agreement to buy" +
                " or sell an asset at a predetermined price at a specified time in the future. Futures contracts can" +
                " be used for hedging or speculative purposes. They are traded on exchanges and are highly liquid",
                "https://www.investopedia.com/terms/f/futurescontract.asp"));
        put("Options", new TermEntity("Options", "An option is a financial derivative that gives " +
                "the buyer the right, but not the obligation, to buy or sell an underlying asset at a specified price " +
                "before or at a certain date.",
                "https://www.investopedia.com/terms/o/option.asp"));
        put("Call Option", new TermEntity("Call Option", " Gives the holder the right to buy " +
                "the underlying asset.",
                "https://www.investopedia.com/terms/c/calloption.asp"));
        put("Put Option", new TermEntity("Put Option", "Gives the holder the right to sell " +
                "the underlying asset.",
                "https://www.investopedia.com/terms/p/putoption.asp"));
        put("Futures Option", new TermEntity("Futures Option", " Futures options are options on " +
                "futures contracts. They give the holder the right, but not the obligation, to enter into a futures " +
                "contract at a specified price before the option expires. This combines the features of both futures " +
                "and options, providing flexibility and leverage.", "kll"));
        put("Vertical Spread", new TermEntity("Vertical Spread", "Involves buying and selling options " +
                "of the same type (calls or puts) with different strike prices but the same expiration date.\n" +
                "  - Example: Buy a call option with a lower strike price and sell a call option with a higher " +
                "strike price.", "https://www.investopedia.com/terms/v/verticalspread.asp"));
        put("Straddle", new TermEntity("Straddle", "Involves buying both a call and a put option with " +
                "the same strike price and expiration date.\n" +
                "  - Example: Buy a call and a put option at the same strike price.",
                "https://www.investopedia.com/terms/s/straddle.asp"));
        put("Strangle", new TermEntity("Strangle", "Involves buying both a call and a put option " +
                "with different strike prices but the same expiration date.\n" +
                "  - Example: Buy a call option with a higher strike price and a put option with a lower strike price.",
                "https://www.investopedia.com/terms/s/strangle.asp"));
        put("Butterfly Spread", new TermEntity("Butterfly Spread", "Involves a combination of buying " +
                "and selling multiple options to create a spread with three strike prices.\n" +
                "  - Example: Buy one call option at a lower strike price, sell two call options at a middle strike " +
                "price, and buy one call option at a higher strike price.",
                "https://www.investopedia.com/terms/b/butterflyspread.asp"));
        put("Iron Condor", new TermEntity("Iron Condor", "Involves selling a call and a put option " +
                "at one strike price and buying a call and a put option at different strike prices.\n" +
                "  - Example: Sell a call and a put option at middle strike prices, buy a call option " +
                "at a higher strike price, and buy a put option at a lower strike price.",
                "https://www.investopedia.com/terms/i/ironcondor.asp"));
        put("Calendar Spread", new TermEntity("Calendar Spread", "Involves buying and selling options " +
                "of the same type and strike price but with different expiration dates.\n" +
                "  - Example: Buy a call option with a later expiration date and sell a call option with an earlier " +
                "expiration date.", "https://www.investopedia.com/terms/c/calendarspread.asp"));
        put("Diagonal Spread", new TermEntity(" Diagonal Spread", " Involves buying and selling options " +
                "of the same type but with different strike prices and expiration dates.\n" +
                "  - Example: Buy a call option with a higher strike price and later expiration date, and sell a " +
                "call option with a lower strike price and earlier expiration date.",
                "https://www.investopedia.com/terms/d/diagonalspread.asp"));
        put("Covered Call", new TermEntity("Covered Call", "Involves holding a long position in an " +
                "underlying asset and selling a call option on that asset.\n" +
                "  - Example: Own the underlying stock and sell a call option on the same stock.",
                "https://www.investopedia.com/articles/optioninvestor/08/covered-call.asp"));
        put("Protective Put", new TermEntity("Protective Put", "Involves holding a long position in " +
                "an underlying asset and buying a put option to protect against a decline in the asset's price.\n" +
                "  - Example: Own the underlying stock and buy a put option on the same stock.",
                "https://www.investopedia.com/terms/p/protective-put.asp"));
        put("Iron Butterfly", new TermEntity("Iron Butterfly", "Involves selling a straddle and " +
                "buying a strangle.\n" +
                "   - Example: Sell a call and a put option at the same strike price, and buy a call option at a " +
                "higher strike price and a put option at a lower strike price.",
                "https://www.investopedia.com/articles/active-trading/030314/what-iron-butterfly-option-strategy.asp"));
    }};

    public static String toHTML() {
        String str = String.valueOf(ALL_TERMS_MAP.values()
                        .stream()
                        .map(TermEntity::toHtml)
                        .collect(Collectors.toList()))
                .replace("[", "")
                .replace("]","")
                .replace("<br>,","<tr><br></tr>");

        return "<html><table>" + str + "</table></html>";
    }
}