package org.flinkfood;

import java.sql.Date;

public class Orders {

    public int id;
    public String name;
    public int customer_id;
    public int restaurant_id;
    public int supplier_id;
    public String order_date;
    public String payment_date;
    public String delivery_date;
    public String description;
    public float total_amount;
    public String currency;
    public boolean supply_oder;

    public Orders() {
    }
}
