package org.flinkfood.schemas.order;

import java.sql.Date;

public class Order {

    public int id;
    public String name;
    public int customer_id;
    public int restaurant_id;
    public int supplier_id;
    public String order_date;
    public String payment_date;
    public String delivery_date;
    public String description;
    public int total_amount;
    public String currency;
    public boolean supply_order;

    public Order() {
    }
}
