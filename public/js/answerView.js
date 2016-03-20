var Backbone = require('backbone');
var _ = require('underscore');
var $ = require('jquery');
var templates = require('./templates');
var AnswerModel = require('./answerModel');

module.exports = Backbone.View.extend({
  model: null,
  el: '.answer-view',
  template: _.template(templates.answerView),
  events: {
    'click button[name="submit-answer"]': 'submitAnswer'
  },
  submitAnswer: function(event){
    event.preventDefault();
    var question = JSON.parse(sessionStorage.getItem('question'));
    var answer = this.$el.find('input[name="answer"]').val();
    if(answer === question[0].answer){
      this.model.set({
        score: question[0].value,
        isCorrect: true
      });
      this.model.save();
    } else {
      this.model.set({
        isCorrect: false
      });
      this.model.save();
    }
  },
  initialize: function(){
    this.$el.append(this.render().el);
    this.model = new AnswerModel({});
  },
  render: function(){
    var markup = this.template();
    this.$el.append(markup);
    return this;
  },

});
