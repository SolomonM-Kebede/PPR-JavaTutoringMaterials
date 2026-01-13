# MongoDB Copy Tool

A simple Java command-line tool to copy all collections and documents from one MongoDB database to another.

---

## Features

- Copies **all collections** from a source MongoDB database
- Supports **authenticated MongoDB connections**
- Configurable via a **properties file**
- Copies documents in **batches** for better performance
- Optionally **drops existing collections** in the destination database
- Works as a **standalone executable JAR**

---

## Requirements

- Java **17** or newer
- Maven **3.8+**
- Network access to the MongoDB server(s)

---

## Configuration

Create a properties file (for example: `Prop.properties`).

### Example `Prop.properties`

```properties
# Source database
source.host=localhost
source.port=27017
source.database=source_db
source.username=source_user
source.password=source_password

# Destination database
dest.host=localhost
dest.port=27017
dest.database=dest_db
dest.username=dest_user
dest.password=dest_password

# Copy options
batch.size=1000
drop.existing.collections=false
```
### Build 
````
mvn celan install
````
### Licence 

This project is provided as-is for educational and internal use.