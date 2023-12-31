import csv
import random


# id,restaurant_id,customer_id,rating,comment

def generate_review():
    '''
    function that chooses random values from the values below and returns a tuple
    that will be added to the table.
    '''
    restaurant_id = random.randint(1, number_of_restaurants)
    customer_id = random.randint(1, number_of_customers)
    rating = random.randint(1, 10)
    comment_list = [
        "Loved it! Great atmosphere, service, and food.",
        "Disappointing. Slow service, overpriced, and lacking ambiance.",
        "Decent selection, friendly staff. Average food. Good for a quick bite.",
        "Perfect for family dinner! Diverse menu, accommodating staff.",
        "Vegetarian heaven! Flavorful plant-based options. Refreshing!",
        "Ideal for a romantic dinner. Dim lighting, attentive service.",
        "Quick and tasty! Fast-casual with fresh ingredients.",
        "Local gem! Authentic flavors, welcoming atmosphere. A favorite!",
        "Fantastic for special occasions. Staff went above and beyond.",
        "Must-try brunch! Creative twists on classics. Refreshing mimosas."
    ]

    comment = random.choice(comment_list)

    return (restaurant_id,customer_id, rating, comment) #tuple to be added on the table

def generate_reviews(num_addresses):
    '''
    function that create a list with sample tuples generated by the function generate_review()
    you can set the number you want for the number of lines
    '''
    addresses = []
    i = 1
    for _ in range(num_addresses):
        address = generate_review()
        addresses.append((i,) + address)
        i += 1

    return addresses

def save_to_csv(addresses, filename):
    '''
    function that saves the list of tuples in a csv file
    '''
    with open(filename, mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow('id,restaurant_id,customer_id,rating,comment'.split(','))
        writer.writerows(addresses)

number_of_restaurants = 10000
number_of_customers = 10
num_addresses = 10000 #set the number of lines you want
addresses = generate_reviews(num_addresses)
save_to_csv(addresses, 'restaurant_reviews_generated.csv')
