package com.IronTrivia.controllers;

import com.IronTrivia.entities.Game;
import com.IronTrivia.entities.Score;
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
import java.util.ArrayList;
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
    public void init() throws SQLException, PasswordStorage.CannotPerformOperationException {
        dbui = Server.createWebServer().start();
        users.save(new User("a", PasswordStorage.createHash("a")));
        users.save(new User("b", PasswordStorage.createHash("b")));
        users.save(new User("c", PasswordStorage.createHash("c")));
        users.save(new User("d", PasswordStorage.createHash("d")));
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

    @RequestMapping(path = "/user/{userName}", method = RequestMethod.POST)
    public User login(HttpSession session, @RequestBody User user) throws Exception {
        String username = user.getUserName();
        User loginUser = users.findByUserName(user.getUserName());//checking database if loginUser is null throws exception cuz that user doesn't exist
        if (loginUser == null) {
            throw new Exception("user does not exist");//probably better to be less specific when displaying the error to actual users, i.e incorrect username/password
        } else if (!PasswordStorage.verifyPassword(user.getPasswordHash(), loginUser.getPasswordHash())) {
            throw new Exception("Incorrect username/password");//checks the password
        }
        session.setAttribute("userName", loginUser.getUserName());
        return loginUser;//just returning the entire user object might change this later, might only need to send up the userName
        //if return type is just username string when yall want the user's info you could just hit the /user/{userName} get route
    }

    //
    //sorry for long comments, being verbose to help in dev
    //login or create new user; i'll try to explain what's going on in methods so you js guys can follow along a little easier
    @RequestMapping(path = "/user", method = RequestMethod.POST)
    public String addUser(@RequestBody User user) throws Exception {
        //check if the username is taken
        if (users.findByUserName(user.getUserName()) != null) {//checks if userName exists in the database, enforcing unique usernames with the logic
            throw new Exception("user already exists");
        }
        user = new User(user.getUserName(), PasswordStorage.createHash(user.getPasswordHash()));
        users.save(user);
        return "success";
    }

    //
    //put route to update user info, just send
    @RequestMapping(path = "/user/{id}", method = RequestMethod.PUT)
    public User editUser(@RequestBody User user) {
        return users.save(user);//sends back the user with updated info
    }

    //
    /*to grab a specific user, just in case yall want it; i could just grab info from the session, leaving it this way
    for now incase yall want to be able to grab some of any users info, if i do it that way this will take no params*/
    @RequestMapping(path = "/user/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable("id") int id) {
        return users.findOne(id);
    }
    //
    //start of game routes
    //this route is void, no return type, just let me know if yall want something returned
    //creates a game in the database
    @RequestMapping(path = "/game", method = RequestMethod.POST)
    public Game createGame(@RequestBody Game game) {
        game = games.save(game);
        String s= null;
        List<String> playerNames = game.getPlayerNames();
        for (String player : playerNames) {
            if (!player.isEmpty()) {
                User user = users.findByUserName(player);//grabbing the player from database
                scores.save(new Score(user, game));//then creating a score for that user connected to that game, this is also the user's link to the game
            }
        }
        return game;
    }
    //
    /*asks for game id creates session for this game for user
    * since this method returns the game a session is created for
    * we probably wont need a separate get method for a single game,
    * this will return the game whenever everyone has answered and or said they
    * are ready, did this in post route since we are posting data to the server*/
    @RequestMapping(path = "/game/{id}", method = RequestMethod.GET)
    public Game getGame(@PathVariable("id") int id) {
        Game game = games.findOne(id);
        return game;
    }
    //
    //this is to join the game checks if everyone else has joined
    @RequestMapping(path = "/game/{id}", method = RequestMethod.POST)
    public boolean joinGame(@PathVariable("id") int id, HttpSession session) throws Exception {
        boolean allReady;
        Game game = games.findOne(id);
        User user = users.findByUserName((String) session.getAttribute("userName"));
        game.setPlayerNames(getPlayers(game));
        for (String player : game.getPlayerNames()) {
            if (!users.findByUserName(player).getIsReady()) {
                allReady = false;
                return allReady;
            }
        }
        session.setAttribute("gameId", id);
        allReady = true;
        return allReady;//if a game object is returned then everyone is ready/has answered
    }
    //
    /*this route removes the game from the session and deletes the game
    * from the database, this route should be run after the game is finished
    * and it will also delete the scores associated with that game
    * also it returns the user that had the highest score*/
    @RequestMapping(path = "/game/{id}", method = RequestMethod.DELETE)
    public Score deleteGame(@PathVariable("id") int id, HttpSession session) {
        User user =  users.findByUserName((String) session.getAttribute("userName"));
        Game game = games.findOne(id);
        Score winningScore = scores.findFirstByGameOrderByScoreDesc(game);//grabs the user with the highest score
        session.removeAttribute("gameId");
        //had to store the winner since i cant grab it after the game is deleted
        games.delete(id);
        user.setHasAnswered(false);
        user.setIsReady(false);
        users.save(user);
        return winningScore;
    }
    //
    //route to grab list of all games
    @RequestMapping(path = "/game", method = RequestMethod.GET)
    public List<Game> getGames(HttpSession session) {
        List<Game> list = (List<Game>) games.findAll();
        return list;
    }
    //
    //may not need this, as the code sets the gameId attribute value every time the route to join a game is hit
    @RequestMapping(path = "/exit-game", method = RequestMethod.POST)
    public void exitGame(HttpSession session) {
        session.removeAttribute("gameId");
        User user = users.findByUserName((String) session.getAttribute("userName"));
        user.setHasAnswered(false);
        user.setIsReady(false);
        users.save(user);
    }
    //
    @RequestMapping(path = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session) {
        session.invalidate();
    }
    //hit this route to check if everyone is ready

    @RequestMapping(path = "/allAnswered", method = RequestMethod.GET)
    public boolean allAnswered(HttpSession session) {
        boolean allAnswered;
        Game game = games.findOne((Integer) session.getAttribute("gameId"));
        User user = users.findByUserName((String) session.getAttribute("userName"));
        game.setPlayerNames(getPlayers(game));
        for (String player : game.getPlayerNames()) {
            if (!users.findByUserName(player).getHasAnswered()) {
                allAnswered = false;
                return allAnswered;
            }
        }
        allAnswered = true;
        return allAnswered;
    }
    //
    /*when the scores are created in the create game method, the score is instantiated at 0
    * this route increments the score by 5 if the user answered correctly*/
    @RequestMapping(path = "/score", method = RequestMethod.PUT)
    public Score updateScore(@RequestBody Score score, HttpSession session) {
        Game game = games.findOne((Integer) session.getAttribute("gameId"));
        User user = users.findByUserName((String) session.getAttribute("userName"));
        game.setPlayerNames(getPlayers(game));
        Score score2 = scores.findByUserAndGame(user, game);
        if (score.getIsCorrect()) {
            score2.addToScore(score.getScore());//adds 5 to the user's score for this game if they had the correct answer
        }
        user.setHasAnswered(false);
        users.save(user);
        scores.save(score2);
        return score;
    }
    //
    /*returns all the scores for a game, could use to display the scores for a game
    *the score objects have a user object in them, so we don't need to request
    *for the list of users separately, they will be included here*/
    @RequestMapping(path = "/score", method = RequestMethod.GET)
    public List<Score> getScores(HttpSession session) {
        Game game = games.findOne((Integer) session.getAttribute("gameId"));
        return scores.findByGame(game);
    }
    //
    /*may not need this route as the /score get route will give the client
    * all of the users' score as well, but here it is*/
    @RequestMapping(path = "/score/{id}", method = RequestMethod.GET)
    public Score getScore(HttpSession session) {
        Game game = games.findOne((Integer) session.getAttribute("gameId"));
        User user = users.findByUserName((String) session.getAttribute("userName"));
        Score score = scores.findByUserAndGame(user, game);
        return score;
    }
    //
    //a method to return the players in the game specified, to avoid some code duplication
    public ArrayList<String> getPlayers(Game game) {
        List<Score> gameScores = scores.findByGame(game);
        game.setPlayerNames(new ArrayList<String>());
        for (Score score : gameScores) {
            game.getPlayerNames().add(score.getUser().getUserName());
        }
        return game.getPlayerNames();
    }
}