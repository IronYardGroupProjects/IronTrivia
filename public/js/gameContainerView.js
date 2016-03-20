var Backbone = require('backbone');
var _ = require('underscore');
var template = require('./templates');
var $ = require('jquery');
var UserModel = require('./userModel');
var GameCollection = require('./gameCollection');
var GameCollectionView = require('./gameCollectionView');
var QuestionModel = require('./questionModel');
var QuestionView = require('./questionView');
var AnswerModel = require('./answerModel');
var AnswerView = require('./answerView');

module.exports = Backbone.View.extend({
  activeUser: null,
  collection: null,
  el: '.game',
  template: _.template(template.gameContainerView),
  initialize: function(){
    this.$el.append(this.render().el);
    var QModel = new QuestionModel();
    QModel.fetch().then((function(data){
      var QView = new QuestionView({model: QModel});
      sessionStorage.setItem('question', JSON.stringify(data));
    }).bind(this));
    var AView = new AnswerView();
  },
  render: function(){
    var markup = this.template();
    this.$el.html(markup);
    return this;
  }

});
