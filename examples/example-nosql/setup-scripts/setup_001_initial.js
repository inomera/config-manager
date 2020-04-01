use testDB;
db.testCollection.insertOne(
    { "application" : "application",
        "profile" : "profile",
        "label" : "master",
        "key" : "first-key",
        "value" : "first-key"
    }
);
db.testCollection.insertOne(
    { "application" : "application",
        "profile" : "profile",
        "label" : "master",
        "key" : "second-key",
        "value" : "second-value"
    }
);
db.testCollection.insertOne(
    { "application" : "application",
        "profile" : "profile",
        "label" : "master",
        "key" : "third-key",
        "value" : "third-value"
    }
);

db.testCollection.insertOne(
    { "application" : "application",
        "profile" : "profile",
        "label" : "master",
        "key" : "fifth-key",
        "value" : "fifth-value"
    }
);

db.testCollection.insertOne(
    { "application" : "charging",
        "profile" : "default",
        "label" : "master",
        "key" : "second-key",
        "value" : "second-value-2"
    }
);

db.testCollection.insertOne(
    { "application" : "charging",
        "profile" : "default",
        "label" : "master",
        "key" : "first-key",
        "value" : "first-value-2"
    }
);


db.testCollection.insertOne(
    { "application" : "charging",
        "profile" : "prod",
        "label" : "master",
        "key" : "second-key",
        "value" : "second-value-3"
    }
);


db.testCollection.insertOne(
    { "application" : "charging",
        "profile" : "prod",
        "label" : "master",
        "key" : "fourth-key",
        "value" : "fourth-value-3"
    }
);
