var Backbone = require('backbone');
var _ = require('underscore');
var $ = require('jquery');
var templates = require('./templates');
var QuestionModel = require('./questionModel');

module.exports = Backbone.View.extend({
  model: null,
  el: '.question-view',
  template: _.template(templates.question),
  events: {

  },
  delete: function(){
    this.model.destroy();
  },
  initialize: function(){
    this.$el.append(this.render().el);
  },
  render: function(){
    var markup = this.template(this.model.toJSON()[0]);
    this.$el.append(markup);
    return this;
  },

});
