package com.IronTrivia;

import com.IronTrivia.entities.Game;
import com.IronTrivia.entities.Score;
import com.IronTrivia.entities.User;
import com.IronTrivia.services.GameRepository;
import com.IronTrivia.services.ScoreRepository;
import com.IronTrivia.services.UserRepository;
import com.IronTrivia.utils.PasswordStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = IronTriviaApplication.class)
@WebAppConfiguration
@FixMethodOrder(MethodSorters.NAME_ASCENDING)//added a letter in front of each method name to order the way they are ordered
public class IronTriviaApplicationTests {


	@Autowired
	UserRepository users;
	@Autowired
	ScoreRepository scores;
	@Autowired
	GameRepository games;
	@Autowired
	WebApplicationContext wap;

	MockMvc mockMvc;

	@Before
	public void before() {
		//keep this line
		mockMvc = MockMvcBuilders.webAppContextSetup(wap).build();
	}

	@Test
	public void AaddUser() throws Exception {
		User user = new User("bob", "password");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/user")
						.content(json)
						.contentType("application/json")
		);

		Assert.assertTrue(users.count() == 5);// because there are 4 test users populating my database postconstruct
	}
	@Test
	public void Blogin() throws Exception {
		User user = new User("a", "a");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/user/bob")
				.content(json)
				.contentType("application/json")
		);
	}
	@Test
	public void CupdateUser() throws Exception {
		User user = new User("alex", "password");
		user.setId(5);//updates user bobs name
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(user);

		mockMvc.perform(
				MockMvcRequestBuilders.put("/user/5")
				.content(json)
				.contentType("application/json")
		);
		Assert.assertTrue(users.findOne(5).getUserName().equals("alex"));
	}
	@Test
	public void DcreateGame() throws Exception {
		Game game = new Game();
		game.setPlayerNames(new ArrayList<String>());
		game.getPlayerNames().add("a");
		game.getPlayerNames().add("b");
		game.getPlayerNames().add("c");
		game.getPlayerNames().add("d");
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(game);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/game")
				.content(json)
				.contentType("application/json")
				.sessionAttr("userName","a")
		);
		Assert.assertTrue(games.count() == 1);
	}
	@Test//an error is thrown if every user isn't ready, changing the set ready shows this// no longer throw error in code but when i do this test still passes
	public void EjoinGameAllReady() throws Exception {//however the update test method relies on this route working as it
		for (User user : users.findAll()) {//sets isReady in user c to false which is not done in the update route but is needed to pass the assertion
			user.setIsReady(true);
			user.setHasAnswered(true);
			users.save(user);
		}
		mockMvc.perform(
				MockMvcRequestBuilders.post("/game/1")
                .sessionAttr("userName", "c")
		);
	}

	@Test
	public void FexitGame() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders.post("/exit-game")
				.sessionAttr("gameId", 1)
				.sessionAttr("userName", "b")
		);
		Assert.assertTrue(!users.findByUserName("b").getIsReady());
		Assert.assertTrue(!users.findByUserName("b").getHasAnswered());
	}
	@Test
	public void GupdateScore() throws Exception {
		Score score = new Score();
		Boolean isCorrect = true;
		Integer pointValue = 5;
		score.setIsCorrect(isCorrect);
		score.setScore(pointValue);
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(score);

		mockMvc.perform(
				MockMvcRequestBuilders.post("/score")
				.content(json)
				.contentType("application/json")
				.sessionAttr("gameId", 1)
				.sessionAttr("userName", "c")
		);
		Assert.assertTrue(scores.findByUserAndGame(users.findByUserName("c"), games.findOne(1)).getScore() == 5);
		Assert.assertTrue(!users.findByUserName("c").getHasAnswered());
	}
    @Test
    public void HupdateScorePut() throws Exception {
        User user = users.findByUserName("c");
        user.setHasAnswered(true);
        Score score = new Score();
        Boolean isCorrect = true;
        Integer pointValue = 10;
        score.setIsCorrect(isCorrect);
        score.setScore(pointValue);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(score);

        mockMvc.perform(
                MockMvcRequestBuilders.put("/score/0")
                        .content(json)
                        .contentType("application/json")
                        .sessionAttr("gameId", 1)
                        .sessionAttr("userName", "c")
        );
        /*in these tests in the assertions i always grab the objects to be checked directly
        * from the database as they have been updated during these routes*/
        Assert.assertTrue(scores.findByUserAndGame(users.findByUserName("c"), games.findOne(1)).getScore() == 15);
        Assert.assertTrue(!users.findByUserName("c").getHasAnswered());//ask for user from db again as the user has been updated in the route
    }
    @Test
    public void IdeleteGame() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.delete("/game/1")
                        .sessionAttr("userName", "a")
        );

        Assert.assertTrue(games.count() == 0);
        Assert.assertTrue(scores.count() == 0);
        Assert.assertTrue(!users.findByUserName("a").getIsReady());
        Assert.assertTrue(!users.findByUserName("a").getHasAnswered());
    }
}
