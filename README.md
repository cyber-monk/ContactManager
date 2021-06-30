# SmartContactManager
Spring boot application to manage contacts. 
In this application we can make multiple users with unique username. This user can add/update/delete his contacts. With all types of validations.
Security also managed by using spring security. Also only that perticular user can access those contact which added by that same user. Other user can not access those contacts.
Also there is facilty to change the password of user. Also Forget password functionality is there to change the password if any user forgot his password. Forgot password is done with email services which can send random otp to user and if otp validate only then user can set his new password.

###### Used Technologies : 
 - Springboot
 - Hibernate
 - Thymeleaf
 - HTML
 - CSS
 - Bootstrap
 - Material
 - Mysql
 
###### Tools required to run project :
 - Ide - STS / Eclipes / Intellij
 - DB - Mysql   
  
## Installation Guide
  1. Download the project and open with any ide. 
  2. Connect to Internet. Right click on project and update the project.(Wait till all maven dependency load).
  3. Create datebase name as "smart". ( later Tables are automatically added in db) 
  4. Then run project as Springboot application. Thats it!! 
  5. To activate email function for forget password :
     Follow "/src/main/java/com/smart/service/" this path. 
  6. Then add your email ID and password in "EmailService.java" this class file.

## Any query? Contact me.. 
```
akshayvdeshmukh1@gmail.com  
```
