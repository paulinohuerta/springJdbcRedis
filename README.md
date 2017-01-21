# springJdbcRedis

The [book](https://www.amazon.com/Getting-started-Spring-Framework-Third-ebook/dp/B01HZXQFUS) has [this Git repository](https://github.com/getting-started-with-spring/3rdEdition) containing the source code of the examples.     
It's a good book explaining how Spring works. The author uses ample examples to build good understanding of the framework.

_**In this project we try to extend the example of chapter 12:**_ _ch12-bankapp_

## Web Project Spring integrating MySQL and Redis

To build and testing    


    mvn clean jetty:run      
    
    
This command _*init*_ the jetty web server, and then through the url **http://localhost:9999/fixedDeposit/list** you can to access to the application

It is necessary to create the database in the MySQL server and create a pair of tables, for this, you can use the script      
_**chapter 8/ch08-bankapp-jdbc/sql/**_ of the book code.    

While for _Redis_, you only need to have the server started.     

### Details about data persistence used

#### Using MySQL

Data for bank accounts

#### Using Redis

Email address of bank account owner



