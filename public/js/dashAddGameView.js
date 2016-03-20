var Backbone = require('backbone');
var _ = require('underscore');
var tmpl= require('./templates');
var $ = require('jquery');
var GameModel = require('./gameModel');
var UserModel = require('./userModel');

module.exports = Backbone.View.extend({
  collection: null,
  el: '.add-game-container',
  template: _.template(tmpl.newGameForm),
  initialize: function(){
    this.$el.append(this.render().el);
    this.model = new GameModel({});
  },
  render: function(){
    var markup = this.template();
    this.$el.html(markup);
    return this;
  },
  events:{
    'click button[name="to-new-game-form"]': 'showForm',
    'click button[name="submit-new-game"]': 'createGame'
  },
  showForm: function(event){
    event.preventDefault();
    this.$el.find('.new-game-form').fadeIn().toggleClass('hide');
  },
  createGame: function(event){
    event.preventDefault();
    var that = this;
    var playerNames = [JSON.parse(sessionStorage.getItem('user')).userName,];
    playerNames.push(this.$el.find('input[name="player-1"]').val());
    playerNames.push(this.$el.find('input[name="player-2"]').val());
    playerNames.push(this.$el.find('input[name="player-3"]').val());
    that.model.set({
      id: null,
      playerNames: playerNames,
      scoreList: null
    });
    that.$el.find('input').val('');
    that.model.save({}, {
      error: function(error){
      console.log("Game creation Error", error);
    }, success: function(data){
      console.log("Game Created", data);
      that.model.fetch().then(function(){
        that.collection.add(that.model);
        that.model = new GameModel({});
      });
      }});
    }
});
