package com.IronTrivia.controllers;

import com.IronTrivia.entities.User;
import com.IronTrivia.services.GameRepository;
import com.IronTrivia.services.ScoreRepository;
import com.IronTrivia.services.UserRepository;
import com.IronTrivia.utils.PasswordStorage;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by alexanderhughes on 3/17/16.
 */
@RestController
public class IronTriviaController {

    @Autowired
    UserRepository users;
    @Autowired
    ScoreRepository scores;
    @Autowired
    GameRepository games;

    Server dbui = null;

    @PostConstruct
    public void init() throws SQLException {
        dbui = Server.createWebServer().start();
    }

    @PreDestroy
    public void destroy() {
        dbui.stop();
    }
    //returns all users
    @RequestMapping(path = "/user", method = RequestMethod.GET)
    public List<User> getUsers() {
        return (List<User>) users.findAll();
    }
    //sorry for long comments, being verbose to help in dev
    //login or create new user; i'll try to explain what's going on in methods so you js guys can follow along a little easier
    @RequestMapping(path = "/user", method = RequestMethod.POST)
    public User addUser(@RequestBody User user, HttpSession session, @PathVariable("createNew") boolean createNew) throws Exception {
        if (!createNew) {//if user does not check the create new user box
            User loginUser = users.findByUserName(user.getUserName());//checking database if loginUser is null throws exception cuz that user doesn't exist
            if (loginUser == null) {
                throw new Exception("user does not exist");//probably better to be less specific when displaying the error to actual users, i.e incorrect username/password
            }
            else if (!PasswordStorage.verifyPassword(user.getPasswordHash(), loginUser.getPasswordHash())) {
                throw new Exception("Incorrect username/password");//checks the password
            }
            session.setAttribute("userName", loginUser.getUserName());
            return loginUser;//just returning the entire user object might change this later, might only need to send up the userName
        }                    //if return type is just username string when yall want the user's info you could just hit the /user/{userName} get route
        else {//check if the username is taken
            if (users.findByUserName(user.getUserName()) != null) {//checks if userName exists in the database, enforcing unique usernames with the logic
                throw new Exception("user already exists");
            }
            return users.save(user);//this method's return type is User so i have to return the new user. might be nice so you can say welcome new user dude
        }
    }
    //put route to update user info, just send
    @RequestMapping(path = "/user", method = RequestMethod.PUT)
    public User editUser(@RequestBody User user) {
        return users.save(user);//sends back the user with updated info
    }

    /*to grab a specific user, just in case yall want it; i could just grab info from the session, leaving it this way
    for now incase yall want to be able to grab some of any users info, if i do it that way this will take no params*/
    @RequestMapping(path = "/user/{userName}", method = RequestMethod.GET)
    public User getUser(@PathVariable("userName") String userName) {
        return users.findByUserName(userName);
    }

    //start of game routes
    //route to grab list of all games
}
