var Backbone = require('backbone');
var _ = require('underscore');
var $ = require('jquery');
var templates = require('./templates');
var AnswerModel = require('./answerModel');
var QuestionModel = require('./questionModel');
var QuestionView = require('./questionView');

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
    if(answer.toLowerCase() === question[0].answer.toLowerCase().trim()){
      this.model.set({
        score: question[0].value,
        isCorrect: true
      });
      this.model.save();
      var QModel = new QuestionModel();
      QModel.fetch().then((function(data){
        var QView = new QuestionView({model: QModel});
        sessionStorage.setItem('question', JSON.stringify(data));
      }).bind(this));
    } else {
      this.model.set({
        isCorrect: false
      });
      this.model.save();
      var QModel = new QuestionModel();
      QModel.fetch().then((function(data){
        var QView = new QuestionView({model: QModel});
        sessionStorage.setItem('question', JSON.stringify(data));
      }).bind(this));
    }
    this.$el.find('input[name="answer"]').val('');
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
