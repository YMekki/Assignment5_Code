error id: file:///C:/Users/Yasin/School/software%20testing/Assignment%205/Assignment5_Code/Assignment5_Code/src/main/java/org/example/Amazon/Amazon.java:
file:///C:/Users/Yasin/School/software%20testing/Assignment%205/Assignment5_Code/Assignment5_Code/src/main/java/org/example/Amazon/Amazon.java
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:

offset: 220
uri: file:///C:/Users/Yasin/School/software%20testing/Assignment%205/Assignment5_Code/Assignment5_Code/src/main/java/org/example/Amazon/Amazon.java
text:
```scala
package org.example.Amazon;

import org.example.Amazon.Cost.PriceRule;

import java.util.List;

public class Amazon {

    private final List<PriceRule> rules;
    private final ShoppingCart carts;

    public@@ Amazon(ShoppingCart carts, List<PriceRule> rules) {
        this.carts = carts;
        this.rules = rules;
    }

    public double calculate() {
        double finalPrice = 0;

        for (PriceRule rule : rules) {
            finalPrice += rule.priceToAggregate(carts.getItems());
        }

        return finalPrice;
    }

    public void addToCart(Item item){
        carts.add(item);
    }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 