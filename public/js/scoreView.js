var Backbone = require('backbone');
var _ = require('underscore');
var templates = require('./templates');
var ScoreModel = require('./scoreModel');
var $ = require('jquery');

module.exports = Backbone.View.extend({
  model: null,
  el: '.score-view',
  template: _.template(templates.scoreView),
  initialize: function(){
    console.log(this.model.toJSON());
    this.$el.append(this.render().el);
  },
  render: function(){
    this.$el.html('');
    var markup = this.template(this.model.toJSON());
    this.$el.append(markup);
    return this;
  },

});
