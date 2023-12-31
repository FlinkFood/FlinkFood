#!/bin/bash

# Database connection details
DB_HOST="postgres"
DB_PORT="5432"
DB_NAME="flinkfood"
DB_USER="postgres"
DB_PASSWORD="postgres"

export PGPASSWORD="$DB_PASSWORD"


update_restaurant_info() {
    # Generate random data variations
    restaurant_id=$(shuf -i 1-9 -n 1)
    phone_suffix=$(shuf -i 1000000000-9999999999 -n 1)
    email_suffix=$(shuf -i 1-100 -n 1)
    cuisine_type_suffix=$(shuf -i 1-5 -n 1)
    price_range_suffix=$(shuf -e low medium high -n 1)
    vat_code_suffix=$(shuf -i 100000000-999999999 -n 1)

    # Construct the SQL query to update specific columns in restaurant_info
    sql_query="UPDATE public.restaurant_info SET
                phone = '$phone_suffix',
                email = 'new.email$email_suffix@example.com',
                cuisine_type = '$cuisine_type_suffix',
                price_range = '$price_range_suffix',
                vat_code = $vat_code_suffix
                WHERE id = $restaurant_id;"

    echo "Updating specific columns in restaurant_info for restaurant_id $restaurant_id."
    # Execute the SQL query
    psql -h $DB_HOST -p $DB_PORT -d $DB_NAME -U $DB_USER -c "$sql_query"

    sleep_duration=$(shuf -i 1-2 -n 1)

    sleep $sleep_duration
}

insert_order() {
    # Generate random data
    id=$(shuf -i 1-10000000 -n 1)
    name="Order_$id"
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
    sql_query="INSERT INTO public.order (id, name, customer_id, restaurant_id, supplier_id, order_date, payment_date, delivery_date, description, total_amount, currency, supply_order) VALUES ($id, '$name', $customer_id, $restaurant_id, $supplier_id, '$order_date', '$payment_date', '$delivery_date', '$description', $total_amount, '$currency', TRUE);"

    echo "Inserting a $name into the database."
    # Execute the SQL query
    psql -h $DB_HOST -p $DB_PORT -d $DB_NAME -U $DB_USER -c "$sql_query"

    sleep_duration=$(shuf -i 1-2 -n 1)

    sleep $sleep_duration
}

while true; do
    insert_order
    update_restaurant_info
done
