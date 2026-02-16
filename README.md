# starzz-boot

This is a REST API backend created using Java's Spring Boot framework.

### The Dataset

This project uses a database of fictional galaxies, constellations and stars.  

Here is a diagram to describe the tables and their relationships:

![Database schema](assets/schema.png)

Stars are located in constellations, which are in turn located in galaxies.

The `galaxies`, `constellations` and `stars` tables contain the additional
fields `added_by` and `verified_by` to indicate the id of the users who made
the finding and verified it, respectively.

The database was created in MySQL.  The scripts to create the tables and
load the dummy data are included in `assets` for reference.  


### The Application

This project was created in IntelliJ IDEA Ultimate.  It uses Java (OpenJDK), Maven and Spring Boot.

First we create a new Spring Boot project.  Instead of manual setup from [https://start.spring.io],
we use IntelliJ:

![Project Setup 1](assets/project_setup_1.png)

We don't add any dependencies for now; we will add them as we go along.

![Project Setup 2](assets/project_setup_2.png)

After clicking **Create**, IntelliJ generates the starter project files (the project also includes Maven):

![Project Setup 3](assets/project_setup_3.png)

All code committed at each chapter is available with the commit message of the chapter name.

#### Chapter 1: Setting up the routes

Project dependencies added:

    Spring Web
    Lombok

*A Postman collection for all routes is included in* `assets/starzz-boot.postman_collection.json`

One of the auto-generated files in our project is `StarzzBootApplication.java`:

    ... 
    @SpringBootApplication
    public class StarzzBootApplication {
    
        public static void main(String[] args) {
            SpringApplication.run(StarzzBootApplication.class, args);
        }
    
    }

This file is in package `com.sanjayrisbud.starzzboot` and defines the class
`StarzzBootApplication`, which serves as the entrypoint to our application.  It has the
`@SpringBootApplication` annotation, which enables Spring Boot’s auto-configuration
and component scanning features.

To build our web application, we add the **Spring Web** dependency in `pom.xml`. Maven resolves
and downloads it automatically from the configured repositories.    

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

(We remove the version number so Spring Boot can manage versioning for us).

We also add the **Lombok** dependency, for automatic generation of getters, setters,
constructors, etc:

    <dependency>
        <artifactId>lombok</artifactId>
        <groupId>org.projectlombok</groupId>
        <scope>annotationProcessor</scope>
    </dependency>

(We add the scope because Lombok is only needed during compilation and shouldn't exist at runtime)

We define a class to hold plain responses to requests to our API endpoints.  In package
`com.sanjayrisbud.starzzboot` we add a new package, `dtos`.  In this package we create `Message`:

    @AllArgsConstructor
    @Getter
    @ToString
    public class Message {
        private String text;
    }

(We annotate the class with `@AllArgsConstructor`, `@Getter` and `@ToString` so **Lombok** 
will generate a constructor, getter and toString() method for us.)

When an instance of this class is returned as a response to an HTTP request, Spring Boot takes
care of serializing the instance to JSON.  When an instance of the class is expected as a
parameter to a method, Spring Boot takes care of deserializing the supplied JSON argument to the
needed instance.

We now define classes that would handle HTTP requests to our different API endpoints and 
respond accordingly.  In package `com.sanjayrisbud.starzzboot` we add a new package,
`controllers`.  This package will contain the classes that will handle the requests.

A sample class in the `controllers` package is `ConstellationController`.  We annotate the
class with `@RestController`, which marks the class as a controller where every method returns
a domain object instead of an HTML view.  The `@RequestMapping` annotation specifies the prefix
of the endpoints that the class handles, in this case, `/constellations` endpoints:

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

The mapping annotations (`@GetMapping`, `@PostMapping`, `@PutMapping` and `@DeleteMapping`)
specify the HTTP verb (*GET, POST, PUT* or *DELETE*) that the class method handles.  The
remainder of the handled URL, i.e. after the prefix, is passed as an argument to the annotation.

For the class methods that have parameters, the annotation `@PathVariable` indicates that the
argument is from the mapping annotation's argument (inside the curly braces).  The `@RequestBody`
annotation indicates that the argument is from the body of the request.

The other classes in the `controllers` package follow a similar logic.
