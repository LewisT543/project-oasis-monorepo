CREATE TABLE IF NOT EXISTS PropertyType (
                                            id SERIAL PRIMARY KEY,
                                            name VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO PropertyType (name) VALUES
                                    ('Detached'),
                                    ('Semi_Detached'),
                                    ('Terraced'),
                                    ('Bungalow'),
                                    ('Flat')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS Tenure (
                                      id SERIAL PRIMARY KEY,
                                      name VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO Tenure (name) VALUES
                              ('Freehold'),
                              ('Leasehold'),
                              ('Shared_Freehold')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS StationType (
                                           id SERIAL PRIMARY KEY,
                                           name VARCHAR(255) UNIQUE NOT NULL
);

INSERT INTO StationType (name) VALUES
                                   ('Train'),
                                   ('Tram'),
                                   ('Bus')
ON CONFLICT DO NOTHING;

CREATE TABLE IF NOT EXISTS Location (
                                        id SERIAL PRIMARY KEY,
                                        road VARCHAR(255),
                                        region VARCHAR(255),
                                        city VARCHAR(255),
                                        postcode VARCHAR(255) UNIQUE
);

CREATE TABLE IF NOT EXISTS Rooms (
                                     id SERIAL PRIMARY KEY,
                                     bedrooms INT,
                                     bathrooms INT,
                                     hasEnsuite BOOLEAN
);

CREATE TABLE IF NOT EXISTS StationDetail (
                                             id SERIAL PRIMARY KEY,
                                             name VARCHAR(255),
                                             stationType_id INT REFERENCES StationType(id),
                                             distanceFromProperty DOUBLE PRECISION
);

CREATE TABLE IF NOT EXISTS Property (
                                        id SERIAL PRIMARY KEY,
                                        location_id INT REFERENCES Location(id),
                                        price INT,
                                        addedOn DATE,
                                        propertyType_id INT REFERENCES PropertyType(id),
                                        tenure_id INT REFERENCES Tenure(id),
                                        rooms_id INT REFERENCES Rooms(id),
                                        size INT NULL, -- Nullable column for size
                                        hasGarden BOOLEAN NULL -- Nullable column for hasGarden
);

CREATE TABLE IF NOT EXISTS PublicTransportDetails (
                                                      id SERIAL PRIMARY KEY,
                                                      property_id INT REFERENCES Property(id),
                                                      stationDetail_id INT REFERENCES StationDetail(id),
                                                      CONSTRAINT property_station_detail_unique UNIQUE (property_id, stationDetail_id)
);