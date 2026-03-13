# starzz-boot

This is a REST API backend created using Java's Spring Boot framework.  It demonstrates a full-stack backend design using MVC architecture, layered services, DTO mapping, validation, exception handling, and database integration with MySQL.

This project serves two purposes:

**Portfolio showcase**: A clear example of building a maintainable, production-ready Spring Boot API.

**Learning resource**: A guided walkthrough for developers familiar with Spring Boot, showing why each layer exists and how the components interact.

The API manages a database of fictional galaxies, constellations, and stars. You’ll see how entities, DTOs, mappers, services, and controllers work together to process requests and return structured responses.
Mermaid diagrams illustrate request flows, allowing readers to quickly grasp the architecture while detailed explanations provide deeper insight.

## The Dataset

Here is a diagram to describe the tables and their relationships:

![Database schema](assets/schema.png)

Stars are located in constellations, which are in turn located in galaxies.

The `galaxies`, `constellations` and `stars` tables contain the additional
fields `added_by` and `verified_by` to indicate the id of the users who made
the finding and verified it, respectively.

The database was created in MySQL.  The scripts to create the tables and
load the dummy data are included in `assets` for reference.  

## The Application

This project was created in IntelliJ IDEA Ultimate.  It uses Java, Spring Boot, Maven and MySQL.

![Java](https://img.shields.io/badge/java-17-blue)
![Spring Boot](https://img.shields.io/badge/springboot-4.0.2-brightgreen)
![Maven](https://img.shields.io/badge/maven-3.3.4-orange)
![MySQL](https://img.shields.io/badge/mysql-9.6-purple)

First we create a new Spring Boot project.  Instead of manual setup from [https://start.spring.io],
we use IntelliJ:

![Project Setup 1](assets/project_setup_1.png)

We don't add any dependencies for now; we will add them as we go along.

![Project Setup 2](assets/project_setup_2.png)

After clicking **Create**, IntelliJ generates the starter project files (the project also includes Maven):

![Project Setup 3](assets/project_setup_3.png)

All code committed at each chapter is available with the commit message of the chapter name.

### Chapter 1: Setting up the routes

Project dependencies added:

    Spring Web
    Lombok

#### Overview

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant DispatcherServlet
    participant Controller

    User ->> DispatcherServlet: HTTP Request
    DispatcherServlet ->> Controller: HTTP Request
    Controller -->> DispatcherServlet: 200 OK
    DispatcherServlet -->> User: HTTP Response
 ```

#### Endpoints

| Endpoint                | Method | Description                                              | Response |
|-------------------------|--------|----------------------------------------------------------|----------|
| `/constellations`       | GET    | Returns *Successfully called getConstellationList()*     | 200 OK   |
| `/constellations/{id}`  | GET    | Returns *Successfully called getConstellation(id)*       | 200 OK   |
| `/constellations`       | POST   | Returns *Successfully called registerConstellation()*    | 200 OK   |
| `/constellations/{id}`  | PUT    | Returns *Successfully called updateConstellation(id)*    | 200 OK   |
| `/constellations/{id}`  | DELETE | Returns *Successfully called deleteConstellation(id)*    | 200 OK   |
| `/galaxies`             | GET    | Returns *Successfully called getGalaxyList()*            | 200 OK   |
| `/galaxies/{id}`        | GET    | Returns *Successfully called getGalaxy(id)*              | 200 OK   |
| `/galaxies`             | POST   | Returns *Successfully called registerGalaxy()*           | 200 OK   |
| `/galaxies/{id}`        | PUT    | Returns *Successfully called updateGalaxy(id)*           | 200 OK   |
| `/galaxies/{id}`        | DELETE | Returns *Successfully called deleteGalaxy(id)*           | 200 OK   |
| `/stars`                | GET    | Returns *Successfully called getStarList()*              | 200 OK   |
| `/stars/{id}`           | GET    | Returns *Successfully called getStar(id)*                | 200 OK   |
| `/stars`                | POST   | Returns *Successfully called registerStar()*             | 200 OK   |
| `/stars/{id}`           | PUT    | Returns *Successfully called updateStar(id)*             | 200 OK   |
| `/stars/{id}`           | DELETE | Returns *Successfully called deleteStar(id)*             | 200 OK   |
| `/users`                | GET    | Returns *Successfully called getUserList()*              | 200 OK   |
| `/users/{id}`           | GET    | Returns *Successfully called getUser(id)*                | 200 OK   |
| `/users`                | POST   | Returns *Successfully called registerUser()*             | 200 OK   |
| `/users/{id}`           | PUT    | Returns *Successfully called updateUser(id)*             | 200 OK   |
| `/users/{id}`           | DELETE | Returns *Successfully called deleteUser(id)*             | 200 OK   |

*A Postman collection for all routes is included in* `assets/starzz-boot.postman_collection.json`

<details>

<summary>Chapter Walkthrough</summary>

A Spring Boot application follows the MVC pattern for web applications.  So to build our application,
we add the **Spring Web** dependency in `pom.xml`. When we add a dependency in `pom.xml`, Maven
resolves and downloads it automatically from the configured repositories.

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

(We remove the version number so Spring Boot can manage versioning for us).

We also add **Lombok** for automatic generation of getters, setters, constructors, etc.:

    <dependency>
        <artifactId>lombok</artifactId>
        <groupId>org.projectlombok</groupId>
        <scope>annotationProcessor</scope>
    </dependency>

(We add the scope because Lombok is only needed during compilation and shouldn't exist at runtime)

For now, we will be referring to the `src/main/java` folder of our project.  We will talk about
`src/test/java` later.

One of the auto-generated files in our project is `StarzzBootApplication.java` in package
`com.sanjayrisbud.starzzboot`:

    ... 
    @SpringBootApplication
    public class StarzzBootApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(StarzzBootApplication.class, args);
        }
    
    }

This file defines the class `StarzzBootApplication`, which serves as the entrypoint to our
application.  The `@SpringBootApplication` annotation enables component scanning, which instructs
Spring to discover and register application components as **beans** within the application context.

In Spring, a bean is an object whose lifecycle is managed by the Spring container. Classes
annotated with `@RestController`, `@Service`, or `@Component` are automatically detected as
beans and can be injected where needed.

We now define our application's endpoints.  First we define a class to hold plain responses to
requests to our API endpoints.  In package `com.sanjayrisbud.starzzboot` we add a new package,
`dtos`.  In this package we create a class `Message`:

    @AllArgsConstructor
    @Getter
    @ToString
    public class Message {
        private String text;
    }

We annotate the class with `@AllArgsConstructor`, `@Getter` and `@ToString` so Lombok will
generate a constructor, getter and toString() method for us.

When an instance of this class is returned as a response to an HTTP request, Spring Boot takes
care of serializing the instance to JSON.  When an instance of the class is expected as a
parameter to a method, Spring Boot takes care of deserializing the supplied JSON argument to the
needed instance.

We now define classes that would handle HTTP requests to our different API endpoints and
respond accordingly.  In package `com.sanjayrisbud.starzzboot` we add a new package,
`controllers`.  This package will contain the classes that will handle the requests.

A sample class in the `controllers` package is `ConstellationController`:

    ...
    @RestController
    @RequestMapping("/constellations")
    public class ConstellationController {
        @GetMapping
        public Message getConstellationList() {
            return new Message("Successfully called getConstellationList()");
        }
    
        @GetMapping("/{id}")
        public Message getConstellation(@PathVariable Long id) {
            return new Message("Successfully called getConstellation(" + id + ")");
        }
    
        @PostMapping
        public Message registerConstellation(@RequestBody Message request) {
            return new Message("Successfully called registerConstellation(" + request + ")");
        }
    ...

We annotate the class with `@RestController`, which marks the class as a controller where every
method returns a domain object instead of an HTML view.  The `@RequestMapping` annotation
specifies the prefix of the endpoints that the class handles, in this case, `/constellations`.

The mapping annotations (`@GetMapping`, `@PostMapping`, `@PutMapping` and `@DeleteMapping`)
specify the HTTP verb (*GET, POST, PUT* or *DELETE*) that the class method handles.  The
remainder of the handled URL, i.e. after the prefix, is passed as an argument to the annotation.

For the class methods that have parameters, the annotation `@PathVariable` indicates that the
argument is from the mapping annotation's argument (inside the curly braces).  The `@RequestBody`
annotation indicates that the argument is from the body of the request.

The other classes in the `controllers` package follow a similar logic.

So far, our controllers return hardcoded responses. While this is useful to verify that our
routing layer works correctly, real-world applications persist and retrieve data from a database.

In the next chapter, we introduce the persistence layer using Spring Data JPA and connect our
application to a MySQL database.

</details>

### Chapter 2: Setting up the database

Project dependencies added:

    Spring Data JPA
    MySQL Driver
    Jakarta Bean Validation

#### Overview

##### HTTP GET

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant DispatcherServlet
    participant ExceptionHandler
    participant Controller
    participant Service
    participant Repository
    participant Database

    User ->> DispatcherServlet: HTTP GET Request
    DispatcherServlet ->> Controller: GET handler
    Controller ->> Service: fetch data
    Service ->> Repository: fetch data
    Repository ->> Database: SQL query
    Database -->> Repository: results
    Repository -->> Service: entity
    Service -->> Controller: DTO
    Controller -->> DispatcherServlet: ResponseEntity<DTO>
    DispatcherServlet -->> User: HTTP Response
```

##### HTTP POST/PUT

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant DispatcherServlet
    participant ExceptionHandler
    participant Controller
    participant Service
    participant Repository
    participant Database

    User ->> DispatcherServlet: HTTP POST | HTTP PUT Request
    DispatcherServlet ->> Controller: POST/PUT handler
    alt validation failed
        Controller -->> ExceptionHandler: validation exception
        ExceptionHandler -->> DispatcherServlet: HTTP 400
    end
    Controller ->> Service: process request
    Service ->> Repository: fetch related entities
    Repository ->> Database: SQL query
    Database -->> Repository: results
    Repository -->> Service: entity
    alt not found
        Service -->> ExceptionHandler: not found exception
        ExceptionHandler -->> DispatcherServlet: HTTP 404
    end
    Service ->> Repository: save and fetch data
    Repository ->> Database: SQL query
    Database -->> Repository: results
    Repository -->> Service: entity
    Service -->> Controller: DTO
    Controller -->> DispatcherServlet: ResponseEntity<DTO>
    DispatcherServlet -->> User: HTTP Response
```

##### HTTP DELETE

```mermaid
sequenceDiagram
    autonumber

    actor User
    participant DispatcherServlet
    participant ExceptionHandler
    participant Controller
    participant Service
    participant Repository
    participant Database

    User ->> DispatcherServlet: HTTP DELETE Request
    DispatcherServlet ->> Controller: DELETE handler
    Controller ->> Service: process request
    Service ->> Repository: fetch entity
    Repository ->> Database: SQL query
    Database -->> Repository: results
    Repository -->> Service: entity
    alt not found
        Service -->> ExceptionHandler: not found exception
        ExceptionHandler -->> DispatcherServlet: HTTP 404
    end
    Service ->> Repository: delete data
    Repository ->> Database: SQL query
    Database -->> Repository: null
    Repository -->> Service: confirmation
    Service -->> Controller: Void
    Controller -->> DispatcherServlet: ResponseEntity<Void>
    DispatcherServlet -->> User: HTTP Response
```

#### Updated Endpoints

| Endpoint                | Method | Description                                              | Response       |
|-------------------------|--------|----------------------------------------------------------|----------------|
| `/constellations`       | GET    | Returns the list of constellations                       | 200 OK         |
| `/constellations/{id}`  | GET    | Returns the constellation with the ID of *id*            | 200 OK         |
| `/constellations`       | POST   | Creates a new constellation record                       | 201 Created    |
| `/constellations/{id}`  | PUT    | Updates the constellation with the ID of *id*            | 200 OK         |
| `/constellations/{id}`  | DELETE | Deletes the constellation with the ID of *id*            | 204 No Content |
| `/galaxies`             | GET    | Returns the list of galaxies                             | 200 OK         |
| `/galaxies/{id}`        | GET    | Returns the galaxy with the ID of *id*                   | 200 OK         |
| `/galaxies`             | POST   | Creates a new galaxy record                              | 201 Created    |
| `/galaxies/{id}`        | PUT    | Updates the galaxy with the ID of *id*                   | 200 OK         |
| `/galaxies/{id}`        | DELETE | Deletes the galaxy with the ID of *id*                   | 204 No Content |
| `/stars`                | GET    | Returns the list of stars                                | 200 OK         |
| `/stars/{id}`           | GET    | Returns the star with the ID of *id*                     | 200 OK         |
| `/stars`                | POST   | Creates a new star record                                | 201 Created    |
| `/stars/{id}`           | PUT    | Updates the star with the ID of *id*                     | 200 OK         |
| `/stars/{id}`           | DELETE | Deletes the star with the ID of *id*                     | 204 No Content |
| `/users`                | GET    | Returns the list of users                                | 200 OK         |
| `/users/{id}`           | GET    | Returns the user with the ID of *id*                     | 200 OK         |
| `/users`                | POST   | Creates a new user record                                | 201 Created    |
| `/users/{id}`           | PUT    | Updates the user with the ID of *id*                     | 200 OK         |

*The Postman collection in* `assets/starzz-boot.postman_collection.json` *has been updated to the
format of requests used in this chapter.*

*A Note on User Deletion* - The application **does not** provide a *DELETE* endpoint for users.
This is a deliberate design decision:

- Users are referenced by galaxies, constellations, and stars (`addedBy` and `verifiedBy`).
- Cascading delete through these relationships would risk data loss and long transactional operations.
- The existing database schema cannot be modified to add a soft-delete flag (`isActive`), so soft deletion is not possible.
- Therefore, user deletion is intentionally disabled to preserve historical data integrity.

<details>

<summary>Chapter Walkthrough</summary>

#### The Persistence Layer

To persist and retrieve data, we now introduce a persistence layer. In Spring Boot applications,
this is typically achieved using Spring Data JPA on top of a relational database.  In our case,
it is our **starzz** database on MySQL.

To allow our application to interact with the database, we would first need to add the
**MySQL driver** to allow our code to execute SQL via JDBC (JDBC is a low-level API
for accessing databases):

        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>

(We add the scope because the driver is only needed at runtime and not needed to compile the code)

The MySQL driver enables JDBC connectivity. We also add **Spring Data JPA** to provide
higher-level, object-oriented database interaction.  (We use Spring Data JPA to
interact with the database in an object-oriented way. Hibernate, the default JPA
implementation, handles the actual SQL generation and persistence management.)

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

Since data that we add to the database comes from user input in the requests, we would
need to validate them before modifying the database.  We add **Jakarta Bean Validation** to
implement request validation:

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

We add the snippets above to `pom.xml`.

With dependencies added, we now configure our data source so Spring Boot can connect to the MySQL
database.  In `src/main/resources/application.yaml` we add the application's data source:

    datasource:
        url: jdbc:mysql://localhost:3309/starzz
        username: root
        password: root
    jpa:
        show-sql: true

(`username` and `password` are the credentials to our database server, accessible via `url`.
We also set `show-sql` under `jpa` to `true` so the application will log the SQL that
Hibernate generates.)

We now create our code to interact with our database.

Since Spring Data JPA allows us to work with database tables in an object-oriented way, we can
add classes to abstract tables and the operations on them.  In `com.sanjayrisbud.starzzboot`,
we add a new package, `models`, to contain the table abstractions.  An example class is
`Constellation`:

    ...
    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    @Table(name = "constellations")
    public class Constellation {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "constellation_id")
        private Integer id;
    
        @Column(name = "constellation_name")
        private String name;
    ...

The `@Entity` annotation specifies that the class is an abstraction of a database table, and
`@Table` specifies the actual table name.  Each instance of this class represents one record
in the table.  `@AllArgsConstructor`, `@NoArgsConstructor`, `@Getter` and `@Setter` are for
Lombok to generate a constructor requiring all fields, a default constructor, getters and setters
for all fields, respectively (these Lombok-generated methods are needed by Spring Data JPA).
`@Builder` (also from Lombok) allows us to create an entity instance using the **Builder**
pattern i.e. one field at a time.  This improves readability by showing which values are
assigned to which fields.

We then have fields with `@Column` corresponding to the table columns.  `@Id` indicates the
primary key and `@GeneratedValue` indicates that the value is generated by the table.

We then define relationships with other entities.  Schema-wise, we have a many-to-one relationship
between `constellations` and `galaxies`; the foreign key is `galaxy_id`  We express that relationship
using:

    ...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "galaxy_id")
    private Galaxy galaxy;
    ...

Our entity for the `galaxies` table is `Galaxy`; we define a field `galaxy` to refer to it.  Since
this field uses a foreign key to refer to the parent `Galaxy` we annotate the field with
`@JoinColumn` to specify the foreign key.  `@ManyToOne` specifies the relationship.  For these
types of relationships, the default behavior of Spring Data JPA when it loads a particular entity
into memory is to automatically fetch the related entity.  This is called eager loading.  In our
case, we indicate `fetch` to override this default and tell Spring Data JPA to fetch the related
entity only when we explicitly ask for it, which is called lazy loading.

In addition, this field has a symmetric field in the entity `Galaxy`:

    @OneToMany(targetEntity = Constellation.class, mappedBy = "galaxy", cascade = CascadeType.REMOVE)
    private Set<Constellation> constellations = new HashSet<>();

This field does not correspond to a physical column in the `galaxies` table; it represents the
inverse side of the relationship.  It is a field for the set of its children `Constellation`
objects.  `@OneToMany` specifies  the relationship, `targetEntity` indicates the related entity,
and `mappedBy` indicates the symmetric field.  `cascade` specifies what operations on the parent
cascade to the children entities; in this case, deleting a galaxy also deletes its children
constellations.

Two other fields in `Constellation` are defined similarly:

    ...
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by")
    private User addedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by")
    private User verifiedBy;
    ...

The symmetric fields in the `User` entity corresponding to the table `users`:

    @OneToMany(targetEntity = Constellation.class, mappedBy = "addedBy")
    private Set<Constellation> constellationsAdded = new HashSet<>();

    @OneToMany(targetEntity = Constellation.class, mappedBy = "verifiedBy")
    private Set<Constellation> constellationsVerified = new HashSet<>();

The other classes in the `models` package follow a similar logic.

Now that we have defined our entities as object-oriented representations of database tables,
we introduce the repository layer.  While entities abstract the structure of our tables,
repositories abstract the operations performed on those entities. They provide a clean interface
for querying, saving, updating, and deleting data without requiring us to write SQL manually.

Our repositories extend the interface `JpaRepository`. Spring Data JPA automatically generates
implementations of these interfaces at runtime and registers them as beans in the application
context. This allows the repositories to be injected into services or controllers where needed.

In `com.sanjayrisbud.starzzboot`, we add a new package, `repositories`, to contain the repositories.
An example interface is`ConstellationRepository`:

    ...
    public interface ConstellationRepository extends JpaRepository<Constellation, Integer> {
    }

The other interfaces in the `repositories` package follow a similar logic.

#### API Representation Layer

Whenever we send data as part of responses, we usually don't send raw entities from our database.
We create mappings from our entities to custom objects, and send those objects instead.  These
custom objects are known as data transfer objects.  We need to create DTOs for our entities.
In `com.sanjayrisbud.starzzboot.dtos`, we add our DTOs.  An example class is `ConstellationSummaryDto`:

    ...
    public record ConstellationSummaryDto (
        Integer constellationId,
        String constellationName
    ) {}

Since we only need fields for the constellation's id and name, we define this class as a
`record` instead.  This guarantees immutability and clearly signifies its intent as a
data carrier.

Another example is `ConstellationDetailsDto`:

    ...
    @Builder
    @Data
    @JsonPropertyOrder({
        "constellationId",
        "constellationName",
        "galaxy",
        "addedBy",
        "verifiedBy"
    })
    public class ConstellationDetailsDto {
        private Integer constellationId;
        private String constellationName;
        private GalaxySummaryDto galaxy;
        private UserSummaryDto addedBy;
        private UserSummaryDto verifiedBy;
    }

This DTO has additional fields to display the parent galaxy, and the adder and verifier.  These
fields are themselves DTOs.  `@JsonPropertyOrder` specifies the order of the fields in the DTO
serialization.  `@Data` tells Lombok to generate boilerplate code such as getters, setters, equals,
hashCode, and toString.  Although the class only carries data, we define it as a regular class instead
of a record because we construct instances using the builder pattern, which allows fields to be set
more flexibly.  We specify this using `@Builder`.

We also have a DTO to accept JSON input when processing requests to add or update constellations:

    @Data
    public class ConstellationDto {
        @NotBlank private String constellationName;
        @NotNull private Integer galaxyId;
        @NotNull private Integer adderId;
        private Integer verifierId;
    }

We add validation annotations to the fields.  `@NotNull` specifies that the field must be present
and cannot be set to `null`.  `@NotBlank` specifies that the field must be present and cannot be
`null`, blank, or an empty string.  When all the validations pass, the JSON is deserialized to
a DTO instance.

The other classes in the `dtos` package follow a similar logic.

We would then need classes to map entities to DTOs.  The **MapStruct** dependency can help
generate mappings for us, but for the purposes of this application we will generate them manually.
In `com.sanjayrisbud.starzzboot`, we add a new package, `mappers`, to contain the classes for
our entity mappers.  An example class is `ConstellationMapper`:

    ...
    @AllArgsConstructor
    @Component
    public class ConstellationMapper {
        private final GalaxyMapper galaxyMapper;
        private final UserMapper userMapper;
    
        public ConstellationSummaryDto toSummaryDto(Constellation constellation) {
            if (constellation == null)
                return null;
    
            return new ConstellationSummaryDto(constellation.getId(), constellation.getName());
        }

        public ConstellationDetailsDto toDetailsDto(Constellation constellation) {
            if (constellation == null)
                return null;
    
            var galaxy = galaxyMapper.toSummaryDto(constellation.getGalaxy());
            var addedBy = userMapper.toSummaryDto(constellation.getAddedBy());
            var verifiedBy = userMapper.toSummaryDto(constellation.getVerifiedBy());
    
            return ConstellationDetailsDto.builder()
                    .constellationId(constellation.getId())
                    .constellationName(constellation.getName())
                    .galaxy(galaxy)
                    .addedBy(addedBy)
                    .verifiedBy(verifiedBy)
                    .build();
        }
    }

`@Component` tags this class as a bean.  In `toSummaryDto()` we create a summary DTO using a regular
constructor.  In `toDetailsDto()` we first get summary DTOs of the related entities, then use the
builder pattern to create the details DTO one field at a time.

The other classes in the `mappers` package follow a similar logic.

Whenever our application processes requests, errors may occur, e.g. a request for a nonexistent
constellation.  Instead of letting the application fail silently, we throw exceptions to convey
information about these events.  We do this with exception handling.  In package
`com.sanjayrisbud.starzzboot.dtos` we add a new class, `ErrorResponseDto`, to contain the
error message:

    ...
    public record ErrorResponseDto(
        String message,
        LocalDateTime timestamp
    ) {}

It has fields for the error message and the error's timestamp.

In `com.sanjayrisbud.starzzboot`, we add a new package, `exceptions`, to contain the classes
for our exception handling.  We create class `ResourceNotFoundException`:

    ...
    @Getter
    public class ResourceNotFoundException extends RuntimeException {
    
        private final String resourceName;
        private final Integer resourceId;
    
        public ResourceNotFoundException(String resourceName, Integer resourceId) {
            super(resourceName + " with id " + resourceId + " not found.");
            this.resourceName = resourceName;
            this.resourceId = resourceId;
        }
    
    }

This will be the custom exception thrown whenever our application searches for nonexistent
resources.

In the same package, we create class `GlobalExceptionHandler`:

    ...
    @RestControllerAdvice
    public class GlobalExceptionHandler {
    
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponseDto> handleNotFound(ResourceNotFoundException ex) {
    
            var errorResponse = new ErrorResponseDto(ex.getMessage(), LocalDateTime.now());
    
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
    ...

The `@RestControllerAdvice` annotation makes this class handle all our custom exceptions.
`@ExceptionHandler` specifies the method to handle specific exception classes.  In this case,
`handleNotFound()` is the handler for `ResourceNotFoundException`.  There are also handlers
for validation exceptions thrown by Spring Boot.

We now introduce a service layer to contain our application's business logic.  Introducing a
service layer ensures a clean separation of concerns; controllers can focus solely on HTTP
request handling and not need to worry about the business rules.

In `com.sanjayrisbud.starzzboot`, we add a new package, `services`, to contain the classes for our
service layer.  An example class is `ConstellationService`:

    ...
    @AllArgsConstructor
    @Service
    public class ConstellationService {
        private final ConstellationRepository constellationRepository;
        private final ConstellationMapper constellationMapper;
        private final GalaxyService galaxyService;
        private final UserService userService;
        
        public List<ConstellationSummaryDto> getConstellationList() {
            return constellationRepository.findAll().stream()
                    .map(constellationMapper::toSummaryDto)
                    .toList();
        }
    
        public ConstellationDetailsDto getConstellation(Integer id) {
            return constellationMapper.toDetailsDto(findById(id));
        }
    
        public ConstellationDetailsDto registerConstellation(ConstellationDto request) {
            var constellation = Constellation.builder()
                    .name(request.getConstellationName())
                    .galaxy(galaxyService.getEntity(request.getGalaxyId(), null))
                    .addedBy(userService.getEntity(request.getAdderId(), null))
                    .verifiedBy(userService.getEntity(request.getVerifierId(),null))
                    .build();
    
            constellationRepository.save(constellation);
            return constellationMapper.toDetailsDto(constellation);
        }
    ...

`@Service` specifies that this class is a bean.  It has private fields for beans, which were
automatically injected by Spring.  We then define methods to implement CRUD operations.

The other classes in the `services` package follow a similar logic.

Finally, we rewrite our `ConstellationController`:

    ...
    @AllArgsConstructor
    @RestController
    @RequestMapping("/constellations")
    public class ConstellationController {
    
        private final ConstellationService constellationService;
    
        @GetMapping
        public List<ConstellationSummaryDto> getConstellationList() {
            return constellationService.getConstellationList();
        }
    
        @GetMapping("/{id}")
        public ResponseEntity<ConstellationDetailsDto> getConstellation(@PathVariable Integer id) {
            return ResponseEntity.ok(constellationService.getConstellation(id));
        }
    
        @PostMapping
        public ResponseEntity<ConstellationDetailsDto> registerConstellation(
                @Valid @RequestBody ConstellationDto request,
                UriComponentsBuilder uriComponentsBuilder) {
            var newConstellation = constellationService.registerConstellation(request);
            var uri = uriComponentsBuilder.path("/constellations/{id}")
                    .buildAndExpand(newConstellation.getConstellationId()).toUri();
            return ResponseEntity.created(uri).body(newConstellation);
        }
    ...

We introduce a private field for the service bean.  When the endpoint handlers receive requests,
they now redirect those requests to different methods in the service bean, and then generate
appropriate responses based on the results of those methods.  Instead of returning DTOs directly,
the controller methods now return a `ResponseEntity<T>`, a generic wrapper that allows us to
specify the HTTP status code and response body.

The other classes in the `controllers` package follow a similar logic.

Now that we’ve finished setting up the database and our controllers, our app is starting to grow.
With more pieces working together, it’s easy to accidentally break something when we add new
features. That’s why it’s a good idea to start writing **unit tests** now — they help us make
sure each part works on its own and keep everything running smoothly as the project grows.

In the next chapter, we’ll dive into **unit testing** using **JUnit 5** and **Mockito**.
We’ll cover how to test our services and controllers, mock dependencies, and build a solid
foundation so future updates won’t surprise us.

</details>

### Chapter 3: Setting up the unit tests

Project dependencies added:

    None

<details>

<summary>Chapter Walkthrough</summary>

To test our application, we will use **JUnit 5** and **Mockito**. JUnit 5 provides the framework
to write and run unit tests, while Mockito allows us to create mocks of our dependencies so we
can test each layer in isolation. To use them in our project, we need the `spring-boot-starter-test`
dependency.  However, we don’t need to manually add this dependency to our `pom.xml`.  When we
created the project, the dependency had already been added to `pom.xml`:

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>

(The scope specifies that the dependency is only needed during testing and won't be included when
the application package is built.)

Up until now, we have been writing all our source code in the `src/main/java` folder of our project:

![Project Structure 1](assets/project_structure_1.png)

We will be writing our tests in the `src/test/java` folder of our project, also added upon project
creation:

![Project Structure 2](assets/project_structure_2.png)

This folder will have a similar package structure to `src/main/java`.  The testing class' name is
the same as the name of the class being tested, with the suffix `Test`.

We start with writing unit tests for our services.  In package `com.sanjayrisbud.starzzboot.services`
we create class `ConstellationServiceTest` to contain our unit tests for `ConstellationService`:

    ...
    @ExtendWith(MockitoExtension.class)
    class ConstellationServiceTest {
        @Mock
        private ConstellationRepository constellationRepository;
        @Mock
        private GalaxyService galaxyService;
        @Mock
        private UserService userService;
        @InjectMocks
        private ConstellationService constellationService;
    
        @Test
        void getEntityGivenNewIdIsNullReturnsNull() {
            Constellation c1 = constellationService.getEntity(null, null);
            assertNull(c1);
    
            Constellation c2 = constellationService.getEntity(null,
                    Constellation.builder().build());
            assertNull(c2);
        }
    
        @Test
        void getEntityGivenNullCurrentConstellationReturnsConstellationFromRepository() {
            Constellation existingConstellation = Constellation.builder().build();
            when(constellationRepository.findById(1))
                    .thenReturn(Optional.of(existingConstellation));
    
            Constellation c = constellationService.getEntity(1,null);
    
            assertEquals(existingConstellation, c);
        }
    ...

To test `ConstellationService`, we create an instance of it.  Since it depends on other classes,
we would also need instances of those dependencies.

In unit tests, we mock dependencies so the class being tested runs in isolation.  Instead of
calling real implementations like repositories or other services, mocks return predefined
results.  This keeps tests fast, predictable, and focused only on the logic of the unit under
test rather than external systems like databases.

We use `@Mock` to create mock instances of `ConstellationRepository`, `GalaxyService` and
`UserService`.  We then use `@InjectMocks` to inject these dependencies into our
`ConstellationService` instance.  Finally, `@ExtendWith(MockitoExtension.class)` enables
Mockito's functionality.

`@Test` specifies that the method is a test.  Both methods above are tests for `getEntity()` in
`ConstellationService`.  There are actually 5 tests for `getEntity()` alone.  Multiple tests for
the same method are beneficial.  This ensures that the method is thoroughly tested.

It is necessary to ensure that private methods behave correctly. In practice, however, private
methods are not tested directly.  Instead, they are exercised through the public methods that
use them. This allows tests to verify behavior without exposing internal implementation details.

Notice also the `assertNull()` and `when()` methods.  They are static methods defined in the
classes *Assertions* and *Mockito*, respectively.  For common testing helpers, we can statically
import them:

    import static org.junit.jupiter.api.Assertions.*;
    import static org.mockito.Mockito.*;

This allows us to call the static methods without having to prefix them with the class name.

The other classes in the `services` package follow a similar logic.

</details>
