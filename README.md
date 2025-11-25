# Staff Management System

A complete Java application that parses XML staff data, stores it in an embedded Neo4j database, and provides both REST API and web interface for managing staff information.

## Features

- **XML Parsing**: Parse staff data from XML files
- **Neo4j Database**: Store data in embedded Neo4j graph database
- **REST API**: Full CRUD operations via RESTful endpoints
- **Web Interface**: Beautiful FreeMarker-based UI for viewing and managing staff
- **Direct JSON Access**: View data in JSON format directly in the browser

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## Project Structure

```
├── data/
│       │ staff.xml
src/
├── main/
│   ├── java/
│   │   └── org/texttechnology/ppr/Kebede/
│   │       ├── MainApplication.java
│   │       ├── database/
│   │       │   └── Neo4jConnection.java
│   │       ├── handler/
│   │       │   ├── RestHandler.java
│   │       │   └── TemplateHandler.java
│   │       ├── helper/
│   │       │   └── XmlParser.java
│               └── 
│   │       ├── implementation/
│   │       │   ├── SalaryImpl.java
│   │       │   └── StaffImpl.java
│   │       ├── neo4j/
│   │       │   ├── SalaryNeo4jImpl.java
│   │       │   └── StaffNeo4jImpl.java
│   │       └── server/
│   │           └── Main.java
│   └── resources/
│       ├── config.properties
│       └── templates/staff.ftl
│       
```

## Setup Instructions

### 1. Place FreeMarker Templates

Create the following directory structure in `src/main/resources/`:

```
src/main/resources/
├── templates/
│   ├── staff.ftl
│   
└── config.properties
```

### 2. Configure Application

Edit `src/main/resources/config.properties`:

```properties
SourcePath=data/staff.xml
Neo4jPath=data/neo4j-db
ServerPort=7777
```

### 3. Build the Project

```bash
mvn clean package
```

### 4. Run the Application

directly with Maven:

```bash
mvn exec:java -Dexec.mainClass="org.texttechnology.ppr.tutor.Kebede.Main"
```

## Accessing the Application

Once started, the application will be available at:

### Web Interface
- **Home Page**: http://localhost:7777/staff
- **search Staff**: http://localhost:7777/staff/id

### REST API Endpoints

#### Get All Staff
```bash
GET http://localhost:7777/api/staff
```

#### Get Staff by ID
```bash
GET http://localhost:7777/api/staff/1001
```

## Viewing Data in Browser

### JSON Format
Simply navigate to any API endpoint in your browser to view the data in JSON format:

- All staff: http://localhost:7777/api/staff
- Specific staff: http://localhost:7777/api/staff/1001


### HTML Format
- View all staff in a beautiful UI: http://localhost:7777
- View individual staff details: http://localhost:7777/staff/1001




## Reloading Data from XML

If you want to reload data from XML:

1. Stop the application
2. Delete the Neo4j database directory (specified in `Neo4jPath`)
3. Restart the application

The application will automatically parse the XML and load fresh data.

## Technologies Used

- **Neo4j 5.14.0**: Graph database for storing staff data
- **Javalin 5.6.3**: Lightweight web framework
- **FreeMarker 2.3.32**: Template engine for HTML views
- **SLF4J**: Logging framework
- **JSON**: Data serialization

## Troubleshooting

### Port Already in Use
If port 7777 is already in use, change the `ServerPort` in `config.properties`.

### Database Lock Error
If you see database lock errors, ensure no other instance of the application is running.

### XML File Not Found
Ensure `staff.xml` is placed in `data/` directory.

## License

This project is for educational purposes. © Solomon Mengesha kebede
xml file source google and expanded for this purpose. 
