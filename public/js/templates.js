var _ = require('underscore');
module.exports = {
  header:[
    '<div class="container">',
      '<span><%= username %></span>',
      '<button>logout</button>',
    '</div>'
  ].join(""),
  question: [
      '<div class="col-sm-12">',
        '<h1><%=question%></h1>',
        '<div class="q-details-wrap">',
          '<p class="q-cat">category: <%=category.title%></p>',
          '<p class="q-val">points: <%=value%></p>',
        '</div>',
      '</div>',
  ].join(''),
  loginForm: [
    '<div class="col-md-6 col-md-offset-3">',
      '<form class="login form-group">',
        '<input class="col-md-12 col-sm-12 col-xs-12" name="username" type="text" placeholder="User Name">',
        '<input class="col-md-12 col-sm-12 col-xs-12" name="password" type="password" placeholder="Password">',
        '<button class="login-button" name="login">Login</button>',
      '</form>',
    '</div>'
  ].join(''),
  userTemp:[
    "<div>",
    "<%= username =%>",
    "<%= score =%>",
    "</div>"
  ].join(''),
  addForm: [
    '<div class="col-md-6 col-md-offset-3">',
      '<button name="newUser">Sign Up</button>',
      '<form class="addUser form-group hide">',
        '<input class="col-md-12 col-sm-12 col-xs-12" name="username" type="text" placeholder="User Name">',
        '<input class="col-md-12 col-sm-12 col-xs-12" name="password" type="password" placeholder="Password">',
        '<input class="col-md-12 col-sm-12 col-xs-12" name="passwordConfirm" type="password" placeholder="Confirm Password">',
        '<button name="addUser">Add User</button>',
      '</form>',
    '</div>'
  ].join(''),
  login: [
    '<h1>Iron Trivia</h1>',
    '<div class="loginForm row"></div>',
    '<div class="addUserForm row"></div>'
  ].join(''),
  dashView: [
    '<h1>Games Available</h1>',
    '<button name="logout-button">logout</button>',
    '<div class="join-game-container row"></div>',
    '<div class="add-game-container row"></div>'
  ].join(''),
  newGameForm: [
    '<div class="col-md-6 col-md-offset-3">',
      '<button name="to-new-game-form">Create game</button>',
      '<form class="new-game-form form-group hide">',
        '<input class="col-md-12 col-sm-12 col-xs-12" type="text" name="player-1" placeholder="player 1">',
        '<input class="col-md-12 col-sm-12 col-xs-12" type="text" name="player-2" placeholder="player 2">',
        '<input class="col-md-12 col-sm-12 col-xs-12" type="text" name="player-3" placeholder="player 3">',
        '<button name="submit-new-game" type="submit">Submit</button>',
      '</form>',
    '</div>'
  ].join(''),
  joinGame: [
    '<div>',
      '<h2>game id: <%= id %></h2>',
      '<h3>Players:</h3>',
      '<% obj.scoreList.forEach(function(el){%>',
      '<p><%= el.user.userName %></p>',
      '<% }) %>',
      '<button name="join-game">start game</button>',
    '</div>'
  ].join(''),
  gameContainerView: [
    '<div class="question-view col-md-8 vcenter"></div>',
    '<div class="answer-view col-md-4 col-sm-12 col-xs-12 vcenter"></div>',
    '<div class="score-view row"></div>'
  ].join(''),
  answerView: [
    '<form>',
      '<input class="col-md-12 col-sm-12 col-xs-12" name="answer" type="text" placeholder="Answer">',
      '<button name="submit-answer">Submit</button>',
    '</form>'
  ].join(''),
  scoreView: [
    '<div>',
      '<% obj.scoreList.forEach(function(el){%>',
      '<h3><%=el.user.userName%></h3>',
      '<p><%=el.score%></p>',
      '<% }) %>',
    '</div>'
  ].join('')
};
