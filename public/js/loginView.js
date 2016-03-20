var Backbone = require('backbone');
var _ = require('underscore');
var tmpl= require('./templates');
var LoginModel = require('./loginModel');
var UserModel = require('./userModel');
var $ = require('jquery');
var Router = require('./router');

module.exports = Backbone.View.extend({
  collection: null,
  el: '.loginForm',
  template: _.template(tmpl.loginForm),
  initialize: function(){
    this.$el.append(this.render().el);
    this.model = new LoginModel({});
  },
  render: function(){
    var markup = this.template();
    this.$el.html(markup);
    return this;
  },
  events:{
    'click button[name="login"]': 'login',
  },
  login: function(event){
    event.preventDefault();
    this.model.set({
      userName: this.$el.find('input[name="username"]').val(),
      passwordHash: this.$el.find('input[name="password"]').val(),
    });
    this.$el.find('input').val('');
    this.model.buildURL();
    this.model.save({}, {
      error: function(error){
      console.log(error);
      {""}
    }, success: function(data){
      sessionStorage.setItem('user', JSON.stringify(data));
      Backbone.history.navigate("dashboard", {trigger: true, replace: true});
    }});
  },

});
