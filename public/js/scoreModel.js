var Backbone = require('backbone');

module.exports =Backbone.Model.extend({
  urlRoot: '/game',
  id: null,
  init:function(){
    console.log('score model initialized');
  },
  updateURL: function(gameId){
    if(gameId){
      this.urlRoot = this.urlRoot + '/' + gameId;
    } else {
      this.urlRoot = '/score';
    }
  }
});
