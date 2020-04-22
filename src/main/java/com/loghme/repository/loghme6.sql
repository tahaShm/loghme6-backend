create table Users (
    name char(100),
    email char(100),
    credit integer,
    phoneNumber char(11),
    username char(50),
    password char(20),
    primary key (username)
);

create table Restaurants (
    id integer NOT NULL AUTO_INCREMENT,
    name char(100),
    logoUrl char(255),
    x float,
    y float,
    primary key (id)
);

create table Foods (
    id integer NOT NULL AUTO_INCREMENT,
    name char(100),
    description text,
    popularity float,
    imageUrl char(255),
    price integer,
    count integer,
    primary key (id)
);

create table PartyFoods (
    id integer NOT NULL AUTO_INCREMENT,
    foodId integer,
    newPrice integer,
    count integer,
    valid char(1),
    primary key (id),
    foreign key (foodId) references Foods(id) on delete cascade
);

create table Menu (
    restaurantId integer,
    foodId integer,
    primary key (restaurantId, foodId),
    foreign key (restaurantId) references Restaurants(id) on delete cascade,
    foreign key (foodId) references Foods(id) on delete cascade
);

create table PartyMenu (
    restaurantId integer,
    partyFoodId integer,
    primary key (restaurantId, partyFoodId),
    foreign key (restaurantId) references Restaurants(id) on delete cascade,
    foreign key (partyFoodId) references PartyFoods(id) on delete cascade
);

create table Orders (
    id integer NOT NULL AUTO_INCREMENT,
    username char(50),
    restaurantId integer,
    status char(1),
    registerTime DATETIME,
    primary key (id),
    foreign key (username) references Users(username) on delete no action,
    foreign key (restaurantId) references Restaurants(id) on delete cascade
);

create table OrderRows (
    orderId integer,
    foodId integer,
    partyFoodId integer,
    foodType char(20),
    amount integer,
    primary key (orderId, foodId, partyFoodId),
    foreign key (orderId) references Orders(id) on delete cascade,
    foreign key (foodId) references Foods(id) on delete no action,
    foreign key (partyFoodId) references PartyFoods(id) on delete no action
);