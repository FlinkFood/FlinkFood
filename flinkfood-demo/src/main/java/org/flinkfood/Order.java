package org.flinkfood;

import java.sql.Date;

public class Order {

    public int id;
    public String name;
    public int customer_id;
    public int restaurant_id;
    public int supplier_id;
    public Date order_Date;
    public Date delivery_date;
    public String description;
    public int total_amount;
    public String currency;
    public boolean supply_oder;

    public Order() {
    }
}
