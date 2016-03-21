var Backbone = require('backbone');
var _ = require('underscore');
var templates = require('./templates');
var GameModel = require('./gameModel');
var UserModel = require('./userModel');
var $ = require('jquery');

module.exports = Backbone.View.extend({
  collection: null,
  tagName: 'article',
  className: 'game-select col-md-4',
  template: _.template(templates.joinGame),
  initialize: function(){
    this.$el.append(this.render().el);
  },
  events: {
    'click button[name="join-game"]': 'joinGame'
  },
  joinGame: function(event){
    event.preventDefault();
    that = this;
    var user = new UserModel(JSON.parse(sessionStorage.getItem('user')));
    user.set({
      isReady: true
    });
    user.save({},{
      error: function(error){
        console.log("UserError", error);
      },
      success: function(data){
        console.log("UserSuccess", data);
        var interval = window.setInterval(function(){
          $.ajax({
            url: '/game/' + that.model.toJSON().id,
            method: 'POST',
            success: function(data){
              if(data){
                console.log(that.model.toJSON());
                sessionStorage.setItem('game', JSON.stringify(that.model.toJSON()));
                Backbone.history.navigate("game", {trigger: true, replace: true});
                window.clearInterval(interval);
              }
            }
          });
        }, 500);
      }
    });
  },
  render: function(){
    var markup = this.template(this.model.toJSON());
    this.$el.html(markup);
    return this;
  },

});
