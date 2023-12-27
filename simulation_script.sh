#!/bin/bash

# Database connection details
DB_HOST="postgres"
DB_PORT="5432"
DB_NAME="flinkfood"
DB_USER="postgres"
DB_PASSWORD="postgres"

export PGPASSWORD="$DB_PASSWORD"

insert_order() {

    # Generate random data
    name="Order_$(shuf -i 1-10000000 -n 1)"
    customer_id=$(shuf -i 1-5 -n 1)
    restaurant_id=$(shuf -i 1-9 -n 1)
    supplier_id=$(shuf -i 1-4 -n 1)
    order_date=$(date "+%Y-%m-%d")
    payment_date=$(date -d "$order_date + $(shuf -i 1-7 -n 1) days" "+%Y-%m-%d")
    delivery_date=$(date -d "$order_date + $(shuf -i 1-14 -n 1) days" "+%Y-%m-%d")
    description="Random order description"
    total_amount=$(shuf -i 10-100 -n 1)
    currency="USD"

    # Construct the SQL query
    sql_query="INSERT INTO public.order (name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ('$name', $customer_id, $restaurant_id, $supplier_id, '$order_date', '$payment_date', '$delivery_date', '$description', $total_amount, '$currency', TRUE);"

    # Execute the SQL query
    psql -h $DB_HOST -p $DB_PORT -d $DB_NAME -U $DB_USER -c "$sql_query"

    echo "Inserted a $name into the database."
}

while true; do
    # Sleep for 5 seconds before the next iteration
    insert_order
    sleep 5
done
