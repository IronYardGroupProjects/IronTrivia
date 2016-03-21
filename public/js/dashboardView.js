var Backbone = require('backbone');
var _ = require('underscore');
var template = require('./templates');
var $ = require('jquery');
var DashboardAddGameView = require('./dashAddGameView');
var DashboardAddGameFormModel = require('./dashAddGameFormModel');
var UserModel = require('./userModel');
var GameCollection = require('./gameCollection');
var GameCollectionView = require('./gameCollectionView');
var LogoutModel = require('./logoutModel')

module.exports = Backbone.View.extend({
  collection: null,
  el: '.dashboard',
  template: _.template(template.dashView),
  initialize: function(){
    that = this;
    this.$el.append(this.render().el);
    var GameCol = new GameCollection();
    GameCol.fetch().then((function(data){
      var user = new UserModel({model: JSON.parse(sessionStorage.getItem('user'))});
      var GameCollView = new GameCollectionView({collection: GameCol,model: user})
      var DashAddFormView = new DashboardAddGameView({collection: GameCol});
    }));
  },
  events:{
    "click button[name= 'logout-button']" : "logout",
  },
  logout: function() {
    event.preventDefault();
    console.log("logout");
    var logout = new LogoutModel();
	logout.save();
    Backbone.history.navigate("/", {trigger: true, replace: true});
  },
  render: function(){
    var markup = this.template();
    this.$el.html(markup);
    return this;
  }

});
